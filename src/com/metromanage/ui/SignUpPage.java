package com.metromanage.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import com.metromanage.domain.AdminRegister;
import com.metromanage.domain.Passenger;

/**
 * Modern Sign Up page for MetroManage application
 */
public class SignUpPage extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(86, 124, 141);
    private static final Color SECONDARY_COLOR = new Color(203, 214, 230);
    private static final Color ACCENT_COLOR = new Color(245, 239, 232);
    private static final Color TEXT_PRIMARY = new Color(47, 65, 86);
    private static final Color TEXT_SECONDARY = new Color(120, 120, 120);
    private static final String FONT_FAMILY = "Inter";

    private JTextField txtName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnSignUp;
    private JLabel lblLoginLink;

    public SignUpPage() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Sign Up - MetroManage");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 850);
        setLocationRelativeTo(null);
        setResizable(true);

        // Create gradient background panel
        JPanel rootPanel = new GradientPanel();
        rootPanel.setLayout(new GridBagLayout());
        setContentPane(rootPanel);

        // Create centered sign-up card
        JPanel signUpCard = createSignUpCard();
        rootPanel.add(signUpCard);

        setVisible(true);
    }

    private JPanel createSignUpCard() {
        RoundedPanel card = new RoundedPanel(25);
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(480, 750));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(30, 50, 30, 50));

        // Card content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Logo
        JPanel logoPanel = createLogoPanel();
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(logoPanel);
        content.add(Box.createVerticalStrut(15));

        // Title
        JLabel lblTitle = new JLabel("Create Account");
        lblTitle.setFont(getCustomFont(Font.BOLD, 28));
        lblTitle.setForeground(TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(lblTitle);

        content.add(Box.createVerticalStrut(5));

        // Subtitle
        JLabel lblSubtitle = new JLabel("Join MetroManage and start your journey");
        lblSubtitle.setFont(getCustomFont(Font.PLAIN, 13));
        lblSubtitle.setForeground(TEXT_SECONDARY);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(lblSubtitle);

        content.add(Box.createVerticalStrut(20));

        // Name field
        content.add(createInputField("Full Name", false));
        content.add(Box.createVerticalStrut(12));

        // Email field
        content.add(createInputField("Email Address", false));
        content.add(Box.createVerticalStrut(12));

        // Phone field
        content.add(createInputField("Phone Number", false));
        content.add(Box.createVerticalStrut(12));

        // Password field
        content.add(createInputField("Password", true));
        content.add(Box.createVerticalStrut(12));

        // Confirm Password field
        content.add(createInputField("Confirm Password", true));
        content.add(Box.createVerticalStrut(20));

        // Sign Up button
        btnSignUp = createSignUpButton();
        btnSignUp.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(btnSignUp);

        content.add(Box.createVerticalStrut(15));

        // Login link
        JPanel loginPanel = createLoginLinkPanel();
        loginPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(loginPanel);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));

        // Try to load logo image
        JLabel logoLabel;
        try {
            Image logoImage = ImageIO.read(new File("src/Resources/logo.png"));
            Image scaledLogo = logoImage.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            logoLabel = new JLabel(new ImageIcon(scaledLogo));
        } catch (IOException e) {
            // Fallback if logo.png is not found - create a circular logo with "M"
            logoLabel = new JLabel("M") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Draw circle background
                    g2.setColor(PRIMARY_COLOR);
                    g2.fillOval(10, 10, 60, 60);
                    
                    // Draw "M" text
                    super.paintComponent(g);
                }
            };
            logoLabel.setFont(getCustomFont(Font.BOLD, 36));
            logoLabel.setForeground(Color.WHITE);
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            logoLabel.setPreferredSize(new Dimension(80, 80));
        }
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(logoLabel);

        return logoPanel;
    }

    private JPanel createInputField(String placeholder, boolean isPassword) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BorderLayout());
        fieldPanel.setOpaque(false);
        fieldPanel.setMaximumSize(new Dimension(380, 65));

        // Label
        JLabel label = new JLabel(placeholder);
        label.setFont(getCustomFont(Font.BOLD, 13));
        label.setForeground(TEXT_PRIMARY);
        fieldPanel.add(label, BorderLayout.NORTH);

        // Input field
        JComponent inputField;
        if (isPassword) {
            inputField = new JPasswordField();
            if (placeholder.equals("Password")) {
                txtPassword = (JPasswordField) inputField;
            } else {
                txtConfirmPassword = (JPasswordField) inputField;
                // Add Enter key listener to trigger sign up
                txtConfirmPassword.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            handleSignUp();
                        }
                    }
                });
            }
        } else {
            inputField = new JTextField();
            if (placeholder.equals("Full Name")) {
                txtName = (JTextField) inputField;
            } else if (placeholder.equals("Email Address")) {
                txtEmail = (JTextField) inputField;
            } else {
                txtPhone = (JTextField) inputField;
            }
        }

        inputField.setFont(getCustomFont(Font.PLAIN, 14));
        inputField.setPreferredSize(new Dimension(380, 40));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        // Add focus effects
        inputField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                inputField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                inputField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(5, 0, 0, 0));
        wrapper.add(inputField, BorderLayout.CENTER);

        fieldPanel.add(wrapper, BorderLayout.CENTER);
        return fieldPanel;
    }

    private JButton createSignUpButton() {
        JButton button = new JButton("Create Account") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(PRIMARY_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(PRIMARY_COLOR.brighter());
                } else {
                    g2.setColor(PRIMARY_COLOR);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Draw text
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
        };
        button.setFont(getCustomFont(Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setPreferredSize(new Dimension(380, 50));
        button.setMaximumSize(new Dimension(380, 50));
        button.setMinimumSize(new Dimension(380, 50));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add action listener
        button.addActionListener(e -> handleSignUp());

        return button;
    }

    private JPanel createLoginLinkPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setOpaque(false);

        JLabel lblText = new JLabel("Already have an account?");
        lblText.setFont(getCustomFont(Font.PLAIN, 13));
        lblText.setForeground(TEXT_SECONDARY);

        lblLoginLink = new JLabel("Login");
        lblLoginLink.setFont(getCustomFont(Font.BOLD, 13));
        lblLoginLink.setForeground(PRIMARY_COLOR);
        lblLoginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        lblLoginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                lblLoginLink.setForeground(PRIMARY_COLOR.darker());
                lblLoginLink.setText("<html><u>Login</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lblLoginLink.setForeground(PRIMARY_COLOR);
                lblLoginLink.setText("Login");
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                navigateToLogin();
            }
        });

        panel.add(lblText);
        panel.add(lblLoginLink);

        return panel;
    }

    private void handleSignUp() {
        // Validation
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());

        // Basic validation
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please fill in all fields.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Email validation
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter a valid email address.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Phone validation
        if (!phone.matches("^\\d{10,15}$")) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter a valid phone number (10-15 digits).",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Password length validation
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(
                this,
                "Password must be at least 6 characters long.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Password match validation
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(
                this,
                "Passwords do not match.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Use AdminRegister to create the passenger
        AdminRegister adminRegister = new AdminRegister();
        Passenger newPassenger = adminRegister.addPassenger(name, email, phone, password, 0.0f);
        
        if (newPassenger == null) {
            // Email already exists
            JOptionPane.showMessageDialog(
                this,
                "An account with this email already exists.\nPlease use a different email or login.",
                "Registration Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Show success message
        JOptionPane.showMessageDialog(
            this,
            "Account created successfully!\n\nName: " + name + "\nEmail: " + email + "\n\nYou can now login with your credentials.",
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );

        // Navigate to login page
        navigateToLogin();
    }

    private void navigateToLogin() {
        new LoginPage();
        dispose();
    }

    private Font getCustomFont(int style, float size) {
        try {
            return new Font(FONT_FAMILY, style, (int) size);
        } catch (Exception e) {
            return new Font("SansSerif", style, (int) size);
        }
    }

    // ==================== HELPER CLASSES ====================

    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w = getWidth(), h = getHeight();
            Color color1 = new Color(203, 214, 230);
            Color color2 = new Color(245, 239, 232);
            GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
        }
    }

    static class RoundedPanel extends JPanel {
        private int radius;

        RoundedPanel(int radius) {
            super();
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw shadow
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, radius, radius);
            
            // Draw card
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, radius, radius);
            g2.dispose();
            
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignUpPage());
    }
}