package com.smartjava.app.form;

public enum DatabaseType {
    ORACLE("Oracle"), 
    MYSQL("MySQL");

    private String displayName;

    DatabaseType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
