package com.metromanage.ui;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * LoginPage - Modern login interface for MetroManage.
 * Features: Gradient background, rounded card design, modern input fields.
 */
public class LoginPage extends JFrame {

    // ==================== COLOR PALETTE ====================
    private static final Color PRIMARY_COLOR = new Color(86, 124, 141);  // Teal accent
    private static final Color SIDEBAR_BACKGROUND = new Color(47, 65, 86);  // Navy
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(47, 65, 86);  // Navy
    private static final Color TEXT_MUTED = new Color(120, 120, 120);  // Gray
    private static final Color SKY = new Color(203, 214, 230);
    private static final Color BEIGE = new Color(245, 239, 232);

    // ==================== FONT ====================
    private static final String FONT_FAMILY = "Inter";

    // ==================== UI COMPONENTS ====================
    private JPanel rootPanel;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblSignUp;
    private JLabel lblForgotPassword;

    public LoginPage() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Login - MetroManage");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        // Root panel with gradient background
        rootPanel = new GradientPanel();
        rootPanel.setLayout(new GridBagLayout());
        setContentPane(rootPanel);

        // Create login card
        JPanel loginCard = createLoginCard();
        rootPanel.add(loginCard);

        setVisible(true);
    }

    // Gradient background panel
    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            GradientPaint gp = new GradientPaint(
                0, 0, new Color(203, 214, 230),
                getWidth(), getHeight(), new Color(245, 239, 232)
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private JPanel createLoginCard() {
        JPanel card = new RoundedPanel(20);
        card.setBackground(CARD_BACKGROUND);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(450, 550));
        card.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Logo/Brand Section
        JPanel logoPanel = createLogoPanel();
        contentPanel.add(logoPanel);
        contentPanel.add(Box.createVerticalStrut(40));

        // Welcome text
        JLabel lblWelcome = new JLabel("Welcome Back!");
        lblWelcome.setFont(getCustomFont(Font.BOLD, 28));
        lblWelcome.setForeground(TEXT_PRIMARY);
        lblWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(lblWelcome);
        contentPanel.add(Box.createVerticalStrut(8));

        JLabel lblSubtext = new JLabel("Please login to continue");
        lblSubtext.setFont(getCustomFont(Font.PLAIN, 14));
        lblSubtext.setForeground(TEXT_MUTED);
        lblSubtext.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(lblSubtext);
        contentPanel.add(Box.createVerticalStrut(35));

        // Email field
        JLabel lblEmail = new JLabel("Email Address");
        lblEmail.setFont(getCustomFont(Font.BOLD, 13));
        lblEmail.setForeground(TEXT_PRIMARY);
        lblEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(lblEmail);
        contentPanel.add(Box.createVerticalStrut(8));

        txtEmail = createStyledTextField("Enter your email");
        contentPanel.add(txtEmail);
        contentPanel.add(Box.createVerticalStrut(20));

        // Password field
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(getCustomFont(Font.BOLD, 13));
        lblPassword.setForeground(TEXT_PRIMARY);
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(lblPassword);
        contentPanel.add(Box.createVerticalStrut(8));

        txtPassword = createStyledPasswordField("Enter your password");
        contentPanel.add(txtPassword);
        contentPanel.add(Box.createVerticalStrut(15));

        // Forgot password link
        lblForgotPassword = new JLabel("Forgot Password?");
        lblForgotPassword.setFont(getCustomFont(Font.PLAIN, 12));
        lblForgotPassword.setForeground(PRIMARY_COLOR);
        lblForgotPassword.setAlignmentX(Component.RIGHT_ALIGNMENT);
        lblForgotPassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblForgotPassword.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(LoginPage.this, 
                    "Password recovery feature coming soon!", 
                    "Forgot Password", 
                    JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                lblForgotPassword.setText("<html><u>Forgot Password?</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lblForgotPassword.setText("Forgot Password?");
            }
        });
        
        JPanel forgotPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        forgotPanel.setOpaque(false);
        forgotPanel.add(lblForgotPassword);
        forgotPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(forgotPanel);
        contentPanel.add(Box.createVerticalStrut(25));

        // Login button
        btnLogin = createStyledButton("LOGIN");
        btnLogin.addActionListener(e -> handleLogin());
        contentPanel.add(btnLogin);
        contentPanel.add(Box.createVerticalStrut(25));

        // Sign up link
        JPanel signUpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        signUpPanel.setOpaque(false);
        signUpPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JLabel lblDontHave = new JLabel("Don't have an account?");
        lblDontHave.setFont(getCustomFont(Font.PLAIN, 13));
        lblDontHave.setForeground(TEXT_MUTED);
        
        lblSignUp = new JLabel("Sign Up");
        lblSignUp.setFont(getCustomFont(Font.BOLD, 13));
        lblSignUp.setForeground(PRIMARY_COLOR);
        lblSignUp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblSignUp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleSignUp();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                lblSignUp.setText("<html><u>Sign Up</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lblSignUp.setText("Sign Up");
            }
        });
        
        signUpPanel.add(lblDontHave);
        signUpPanel.add(lblSignUp);
        contentPanel.add(signUpPanel);

        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setOpaque(false);
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Try to load logo from Resources folder
        try {
            Image logoImage = ImageIO.read(new File("src/Resources/logo.png"));
            Image scaledLogo = logoImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoPanel.add(logoLabel);
            logoPanel.add(Box.createVerticalStrut(15));
        } catch (Exception e) {
            // Fallback: Create circle with M if logo can't be loaded
            JPanel logoCircle = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Outer circle
                    g2d.setColor(PRIMARY_COLOR);
                    g2d.fillOval(0, 0, 80, 80);
                    
                    // Inner design - Metro symbol
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(3));
                    
                    // Draw "M" for Metro
                    g2d.setFont(new Font(FONT_FAMILY, Font.BOLD, 40));
                    FontMetrics fm = g2d.getFontMetrics();
                    String text = "M";
                    int x = (80 - fm.stringWidth(text)) / 2;
                    int y = ((80 - fm.getHeight()) / 2) + fm.getAscent();
                    g2d.drawString(text, x, y);
                }
            };
            logoCircle.setPreferredSize(new Dimension(80, 80));
            logoCircle.setMaximumSize(new Dimension(80, 80));
            logoCircle.setOpaque(false);
            logoCircle.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoPanel.add(logoCircle);
            logoPanel.add(Box.createVerticalStrut(15));
        }

        // App name
        JLabel lblAppName = new JLabel("MetroManage");
        lblAppName.setFont(getCustomFont(Font.BOLD, 24));
        lblAppName.setForeground(TEXT_PRIMARY);
        lblAppName.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(lblAppName);

        return logoPanel;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder);
        textField.setFont(getCustomFont(Font.PLAIN, 14));
        textField.setForeground(TEXT_MUTED);
        textField.setPreferredSize(new Dimension(350, 45));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Placeholder functionality
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(TEXT_PRIMARY);
                }
                textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(TEXT_MUTED);
                }
                textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
        });

        return textField;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField passwordField = new JPasswordField(placeholder);
        passwordField.setFont(getCustomFont(Font.PLAIN, 14));
        passwordField.setForeground(TEXT_MUTED);
        passwordField.setPreferredSize(new Dimension(350, 45));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setEchoChar((char) 0); // Show placeholder

        // Placeholder functionality
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setEchoChar('•');
                    passwordField.setForeground(TEXT_PRIMARY);
                }
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).isEmpty()) {
                    passwordField.setEchoChar((char) 0);
                    passwordField.setText(placeholder);
                    passwordField.setForeground(TEXT_MUTED);
                }
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
        });

        return passwordField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(PRIMARY_COLOR.getRed() - 20, 
                                          PRIMARY_COLOR.getGreen() - 20, 
                                          PRIMARY_COLOR.getBlue() - 20));
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(PRIMARY_COLOR.getRed() + 15, 
                                          PRIMARY_COLOR.getGreen() + 15, 
                                          PRIMARY_COLOR.getBlue() + 15));
                } else {
                    g2d.setColor(PRIMARY_COLOR);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw text
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };
        
        button.setFont(getCustomFont(Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(350, 50));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        return button;
    }

    private void handleLogin() {
        String email = txtEmail.getText();
        String password = String.valueOf(txtPassword.getPassword());
        
        // Placeholder validation
        if (email.equals("Enter your email") || email.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter your email address", 
                "Login Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.equals("Enter your password") || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter your password", 
                "Login Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // For demo purposes - show success message
        JOptionPane.showMessageDialog(this, 
            "Login functionality will be connected to database!", 
            "Login", 
            JOptionPane.INFORMATION_MESSAGE);
        
        // Open dashboard (placeholder)
        new DashBoard();
        dispose();
    }

    private void handleSignUp() {
        JOptionPane.showMessageDialog(this, 
            "Sign up page coming soon!", 
            "Sign Up", 
            JOptionPane.INFORMATION_MESSAGE);
        
        // Open sign up page (placeholder)
        // new SignUpPage();
        // dispose();
    }

    // ==================== HELPER METHODS ====================

    private Font getCustomFont(int style, float size) {
        try {
            return new Font(FONT_FAMILY, style, (int)size);
        } catch (Exception e) {
            return new Font("Arial", style, (int)size);
        }
    }

    // ==================== INNER CLASSES ====================

    /**
     * Rounded panel with custom painting
     */
    private static class RoundedPanel extends JPanel {
        private int cornerRadius;

        public RoundedPanel(int cornerRadius) {
            super();
            this.cornerRadius = cornerRadius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw shadow
            g2d.setColor(new Color(0, 0, 0, 30));
            g2d.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, cornerRadius, cornerRadius);
            
            // Draw background
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, cornerRadius, cornerRadius);
        }
    }

    // ==================== MAIN METHOD ====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage());
    }
}
