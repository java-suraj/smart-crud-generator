package com.smartjava.app.form;

import lombok.Data;

@Data
public class CodeForm {
	private DatabaseType databaseType;
	private String dbProperties;
	private String dbFromDataSource;
	private String tableName;
	private String sequenceName;
}
