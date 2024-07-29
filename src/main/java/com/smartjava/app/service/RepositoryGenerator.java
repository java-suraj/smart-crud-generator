package com.smartjava.app.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class RepositoryGenerator {

	public Map<String, String> generateRepositoryInterface(String baseDir, String tableName) throws IOException {
		String className = CommonUtil.toCamelCase(tableName.toLowerCase(), true);
		Map<String, String> fileContent = new HashMap<>();
		File modelDir = new File(baseDir + "/repository");
		modelDir.mkdirs();
		StringBuilder interfaceContent = new StringBuilder();
		interfaceContent.append("\nimport org.springframework.data.jpa.repository.JpaRepository;\n");
		interfaceContent.append("import org.springframework.stereotype.Repository;\n");
		interfaceContent.append("\n");
//        interfaceContent.append("@Repository\n");
		interfaceContent.append("public interface ").append(className).append("Repository extends JpaRepository&lt;")
				.append(className).append(", Long&gt; {\n\n\n");
		interfaceContent.append("}\n");
		// CommonUtil.writeDataInFile(interfaceContent.toString(), className +
		// "Repository");
		// FileUtils.writeFile(baseDir + "/model", className + ".java",
		// interfaceContent.toString());
		fileContent.put(className + "Repository", interfaceContent.toString());
		return fileContent;
	}
}
