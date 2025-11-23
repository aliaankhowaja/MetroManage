package com.metromanage.ui;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.metromanage.domain.AdminRegister;
import com.metromanage.domain.Passenger;
import com.metromanage.model.PassengerPersistanceHandler;

/**
 * ManageUsers - Admin dashboard for user management with modern UI.
 * Features: Analytics cards, user table with CRUD operations, search functionality.
 * Uses mock data only - no database integration yet.
 */
public class ManageUsers extends JFrame {

    // ==================== COLOR PLACEHOLDERS ====================
    // [PLACEHOLDER: for PRIMARY_COLOR_HEX]
    private static final Color PRIMARY_COLOR = new Color(86, 124, 141);  // Teal accent
    
    // [PLACEHOLDER: for SIDEBAR_BACKGROUND_COLOR]
    private static final Color SIDEBAR_BACKGROUND = new Color(47, 65, 86);  // Navy
    
    // [PLACEHOLDER: for SIDEBAR_ACTIVE_ITEM_COLOR]
    private static final Color SIDEBAR_ACTIVE = new Color(86, 124, 141);  // Teal for active
    
    // [PLACEHOLDER: for MAIN_BACKGROUND_COLOR]
    private static final Color MAIN_BACKGROUND = new Color(245, 239, 232);  // Light beige
    
    // [PLACEHOLDER: for CARD_BACKGROUND_COLOR]
    private static final Color CARD_BACKGROUND = Color.WHITE;
    
    // [PLACEHOLDER: for TEXT_PRIMARY_COLOR]
    private static final Color TEXT_PRIMARY = new Color(47, 65, 86);  // Navy
    
    // [PLACEHOLDER: for TEXT_MUTED_COLOR]
    private static final Color TEXT_MUTED = new Color(120, 120, 120);  // Gray
    
    // Supporting colors
    private static final Color SKY = new Color(203, 214, 230);
    private static final Color BEIGE = new Color(245, 239, 232);

    // ==================== FONT PLACEHOLDERS ====================
    // [PLACEHOLDER: for FONT_FAMILY]
    private static final String FONT_FAMILY = "Inter";  // Fallback to Arial if not available
    
    // ==================== UI COMPONENTS ====================
    private JPanel rootPanel;
    private JPanel pnlSidebar;
    private JPanel pnlContent;
    private JPanel pnlCards;
    private JPanel pnlTableContainer;
    
    private JLabel lblGreeting;
    private JLabel lblRole;
    
    // Analytics card components
    private JLabel lblTotalUsers;
    private JLabel lblTotalUsersValue;
    private JLabel lblTotalUsersSubtext;
    
    private JLabel lblNewUsers;
    private JLabel lblNewUsersValue;
    private JLabel lblNewUsersSubtext;
    
    private JLabel lblBlockedUsers;
    private JLabel lblBlockedUsersValue;
    private JLabel lblBlockedUsersSubtext;
    
    // Table components
    private JTable tblUsers;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JTextField txtSearch;
    private JButton btnAddUser;
    private TableRowSorter<DefaultTableModel> rowSorter;
    
    // Navigation buttons
    private JPanel navDashboard;
    private JPanel navManageUsers;
    private JPanel navManageFleet;
    private JPanel navLogout;
    
    // Backend handlers
    private AdminRegister adminRegister;
    private PassengerPersistanceHandler passengerPersistanceHandler;
    
    // Data structures
    private java.util.ArrayList<Passenger> passengers;

    public ManageUsers() {
        // Initialize backend handlers
        adminRegister = new AdminRegister();
        passengerPersistanceHandler = new PassengerPersistanceHandler();
        
        initializeUI();
        loadRealData();
    }

    private void initializeUI() {
        setTitle("Manage Users - MetroManage");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);

        // Root panel with gradient background
        rootPanel = new GradientPanel();
        rootPanel.setLayout(new BorderLayout(0, 0));
        setContentPane(rootPanel);

