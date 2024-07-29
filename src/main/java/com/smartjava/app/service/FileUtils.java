package com.smartjava.app.service;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.http.ResponseEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils {

	public static void writeDataInFile(String data, String fileName) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName + ".java"))) {
			bw.write(data);
		}
	}

	public static String toCamelCase(String str, boolean capitalizeFirstLetter) {
		StringBuilder builder = new StringBuilder();
		boolean nextUpperCase = false;
		for (char c : str.toCharArray()) {
			if (c == '_') {
				nextUpperCase = true;
			} else {
				if (nextUpperCase) {
					builder.append(Character.toUpperCase(c));
					nextUpperCase = false;
				} else {
					builder.append(Character.toLowerCase(c));
				}
			}
		}
		if (capitalizeFirstLetter) {
			builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));
		}
		return builder.toString();
	}

	public static String mapToJavaType(String dataType) {
		String javaType = "";
		switch (dataType) {
		case "NUMBER":
			javaType = "Long";
			break;
		case "VARCHAR2":
		case "VARCHAR":
			javaType = "String";
			break;
		case "DATE":
			javaType = "Date";
			break;
		case "BLOB":
			javaType = "Blob";
			break;
		case "CLOB":
			javaType = "Clob";
			break;
		default:
			javaType = "String";
			break;
		}
		return javaType;
	}

	public static ResponseEntity<byte[]> compressFiles(List<String> list) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ZipOutputStream zos = new ZipOutputStream(baos)) {
			for (String fileName : list) {
				try (FileInputStream fis = new FileInputStream(fileName)) {
					ZipEntry entry = new ZipEntry(fileName);
					zos.putNextEntry(entry);
					byte[] bytes = new byte[1024];
					int length;
					while ((length = fis.read(bytes)) >= 0) {
						zos.write(bytes, 0, length);
					}
					zos.closeEntry();
				}
			}
		}
		baos.flush();
		baos.close();

		// Writing zip content to a file
		String zipFileName = "output.zip";
		File zipFile = new File(zipFileName);
		try (FileOutputStream fos = new FileOutputStream(zipFile)) {
			baos.writeTo(fos);
		}
		baos.close();

		// Convert to ResponseEntity and return
		byte[] zipBytes = baos.toByteArray();
		return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=\"" + zipFileName + "\"")
				.body(zipBytes);
	}

	public static void writeDataInFile(String str, String directoryPath, String fileName) throws IOException {
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		File file = new File(directoryPath + File.separator + fileName + ".java");
		try (FileOutputStream fos = new FileOutputStream(file)) {
			fos.write(str.getBytes());
		} catch (IOException e) {
			log.error("Error writing data to file: {}", e.getMessage());
			throw e;
		}
	}
	
	public static void writeFile(String dirPath, String fileName, String content) throws IOException {
		Files.createDirectories(Paths.get(dirPath));
		Files.write(Paths.get(dirPath, fileName), content.getBytes());
	}
}
