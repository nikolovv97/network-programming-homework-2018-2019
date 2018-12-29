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
				
				client.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void processClient(Socket client) throws IOException {
		try (BufferedInputStream br = new BufferedInputStream(client.getInputStream());
				PrintStream ps = new PrintStream(client.getOutputStream(), true)) {

			read(ps, br);
		}
	}

	private String read(PrintStream ps, BufferedInputStream br) throws IOException {
		if (br != null) {
			StringBuilder request = new StringBuilder();

			byte[] buffer = new byte[1024];
			int bytesRead = 0;

			while ((bytesRead = br.read(buffer, 0, 1024)) > 0) {
				request.append(new String(buffer, 0, bytesRead));

				if (bytesRead < 1024) {
					break;
				}
			}

			return HttpRequest.parseRequest(ps, request.toString());
		}
		return "Error";
	}
}
