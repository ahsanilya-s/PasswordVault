package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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

    public static void createTables() {
        String createMasterTable = """
            CREATE TABLE IF NOT EXISTS master (
                id INTEGER PRIMARY KEY,
                hash TEXT NOT NULL
            )
        """;

        String createPasswordTable = """
            CREATE TABLE IF NOT EXISTS passwords (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                category TEXT,
                site TEXT,
                username TEXT,
                password TEXT
            )
        """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createMasterTable);
            stmt.execute(createPasswordTable);
            System.out.println("✅ Tables created successfully");
        } catch (SQLException e) {
            System.out.println("❌ Error creating tables: " + e.getMessage());
        }
    }
}
