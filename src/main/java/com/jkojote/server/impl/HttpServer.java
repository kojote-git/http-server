package com.jkojote.server.impl;

import com.jkojote.server.ServerConfiguration;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer implements Runnable {
	private ServerSocket socket;
	private ServerConfiguration configuration;
	private ExecutorService executorService;
	private boolean running;

	public HttpServer(ServerConfiguration configuration, int port) throws IOException {
		this.socket = new ServerSocket(port);
		this.configuration = configuration;
		this.executorService = Executors.newCachedThreadPool();
	}

	public HttpServer(ServerConfiguration configuration) throws IOException {
		this(configuration, 0);
	}

	public synchronized void start() {
		running = true;
		executorService.execute(this);
	}

	public synchronized void stop() {
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	public int getLocalPort() {
		return socket.getLocalPort();
	}

	@Override
	public void run() {
		while (running) {
			try {
				executorService.execute(new HttpRequestHandler(socket.accept(), configuration));
			} catch (IOException e) {
				// TODO logging
				e.printStackTrace();
			}
		}
	}
}
