package com.fmi.mpr.hw.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

	ServerSocket ss;
	
	HttpRequestService httpService; 

	public HttpServer(int port) throws IOException {

		ss = new ServerSocket(port);
		
		httpService = new HttpRequestService();
	}

	public void run() {
		while (true) {
			try {
				Socket client = ss.accept();

				System.out.printf("Client from %s connected%n", client.getRemoteSocketAddress());
				System.out.println("Client being processed...\n");

				processClient(client);

				client.close();

				System.out.println("Client disconnected.\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void processClient(Socket client) throws IOException {
		try (BufferedInputStream br = new BufferedInputStream(client.getInputStream());
				PrintStream ps = new PrintStream(client.getOutputStream(), true)) {
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
				
				boolean response = httpService.parseRequest(ps, request.toString());
				
				write(ps, response);
			}
		}
	}

	private void write(PrintStream ps, boolean response) {

		if (ps != null && !response) {
			ps.println("HTTP/1.0 200 OK");
			ps.println();
			ps.println("<!DOCTYPE html>\n" + 
					"<html>\n" + 
					"<body>\n" + 
					
					"<form method=\"POST\" action=\"/\">" +
						" <input type=\"file\">" +
						"<input type=\"submit\" value=\"Upload\">" +
					"</form>" +
						
					"<form method=\"GET\" >" +
					"<input type=\"text\" name=\"filename\">" +
					"<input type=\"submit\" value=\"Get File\">" +
					"</form>" +
					
					"</body>\n" + 
					"</html>");
		}
	}
}
