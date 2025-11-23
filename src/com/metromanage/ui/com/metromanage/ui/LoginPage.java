package com.metromanage.ui;
import javax.swing.*;
import java.awt.*;

public class LoginPage extends JFrame {
    public LoginPage() {
        setTitle("Login - MetroManage");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel lbl = new JLabel("Login Page (stub)");
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 18f));
        p.add(lbl, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(2, 2, 8, 8));
        center.add(new JLabel("Email:"));
        center.add(new JTextField());
        center.add(new JLabel("Password:"));
        center.add(new JPasswordField());
        p.add(center, BorderLayout.CENTER);

        JButton login = new JButton("Login");
        login.addActionListener(e -> JOptionPane.showMessageDialog(this, "Attempt login (not implemented)"));
        p.add(login, BorderLayout.SOUTH);

        setContentPane(p);
    }
}