        // Create main layout areas
        createSidebar();
        createContentArea();

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
                0, 0, Color.WHITE,
                0, getHeight(), BEIGE
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void createSidebar() {
        pnlSidebar = new JPanel();
        pnlSidebar.setLayout(new BoxLayout(pnlSidebar, BoxLayout.Y_AXIS));
        pnlSidebar.setBackground(SIDEBAR_BACKGROUND);
        pnlSidebar.setPreferredSize(new Dimension(280, 0));
        pnlSidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        // User info section at top
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setOpaque(false);
        userInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userInfoPanel.setBorder(new EmptyBorder(0, 10, 30, 10));

        // Avatar placeholder (circular)
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(PRIMARY_COLOR);
                g2d.fillOval(0, 0, 60, 60);
                g2d.setColor(Color.WHITE);
                g2d.setFont(getCustomFont(Font.BOLD, 24));
                FontMetrics fm = g2d.getFontMetrics();
                String initial = "A";
                int x = (60 - fm.stringWidth(initial)) / 2;
                int y = ((60 - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(initial, x, y);
            }
        };
        avatarPanel.setPreferredSize(new Dimension(60, 60));
        avatarPanel.setMaximumSize(new Dimension(60, 60));
        avatarPanel.setOpaque(false);
        avatarPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userInfoPanel.add(avatarPanel);
        userInfoPanel.add(Box.createVerticalStrut(15));

        // Greeting
        lblGreeting = new JLabel("Hello, AdminUser");
        lblGreeting.setFont(getCustomFont(Font.BOLD, 18));
        lblGreeting.setForeground(Color.WHITE);
        lblGreeting.setAlignmentX(Component.LEFT_ALIGNMENT);
        userInfoPanel.add(lblGreeting);
        userInfoPanel.add(Box.createVerticalStrut(5));

        // Role
        lblRole = new JLabel("Admin");
        lblRole.setFont(getCustomFont(Font.PLAIN, 13));
        lblRole.setForeground(SKY);
        lblRole.setAlignmentX(Component.LEFT_ALIGNMENT);
        userInfoPanel.add(lblRole);

        pnlSidebar.add(userInfoPanel);
        pnlSidebar.add(Box.createVerticalStrut(20));

        // Navigation items
        JLabel navLabel = new JLabel("NAVIGATION");
        navLabel.setFont(getCustomFont(Font.BOLD, 11));
        navLabel.setForeground(SKY);
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        navLabel.setBorder(new EmptyBorder(0, 12, 12, 0));
        pnlSidebar.add(navLabel);

        navDashboard = createNavItem("Dashboard", false);
        pnlSidebar.add(navDashboard);
        pnlSidebar.add(Box.createVerticalStrut(8));

        navManageUsers = createNavItem("Manage Users", true);  // Active
        pnlSidebar.add(navManageUsers);
        pnlSidebar.add(Box.createVerticalStrut(8));

        navManageFleet = createNavItem("Manage Fleet", false);
        pnlSidebar.add(navManageFleet);
        pnlSidebar.add(Box.createVerticalStrut(8));

        navLogout = createNavItem("Logout", false);
        pnlSidebar.add(navLogout);

        pnlSidebar.add(Box.createVerticalGlue());

        rootPanel.add(pnlSidebar, BorderLayout.WEST);
    }

    private JPanel createNavItem(String text, boolean active) {
        JPanel navItem = new JPanel(new BorderLayout());
        navItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        navItem.setPreferredSize(new Dimension(240, 45));
        navItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        navItem.setBorder(new EmptyBorder(0, 15, 0, 15));

        if (active) {
            navItem.setBackground(SIDEBAR_ACTIVE);
        } else {
            navItem.setOpaque(false);
        }

        JLabel label = new JLabel(text);
        label.setFont(getCustomFont(active ? Font.BOLD : Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
        label.setBorder(new EmptyBorder(0, 10, 0, 0));
        navItem.add(label, BorderLayout.CENTER);

        // Hover effect for non-active items
        if (!active) {
            navItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    navItem.setBackground(new Color(SIDEBAR_BACKGROUND.getRed() + 20, 
                                                     SIDEBAR_BACKGROUND.getGreen() + 20, 
                                                     SIDEBAR_BACKGROUND.getBlue() + 20));
                    navItem.setOpaque(true);
                    navItem.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    navItem.setOpaque(false);
                    navItem.repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    handleNavigation(text);
                }
            });
        }

