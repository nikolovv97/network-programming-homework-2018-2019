package com.fmi.mpr.hw.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

	ServerSocket ss;

	public HttpServer(int port) throws IOException {

		ss = new ServerSocket(port);
	}

	public void run() {
		while (true) {
			try {
				Socket client = ss.accept();

				processClient(client);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void processClient(Socket client) throws IOException {
		try (BufferedInputStream br = new BufferedInputStream(client.getInputStream());
				PrintStream ps = new PrintStream(client.getOutputStream(), true)) {

			String response = read(ps, br);
			write(ps, response);
		}
	}

	private String read(PrintStream ps, BufferedInputStream br) {
		return "";
	}

	private void write(PrintStream ps, String response) {

	}
}
