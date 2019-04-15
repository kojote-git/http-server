package com.jkojote.server.impl;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.ErrorProperties;
import com.jkojote.server.HeaderName;
import com.jkojote.server.HttpHeader;
import com.jkojote.server.HttpMethod;
import com.jkojote.server.HttpRequestBody;
import com.jkojote.server.HttpStatus;
import com.jkojote.server.PathVariables;
import com.jkojote.server.QueryString;
import com.jkojote.server.ServerConfiguration;
import com.jkojote.server.HttpRequest;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.bodies.StreamRequestBody;
import com.jkojote.server.exceptions.BadRequestException;
import com.jkojote.server.exceptions.PathVariableConversionException;
import com.jkojote.server.utils.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jkojote.server.ServerConfiguration.RequestResolution;
import static com.jkojote.server.ServerConfiguration.ErrorData;

class HttpRequestHandler implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger("com.jkojote.server.HttpServer");

	static final Pattern METHOD_PATTERN = Pattern.compile(
		"GET|POST|PUT|PATCH|OPTIONS|DELETE|HEAD"
	);
	// check general format for request line without validating URI
	static final Pattern REQUEST_LINE_PATTERN = Pattern.compile(
		"^(" + METHOD_PATTERN + ") " + "\\S+ HTTP/\\d\\.\\d$"
	);

	private static final ArgumentsResolver ARGUMENTS_RESOLVER = new DefaultArgumentResolver();
	private static final byte[] CRLF = "\r\n".getBytes();
	private static final byte[] HTTP_VERSION = "HTTP/1.1".getBytes();
	private static final byte[] SPACE = " ".getBytes();
	private static final byte[] COLON = ":".getBytes();
	private static final int WRITE_BUFFER_SIZE = 4096;

	private Socket socket;
	private ServerConfiguration configuration;

	HttpRequestHandler(Socket socket, ServerConfiguration configuration) {
		this.socket = socket;
		this.configuration = configuration;
	}

	@Override
	public void run() {
		try (InputStream in = socket.getInputStream();
			 OutputStream out = socket.getOutputStream()) {
			handleRequest(in, out);
		} catch (Exception e) {
			LOG.error("An exception occurred", e);
		}
	}

	private void handleRequest(InputStream in, OutputStream out) throws IOException {
		try {
			HttpRequest request = readRequest(in);
			RequestResolution requestResolution = configuration.resolveRequest(request);
			if (requestResolution == null) {
				ErrorDataImpl errorData = new ErrorDataImpl()
					.setMessage("cannot resolve request")
					.putStatus(HttpStatus.NOT_FOUND)
					.putRequest(request)
					.putProperty(ErrorProperties.PATH, request.getPath());
				HttpResponse notFound = configuration.getResponseOnError(
					HttpStatus.NOT_FOUND, errorData
				);
				writeResponse(out, notFound == null ? Responses.NOT_FOUND : notFound);
				return;
			}
			ControllerMethod method = requestResolution.getMethod();
			PathVariables pathVariables = requestResolution.getPathVariables();
			Object[] args = ARGUMENTS_RESOLVER.resolve(method, request, pathVariables);
			writeResponse(out, method.execute(args));
		} catch (BadRequestException | PathVariableConversionException e) {
			ErrorDataImpl errorData = new ErrorDataImpl()
					.setMessage("bad request")
					.putStatus(HttpStatus.BAD_REQUEST)
					.putException(e);
			HttpResponse badRequest = configuration.getResponseOnError(
					HttpStatus.BAD_REQUEST, errorData
			);
			writeResponse(out, badRequest == null ? Responses.BAD_REQUEST : badRequest);
		} catch (RuntimeException e) {
			ErrorDataImpl errorData = new ErrorDataImpl()
				.setMessage("internal error")
				.putStatus(HttpStatus.INTERNAL_ERROR)
				.putException(e);
			HttpResponse internalError = configuration.getResponseOnError(
				HttpStatus.INTERNAL_ERROR, errorData
			);
			LOG.error("An exception occurred ", e);
			writeResponse(out, internalError == null ? Responses.INTERNAL_ERROR : internalError);
		}
	}

	private HttpRequest readRequest(InputStream in) throws IOException {
		String firstLine = readLine(in);
		RequestLine requestLine = parseRequestLine(firstLine);
		Map<HeaderName, String> headers = readHeaders(in);
		long contentLength = getContentLength(headers);
		HttpRequestBody body = getRequestBody(contentLength, in);
		QueryString queryString = parseQueryString(firstLine, true);
		return new HttpRequestImpl(
			firstLine, requestLine.path, requestLine.method,
			body, queryString, headers
		);
	}

	private RequestLine parseRequestLine(String requestLine) {
		Matcher methodMatcher = METHOD_PATTERN.matcher(requestLine);
		if (!methodMatcher.find()) {
			throw new BadRequestException("unsupported method");
		}
		if (!REQUEST_LINE_PATTERN.matcher(requestLine).matches()) {
			throw new BadRequestException("malformed request line");
		}
		String method = methodMatcher.group();
		String uri = getUri(requestLine);
		return new RequestLine(Enum.valueOf(HttpMethod.class, method), uri);
	}

	private String getUri(String requestLine) {
		int begin = requestLine.indexOf(' ');
		int end = requestLine.lastIndexOf(' ');
		if (begin == -1 || begin == end) {
			throw new BadRequestException("resource is not specified");

		}
		String uri = requestLine.substring(begin + 1, end);
		try {
			new URI(uri);
		} catch (URISyntaxException e) {
			throw new BadRequestException("malformed uri");
		}
		int beginOfTheQueryString = uri.indexOf('?');
		if (beginOfTheQueryString == -1) {
			return uri;
		} else {
			return uri.substring(0, beginOfTheQueryString);
		}
	}

	private static class RequestLine {
		private HttpMethod method;
		private String path;

		RequestLine(HttpMethod method, String url) {
			this.method = method;
			this.path = url;
		}
	}

	private Map<HeaderName, String> readHeaders(InputStream in) throws IOException {
		Map<HeaderName, String> headers = new HashMap<>();
		String line = readLine(in);
		while (!line.isEmpty()) {
			String[] header = line.split(":");
			String name = header[0];
			String value = (header.length == 1) ? "" : header[1];
			headers.put(HeaderName.of(name), value);
			line = readLine(in);
		}
		return headers;
	}

	private QueryString parseQueryString(String requestLine, boolean ignoreMalformedParameters) {
		int begin = requestLine.indexOf('?');
		if (begin == -1) {
			return new QueryStringImpl("");
		}
		int end = requestLine.lastIndexOf(' ');
		String queryString = requestLine.substring(begin + 1, end);
		return new QueryStringImpl(queryString, ignoreMalformedParameters);
	}

	private long getContentLength(Map<HeaderName, String> headers) {
		String header = headers.get(HeaderName.of("Content-Length"));
		try {
			return header == null ? 0 : Long.parseLong(header.trim());
		} catch (NumberFormatException e) {
			throw new BadRequestException("Illegal value for \"Content-Length\" header: " + header);
		}
	}

	private HttpRequestBody getRequestBody(long contentLength, InputStream in) {
		return new StreamRequestBody(contentLength, in);
	}

	private void writeResponse(OutputStream out, HttpResponse response) throws IOException {
		writeStatus(out, response);
		writeHeaders(out, response);
		writeBody(out, response);
	}

	private void writeStatus(OutputStream out, HttpResponse response) throws IOException {
		HttpStatus status = response.getStatus();
		out.write(HTTP_VERSION);
		out.write(SPACE);
		out.write(intToBytes(status.getCode()));
		out.write(SPACE);
		out.write(status.getReasonPhrase().getBytes());
		out.write(CRLF);
	}

	private void writeHeaders(OutputStream out, HttpResponse response) throws IOException {
		Iterable<HttpHeader> headers = response.getHeaders();
		for (HttpHeader header : headers) {
			out.write(header.getName().getBytes());
			out.write(COLON);
			out.write(header.getValue().getBytes());
			out.write(CRLF);
		}
		out.write(CRLF);
	}

	private void writeBody(OutputStream out, HttpResponse response) throws IOException {
		try (InputStream in = response.getResponseBody().getInputStream()) {
			IOUtils.transfer(in, out, WRITE_BUFFER_SIZE);
		}
	}

	private byte[] intToBytes(int i) {
		return String.valueOf(i).getBytes();
	}

	private String readLine(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		int c;
		while ((c = in.read()) > 0) {
			if (c == '\r') {
				if (in.read() != '\n') {
					throw new BadRequestException("Illegal line separator. Expected CRLF");
				}
				return sb.toString();
			}
			sb.append((char) c);
		}
		return sb.toString();
	}

	private static class ErrorDataImpl implements ErrorData  {
		private String message;
		private Map<String, Object> properties;

		private ErrorDataImpl() {
			properties = new HashMap<>();
			message = "";
		}

		@Override
		public String getMessage() {
			return message;
		}

		@Override
		public Object getProperty(String name) {
			return properties.get(name);
		}

		ErrorDataImpl setMessage(String message) {
			this.message = message;
			return this;
		}

		ErrorDataImpl putProperty(String name, Object obj) {
			properties.put(name, obj);
			return this;
		}

		ErrorDataImpl putStatus(HttpStatus status) {
			properties.put(ErrorProperties.STATUS, status);
			return this;
		}

		ErrorDataImpl putRequest(HttpRequest request) {
			properties.put(ErrorProperties.REQUEST, request);
			return this;
		}

		ErrorDataImpl putException(Exception e) {
			properties.put(ErrorProperties.EXCEPTION, e);
			return this;
		}

		@Override
		public boolean hasProperty(String name) {
			return properties.containsKey(name);
		}
	}
}