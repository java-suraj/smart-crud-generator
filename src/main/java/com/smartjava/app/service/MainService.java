package com.smartjava.app.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MainService {

	@Autowired
	private ModelClassGenerator classGenerator;

	@Autowired
	private PaylaodGenerator payloadGenerator; // Corrected spelling

	@Autowired
	private RepositoryGenerator repositoryGenerator;
	
	@Autowired
	private ServiceClassGenerator serviceClassGenerator;

	@Transactional(readOnly = true)
	public Map<String, String> generateFiles(String baseDir, String tableName, String sequenceName, String databaseUrl,
			String databaseUsername, String databasePassword) throws IOException, SQLException {

		Connection con = null;
		ResultSet columns = null;
		Map<String, String> allFileContent = new HashMap<>();
		try {
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				con = DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			System.out.println("Connection Object ::" + con);

			DatabaseMetaData metaData = con.getMetaData();
			columns = metaData.getColumns(null, null, tableName, null);
			if (!columns.next()) {
				allFileContent.put(tableName, "Record not found for this table");
				return allFileContent;
			}

			Map<String, String> entityClass = classGenerator.generateEntityClass(baseDir, tableName, sequenceName,
					metaData.getColumns(null, null, tableName, null));
			allFileContent.putAll(entityClass);

			Map<String, String> repositoryInterface = repositoryGenerator.generateRepositoryInterface(baseDir,
					tableName);
			allFileContent.putAll(repositoryInterface);

			Map<String, String> payloadClass = payloadGenerator.generatePayloadClass(baseDir, tableName,
					metaData.getColumns(null, null, tableName, null));

			allFileContent.putAll(payloadClass);
			
			Map<String, String> serviceclass = serviceClassGenerator.generateServiceClass(tableName);
			allFileContent.putAll(serviceclass);

			return allFileContent;
		} finally {
			if (columns != null) {
				columns.close();
			}
			if (con != null) {
				con.close();
			}
		}
	}
}
