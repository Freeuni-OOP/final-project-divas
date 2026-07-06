package org.example.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/quizdb?useSSL=false&serverTimezone=UTC";
    private static final String USER = "quizuser";
    private static final String PASSWORD = "quizpass";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
