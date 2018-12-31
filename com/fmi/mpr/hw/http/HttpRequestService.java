package com.fmi.mpr.hw.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestService {
	private static final Map<String, String> contentTypeMap = createContentTypeMap();

	private static Map<String, String> createContentTypeMap() {
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

	public String parseRequest(PrintStream ps, String request) throws IOException {
		String[] lines = request.split("\n");

		String firstHeader = lines[0];

		String filename = firstHeader.split(" ")[1];

		if (firstHeader.split(" ")[0].equals("GET")) {
			try {
				return processGET(ps, filename);
			} catch (FileNotFoundException e) {
				createResponse(ps, "404 Not Found", "text/plain");

				byte[] errorMessage = ("No file named \"" + filename + "\" found on the server").getBytes();

				ps.write(errorMessage, 0, errorMessage.length);

				return "Error";
			}
		} else {
			processPOST(ps, lines);

			createResponse(ps, "200 OK", "text/plain");

			byte[] postMessage = "File uploaded successfully!".getBytes();

			ps.write(postMessage, 0, postMessage.length);

			return "success";
		}
	}

	private String processGET(PrintStream ps, String url) throws IOException {
		if (url.equals("/")) {
			return null;
		}

		String[] urlParts = url.split("\\=");
		String filename = urlParts[0].substring(1);

		if (urlParts.length > 1) { // url could be
									// localhost:8080/?filename=hype.gif
			filename = urlParts[1];
		}

		String[] filenameParts = filename.split("\\.");

		if (filenameParts.length == 1 || !contentTypeMap.containsKey(filenameParts[1])) {
			throw new FileNotFoundException("There is no such file");
		}

		getFile(ps, filename, filenameParts);

		return "Success";
	}

	private void getFile(PrintStream ps, String filename, String[] filenameParts) throws IOException {
		FileInputStream fis = new FileInputStream(new File(filename));

		createResponse(ps, "200 OK", contentTypeMap.get(filenameParts[1]));

		int bytesRead = 0;
		byte[] buffer = new byte[8192];

		while ((bytesRead = fis.read(buffer, 0, 8192)) > 0) {
			ps.write(buffer, 0, bytesRead);
		}

		System.out.println("File sent!");

		fis.close();
	}

	private void processPOST(PrintStream ps, String[] lines) throws IOException {
		uploadFile(ps, lines);
	}

	private void uploadFile(PrintStream ps, String[] lines) throws IOException {
		StringBuffer x = new StringBuffer();
		int lineFilestart = 0;
		String filename="";
		for (int i = 0; i < lines.length; ++i) {
			if (lines[i].startsWith("Content-Disposition")) {
				String[] properties = lines[i].split(" ");
				filename = properties[properties.length - 1];
				filename = filename.split("=")[1];
				filename = filename.substring(1, filename.length() - 2);
				
				lineFilestart = i + 3;
				break;
			}
		}

		for (int i = lineFilestart; i < lines.length - 1; ++i) {
			x.append(lines[i]);
			x.append("\n");
		}

		FileOutputStream fout = new FileOutputStream(new File(filename));

		byte[] arr = x.toString().getBytes("ISO_8859_1");
		fout.write(arr, 0, arr.length);
		fout.close();
	}

	private void createResponse(PrintStream ps, String status, String contentType) {
		ps.println("HTTP/1.0 " + status);
		ps.println("Content-Type: " + contentType);
		ps.println();

	}
}
