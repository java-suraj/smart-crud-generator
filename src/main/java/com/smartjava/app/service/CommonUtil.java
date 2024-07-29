package com.smartjava.app.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class CommonUtil {
	public static String mapToJavaType(String sqlType) {
		switch (sqlType.toUpperCase()) {
		case "INT":
		case "INTEGER":
		case "NUMBER":
			return "Long";
		case "VARCHAR":
		case "VARCHAR2":
		case "CHAR":
		case "TEXT":
			return "String";
		case "DATE":
			return "Date";
		case "BOOLEAN":
			return "Boolean";
		case "BLOB":
			return "Blob";
		case "CLOB":
			return "Clob";
		default:
			return "Object";
		}
	}

	public static String toCamelCase(String input, boolean capitalizeFirst) {
		StringBuilder result = new StringBuilder();
		boolean capitalizeNext = capitalizeFirst;

		for (char c : input.toCharArray()) {
			if (c == '_') {
				capitalizeNext = true;
			} else {
				if (capitalizeNext) {
					result.append(Character.toUpperCase(c));
					capitalizeNext = false;
				} else {
					result.append(c);
				}
			}
		}
		return result.toString();
	}

	public static void writeDataInFile(String str, String fileName) throws IOException {
		String directoryPath = "/SpringBoot/";
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		File file = new File(directoryPath + fileName + ".java");
		try (FileOutputStream fos = new FileOutputStream(file)) {
			fos.write(str.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ResponseEntity<byte[]> compressFiles(@RequestBody List<String> filePaths) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			String directoryPath = "/SpringBoot/";
			try (ZipOutputStream zos = new ZipOutputStream(baos)) {
				for (String filePath : filePaths) {
					File file = new File(directoryPath + filePath);
					try (FileInputStream fis = new FileInputStream(file)) {
						ZipEntry zipEntry = new ZipEntry(file.getName());
						zos.putNextEntry(zipEntry);

						byte[] buffer = new byte[1024];
						int length;
						while ((length = fis.read(buffer)) >= 0) {
							zos.write(buffer, 0, length);
						}
						zos.closeEntry();
					}
				}
			}

			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "attachment; filename=files.zip");

			return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
