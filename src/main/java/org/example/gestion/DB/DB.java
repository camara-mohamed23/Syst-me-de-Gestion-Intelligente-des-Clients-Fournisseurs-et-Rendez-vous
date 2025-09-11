package org.example.gestion.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    private static final String URL = "jdbc:mysql://localhost:3307/Gadior?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // à adapter
    private static final String PASS = ""; // à adapter

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
