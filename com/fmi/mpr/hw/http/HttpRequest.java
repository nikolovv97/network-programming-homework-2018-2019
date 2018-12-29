package com.fmi.mpr.hw.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
	private static final Map<String,String> contentTypeMap = createContentTypeMap();
	
	private static Map<String,String> createContentTypeMap() {
		Map<String, String> myMap = new HashMap<String, String>();
		myMap.put("mpeg", "video/mpeg");
		myMap.put("mp4", "video/mp4");
		myMap.put("webm", "video/webm");

		myMap.put("gif", "image/gif");
		myMap.put("jpeg", "image/jpeg");
		myMap.put("jpg", "image/jpg");
		myMap.put("png", "image/png");

		myMap.put("css", "text/css");
		myMap.put("csv", "text/csv");
		myMap.put("html", "text/xml");
		myMap.put("xml", "text/xml");
		myMap.put("txt", "text/plain");

		return myMap;
	}

	public static String parseRequest(PrintStream ps, String request) throws IOException {

		System.out.println(request);

		String[] lines = request.split("\n");

		String firstHeader = lines[0];

		if (firstHeader.split(" ")[0].equals("GET")) {
			String filename = firstHeader.split(" ")[1];

			try {
				getFile(ps, filename);
			} catch (FileNotFoundException e) {
				ps.println("HTTP/1.0 404 Not Found");
				ps.println("Content-Type: text/plain");
				ps.println();

				byte[] errorMessage = ("No file named \"" + filename + "\" found on the server").getBytes();

				ps.write(errorMessage, 0, errorMessage.length);
			}
		} else {
			uploadFile();
		}
		
		return null;
	}

	private static void getFile(PrintStream ps, String filename) throws IOException {
		String[] fileparts = filename.split("\\.");

		if (fileparts.length == 1 || !contentTypeMap.containsKey(fileparts[1])) {
			throw new FileNotFoundException("There is no such file");
		}

		FileInputStream fis = new FileInputStream(new File(filename.substring(1)));

		ps.println("HTTP/1.0 200 OK");
		ps.println("Content-Type: " + contentTypeMap.get(fileparts[1]));
		ps.println();

		getFile(ps, fis);

		fis.close();
	}

	private static void getFile(PrintStream ps, FileInputStream fis) throws IOException {

		int bytesRead = 0;
		byte[] buffer = new byte[8192];

		while ((bytesRead = fis.read(buffer, 0, 8192)) > 0) {
			ps.write(buffer, 0, bytesRead);
		}

		System.out.println("Image sent!");
	}

	private static void uploadFile() {

	}

}
