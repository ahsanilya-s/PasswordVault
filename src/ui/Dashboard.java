package ui;

import db.DatabaseHelper;
import security.AESUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Dashboard extends JFrame {
    private DefaultTableModel tableModel;

    public Dashboard() {
        setTitle("Password Vault - Dashboard");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Top panel for form and search
        JPanel topPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        // Form fields
        JTextField siteField = new JTextField();
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JTextField categoryField = new JTextField();
        JButton saveBtn = new JButton("Save Credential");

        // Search field
        JTextField searchField = new JTextField();

        // Add form components
        topPanel.add(new JLabel("Site:"));
        topPanel.add(siteField);
        topPanel.add(new JLabel("Username:"));
        topPanel.add(userField);
        topPanel.add(new JLabel("Password:"));
        topPanel.add(passField);
        topPanel.add(new JLabel("Category:"));
        topPanel.add(categoryField);
        topPanel.add(saveBtn);

        // Add search label and field
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.add(new JLabel("Search (site/category):"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Table for credentials
        String[] columns = {"Site", "Username", "Password", "Category"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Add panels to frame
        add(topPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // Save button action
        saveBtn.addActionListener(e -> {
            String site = siteField.getText().trim();
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());
            String category = categoryField.getText().trim();

            if (site.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Site, Username, and Password are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String encryptedPass = AESUtil.encrypt(pass);

            try (Connection conn = DatabaseHelper.connect()) {
                String sql = "INSERT INTO passwords (site, username, password, category) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, site);
                stmt.setString(2, user);
                stmt.setString(3, encryptedPass);
                stmt.setString(4, category);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Credential saved successfully!");
                siteField.setText("");
                userField.setText("");
                passField.setText("");
                categoryField.setText("");

                loadCredentials(""); // reload table
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving credential: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Search field action
        searchField.addActionListener(e -> {
            String keyword = searchField.getText().trim().toLowerCase();
            loadCredentials(keyword);
        });

        // Load all credentials on start
        loadCredentials("");

        setVisible(true);
    }

    private void loadCredentials(String keyword) {
        try (Connection conn = DatabaseHelper.connect()) {
            String sql = "SELECT * FROM passwords";
            if (!keyword.isEmpty()) {
                sql += " WHERE LOWER(site) LIKE ? OR LOWER(category) LIKE ?";
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            if (!keyword.isEmpty()) {
                String likeKeyword = "%" + keyword + "%";
                stmt.setString(1, likeKeyword);
                stmt.setString(2, likeKeyword);
            }
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0); // clear table

            while (rs.next()) {
                String site = rs.getString("site");
                String user = rs.getString("username");
                String encrypted = rs.getString("password");
                String category = rs.getString("category");

                String decrypted = AESUtil.decrypt(encrypted);

                tableModel.addRow(new Object[]{site, user, decrypted, category});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading credentials: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
