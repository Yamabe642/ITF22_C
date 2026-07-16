package kkk;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL =
        "jdbc:mysql://localhost:3306/karaoke?useSSL=false&serverTimezone=Asia/Tokyo";

    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {

        try {
            Connection conn = DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

            System.out.println("DB接続成功");
            return conn;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}