package com.jkojote.server.impl;

import com.jkojote.server.ServerConfiguration;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
	private ServerSocket socket;
	private ServerConfiguration resolver;
	private ExecutorService executorService;

	public HttpServer(ServerConfiguration resolver, int port) throws IOException {
		this.socket = new ServerSocket(port);
		this.resolver = resolver;
		this.executorService = Executors.newCachedThreadPool();
	}

	public HttpServer(ServerConfiguration resolver) throws IOException {
		this(resolver, 0);
	}

	
}
