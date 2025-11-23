package com.metromanage.ui;
import javax.swing.*;
import java.awt.*;

public class SignUpPage extends JFrame {
    public SignUpPage() {
        setTitle("Sign Up - MetroManage");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(450, 380);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel lbl = new JLabel("Sign Up (stub)");
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 18f));
        p.add(lbl, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.add(new JLabel("Name:"));
        form.add(new JTextField());
        form.add(new JLabel("Email:"));
        form.add(new JTextField());
        form.add(new JLabel("Password:"));
        form.add(new JPasswordField());
        form.add(new JLabel("Confirm:"));
        form.add(new JPasswordField());
        p.add(form, BorderLayout.CENTER);

        JButton signup = new JButton("Create Account");
        signup.addActionListener(e -> JOptionPane.showMessageDialog(this, "Sign up (not implemented)"));
        p.add(signup, BorderLayout.SOUTH);

        setContentPane(p);
    }
}