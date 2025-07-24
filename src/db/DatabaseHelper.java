package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:vault.db";

    public static Connection connect() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            System.out.println("✅ Connected to database");
            return conn;
        } catch (SQLException e) {
            System.out.println("❌ DB connection failed: " + e.getMessage());
            return null;
        }
    }
}

