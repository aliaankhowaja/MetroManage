package com.metromanage.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WelcomePage extends JFrame {

    // Paths to images from bin directory
    private static final String BG_IMAGE_PATH = "bin/Resources/background2.jpg";
    private static final String LOGO_IMAGE_PATH = "bin/Resources/logo.png";
   

    public WelcomePage() {
        setTitle("Welcome - MetroManage");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);

        // Root panel with enhanced background image painting
        BackgroundPanel root = new BackgroundPanel(BG_IMAGE_PATH);
        root.setLayout(new BorderLayout());
        setContentPane(root);

        // Top navigation bar with glassmorphism effect
        JPanel topBar = createModernTopBar();
        root.add(topBar, BorderLayout.NORTH);

        // Center overlay panel for welcome text & button with animations
        JPanel centerOverlay = createCenterContent();
        root.add(centerOverlay, BorderLayout.CENTER);

        // Show window (fade-in animation removed to avoid frame decoration issues)
        setVisible(true);
        
        // Trigger content animations after window is visible
        startContentAnimations();
    }

    private JPanel createModernTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setPreferredSize(new Dimension(0, 90));
        topBar.setBorder(BorderFactory.createEmptyBorder(20, 32, 12, 32));

        // Logo with refined sizing
        ImageIcon rawLogo = new ImageIcon(LOGO_IMAGE_PATH);
        Image scaledLogo = rawLogo.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        ImageIcon logoIcon = new ImageIcon(scaledLogo);

        JLabel logoLabel;
        if (logoIcon.getIconWidth() > 0) {
            logoLabel = new JLabel(logoIcon);
        } else {
            System.err.println("⚠ Could not load logo at: " + LOGO_IMAGE_PATH);
            logoLabel = new JLabel("MetroManage");
            try {
                logoLabel.setFont(new Font("Inter", Font.BOLD, 24));
            } catch (Exception e) {
                try {
                    logoLabel.setFont(new Font("SF Pro Display", Font.BOLD, 24));
                } catch (Exception ex) {
                    logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
                }
            }
            logoLabel.setForeground(Color.WHITE);
        }

        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        topBar.add(logoLabel, BorderLayout.WEST);

        // Enhanced navigation with better spacing
        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new FlowLayout(FlowLayout.RIGHT, 28, 8));

        String[] items = {"Login", "Services", "Contact"};
        for (String item : items) {
            JLabel navItem = createNavItem(item);
            nav.add(navItem);
        }

        topBar.add(nav, BorderLayout.EAST);
        return topBar;
    }

    private JLabel createNavItem(String text) {
        JLabel navItem = new JLabel(text);
        navItem.setForeground(new Color(220, 230, 255, 230));
        try {
            navItem.setFont(new Font("Inter", Font.PLAIN, 15));
        } catch (Exception e) {
            try {
                navItem.setFont(new Font("SF Pro Text", Font.PLAIN, 15));
            } catch (Exception ex) {
                navItem.setFont(new Font("Arial", Font.PLAIN, 15));
            }
        }
        navItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        navItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                navItem.setForeground(Color.WHITE);
                try {
                    navItem.setFont(new Font("Inter", Font.BOLD, 15));
                } catch (Exception ex) {
                    try {
                        navItem.setFont(new Font("SF Pro Text", Font.BOLD, 15));
                    } catch (Exception ex2) {
                        navItem.setFont(new Font("Arial", Font.BOLD, 15));
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                navItem.setForeground(new Color(220, 230, 255, 230));
                try {
                    navItem.setFont(new Font("Inter", Font.PLAIN, 15));
                } catch (Exception ex) {
                    try {
                        navItem.setFont(new Font("SF Pro Text", Font.PLAIN, 15));
                    } catch (Exception ex2) {
                        navItem.setFont(new Font("Arial", Font.PLAIN, 15));
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                handleNavClick(text);
            }
        });

        return navItem;
    }

    private void handleNavClick(String item) {
        switch (item) {
            case "Login":
                openLogin();
                break;
            case "Services":
                JOptionPane.showMessageDialog(WelcomePage.this, "Services - Coming Soon!");
                break;
            case "Contact":
                JOptionPane.showMessageDialog(WelcomePage.this, "Contact - info@metromanage.com");
                break;
        }
    }

    private JPanel createCenterContent() {
        JPanel centerOverlay = new JPanel();
        centerOverlay.setOpaque(false);
        centerOverlay.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 60, 10, 60);

        // Animated welcome text with improved typography
        JLabel main = new JLabel("Welcome");
        try {
            main.setFont(new Font("Inter", Font.BOLD, 88));
        } catch (Exception e) {
            try {
                main.setFont(new Font("SF Pro Display", Font.BOLD, 88));
            } catch (Exception ex) {
                main.setFont(new Font("Arial", Font.BOLD, 88));
            }
        }
        main.setForeground(Color.WHITE);
        gbc.gridy = 0;
        centerOverlay.add(main, gbc);

        // Refined subtitle with letter spacing effect
        JLabel subtitle = new JLabel("<html><span style='letter-spacing: 3px;'>TO METROMANAGE</span></html>");
        try {
            subtitle.setFont(new Font("Inter", Font.PLAIN, 20));
        } catch (Exception e) {
            try {
                subtitle.setFont(new Font("SF Pro Text", Font.PLAIN, 20));
            } catch (Exception ex) {
                subtitle.setFont(new Font("Arial", Font.PLAIN, 20));
            }
        }
        subtitle.setForeground(new Color(200, 220, 255, 200));
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 60, 20, 60);
        centerOverlay.add(subtitle, gbc);

        // Tagline
        JLabel tagline = new JLabel("Your Smart Transit Solution");
        try {
            tagline.setFont(new Font("Inter", Font.ITALIC, 14));
        } catch (Exception e) {
            try {
                tagline.setFont(new Font("SF Pro Text", Font.ITALIC, 14));
            } catch (Exception ex) {
                tagline.setFont(new Font("Arial", Font.ITALIC, 14));
            }
        }
        tagline.setForeground(new Color(180, 200, 235, 180));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 60, 30, 60);
        centerOverlay.add(tagline, gbc);

        // Modern CTA button with hover animation
        JButton ctaButton = createModernButton("Get Started");
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 60, 10, 60);
        centerOverlay.add(ctaButton, gbc);

        // Add subtle text shadow using glass pane
        getRootPane().setGlassPane(new EnhancedTextShadowGlassPane(main));
        getRootPane().getGlassPane().setVisible(true);

        return centerOverlay;
    }

    private JButton createModernButton(String text) {
        JButton btn = new JButton(text);
        try {
            btn.setFont(new Font("Inter", Font.BOLD, 16));
        } catch (Exception e) {
            try {
                btn.setFont(new Font("SF Pro Text", Font.BOLD, 16));
            } catch (Exception ex) {
                btn.setFont(new Font("Arial", Font.BOLD, 16));
            }
        }
        btn.setFocusPainted(false);
        btn.setBackground(new Color(255, 255, 255, 25));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 230, 255, 180), 2, true),
            BorderFactory.createEmptyBorder(14, 40, 14, 40)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 54));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        
        btn.addActionListener(e -> openDashboard());

        // Smooth hover animation
        btn.addMouseListener(new MouseAdapter() {
            private javax.swing.Timer hoverTimer;
            
            @Override
            public void mouseEntered(MouseEvent e) {
                animateButtonHover(btn, true);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                animateButtonHover(btn, false);
            }
            
            private void animateButtonHover(JButton button, boolean enter) {
                if (hoverTimer != null && hoverTimer.isRunning()) hoverTimer.stop();
                
                final int[] alpha = {enter ? 25 : 80};
                final int targetAlpha = enter ? 80 : 25;
                
                hoverTimer = new javax.swing.Timer(20, null);
                hoverTimer.addActionListener(event -> {
                    if (enter) {
                        alpha[0] = Math.min(alpha[0] + 5, targetAlpha);
                    } else {
                        alpha[0] = Math.max(alpha[0] - 5, targetAlpha);
                    }
                    
                    button.setBackground(new Color(255, 255, 255, alpha[0]));
                    button.repaint();
                    
                    if (alpha[0] == targetAlpha) {
                        hoverTimer.stop();
                    }
                });
                hoverTimer.start();
            }
        });

        return btn;
    }

    // Content animation (replaces window opacity animation)
    private void startContentAnimations() {
        // Components are already visible, just ensure proper rendering
        revalidate();
        repaint();
    }

    // === Actions: open the other windows ===
    private void openLogin() {
        SwingUtilities.invokeLater(() -> {
            LoginPage login = new LoginPage();
            login.setVisible(true);
        });
    }

    private void openDashboard() {
        openLogin();
    }

  
    // Enhanced background panel with better gradient overlay
    private static class BackgroundPanel extends JPanel {
        private Image bg;

        BackgroundPanel(String imgPath) {
            try {
                ImageIcon icon = new ImageIcon(imgPath);
                bg = icon.getImage();
                if (icon.getIconWidth() <= 0)
                    System.err.println("⚠ Background image NOT FOUND: " + imgPath);
            } catch (Exception ex) {
                System.err.println("❌ Error loading background: " + imgPath);
                bg = null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            
            if (bg != null) {
                int panelW = getWidth(), panelH = getHeight();
                double imgW = bg.getWidth(this), imgH = bg.getHeight(this);

                double scale = Math.max(panelW / imgW, panelH / imgH);
                int drawW = (int) (imgW * scale);
                int drawH = (int) (imgH * scale);
                int x = (panelW - drawW) / 2;
                int y = (panelH - drawH) / 2;

                g2.drawImage(bg, x, y, drawW, drawH, this);
            }

            // Refined gradient overlay for better readability
            GradientPaint gp = new GradientPaint(
                0, 0, new Color(15, 25, 50, 120),
                0, getHeight(), new Color(25, 40, 70, 160)
            );
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    // Enhanced shadow glass pane with subtle glow effect
    private static class EnhancedTextShadowGlassPane extends JComponent {
        private final JLabel target;

        EnhancedTextShadowGlassPane(JLabel target) {
            this.target = target;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (!target.isShowing()) return;

            Point p = SwingUtilities.convertPoint(target.getParent(), target.getLocation(), this);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setFont(target.getFont());
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            FontMetrics fm = g2.getFontMetrics();
            int x = p.x, y = p.y + fm.getAscent();
            String txt = target.getText();

            // Subtle glow effect
            g2.setColor(new Color(100, 150, 255, 40));
            for (int i = 0; i < 3; i++) {
                g2.drawString(txt, x + i, y + i);
            }

            // Main shadow
            g2.setColor(new Color(0, 0, 0, 140));
            g2.drawString(txt, x + 2, y + 3);

            // Text itself
            g2.setColor(target.getForeground());
            g2.drawString(txt, x, y);

            g2.dispose();
        }
    }
}
