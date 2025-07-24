package security;

import db.DatabaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class MasterPasswordManager {
    private static Scanner scanner = new Scanner(System.in);

    public static boolean isMasterSet() {
        String sql = "SELECT hash FROM master";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next(); // returns true if a row exists
        } catch (Exception e) {
            return false;
        }
    }

    public static void createMasterPassword() {
        System.out.print("🔐 Set master password: ");
        String input = scanner.nextLine();
        String hash = PasswordSecurity.hash(input);

        String sql = "INSERT INTO master(id, hash) VALUES(1, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hash);
            stmt.executeUpdate();
            System.out.println("✅ Master password saved.");
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static boolean verifyMasterPassword() {
        System.out.print("🔐 Enter master password: ");
        String input = scanner.nextLine();
        String hashInput = PasswordSecurity.hash(input);

        String sql = "SELECT hash FROM master WHERE id = 1";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String hashStored = rs.getString("hash");
                return hashStored.equals(hashInput);
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        return false;
    }
}
