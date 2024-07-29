package com.smartjava.app.service;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModelClassGenerator {

    public Map<String, String> generateEntityClass(String baseDir, String tableName, String sequenceGenerator, ResultSet columns) throws IOException {
        File modelDir = new File(baseDir + "/model");
        modelDir.mkdirs();
        String className = "";
        log.info("sequenceGenerator : {}", sequenceGenerator);
        Map<String, String> fileContent = new HashMap<>();
        
        Set<String> imports = new HashSet<>();
        
        StringBuilder classContent = new StringBuilder();
        try {
            className = CommonUtil.toCamelCase(tableName.toLowerCase(), true);
            
            imports.add("import javax.persistence.*;");
            imports.add("import java.sql.*;"); 
            imports.add("import lombok.Data;");
            imports.add("import org.springframework.data.jpa.domain.support.AuditingEntityListener;");
            
            classContent.append("@Data\n");
            classContent.append("@Entity\n");
            classContent.append("@EntityListeners(AuditingEntityListener.class)\n");
            classContent.append("@Table(name = \"").append(tableName.toUpperCase()).append("\")\n");
            classContent.append("public class ").append(className).append(" {\n");
            classContent.append("    @Id\n");
            if (!sequenceGenerator.isEmpty()) {
                String str = "    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = \"${SEQUENCE}\")\n" +
                             "    @SequenceGenerator(sequenceName = \"${SEQUENCE}\", allocationSize = 1, name = \"${SEQUENCE}\")\n";
                str = str.replace("${SEQUENCE}", sequenceGenerator);
                classContent.append(str);
            }

            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String dataType = columns.getString("TYPE_NAME");
                String javaType = CommonUtil.mapToJavaType(dataType);

                if (dataType.equalsIgnoreCase("BLOB") || dataType.equalsIgnoreCase("CLOB")) {
                    imports.add("import javax.persistence.Lob;");
                }
                if (dataType.equalsIgnoreCase("DATE") || dataType.equalsIgnoreCase("TIMESTAMP")) {
                    imports.add("import java.util.*;");
                }

                if (columnName.startsWith("ATTRIBUTE")) {
                    int attributeNumber = Integer.parseInt(columnName.substring("attribute".length()));
                    if (attributeNumber >= 26 && attributeNumber <= 30) {
                        classContent.append("    @Lob\n");
                        classContent.append("    private ").append(javaType).append(" ")
                                .append(CommonUtil.toCamelCase(columnName.toLowerCase(), false)).append(";\n");
                        continue;
                    }
                }

                switch (columnName) {
                    case "CREATED_BY":
                        classContent.append("    @Column(updatable = false)\n");
                        classContent.append("    @CreatedBy\n");
                        classContent.append("    private ").append(javaType).append(" ")
                                .append(CommonUtil.toCamelCase(columnName.toLowerCase(), false)).append(";\n");
                        break;
                    case "CREATION_DATE":
                        classContent.append("    @ApiModelProperty(notes = \"creationDate format YYYY-MM-DD\", example = \"2019-12-12\", required = true, position = 0)\n");
                        classContent.append("    @Column(updatable = false)\n");
                        classContent.append("    @CreatedDate\n");
                        classContent.append("    private ").append(javaType).append(" ")
                                .append(CommonUtil.toCamelCase(columnName.toLowerCase(), false)).append(";\n");
                        break;
                    case "LAST_UPDATED_BY":
                        classContent.append("    @LastModifiedBy\n");
                        classContent.append("    private ").append(javaType).append(" ")
                                .append(CommonUtil.toCamelCase(columnName.toLowerCase(), false)).append(";\n");
                        break;
                    case "LAST_UPDATE_DATE":
                        classContent.append("    @ApiModelProperty(notes = \"Last Updated date format YYYY-MM-DD\", example = \"2019-12-12\", required = true, position = 0)\n");
                        classContent.append("    @LastModifiedDate\n");
                        classContent.append("    private ").append(javaType).append(" ")
                                .append(CommonUtil.toCamelCase(columnName.toLowerCase(), false)).append(";\n");
                        break;
                    case "VERSION_NUMBER":
                        classContent.append("    @Version\n");
                        classContent.append("    private ").append(javaType).append(" ")
                                .append(CommonUtil.toCamelCase(columnName.toLowerCase(), false)).append(";\n");
                        break;
                    case "CLIENT_ID":
                        classContent.append("    @Column(updatable = false)\n");
                        classContent.append("    private ").append(javaType).append(" ")
                                .append(CommonUtil.toCamelCase(columnName.toLowerCase(), false)).append(";\n");
                        break;
                    case "IS_SEEDED":
                        classContent.append("    private String isSeeded;\n");
                        classContent.append("\n    @PrePersist\n");
                        classContent.append("    public void preInsert() {\n");
                        classContent.append("        this.isSeeded = \"N\";\n");
                        classContent.append("    }\n");
                        break;
                    default:
                        classContent.append("    private ").append(javaType).append(" ")
                                .append(CommonUtil.toCamelCase(columnName.toLowerCase(), false)).append(";\n");
                        break;
                }
            }
            classContent.append("}\n");
        } catch (SQLException e) {
            log.error("SQL Exception occurred: ", e);
        }
        
        StringBuilder importsContent = new StringBuilder();
        for (String importLine : imports) {
            importsContent.append(importLine).append("\n");
        }
        fileContent.put(className, importsContent.append(classContent).toString());
        return fileContent;
    }
}