        return navItem;
    }

    private void createContentArea() {
        pnlContent = new JPanel(new BorderLayout());
        pnlContent.setOpaque(false);
        pnlContent.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Top section: Analytics cards
        createAnalyticsCards();
        pnlContent.add(pnlCards, BorderLayout.NORTH);

        // Bottom section: User table
        createUserTable();
        pnlContent.add(pnlTableContainer, BorderLayout.CENTER);

        rootPanel.add(pnlContent, BorderLayout.CENTER);
    }

    private void createAnalyticsCards() {
        pnlCards = new JPanel(new GridLayout(1, 3, 20, 0));
        pnlCards.setOpaque(false);
        pnlCards.setBorder(new EmptyBorder(0, 0, 25, 0));
        pnlCards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        pnlCards.setPreferredSize(new Dimension(1000, 140));

        // Card 1: Total Users - Initialize labels
        lblTotalUsersValue = new JLabel("0");
        lblTotalUsersSubtext = new JLabel("Loading...");
        JPanel card1 = createAnalyticsCardWithLabels(
            "Total Users",
            lblTotalUsersValue,
            lblTotalUsersSubtext,
            PRIMARY_COLOR
        );
        pnlCards.add(card1);

        // Card 2: New Users - Initialize labels
        lblNewUsersValue = new JLabel("0");
        lblNewUsersSubtext = new JLabel("Loading...");
        JPanel card2 = createAnalyticsCardWithLabels(
            "New Users (Last 7 Days)",
            lblNewUsersValue,
            lblNewUsersSubtext,
            new Color(76, 175, 80)
        );
        pnlCards.add(card2);

        // Card 3: Blocked/Suspended - Initialize labels
        lblBlockedUsersValue = new JLabel("0");
        lblBlockedUsersSubtext = new JLabel("Loading...");
        JPanel card3 = createAnalyticsCardWithLabels(
            "Blocked / Suspended",
            lblBlockedUsersValue,
            lblBlockedUsersSubtext,
            new Color(244, 67, 54)
        );
        pnlCards.add(card3);
    }

    private JPanel createAnalyticsCardWithLabels(String title, JLabel valueLabel, JLabel subtextLabel, Color accentColor) {
        JPanel card = new RoundedPanel(15);
        card.setBackground(CARD_BACKGROUND);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Content panel
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Title
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(getCustomFont(Font.PLAIN, 13));
        lblTitle.setForeground(TEXT_MUTED);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblTitle);
        content.add(Box.createVerticalStrut(10));

        // Value - use the provided label reference
        valueLabel.setFont(getCustomFont(Font.BOLD, 36));
        valueLabel.setForeground(TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(valueLabel);
        content.add(Box.createVerticalStrut(8));

        // Subtext - use the provided label reference
        subtextLabel.setFont(getCustomFont(Font.PLAIN, 12));
        subtextLabel.setForeground(TEXT_MUTED);
        subtextLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(subtextLabel);

        // Add left padding to content to create space from indicator
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(new EmptyBorder(0, 15, 0, 0));
        contentWrapper.add(content, BorderLayout.CENTER);
        card.add(contentWrapper, BorderLayout.CENTER);

        // Accent indicator (left border)
        JPanel indicator = new JPanel();
        indicator.setBackground(accentColor);
        indicator.setPreferredSize(new Dimension(4, 0));
        card.add(indicator, BorderLayout.WEST);

        return card;
    }

    private void createUserTable() {
        pnlTableContainer = new RoundedPanel(15);
        pnlTableContainer.setBackground(CARD_BACKGROUND);
        pnlTableContainer.setLayout(new BorderLayout());
        pnlTableContainer.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header section with title and controls
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel tableTitle = new JLabel("Users");
        tableTitle.setFont(getCustomFont(Font.BOLD, 20));
        tableTitle.setForeground(TEXT_PRIMARY);
        headerPanel.add(tableTitle, BorderLayout.WEST);

        // Right controls (search + add button)
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlsPanel.setOpaque(false);

        txtSearch = new JTextField(20);
        txtSearch.setFont(getCustomFont(Font.PLAIN, 13));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        txtSearch.setForeground(TEXT_MUTED);
        txtSearch.setText("Search by name or email");
        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtSearch.getText().equals("Search by name or email")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(TEXT_PRIMARY);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setText("Search by name or email");
                    txtSearch.setForeground(TEXT_MUTED);
                }
            }
        });
        // Add document listener for real-time search
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applySearchFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applySearchFilter(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applySearchFilter(); }
        });
        controlsPanel.add(txtSearch);

        btnAddUser = new JButton("+ Add User");
        btnAddUser.setFont(getCustomFont(Font.BOLD, 13));
        btnAddUser.setBackground(PRIMARY_COLOR);
        btnAddUser.setForeground(Color.WHITE);
        btnAddUser.setFocusPainted(false);
        btnAddUser.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnAddUser.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAddUser.addActionListener(e -> openAddUserDialog());
        controlsPanel.add(btnAddUser);

        headerPanel.add(controlsPanel, BorderLayout.EAST);

        pnlTableContainer.add(headerPanel, BorderLayout.NORTH);

        // Create table
        String[] columns = {"PassengerID", "Full Name", "Phone Number", "Email", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only Actions column is "editable" (for buttons)
            }
        };

        tblUsers = new JTable(tableModel);
        tblUsers.setFont(getCustomFont(Font.PLAIN, 13));
        tblUsers.setRowHeight(45);
        tblUsers.setShowGrid(false);
        tblUsers.setIntercellSpacing(new Dimension(0, 0));
        tblUsers.setSelectionBackground(new Color(240, 240, 240));
        tblUsers.setSelectionForeground(TEXT_PRIMARY);

        // Style table header
        JTableHeader header = tblUsers.getTableHeader();
        header.setFont(getCustomFont(Font.BOLD, 13));
        header.setBackground(new Color(250, 250, 250));
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Custom renderer for Status column
        tblUsers.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String status = value.toString();
                JLabel label = (JLabel) c;
                label.setFont(getCustomFont(Font.BOLD, 12));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setOpaque(true);
                
                if (status.equals("Active")) {
                    label.setForeground(new Color(76, 175, 80));
                    label.setBackground(new Color(232, 245, 233));
                } else if (status.equals("Inactive")) {
                    label.setForeground(new Color(158, 158, 158));
                    label.setBackground(new Color(245, 245, 245));
                } else if (status.equals("Suspended")) {
                    label.setForeground(new Color(244, 67, 54));
                    label.setBackground(new Color(255, 235, 238));
                }
                
                label.setBorder(new EmptyBorder(5, 10, 5, 10));
                return label;
            }
        });

        // Custom renderer and editor for Actions column with actual clickable buttons
        ButtonRenderer buttonRenderer = new ButtonRenderer();
        ButtonEditor buttonEditor = new ButtonEditor(new JCheckBox());
        
        tblUsers.getColumnModel().getColumn(5).setCellRenderer(buttonRenderer);
        tblUsers.getColumnModel().getColumn(5).setCellEditor(buttonEditor);

        // Set column widths
        tblUsers.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblUsers.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblUsers.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblUsers.getColumnModel().getColumn(3).setPreferredWidth(200);
        tblUsers.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblUsers.getColumnModel().getColumn(5).setPreferredWidth(250);

        // Setup row sorter for filtering/searching
        rowSorter = new TableRowSorter<>(tableModel);
        tblUsers.setRowSorter(rowSorter);

        scrollPane = new JScrollPane(tblUsers);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Apply custom modern scrollbar
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        pnlTableContainer.add(scrollPane, BorderLayout.CENTER);
    }

    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(getCustomFont(Font.PLAIN, 11));
        btn.setForeground(color);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 1, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color);
                btn.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
                btn.setForeground(color);
            }
        });
        
        return btn;
    }

    private void loadRealData() {
        // Load passengers from database
        passengers = passengerPersistanceHandler.getAllPassengers();
        if (passengers == null) {
            passengers = new java.util.ArrayList<>();
        }
        
        // Clear existing table data
        tableModel.setRowCount(0);
        
        // Populate table with real passenger data
        for (Passenger passenger : passengers) {
            int passengerID = passenger.getPassengerID();
            String name = passenger.getName();
            String phoneNumber = passenger.getPhoneNumber();
            String email = passenger.getEmail();
            String status = passenger.getStatus();
            
            // Add row to table (PassengerID, Full Name, Phone Number, Email, Status, Actions)
            tableModel.addRow(new Object[]{passengerID, name, phoneNumber, email, status, ""});
        }
        
        // Refresh analytics cards with real data
        refreshAnalyticsCards();
    }
    
    private void refreshAnalyticsCards() {
        int totalUsers = 0;
        int activeUsers = 0;
        int inactiveUsers = 0;
        int suspendedUsers = 0;
        int newUsersLast7Days = 0;
        
        java.time.LocalDateTime sevenDaysAgo = java.time.LocalDateTime.now().minusDays(7);
        
        for (Passenger passenger : passengers) {
            String status = passenger.getStatus();
            if (status != null && !status.equalsIgnoreCase("deleted")) {
                totalUsers++;
                if (status.equalsIgnoreCase("Active")) {
                    activeUsers++;
                } else if (status.equalsIgnoreCase("Inactive")) {
                    inactiveUsers++;
                } else if (status.equalsIgnoreCase("Suspended")) {
                    suspendedUsers++;
                }
                
                // Count new users in last 7 days
                if (passenger.getRegistrationDate() != null && passenger.getRegistrationDate().isAfter(sevenDaysAgo)) {
                    newUsersLast7Days++;
                }
            }
        }
        
        // Update card 1: Total Users
        lblTotalUsersValue.setText(String.valueOf(totalUsers));
        lblTotalUsersSubtext.setText("Active: " + activeUsers + "  |  Inactive: " + inactiveUsers);
        
        // Update card 2: New Users
        lblNewUsersValue.setText(String.valueOf(newUsersLast7Days));
        lblNewUsersSubtext.setText("Registered in last 7 days");
        
        // Update card 3: Blocked/Suspended
        lblBlockedUsersValue.setText(String.valueOf(suspendedUsers));
        if (suspendedUsers > 0) {
            lblBlockedUsersSubtext.setText("Requires review");
        } else {
            lblBlockedUsersSubtext.setText("No suspended users");
        }
    }

    // ==================== ACTION HANDLERS ====================

    private void applySearchFilter() {
        String searchText = txtSearch.getText().trim();
        
        // If placeholder text or empty, show all rows
        if (searchText.isEmpty() || searchText.equals("Search by name or email")) {
            rowSorter.setRowFilter(null);
            return;
        }
        
        // Search in columns: Full Name (index 1) and Email (index 3)
        java.util.List<javax.swing.RowFilter<DefaultTableModel, Object>> filters = new java.util.ArrayList<>();
        filters.add(javax.swing.RowFilter.regexFilter("(?i)" + searchText, 1)); // Full Name
        filters.add(javax.swing.RowFilter.regexFilter("(?i)" + searchText, 3)); // Email
        
        // Combine filters with OR logic (match any column)
        rowSorter.setRowFilter(javax.swing.RowFilter.orFilter(filters));
    }

    private void openAddUserDialog() {
        UserDialog dialog = new UserDialog(this, "Add New User", null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            UserData userData = dialog.getUserData();
            
            // Create passenger using AdminRegister
            Passenger newPassenger = adminRegister.addPassenger(
                userData.fullName,
                userData.email,
                userData.phoneNumber,
                userData.password,
                0.0f  // Initial wallet balance
            );
            
            if (newPassenger != null) {
                // Reload data from database
                loadRealData();
                JOptionPane.showMessageDialog(this, "User added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add user! Email may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editUser(int row) {
        int actualRow = tblUsers.convertRowIndexToModel(row);
        
        // Get passenger ID from table
        int passengerID = Integer.parseInt(tableModel.getValueAt(actualRow, 0).toString());
        
        // Find the actual Passenger object
        Passenger passenger = null;
        for (Passenger p : passengers) {
            if (p.getPassengerID() == passengerID) {
                passenger = p;
                break;
            }
        }
        
        if (passenger == null) {
            JOptionPane.showMessageDialog(this, "Passenger not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create existing data for dialog
        UserData existingData = new UserData(
            passenger.getName(),
            passenger.getPhoneNumber(),
            passenger.getEmail(),
            "",  // Don't populate password
            passenger.getStatus()
        );
        
        UserDialog dialog = new UserDialog(this, "Edit User", existingData);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            UserData userData = dialog.getUserData();
            
            // Update passenger using AdminRegister
            // Note: password should be empty for edit (not changing it)
            String passwordToUpdate = userData.password.isEmpty() ? passenger.getPasswordHash() : userData.password;
            
            adminRegister.updatePassenger(
                passenger.getEmail(),  // Use existing email as identifier
                userData.fullName,
                userData.phoneNumber,
                passwordToUpdate,
                userData.status,
                passenger.getWalletBalance()
            );
            
            // Reload data from database
            loadRealData();
            JOptionPane.showMessageDialog(this, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void toggleUserStatus(int row) {
        int actualRow = tblUsers.convertRowIndexToModel(row);
        int passengerID = Integer.parseInt(tableModel.getValueAt(actualRow, 0).toString());
        String currentStatus = tableModel.getValueAt(actualRow, 4).toString();
        String name = tableModel.getValueAt(actualRow, 1).toString();
        
        // Find the actual Passenger object
        Passenger passenger = null;
        for (Passenger p : passengers) {
            if (p.getPassengerID() == passengerID) {
                passenger = p;
                break;
            }
        }
        
        if (passenger == null) {
            JOptionPane.showMessageDialog(this, "Passenger not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String newStatus = currentStatus.equalsIgnoreCase("Active") ? "Inactive" : "Active";
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Change status for: " + name + "\n\n" +
            "Current Status: " + currentStatus + "\n" +
            "New Status: " + newStatus,
            "Confirm Status Change",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Update passenger status
            adminRegister.updatePassenger(
                passenger.getEmail(),
                passenger.getName(),
                passenger.getPhoneNumber(),
                passenger.getPasswordHash(),
                newStatus,
                passenger.getWalletBalance()
            );
            
            // Reload data from database
            loadRealData();
            JOptionPane.showMessageDialog(this, "Status updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteUser(int row) {
        int actualRow = tblUsers.convertRowIndexToModel(row);
        int passengerID = Integer.parseInt(tableModel.getValueAt(actualRow, 0).toString());
        String name = tableModel.getValueAt(actualRow, 1).toString();
        String email = tableModel.getValueAt(actualRow, 3).toString();
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete user: " + name + "?\n" +
            "Email: " + email + "\n\n" +
            "This will mark the user as deleted.\n" +
            "⚠️ This action cannot be easily undone!",
            "Confirm Delete User",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Delete passenger using AdminRegister (marks as deleted)
            adminRegister.deletePassenger(email);
            
            // Reload data from database
            loadRealData();
            JOptionPane.showMessageDialog(this, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleNavigation(String destination) {
        // TODO: Implement navigation to other screens
        switch (destination) {
            case "Dashboard":
                new DashBoard();
                dispose();
                break;
            case "Manage Fleet":
                new ManageFleet();
                dispose();
                break;
            case "Logout":
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    new WelcomePage();
                    dispose();
                }
                break;
        }
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
     * Rounded panel with custom painting for soft corners and shadow effect
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
            g2d.setColor(new Color(0, 0, 0, 20));
            g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, cornerRadius, cornerRadius);
            
            // Draw background
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, cornerRadius, cornerRadius);
        }
    }

    /**
     * Modern custom scrollbar UI
     */
    private static class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        private static final int THUMB_SIZE = 8;
        
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(180, 180, 180);
            this.thumbDarkShadowColor = new Color(180, 180, 180);
            this.thumbHighlightColor = new Color(180, 180, 180);
            this.thumbLightShadowColor = new Color(180, 180, 180);
            this.trackColor = new Color(245, 245, 245);
            this.trackHighlightColor = new Color(245, 245, 245);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(trackColor);
            g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int x = thumbBounds.x;
            int y = thumbBounds.y;
            int width = thumbBounds.width;
            int height = thumbBounds.height;

            // Make thumb thinner and centered
            if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
                x += (width - THUMB_SIZE) / 2;
                width = THUMB_SIZE;
            } else {
                y += (height - THUMB_SIZE) / 2;
                height = THUMB_SIZE;
            }

            // Draw rounded thumb
            g2.setColor(thumbColor);
            g2.fillRoundRect(x, y, width, height, THUMB_SIZE, THUMB_SIZE);
        }

        @Override
        protected void setThumbBounds(int x, int y, int width, int height) {
            super.setThumbBounds(x, y, width, height);
            scrollbar.repaint();
        }
    }

    /**
     * Button renderer for table cells
     */
    private class ButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            
            JButton btnEdit = createActionButton("Edit", PRIMARY_COLOR);
            JButton btnToggle = createActionButton("Deactivate", new Color(255, 152, 0));
            JButton btnDelete = createActionButton("Delete", new Color(244, 67, 54));
            
            add(btnEdit);
            add(btnToggle);
            add(btnDelete);
            
            return this;
        }
    }

    /**
     * Button editor for table cells
     */
    private class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton btnEdit;
        private JButton btnToggle;
        private JButton btnDelete;
        private int editingRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            
            btnEdit = createActionButton("Edit", PRIMARY_COLOR);
            btnToggle = createActionButton("Deactivate", new Color(255, 152, 0));
            btnDelete = createActionButton("Delete", new Color(244, 67, 54));
            
            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                editUser(editingRow);
            });
            
            btnToggle.addActionListener(e -> {
                fireEditingStopped();
                toggleUserStatus(editingRow);
            });
            
            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                deleteUser(editingRow);
            });
            
            panel.add(btnEdit);
            panel.add(btnToggle);
            panel.add(btnDelete);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            editingRow = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    /**
     * User data model for dialog
     */
    private static class UserData {
        String fullName;
        String phoneNumber;
        String email;
        String password;
        String status;

        public UserData(String fullName, String phoneNumber, String email, String password, String status) {
            this.fullName = fullName;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.password = password;
            this.status = status;
        }
    }

    /**
     * Dialog for adding/editing users
     */
    private class UserDialog extends JDialog {
        private JTextField txtFullName;
        private JTextField txtPhoneNumber;
        private JTextField txtEmail;
        private JPasswordField txtPassword;
        private JComboBox<String> cmbStatus;
        private boolean confirmed = false;
        private UserData userData;

        public UserDialog(JFrame parent, String title, UserData existingData) {
            super(parent, title, true);
            setSize(450, 450);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            JPanel formPanel = new JPanel();
            formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
            formPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
            formPanel.setBackground(Color.WHITE);

            // Full Name
            formPanel.add(createLabel("Full Name:"));
            txtFullName = new JTextField();
            txtFullName.setFont(getCustomFont(Font.PLAIN, 13));
            txtFullName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            if (existingData != null) txtFullName.setText(existingData.fullName);
            formPanel.add(txtFullName);
            formPanel.add(Box.createVerticalStrut(15));

            // Phone Number
            formPanel.add(createLabel("Phone Number:"));
            txtPhoneNumber = new JTextField();
            txtPhoneNumber.setFont(getCustomFont(Font.PLAIN, 13));
            txtPhoneNumber.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            if (existingData != null) txtPhoneNumber.setText(existingData.phoneNumber);
            formPanel.add(txtPhoneNumber);
            formPanel.add(Box.createVerticalStrut(15));

            // Email
            formPanel.add(createLabel("Email:"));
            txtEmail = new JTextField();
            txtEmail.setFont(getCustomFont(Font.PLAIN, 13));
            txtEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            if (existingData != null) {
                txtEmail.setText(existingData.email);
                txtEmail.setEditable(false);  // Email cannot be changed (used as identifier)
                txtEmail.setBackground(new Color(245, 245, 245));
            }
            formPanel.add(txtEmail);
            formPanel.add(Box.createVerticalStrut(15));

            // Password (only for new users)
            if (existingData == null) {
                formPanel.add(createLabel("Password:"));
                txtPassword = new JPasswordField();
                txtPassword.setFont(getCustomFont(Font.PLAIN, 13));
                txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
                formPanel.add(txtPassword);
                formPanel.add(Box.createVerticalStrut(15));
            }

            // Status
            formPanel.add(createLabel("Status:"));
            cmbStatus = new JComboBox<>(new String[]{"Active", "Inactive", "Suspended"});
            cmbStatus.setFont(getCustomFont(Font.PLAIN, 13));
            cmbStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            if (existingData != null) cmbStatus.setSelectedItem(existingData.status);
            formPanel.add(cmbStatus);

            add(formPanel, BorderLayout.CENTER);

            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
            buttonPanel.setBackground(Color.WHITE);

            JButton btnCancel = new JButton("Cancel");
            btnCancel.setFont(getCustomFont(Font.PLAIN, 13));
            btnCancel.setFocusPainted(false);
            btnCancel.addActionListener(e -> dispose());
            buttonPanel.add(btnCancel);

            JButton btnSave = new JButton("Save");
            btnSave.setFont(getCustomFont(Font.BOLD, 13));
            btnSave.setBackground(PRIMARY_COLOR);
            btnSave.setForeground(Color.WHITE);
            btnSave.setFocusPainted(false);
            btnSave.addActionListener(e -> {
                if (validateInput()) {
                    String password = existingData == null ? new String(txtPassword.getPassword()) : "";
                    userData = new UserData(
                        txtFullName.getText().trim(),
                        txtPhoneNumber.getText().trim(),
                        txtEmail.getText().trim(),
                        password,
                        cmbStatus.getSelectedItem().toString()
                    );
                    confirmed = true;
                    dispose();
                }
            });
            buttonPanel.add(btnSave);

            add(buttonPanel, BorderLayout.SOUTH);
        }

        private JLabel createLabel(String text) {
            JLabel label = new JLabel(text);
            label.setFont(getCustomFont(Font.BOLD, 13));
            label.setForeground(TEXT_PRIMARY);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            return label;
        }

        private boolean validateInput() {
            if (txtFullName.getText().trim().isEmpty() ||
                txtPhoneNumber.getText().trim().isEmpty() ||
                txtEmail.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (txtPassword != null && txtPassword.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this, "Password is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Basic email validation
            if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                JOptionPane.showMessageDialog(this, "Invalid email format!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            return true;
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        public UserData getUserData() {
            return userData;
        }
    }

    // ==================== MAIN METHOD ====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManageUsers());
    }
}
    