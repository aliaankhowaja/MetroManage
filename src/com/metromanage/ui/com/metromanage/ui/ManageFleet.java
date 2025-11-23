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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ManageFleet - Admin dashboard for fleet management with modern UI.
 * Features: Analytics cards, fleet table with CRUD operations, status filtering.
 * Uses mock data only - no database integration yet.
 */
public class ManageFleet extends JFrame {

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
    private JPanel pnlSummaryCards;
    private JPanel pnlTableContainer;
    
    private JLabel lblGreeting;
    private JLabel lblRole;
    
    // Analytics card components
    private JLabel lblTotalBuses;
    private JLabel lblTotalBusesValue;
    private JLabel lblTotalBusesSubtext;
    
    private JLabel lblOutOfService;
    private JLabel lblOutOfServiceValue;
    private JLabel lblOutOfServiceSubtext;
    
    private JLabel lblTodayTrips;
    private JLabel lblTodayTripsValue;
    private JLabel lblTodayTripsSubtext;
    
    // Table components
    private JTable tblFleet;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JTextField txtSearch;
    private JComboBox<String> cmbStatusFilter;
    private JButton btnAddBus;
    private TableRowSorter<DefaultTableModel> rowSorter;
    
    // Navigation buttons
    private JPanel navDashboard;
    private JPanel navManageUsers;
    private JPanel navManageFleet;
    private JPanel navLogout;

    public ManageFleet() {
        initializeUI();
        loadMockData();
    }

    private void initializeUI() {
        setTitle("Manage Fleet - MetroManage");
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

        navManageUsers = createNavItem("Manage Users", false);
        pnlSidebar.add(navManageUsers);
        pnlSidebar.add(Box.createVerticalStrut(8));

        navManageFleet = createNavItem("Manage Fleet", true);  // Active
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
        pnlContent.add(pnlSummaryCards, BorderLayout.NORTH);

        // Bottom section: Fleet table
        createFleetTable();
        pnlContent.add(pnlTableContainer, BorderLayout.CENTER);

        rootPanel.add(pnlContent, BorderLayout.CENTER);
    }

    private void createAnalyticsCards() {
        pnlSummaryCards = new JPanel(new GridLayout(1, 3, 20, 0));
        pnlSummaryCards.setOpaque(false);
        pnlSummaryCards.setBorder(new EmptyBorder(0, 0, 25, 0));
        pnlSummaryCards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        pnlSummaryCards.setPreferredSize(new Dimension(1000, 140));

        // Card 1: Total Buses
        JPanel card1 = createAnalyticsCard(
            "Total Buses",
            "80",
            "In Service: 60  |  Reserved: 10",
            PRIMARY_COLOR
        );
        pnlSummaryCards.add(card1);

        // Card 2: Out of Service
        JPanel card2 = createAnalyticsCard(
            "Out of Service",
            "5",
            "Maintenance ongoing",
            new Color(244, 67, 54)
        );
        pnlSummaryCards.add(card2);

        // Card 3: Today's Trips
        JPanel card3 = createAnalyticsCard(
            "Today's Trips",
            "230",
            "Estimated on-time: 92%",
            new Color(76, 175, 80)
        );
        pnlSummaryCards.add(card3);
    }

    private JPanel createAnalyticsCard(String title, String value, String subtext, Color accentColor) {
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

        // Value
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(getCustomFont(Font.BOLD, 36));
        lblValue.setForeground(TEXT_PRIMARY);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblValue);
        content.add(Box.createVerticalStrut(8));

