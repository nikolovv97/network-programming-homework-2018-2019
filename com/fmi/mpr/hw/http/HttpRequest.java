package com.fmi.mpr.hw.http;

import java.io.IOException;
import java.io.PrintStream;

public class HttpRequest {
	public static String parseRequest(PrintStream ps, String request) throws IOException {

		System.out.println(request);

		String[] lines = request.split("\n");

		String firstHeader = lines[0];

		if (firstHeader.split(" ")[0].equals("GET")) {
			return getFile();
		} else {
			uploadFile();
			return "";
		}
	}

	private static void uploadFile() {

	}

	private static String getFile() {
		return "";
	}
}
