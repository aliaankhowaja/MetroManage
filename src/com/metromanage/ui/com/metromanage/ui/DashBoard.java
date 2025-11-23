package com.metromanage.ui;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Enhanced Dashboard UI with modern aesthetics, smooth animations, and refined typography.
 * Uses the existing color palette with improved visual hierarchy and user experience.
 */
public class DashBoard extends JFrame {

    // Palette (approximate values extracted from the uploaded palette image)
    private static final Color NAVY = new Color(47, 65, 86);      // deep navy
    private static final Color TEAL = new Color(86, 124, 141);    // teal
    private static final Color SKY = new Color(203, 214, 230);    // sky blue / cards
    private static final Color BEIGE = new Color(245, 239, 232);  // light beige
    private static final Color OFFWHITE = Color.WHITE;

    public DashBoard() {
        setTitle("Dashboard - MetroManage");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);

        // root layout with subtle gradient background
        JPanel root = new GradientPanel();
        root.setLayout(new BorderLayout());
        setContentPane(root);

        // Modern top bar with elevated design
        JPanel topBar = createModernTopBar();
        root.add(topBar, BorderLayout.NORTH);

        // Elegant left sidebar
        JPanel leftSidebar = createModernSidebar();
        root.add(leftSidebar, BorderLayout.WEST);

        // Center panel with refined card layout
        JPanel center = createCenterPanel();
        root.add(center, BorderLayout.CENTER);

        // Minimal footer
        JPanel footer = createFooter();
        root.add(footer, BorderLayout.SOUTH);

