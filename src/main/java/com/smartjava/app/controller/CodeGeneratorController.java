package com.smartjava.app.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smartjava.app.form.CodeForm;
import com.smartjava.app.service.MainService;

@Controller
@RequestMapping("")
public class CodeGeneratorController {

	@Autowired
	private MainService mainService;

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("codeForm", new CodeForm());
		return "index";
	}

	@Transactional(readOnly = true)
	@PostMapping("/generate")
	public String generateCode(@ModelAttribute("codeForm") CodeForm codeForm, Model model) {
		String databaseUrl = "";
		String databaseUsername = "";
		String databasePassword = "";
		try {
			Properties properties = new Properties();
			properties.load(new StringReader(codeForm.getDbProperties()));
			if (codeForm.getDbProperties() != null && !codeForm.getDbProperties().isEmpty()) {
				databaseUrl = properties.getProperty("spring.datasource.url");
				databaseUsername = properties.getProperty("spring.datasource.username");
				databasePassword = properties.getProperty("spring.datasource.password");
			}
			System.out.println("DB Con created");
			String baseDir = "generated-code";
			Map<String, String> fileContent = mainService.generateFiles(baseDir, codeForm.getTableName(),
					codeForm.getSequenceName(), databaseUrl, databaseUsername, databasePassword);
			File[] files = new File(baseDir).listFiles();
			model.addAttribute("fileContent", fileContent);
			// model.addAttribute("generatedFiles", files);
			return "generated-code";
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			model.addAttribute("message", "Error generating code: " + e.getMessage());
			return "error";
		}
	}

	@GetMapping("/download")
	public ResponseEntity<InputStreamResource> downloadGeneratedCode() throws IOException {
		String baseDir = "generated-code";
		String zipFilePath = baseDir + ".zip";

		zipDirectory(baseDir, zipFilePath);

		InputStreamResource resource = new InputStreamResource(new FileInputStream(zipFilePath));
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=generated-code.zip");

		// Schedule deletion of the zip file and the directory
		new File(zipFilePath).deleteOnExit();
		deleteDirectoryOnExit(new File(baseDir));

		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}

	private void zipDirectory(String sourceDirPath, String zipFilePath) throws IOException {
		try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
			File sourceDir = new File(sourceDirPath);
			File[] files = sourceDir.listFiles();
			if (files != null) {
				for (File file : files) {
					addFileToZip(zipOut, file, "");
				}
			}
		}
	}

	private void addFileToZip(ZipOutputStream zipOut, File file, String parentDir) throws IOException {
		String zipEntryName = parentDir + file.getName();
		if (file.isDirectory()) {
			zipEntryName = zipEntryName.endsWith("/") ? zipEntryName : zipEntryName + "/";
			zipOut.putNextEntry(new ZipEntry(zipEntryName));
			File[] files = file.listFiles();
			if (files != null) {
				for (File childFile : files) {
					addFileToZip(zipOut, childFile, zipEntryName);
				}
			}
			zipOut.closeEntry();
		} else {
			try (FileInputStream fis = new FileInputStream(file)) {
				ZipEntry zipEntry = new ZipEntry(zipEntryName);
				zipOut.putNextEntry(zipEntry);
				byte[] bytes = new byte[1024];
				int length;
				while ((length = fis.read(bytes)) >= 0) {
					zipOut.write(bytes, 0, length);
				}
				zipOut.closeEntry();
			}
		}
	}

	private void deleteDirectoryOnExit(File directory) {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				Files.walk(directory.toPath()).sorted((path1, path2) -> path2.compareTo(path1))
						.map(java.nio.file.Path::toFile).forEach(File::delete);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}));
	}
}
