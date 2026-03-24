package com.kaatyassss.planner.web.db;

import com.kaatyassss.planner.web.util.EnvLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DbConnection {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        String host = EnvLoader.get("DB_HOST", "localhost");
        String port = EnvLoader.get("DB_PORT", "5432");
        String name = EnvLoader.get("DB_NAME", "planner_db");
        URL = "jdbc:postgresql://" + host + ":" + port + "/" + name;
        USER = EnvLoader.get("DB_USER", "postgres");
        PASSWORD = EnvLoader.get("DB_PASSWORD", "");
    }

    private DbConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
