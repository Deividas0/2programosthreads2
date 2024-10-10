package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Constants {
    public final static String URL = "jdbc:mysql://localhost:3306/javadarbas";
    public final static String USERNAME = "root";
    public final static String PASSWORD = "l3g10n4s";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