        // Subtext
        JLabel lblSubtext = new JLabel(subtext);
        lblSubtext.setFont(getCustomFont(Font.PLAIN, 12));
        lblSubtext.setForeground(TEXT_MUTED);
        lblSubtext.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblSubtext);

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

    private void createFleetTable() {
        pnlTableContainer = new RoundedPanel(15);
        pnlTableContainer.setBackground(CARD_BACKGROUND);
        pnlTableContainer.setLayout(new BorderLayout());
        pnlTableContainer.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header section with title and controls
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel tableTitle = new JLabel("Fleet Overview");
        tableTitle.setFont(getCustomFont(Font.BOLD, 20));
        tableTitle.setForeground(TEXT_PRIMARY);
        headerPanel.add(tableTitle, BorderLayout.WEST);

        // Right controls (search + filter + add button)
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlsPanel.setOpaque(false);

        txtSearch = new JTextField(20);
        txtSearch.setFont(getCustomFont(Font.PLAIN, 13));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        txtSearch.setForeground(TEXT_MUTED);
        txtSearch.setText("Search by ID, plate, or route");
        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtSearch.getText().equals("Search by ID, plate, or route")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(TEXT_PRIMARY);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setText("Search by ID, plate, or route");
                    txtSearch.setForeground(TEXT_MUTED);
                }
            }
        });
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applySearchFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applySearchFilter(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applySearchFilter(); }
        });
        controlsPanel.add(txtSearch);

        cmbStatusFilter = new JComboBox<>(new String[]{"All", "In Service", "Available", "Out of Service", "Retired"});
        cmbStatusFilter.setFont(getCustomFont(Font.PLAIN, 13));
        cmbStatusFilter.setBackground(Color.WHITE);
        cmbStatusFilter.setForeground(TEXT_PRIMARY);
        cmbStatusFilter.setPreferredSize(new Dimension(150, 35));
        cmbStatusFilter.addActionListener(e -> applyFilters());
        controlsPanel.add(cmbStatusFilter);

        btnAddBus = new JButton("+ Add Bus");
        btnAddBus.setFont(getCustomFont(Font.BOLD, 13));
        btnAddBus.setBackground(PRIMARY_COLOR);
        btnAddBus.setForeground(Color.WHITE);
        btnAddBus.setFocusPainted(false);
        btnAddBus.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnAddBus.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAddBus.addActionListener(e -> openAddBusDialog());
        controlsPanel.add(btnAddBus);

        headerPanel.add(controlsPanel, BorderLayout.EAST);

        pnlTableContainer.add(headerPanel, BorderLayout.NORTH);

        // Create table
        String[] columns = {"BusID", "Plate Number", "Route", "Capacity", "Status", "Last Service Date", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only Actions column is "editable" (for buttons)
            }
        };

        tblFleet = new JTable(tableModel);
        tblFleet.setFont(getCustomFont(Font.PLAIN, 13));
        tblFleet.setRowHeight(45);
        tblFleet.setShowGrid(false);
        tblFleet.setIntercellSpacing(new Dimension(0, 0));
        tblFleet.setSelectionBackground(new Color(240, 240, 240));
        tblFleet.setSelectionForeground(TEXT_PRIMARY);

        // Style table header
        JTableHeader header = tblFleet.getTableHeader();
        header.setFont(getCustomFont(Font.BOLD, 13));
        header.setBackground(new Color(250, 250, 250));
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Custom renderer for Status column
        tblFleet.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String status = value.toString();
                JLabel label = (JLabel) c;
                label.setFont(getCustomFont(Font.BOLD, 12));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setOpaque(true);
                
                if (status.equals("In Service")) {
                    label.setForeground(new Color(76, 175, 80));
                    label.setBackground(new Color(232, 245, 233));
                } else if (status.equals("Available")) {
                    label.setForeground(new Color(33, 150, 243));
                    label.setBackground(new Color(227, 242, 253));
                } else if (status.equals("Out of Service")) {
                    label.setForeground(new Color(255, 152, 0));
                    label.setBackground(new Color(255, 243, 224));
                } else if (status.equals("Retired")) {
                    label.setForeground(new Color(158, 158, 158));
                    label.setBackground(new Color(245, 245, 245));
                }
                
                label.setBorder(new EmptyBorder(5, 10, 5, 10));
                return label;
            }
        });

        // Custom renderer and editor for Actions column with actual clickable buttons
        ButtonRenderer buttonRenderer = new ButtonRenderer();
        ButtonEditor buttonEditor = new ButtonEditor(new JCheckBox());
        
        tblFleet.getColumnModel().getColumn(6).setCellRenderer(buttonRenderer);
        tblFleet.getColumnModel().getColumn(6).setCellEditor(buttonEditor);

        // Set column widths
        tblFleet.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblFleet.getColumnModel().getColumn(1).setPreferredWidth(120);
        tblFleet.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblFleet.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblFleet.getColumnModel().getColumn(4).setPreferredWidth(120);
        tblFleet.getColumnModel().getColumn(5).setPreferredWidth(140);
        tblFleet.getColumnModel().getColumn(6).setPreferredWidth(280);

        // Setup row sorter for filtering
        rowSorter = new TableRowSorter<>(tableModel);
        tblFleet.setRowSorter(rowSorter);

        scrollPane = new JScrollPane(tblFleet);
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

    private void loadMockData() {
        // TODO: replace with real FleetService later
        Object[][] mockData = {
            {1001, "PKM-2301", "Route 5", 40, "In Service", "2024-11-15"},
            {1002, "PKM-2302", "Route 12", 45, "In Service", "2024-11-10"},
            {1003, "PKM-2303", "Route 3", 40, "Available", "2024-11-18"},
            {1004, "PKM-2304", "Route 8", 50, "In Service", "2024-11-05"},
            {1005, "PKM-2305", "Route 15", 40, "Out of Service", "2024-10-28"},
            {1006, "PKM-2306", "Route 7", 45, "In Service", "2024-11-12"},
            {1007, "PKM-2307", "Route 10", 40, "Available", "2024-11-20"},
            {1008, "PKM-2308", "Route 2", 50, "Retired", "2024-09-15"},
            {1009, "PKM-2309", "Route 6", 45, "In Service", "2024-11-17"},
            {1010, "PKM-2310", "Route 11", 40, "Available", "2024-11-14"}
        };

        for (Object[] row : mockData) {
            tableModel.addRow(new Object[]{row[0], row[1], row[2], row[3], row[4], row[5], ""});
        }
    }

    // ==================== ACTION HANDLERS ====================

    private void applySearchFilter() {
        String searchText = txtSearch.getText().trim();
        if (searchText.isEmpty() || searchText.equals("Search by ID, plate, or route")) {
            applyFilters();
            return;
        }
        applyFilters();
    }

    private void applyFilters() {
        String selectedStatus = (String) cmbStatusFilter.getSelectedItem();
        String searchText = txtSearch.getText().trim();
        
        java.util.List<RowFilter<DefaultTableModel, Object>> filters = new java.util.ArrayList<>();
        
        // Add status filter
        if (!selectedStatus.equals("All")) {
            filters.add(RowFilter.regexFilter(selectedStatus, 4));
        }
        
        // Add search filter (searches BusID, Plate Number, and Route columns)
        if (!searchText.isEmpty() && !searchText.equals("Search by ID, plate, or route")) {
            filters.add(RowFilter.orFilter(java.util.Arrays.asList(
                RowFilter.regexFilter("(?i)" + searchText, 0), // BusID
                RowFilter.regexFilter("(?i)" + searchText, 1), // Plate Number
                RowFilter.regexFilter("(?i)" + searchText, 2)  // Route
            )));
        }
        
        // Apply combined filters
        if (filters.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private void openAddBusDialog() {
        BusDialog dialog = new BusDialog(this, "Add New Bus", null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            BusData busData = dialog.getBusData();
            int newId = 1000 + tableModel.getRowCount() + 1;
            tableModel.addRow(new Object[]{
                newId,
                busData.plateNumber,
                busData.route,
                busData.capacity,
                busData.status,
                busData.lastServiceDate,
                ""
            });
            JOptionPane.showMessageDialog(this, "Bus added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editBus(int row) {
        // TODO: replace with real FleetService later
        int actualRow = tblFleet.convertRowIndexToModel(row);
        
        BusData existingData = new BusData(
            tableModel.getValueAt(actualRow, 0).toString(),
            tableModel.getValueAt(actualRow, 1).toString(),
            tableModel.getValueAt(actualRow, 2).toString(),
            tableModel.getValueAt(actualRow, 3).toString(),
            tableModel.getValueAt(actualRow, 4).toString(),
            tableModel.getValueAt(actualRow, 5).toString()
        );
        
        BusDialog dialog = new BusDialog(this, "Edit Bus", existingData);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            BusData busData = dialog.getBusData();
            tableModel.setValueAt(busData.plateNumber, actualRow, 1);
            tableModel.setValueAt(busData.route, actualRow, 2);
            tableModel.setValueAt(busData.capacity, actualRow, 3);
            tableModel.setValueAt(busData.status, actualRow, 4);
            tableModel.setValueAt(busData.lastServiceDate, actualRow, 5);
            JOptionPane.showMessageDialog(this, "Bus updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void retireBus(int row) {
        // TODO: replace with real FleetService later
        int actualRow = tblFleet.convertRowIndexToModel(row);
        String busId = tableModel.getValueAt(actualRow, 0).toString();
        String plateNumber = tableModel.getValueAt(actualRow, 1).toString();
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to retire bus " + plateNumber + " (ID: " + busId + ")?\n\n" +
            "This will change its status to 'Retired' and it will no longer be available for service.",
            "Confirm Retire Bus",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.setValueAt("Retired", actualRow, 4);
            JOptionPane.showMessageDialog(this, "Bus retired successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void reassignRoute(int row) {
        // TODO: replace with real FleetService later
        int actualRow = tblFleet.convertRowIndexToModel(row);
        String busId = tableModel.getValueAt(actualRow, 0).toString();
        String plateNumber = tableModel.getValueAt(actualRow, 1).toString();
        String currentRoute = tableModel.getValueAt(actualRow, 2).toString();
        
        String[] routes = {"Route 1", "Route 2", "Route 3", "Route 5", "Route 6", "Route 7", 
                          "Route 8", "Route 10", "Route 11", "Route 12", "Route 15"};
        
        String newRoute = (String) JOptionPane.showInputDialog(
            this,
            "Select new route for bus " + plateNumber + " (ID: " + busId + ")\n" +
            "Current route: " + currentRoute,
            "Reassign Route",
            JOptionPane.QUESTION_MESSAGE,
            null,
            routes,
            currentRoute
        );
        
        if (newRoute != null && !newRoute.equals(currentRoute)) {
            tableModel.setValueAt(newRoute, actualRow, 2);
            JOptionPane.showMessageDialog(this, 
                "Bus " + plateNumber + " successfully reassigned to " + newRoute + "!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleNavigation(String destination) {
        // TODO: Implement navigation to other screens
        switch (destination) {
            case "Dashboard":
                new DashBoard();
                dispose();
                break;
            case "Manage Users":
                new ManageUsers();
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
            JButton btnRetire = createActionButton("Retire", new Color(244, 67, 54));
            JButton btnReassign = createActionButton("Reassign Route", new Color(255, 152, 0));
            
            add(btnEdit);
            add(btnRetire);
            add(btnReassign);
            
            return this;
        }
    }

    /**
     * Button editor for table cells
     */
    private class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton btnEdit;
        private JButton btnRetire;
        private JButton btnReassign;
        private int editingRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            
            btnEdit = createActionButton("Edit", PRIMARY_COLOR);
            btnRetire = createActionButton("Retire", new Color(244, 67, 54));
            btnReassign = createActionButton("Reassign Route", new Color(255, 152, 0));
            
            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                editBus(editingRow);
            });
            
            btnRetire.addActionListener(e -> {
                fireEditingStopped();
                retireBus(editingRow);
            });
            
            btnReassign.addActionListener(e -> {
                fireEditingStopped();
                reassignRoute(editingRow);
            });
            
            panel.add(btnEdit);
            panel.add(btnRetire);
            panel.add(btnReassign);
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
     * Bus data model for dialog
     */
    private static class BusData {
        String busId;
        String plateNumber;
        String route;
        String capacity;
        String status;
        String lastServiceDate;

        public BusData(String busId, String plateNumber, String route, String capacity, String status, String lastServiceDate) {
            this.busId = busId;
            this.plateNumber = plateNumber;
            this.route = route;
            this.capacity = capacity;
            this.status = status;
            this.lastServiceDate = lastServiceDate;
        }
    }

    /**
     * Dialog for adding/editing buses
     */
    private class BusDialog extends JDialog {
        private JTextField txtBusId;
        private JTextField txtPlateNumber;
        private JComboBox<String> cmbRoute;
        private JTextField txtCapacity;
        private JComboBox<String> cmbStatus;
        private JTextField txtLastServiceDate;
        private boolean confirmed = false;
        private BusData busData;

        public BusDialog(JFrame parent, String title, BusData existingData) {
            super(parent, title, true);
            setSize(450, 500);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            JPanel formPanel = new JPanel();
            formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
            formPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
            formPanel.setBackground(Color.WHITE);

            // Bus ID (read-only for edit)
            formPanel.add(createLabel("Bus ID:"));
            txtBusId = new JTextField();
            txtBusId.setFont(getCustomFont(Font.PLAIN, 13));
            txtBusId.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            if (existingData != null) {
                txtBusId.setText(existingData.busId);
                txtBusId.setEditable(false);
                txtBusId.setBackground(new Color(245, 245, 245));
            } else {
                txtBusId.setText("Auto-generated");
                txtBusId.setEditable(false);
                txtBusId.setBackground(new Color(245, 245, 245));
            }
            formPanel.add(txtBusId);
            formPanel.add(Box.createVerticalStrut(15));

            // Plate Number
            formPanel.add(createLabel("Plate Number:"));
            txtPlateNumber = new JTextField();
            txtPlateNumber.setFont(getCustomFont(Font.PLAIN, 13));
            txtPlateNumber.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            if (existingData != null) txtPlateNumber.setText(existingData.plateNumber);
            formPanel.add(txtPlateNumber);
            formPanel.add(Box.createVerticalStrut(15));

            // Route
            formPanel.add(createLabel("Route:"));
            String[] routes = {"Route 1", "Route 2", "Route 3", "Route 5", "Route 6", "Route 7", 
                              "Route 8", "Route 10", "Route 11", "Route 12", "Route 15"};
            cmbRoute = new JComboBox<>(routes);
            cmbRoute.setFont(getCustomFont(Font.PLAIN, 13));
            cmbRoute.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            if (existingData != null) cmbRoute.setSelectedItem(existingData.route);
            formPanel.add(cmbRoute);
            formPanel.add(Box.createVerticalStrut(15));

            // Capacity
            formPanel.add(createLabel("Capacity:"));
            txtCapacity = new JTextField();
            txtCapacity.setFont(getCustomFont(Font.PLAIN, 13));
            txtCapacity.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            if (existingData != null) txtCapacity.setText(existingData.capacity);
            formPanel.add(txtCapacity);
            formPanel.add(Box.createVerticalStrut(15));

            // Status
            formPanel.add(createLabel("Status:"));
            cmbStatus = new JComboBox<>(new String[]{"In Service", "Available", "Out of Service", "Retired"});
            cmbStatus.setFont(getCustomFont(Font.PLAIN, 13));
            cmbStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            if (existingData != null) cmbStatus.setSelectedItem(existingData.status);
            formPanel.add(cmbStatus);
            formPanel.add(Box.createVerticalStrut(15));

            // Last Service Date
            formPanel.add(createLabel("Last Service Date (YYYY-MM-DD):"));
            txtLastServiceDate = new JTextField();
            txtLastServiceDate.setFont(getCustomFont(Font.PLAIN, 13));
            txtLastServiceDate.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            if (existingData != null) {
                txtLastServiceDate.setText(existingData.lastServiceDate);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                txtLastServiceDate.setText(sdf.format(new Date()));
            }
            formPanel.add(txtLastServiceDate);

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
                    busData = new BusData(
                        txtBusId.getText().trim(),
                        txtPlateNumber.getText().trim(),
                        cmbRoute.getSelectedItem().toString(),
                        txtCapacity.getText().trim(),
                        cmbStatus.getSelectedItem().toString(),
                        txtLastServiceDate.getText().trim()
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
            if (txtPlateNumber.getText().trim().isEmpty() ||
                txtCapacity.getText().trim().isEmpty() ||
                txtLastServiceDate.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Validate capacity is a number
            try {
                int capacity = Integer.parseInt(txtCapacity.getText().trim());
                if (capacity <= 0) {
                    JOptionPane.showMessageDialog(this, "Capacity must be a positive number!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Capacity must be a valid number!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Basic date format validation
            if (!txtLastServiceDate.getText().matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(this, "Date must be in YYYY-MM-DD format!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            return true;
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        public BusData getBusData() {
            return busData;
        }
    }

    // ==================== MAIN METHOD ====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManageFleet());
    }
}