        // Show window (fade-in removed due to frame decoration constraints)
        setVisible(true);
    }

    // Gradient background panel for subtle depth
    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            GradientPaint gp = new GradientPaint(
                0, 0, OFFWHITE,
                0, getHeight(), BEIGE
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private JPanel createModernTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(20, 28, 20, 28));

        // Title with modern typography
        JLabel title = new JLabel("MetroManage");
        try {
            Font modernFont = new Font("Inter", Font.BOLD, 28);
            title.setFont(modernFont);
        } catch (Exception e) {
            // Fallback to SF Pro or system font
            try {
                title.setFont(new Font("SF Pro Display", Font.BOLD, 28));
            } catch (Exception ex) {
                title.setFont(new Font("Arial", Font.BOLD, 28));
            }
        }
        title.setForeground(NAVY);
        topBar.add(title, BorderLayout.WEST);

        // Subtitle/tagline
        JLabel tagline = new JLabel("Your Smart Transit Dashboard");
        try {
            Font lightFont = new Font("Inter", Font.PLAIN, 13);
            tagline.setFont(lightFont);
        } catch (Exception e) {
            try {
                tagline.setFont(new Font("SF Pro Text", Font.PLAIN, 13));
            } catch (Exception ex) {
                tagline.setFont(new Font("Arial", Font.PLAIN, 13));
            }
        }
        tagline.setForeground(TEAL);
        topBar.add(tagline, BorderLayout.CENTER);
        tagline.setBorder(new EmptyBorder(8, 15, 0, 0));

        return topBar;
    }

    private JPanel createModernSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(NAVY);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(new EmptyBorder(24, 12, 24, 12));

        // Sidebar title
        JLabel sidebarTitle = new JLabel("QUICK ACCESS");
        sidebarTitle.setForeground(SKY);
        sidebarTitle.setFont(sidebarTitle.getFont().deriveFont(Font.BOLD, 11f));
        sidebarTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarTitle.setBorder(new EmptyBorder(0, 12, 16, 0));
        sidebar.add(sidebarTitle);

        // Sidebar buttons with visual indicators
        sidebar.add(makeModernSidebarButton("Manage Fleet", () -> openManageFleet()));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(makeModernSidebarButton("Manage Users", () -> openManageUsers()));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(makeModernSidebarButton("Allocate Buses", () -> openAllocateBuses()));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(makeModernSidebarButton("Purchase Ticket", () -> openPurchaseTicket()));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(makeModernSidebarButton("Submit Feedback", () -> openSubmitFeedback()));

        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private JPanel createCenterPanel() {
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(12, 24, 24, 24));

        // Section header
        JLabel header = new JLabel("Analytics & Reports");
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));
        header.setForeground(NAVY);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));
        center.add(header, BorderLayout.NORTH);

        // Action cards with better spacing and elevation
        JPanel cardArea = new JPanel(new GridLayout(2, 2, 20, 20));
        cardArea.setOpaque(false);

        cardArea.add(makeEnhancedActionCard("Boarding Totals", "Real-time passenger analytics", TEAL, () -> openViewBoardingTotals()));
        cardArea.add(makeEnhancedActionCard("Check Balance", "View account balance", SKY, () -> openCheckBalance()));
        cardArea.add(makeEnhancedActionCard("Peak Hours", "Traffic analysis dashboard", SKY, () -> openViewPeakHours()));
        cardArea.add(makeEnhancedActionCard("View Schedule", "Bus schedules & routes", TEAL, () -> openViewSchedule()));

        center.add(cardArea, BorderLayout.CENTER);

        // Refined quick links section
        JPanel quickLinksSection = new JPanel(new BorderLayout());
        quickLinksSection.setOpaque(false);
        quickLinksSection.setBorder(new EmptyBorder(24, 0, 0, 0));

        JLabel quickLinksLabel = new JLabel("QUICK LINKS");
        quickLinksLabel.setFont(quickLinksLabel.getFont().deriveFont(Font.BOLD, 11f));
        quickLinksLabel.setForeground(TEAL);
        quickLinksSection.add(quickLinksLabel, BorderLayout.NORTH);

        JPanel quickLinks = new JPanel(new GridLayout(1, 4, 12, 0));
        quickLinks.setOpaque(false);
        quickLinks.setBorder(new EmptyBorder(12, 0, 0, 0));

        quickLinks.add(makeModernQuickLink("Announcements"));
        quickLinks.add(makeModernQuickLink("Reports"));
        quickLinks.add(makeModernQuickLink("Settings"));
        quickLinks.add(makeModernQuickLink("Help"));

        quickLinksSection.add(quickLinks, BorderLayout.CENTER);
        center.add(quickLinksSection, BorderLayout.SOUTH);

        return center;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(8, 16, 12, 16));
        
        JLabel ver = new JLabel("Version 1.0 • © 2025 MetroManage");
        ver.setForeground(TEAL);
        ver.setFont(ver.getFont().deriveFont(Font.PLAIN, 11f));
        footer.add(ver);
        
        return footer;
    }

    // Modern sidebar button with smooth hover animations
    private JButton makeModernSidebarButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        try {
            btn.setFont(new Font("Inter", Font.PLAIN, 13));
        } catch (Exception e) {
            try {
                btn.setFont(new Font("SF Pro Text", Font.PLAIN, 13));
            } catch (Exception ex) {
                btn.setFont(new Font("Arial", Font.PLAIN, 13));
            }
        }
        btn.setForeground(SKY);
        btn.setBackground(NAVY);
        btn.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        btn.addActionListener(e -> action.run());

        // Smooth hover animation with rounded corners
        btn.addMouseListener(new MouseAdapter() {
            private javax.swing.Timer hoverTimer;
            private float[] currentColor = {NAVY.getRed(), NAVY.getGreen(), NAVY.getBlue()};
            private float[] targetColor = {TEAL.getRed(), TEAL.getGreen(), TEAL.getBlue()};
            private boolean isHovered = false;
            
            @Override
            public void mouseEntered(MouseEvent evt) {
                isHovered = true;
                if (hoverTimer != null && hoverTimer.isRunning()) hoverTimer.stop();
                targetColor = new float[]{TEAL.getRed(), TEAL.getGreen(), TEAL.getBlue()};
                animateColorTransition(btn, currentColor, targetColor);
            }
            
            @Override
            public void mouseExited(MouseEvent evt) {
                isHovered = false;
                if (hoverTimer != null && hoverTimer.isRunning()) hoverTimer.stop();
                targetColor = new float[]{NAVY.getRed(), NAVY.getGreen(), NAVY.getBlue()};
                animateColorTransition(btn, currentColor, targetColor);
            }
            
            private void animateColorTransition(JButton button, float[] from, float[] to) {
                hoverTimer = new javax.swing.Timer(10, null);
                final int[] step = {0};
                final int maxSteps = 15;
                
                hoverTimer.addActionListener(e -> {
                    step[0]++;
                    float progress = (float) step[0] / maxSteps;
                    
                    currentColor[0] = from[0] + (to[0] - from[0]) * progress;
                    currentColor[1] = from[1] + (to[1] - from[1]) * progress;
                    currentColor[2] = from[2] + (to[2] - from[2]) * progress;
                    
                    button.setBackground(new Color((int)currentColor[0], (int)currentColor[1], (int)currentColor[2]));
                    
                    if (step[0] >= maxSteps) {
                        hoverTimer.stop();
                    }
                });
                hoverTimer.start();
            }
        });

        // Custom painting for rounded corners on hover
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                JButton button = (JButton) c;
                boolean isHovered = button.getModel().isRollover();
                
                if (isHovered) {
                    g2d.setColor(button.getBackground());
                    g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 12, 12);
                } else {
                    g2d.setColor(button.getBackground());
                    g2d.fillRect(0, 0, c.getWidth(), c.getHeight());
                }
                
                g2d.dispose();
                super.paint(g, c);
            }
        });

        return btn;
    }

    // Enhanced action card with elevation effect and smooth animations
    private JPanel makeEnhancedActionCard(String title, String subtitle, Color bg, Runnable onClick) {
        JPanel card = new RoundedPanel(16, bg);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Title with accent bar and better typography
        JPanel titlePanel = new JPanel(new BorderLayout(8, 0));
        titlePanel.setOpaque(false);
        
        // Colored accent bar
        JPanel accentBar = new JPanel();
        accentBar.setBackground(NAVY);
        accentBar.setPreferredSize(new Dimension(4, 24));
        titlePanel.add(accentBar, BorderLayout.WEST);
        
        JLabel t = new JLabel(title);
        try {
            t.setFont(new Font("Inter", Font.BOLD, 20));
        } catch (Exception e) {
            try {
                t.setFont(new Font("SF Pro Display", Font.BOLD, 20));
            } catch (Exception ex) {
                t.setFont(new Font("Arial", Font.BOLD, 20));
            }
        }
        t.setForeground(NAVY);
        titlePanel.add(t, BorderLayout.CENTER);
        card.add(titlePanel, BorderLayout.NORTH);

        // Subtitle with refined style
        JLabel sub = new JLabel("<html>" + subtitle + "</html>");
        try {
            sub.setFont(new Font("Inter", Font.PLAIN, 13));
        } catch (Exception e) {
            try {
                sub.setFont(new Font("SF Pro Text", Font.PLAIN, 13));
            } catch (Exception ex) {
                sub.setFont(new Font("Arial", Font.PLAIN, 13));
            }
        }
        sub.setForeground(new Color(70, 80, 100));
        card.add(sub, BorderLayout.CENTER);

        // Action button with modern design
        JButton action = new JButton("View →");
        try {
            action.setFont(new Font("Inter", Font.BOLD, 13));
        } catch (Exception e) {
            try {
                action.setFont(new Font("SF Pro Text", Font.BOLD, 13));
            } catch (Exception ex) {
                action.setFont(new Font("Arial", Font.BOLD, 13));
            }
        }
        action.setBackground(NAVY);
        action.setForeground(Color.WHITE);
        action.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        action.setFocusPainted(false);
        action.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        action.addActionListener(e -> onClick.run());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottom.setOpaque(false);
        bottom.add(action);
        card.add(bottom, BorderLayout.SOUTH);

        // Hover animation with elevation effect
        card.addMouseListener(new MouseAdapter() {
            private javax.swing.Timer elevationTimer;
            private int[] currentElevation = {0};
            
            @Override
            public void mouseClicked(MouseEvent evt) {
                onClick.run();
            }
            
            @Override
            public void mouseEntered(MouseEvent evt) {
                animateElevation(card, 0, 8);
            }
            
            @Override
            public void mouseExited(MouseEvent evt) {
                animateElevation(card, 8, 0);
            }
            
            private void animateElevation(JPanel panel, int from, int to) {
                if (elevationTimer != null && elevationTimer.isRunning()) elevationTimer.stop();
                
                elevationTimer = new javax.swing.Timer(15, null);
                final int[] step = {0};
                final int maxSteps = 10;
                
                elevationTimer.addActionListener(e -> {
                    step[0]++;
                    float progress = (float) step[0] / maxSteps;
                    currentElevation[0] = (int)(from + (to - from) * progress);
                    
                    int padding = 24 - currentElevation[0] / 2;
                    panel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
                    panel.repaint();
                    
                    if (step[0] >= maxSteps) {
                        elevationTimer.stop();
                    }
                });
                elevationTimer.start();
            }
        });

        return card;
    }

    // Rounded panel for modern card design
    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color backgroundColor;

        RoundedPanel(int radius, Color bg) {
            super();
            this.radius = radius;
            this.backgroundColor = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Modern quick link button
    private JButton makeModernQuickLink(String label) {
        JButton tile = new JButton(label);
        try {
            tile.setFont(new Font("Inter", Font.PLAIN, 12));
        } catch (Exception e) {
            try {
                tile.setFont(new Font("SF Pro Text", Font.PLAIN, 12));
            } catch (Exception ex) {
                tile.setFont(new Font("Arial", Font.PLAIN, 12));
            }
        }
        tile.setBackground(new Color(255, 255, 255, 200));
        tile.setForeground(NAVY);
        tile.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SKY, 1, true),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        tile.setFocusPainted(false);
        tile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tile.addActionListener(e -> JOptionPane.showMessageDialog(this, label + " clicked"));
        
        // Hover effect
        tile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                tile.setBackground(SKY);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                tile.setBackground(new Color(255, 255, 255, 200));
            }
        });
        
        return tile;
    }

    // Helper: create a styled sidebar button
    private JButton makeSidebarButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 13f));
        btn.setForeground(Color.WHITE);
        btn.setBackground(NAVY);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> action.run());

        // hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(TEAL.darker()); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt)  { btn.setBackground(NAVY); }
        });

        return btn;
    }



    // Helper: create a large action card (JButton styled)
    private JPanel makeActionCard(String title, String subtitle, Color bg, Runnable onClick) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bg);
        card.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel t = new JLabel(title);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 18f));
        t.setForeground(OFFWHITE.darker());
        card.add(t, BorderLayout.NORTH);

        JLabel sub = new JLabel("<html><small>" + subtitle + "</small></html>");
        sub.setForeground(OFFWHITE);
        card.add(sub, BorderLayout.CENTER);

        JButton action = new JButton("Open");
        action.setBackground(OFFWHITE);
        action.setForeground(bg.darker());
        action.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        action.setFocusPainted(false);
        action.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        action.addActionListener(e -> onClick.run());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottom.setOpaque(false);
        bottom.add(action);
        card.add(bottom, BorderLayout.SOUTH);

        // click anywhere on the card also triggers action
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) { onClick.run(); }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) { card.setBorder(BorderFactory.createLineBorder(OFFWHITE, 2)); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) { card.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14)); }
        });

        return card;
    }

    // Helper: make small quick-link tiles
    private JButton makeSmallTile(String label) {
        JButton tile = new JButton(label);
        tile.setFont(tile.getFont().deriveFont(13f));
        tile.setBackground(OFFWHITE);
        tile.setForeground(NAVY.darker());
        tile.setBorder(BorderFactory.createLineBorder(SKY, 1, true));
        tile.setFocusPainted(false);
        tile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tile.addActionListener(e -> JOptionPane.showMessageDialog(this, label + " clicked"));
        return tile;
    }

    // === Actions that open new windows ===
    private void openManageFleet() {
        SwingUtilities.invokeLater(() -> new ManageFleet().setVisible(true));
    }

    private void openManageUsers() {
        SwingUtilities.invokeLater(() -> new ManageUsers().setVisible(true));
    }

    private void openPurchaseTicket() {
        SwingUtilities.invokeLater(() -> new PurchaseTicket().setVisible(true));
    }

    private void openSubmitFeedback() {
        SwingUtilities.invokeLater(() -> new SubmitFeedBack().setVisible(true));
    }

    private void openViewBoardingTotals() {
        SwingUtilities.invokeLater(() -> new ViewBoardingTotals().setVisible(true));
    }

    private void openCheckBalance() {
        SwingUtilities.invokeLater(() -> new CheckBalance().setVisible(true));
    }

    private void openViewPeakHours() {
        SwingUtilities.invokeLater(() -> new ViewPeakHours().setVisible(true));
    }

    private void openViewSchedule() {
        SwingUtilities.invokeLater(() -> new ViewSchedule().setVisible(true));
    }

    private void openAllocateBuses() {
        SwingUtilities.invokeLater(() -> {
            BusAllocation busAllocationWindow = new BusAllocation();
            busAllocationWindow.setVisible(true);
        });
    }


}