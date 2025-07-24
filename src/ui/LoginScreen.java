package ui;

import security.MasterPasswordManager;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {
    public LoginScreen() {
        setTitle("Password Vault - Login");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Enter Master Password:");
        JPasswordField passField = new JPasswordField();
        JButton loginBtn = new JButton("Login");

        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.add(label);
        panel.add(passField);
        panel.add(loginBtn);

        add(panel, BorderLayout.CENTER);

        loginBtn.addActionListener(e -> {
            String input = new String(passField.getPassword());
            if (MasterPasswordManager.verifyMasterPassword(input)) {
                dispose(); // close login
                new Dashboard(); // open dashboard
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }
}
