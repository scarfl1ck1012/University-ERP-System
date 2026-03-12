package edu.univ.erp.data;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConnector {

    private static Properties p = new Properties();

    static {
        try {
            InputStream in = DatabaseConnector.class.getClassLoader().getResourceAsStream("config.properties");
            if (in != null) {
                p.load(in);
            } else {
                System.out.println("config file missing");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getAuthConnection() throws java.sql.SQLException {
        String url = p.getProperty("auth.db.url");
        String u = p.getProperty("auth.db.user");
        String pass = p.getProperty("auth.db.pass");
        if (url == null) {
            throw new java.sql.SQLException("Auth DB URL missing in config");
        }
        return DriverManager.getConnection(url, u, pass);
    }

    public static Connection getErpConnection() throws java.sql.SQLException {
        String url = p.getProperty("erp.db.url");
        String u = p.getProperty("erp.db.user");
        String pass = p.getProperty("erp.db.pass");
        if (url == null) {
            throw new java.sql.SQLException("ERP DB URL missing in config");
        }
        return DriverManager.getConnection(url, u, pass);
    }
}