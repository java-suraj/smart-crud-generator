package com.smartjava.app.service;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class PaylaodGenerator {
    public Map<String, String> generatePayloadClass(String baseDir, String tableName, ResultSet columns)
            throws IOException {
        StringBuilder classContent = new StringBuilder();
        Map<String, String> fileContent = new HashMap<>();
        String className = "";
        boolean firstColumnProcessed = false; 
        try {
            className = CommonUtil.toCamelCase(tableName.toLowerCase(), true);
            classContent.append("import lombok.Data;\n");
            classContent.append("import javax.persistence.Id;\n"); 
            classContent.append("import com.fasterxml.jackson.annotation.JsonIgnoreProperties;\n"); 
            classContent.append("@Data\n@JsonIgnoreProperties(ignoreUnknown = true)\n");
            classContent.append("public class ").append(className).append("Payload {\n");

            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String dataType = columns.getString("TYPE_NAME");
                String javaType = CommonUtil.mapToJavaType(dataType);
     
                if (!firstColumnProcessed) {
                    classContent.append("    @Id\n");
                    firstColumnProcessed = true;
                }

                if (columnName.startsWith("ATTRIBUTE") || 
                        "CREATED_BY".equalsIgnoreCase(columnName) || 
                        "CREATION_DATE".equalsIgnoreCase(columnName) || 
                        "LAST_UPDATED_BY".equalsIgnoreCase(columnName) || 
                        "LAST_UPDATE_DATE".equalsIgnoreCase(columnName) || 
                        "VERSION_NUMBER".equalsIgnoreCase(columnName) || 
                        "CLIENT_ID".equalsIgnoreCase(columnName)) {
                    continue;
                }

                classContent.append("    private ").append(javaType).append(" ")
                        .append(CommonUtil.toCamelCase(columnName.toLowerCase(), false)).append(";\n");
            }
            classContent.append("}\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        fileContent.put(className + "Paylaod", String.valueOf(classContent));
        return fileContent;
    }
}
