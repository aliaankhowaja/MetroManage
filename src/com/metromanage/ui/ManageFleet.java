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
import com.metromanage.domain.Bus;
import com.metromanage.domain.Route;
import com.metromanage.model.BusPersistanceHandler;
import com.metromanage.model.RoutePersistanceHandler;
import com.metromanage.model.RidePersistanceHandler;

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
    
    // Backend handlers
    private AdminRegister adminRegister;
    private BusPersistanceHandler busPersistanceHandler;
    private RoutePersistanceHandler routePersistanceHandler;
    private RidePersistanceHandler ridePersistanceHandler;
    
    // Data structures
    private java.util.ArrayList<Bus> buses;
    private java.util.ArrayList<Route> routes;

    public ManageFleet() {
        // Initialize backend handlers
        adminRegister = new AdminRegister();
        busPersistanceHandler = new BusPersistanceHandler();
        routePersistanceHandler = new RoutePersistanceHandler();
        ridePersistanceHandler = new RidePersistanceHandler();
        
        initializeUI();
        loadRealData();
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

        // Card 1: Total Buses - Initialize labels
        lblTotalBusesValue = new JLabel("0");
        lblTotalBusesSubtext = new JLabel("Loading...");
        JPanel card1 = createAnalyticsCardWithLabels(
            "Total Buses",
            lblTotalBusesValue,
            lblTotalBusesSubtext,
            PRIMARY_COLOR
        );
        pnlSummaryCards.add(card1);

        // Card 2: Out of Service - Initialize labels
        lblOutOfServiceValue = new JLabel("0");
        lblOutOfServiceSubtext = new JLabel("Loading...");
        JPanel card2 = createAnalyticsCardWithLabels(
            "Out of Service",
            lblOutOfServiceValue,
            lblOutOfServiceSubtext,
            new Color(244, 67, 54)
        );
        pnlSummaryCards.add(card2);

        // Card 3: Today's Trips - Initialize labels
        lblTodayTripsValue = new JLabel("0");
        lblTodayTripsSubtext = new JLabel("Loading...");
        JPanel card3 = createAnalyticsCardWithLabels(
            "Today's Trips",
            lblTodayTripsValue,
            lblTodayTripsSubtext,
            new Color(76, 175, 80)
        );
        pnlSummaryCards.add(card3);
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

        cmbStatusFilter = new JComboBox<>(new String[]{"All", "Active", "UnderMaintenance", "Inactive", "OutOfService"});
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
        String[] columns = {"BusID", "Plate Number", "Route", "Capacity", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only Actions column is "editable" (for buttons)
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

        // Custom renderer for Status column (now column index 4)
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
                
                if (status.equals("Active")) {
                    label.setForeground(new Color(76, 175, 80));
                    label.setBackground(new Color(232, 245, 233));
                } else if (status.equals("Inactive")) {
                    label.setForeground(new Color(33, 150, 243));
                    label.setBackground(new Color(227, 242, 253));
                } else if (status.equals("OutOfService")) {
                    label.setForeground(new Color(244, 67, 54));
                    label.setBackground(new Color(255, 235, 238));
                } else if (status.equals("UnderMaintenance")) {
                    label.setForeground(new Color(255, 152, 0));
                    label.setBackground(new Color(255, 243, 224));
                } else if (status.equals("Deleted")) {
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
        
        tblFleet.getColumnModel().getColumn(5).setCellRenderer(buttonRenderer);
        tblFleet.getColumnModel().getColumn(5).setCellEditor(buttonEditor);

        // Set column widths
        tblFleet.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblFleet.getColumnModel().getColumn(1).setPreferredWidth(120);
        tblFleet.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblFleet.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblFleet.getColumnModel().getColumn(4).setPreferredWidth(120);
        tblFleet.getColumnModel().getColumn(5).setPreferredWidth(280);

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

    private void loadRealData() {
        // Load buses from database
        buses = busPersistanceHandler.getAllBuses();
        if (buses == null) {
            buses = new java.util.ArrayList<>();
        }
        
        // Load routes from database
        routes = routePersistanceHandler.getAllRoutes();
        if (routes == null) {
            routes = new java.util.ArrayList<>();
        }
        
        // Clear existing table data
        tableModel.setRowCount(0);
        
        // Populate table with real bus data
        for (Bus bus : buses) {
            int busID = bus.getBusID();
            String plateNumber = bus.getPlateNumber();
            int capacity = bus.getCapacity();
            String status = bus.getStatus();
            int routeID = bus.getRouteID();
            
            // Get route name from routeID
            String routeName = "Not Assigned";
            if (routeID > 0) {
                for (Route route : routes) {
                    if (route.getRouteID() == routeID) {
                        routeName = route.getRouteName();
                        break;
                    }
                }
            }
            
            // Add row to table (BusID, Plate Number, Route, Capacity, Status, Actions)
            tableModel.addRow(new Object[]{busID, plateNumber, routeName, capacity, status, ""});
        }
        
        // Refresh analytics cards with real data
        refreshAnalyticsCards();
    }

    private void refreshAnalyticsCards() {
        int totalBuses = 0;
        int outOfService = 0;
        int active = 0;
        int underMaintenance = 0;
        int inactive = 0;
        
        for (Bus bus : buses) {
            totalBuses++;
            String status = bus.getStatus();
            
            if (status.equalsIgnoreCase("OutOfService")) {
                outOfService++;
            } else if (status.equalsIgnoreCase("Active")) {
                active++;
            } else if (status.equalsIgnoreCase("UnderMaintenance")) {
                underMaintenance++;
            } else if (status.equalsIgnoreCase("Inactive")) {
                inactive++;
            }
        }
        
        // Update card 1: Total Buses
        lblTotalBusesValue.setText(String.valueOf(totalBuses));
        lblTotalBusesSubtext.setText("Active: " + active + "  |  Inactive: " + inactive);
        
        // Update card 2: Out of Service (including Under Maintenance)
        int totalOutOfService = outOfService + underMaintenance;
        lblOutOfServiceValue.setText(String.valueOf(totalOutOfService));
        lblOutOfServiceSubtext.setText("Maintenance: " + underMaintenance);
        
        // Update card 3: Today's Trips from database
        int todayTrips = ridePersistanceHandler.getTodayTripsCount();
        lblTodayTripsValue.setText(String.valueOf(todayTrips));
        if (todayTrips > 0) {
            lblTodayTripsSubtext.setText("Active rides today");
        } else {
            lblTodayTripsSubtext.setText("No trips recorded today");
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
            
            // Parse route ID from route string
            int routeID = 0;
            if (busData.routeID != null && !busData.routeID.isEmpty()) {
                try {
                    routeID = Integer.parseInt(busData.routeID);
                } catch (NumberFormatException e) {
                    routeID = 0;
                }
            }
            
            // Parse capacity
            int capacity = Integer.parseInt(busData.capacity);
            
            // Create bus using AdminRegister
            Bus newBus = adminRegister.addBus(busData.plateNumber, capacity, busData.status, routeID);
            
            if (newBus != null) {
                // Reload data from database
                loadRealData();
                JOptionPane.showMessageDialog(this, "Bus added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add bus!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editBus(int row) {
        int actualRow = tblFleet.convertRowIndexToModel(row);
        
        // Get bus ID from table
        int busID = Integer.parseInt(tableModel.getValueAt(actualRow, 0).toString());
        
        // Find the actual Bus object
        Bus bus = null;
        for (Bus b : buses) {
            if (b.getBusID() == busID) {
                bus = b;
                break;
            }
        }
        
        if (bus == null) {
            JOptionPane.showMessageDialog(this, "Bus not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create existing data for dialog
        BusData existingData = new BusData(
            String.valueOf(bus.getBusID()),
            bus.getPlateNumber(),
            String.valueOf(bus.getRouteID()),
            String.valueOf(bus.getCapacity()),
            bus.getStatus()
        );
        
        BusDialog dialog = new BusDialog(this, "Edit Bus", existingData);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            BusData busData = dialog.getBusData();
            
            // Parse route ID
            int routeID = 0;
            if (busData.routeID != null && !busData.routeID.isEmpty()) {
                try {
                    routeID = Integer.parseInt(busData.routeID);
                } catch (NumberFormatException e) {
                    routeID = 0;
                }
            }
            
            // Parse capacity
            int capacity = Integer.parseInt(busData.capacity);
            
            // Update bus using AdminRegister
            adminRegister.updateBus(busID, busData.plateNumber, capacity, busData.status, routeID);
            
            // Reload data from database
            loadRealData();
            JOptionPane.showMessageDialog(this, "Bus updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void retireBus(int row) {
        int actualRow = tblFleet.convertRowIndexToModel(row);
        int busID = Integer.parseInt(tableModel.getValueAt(actualRow, 0).toString());
        String plateNumber = tableModel.getValueAt(actualRow, 1).toString();
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to retire bus " + plateNumber + " (ID: " + busID + ")?\n\n" +
            "This will mark the bus as deleted and it will no longer be available for service.",
            "Confirm Retire Bus",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Delete bus using AdminRegister (marks as deleted)
            adminRegister.deleteBus(busID);
            
            // Reload data from database
            loadRealData();
            JOptionPane.showMessageDialog(this, "Bus retired successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void reassignRoute(int row) {
        int actualRow = tblFleet.convertRowIndexToModel(row);
        int busID = Integer.parseInt(tableModel.getValueAt(actualRow, 0).toString());
        String plateNumber = tableModel.getValueAt(actualRow, 1).toString();
        String currentRouteName = tableModel.getValueAt(actualRow, 2).toString();
        
        // Find the Bus object to get current route ID
        Bus bus = null;
        for (Bus b : buses) {
            if (b.getBusID() == busID) {
                bus = b;
                break;
            }
        }
        
        if (bus == null) {
            JOptionPane.showMessageDialog(this, "Bus not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Build route options from database
        String[] routeOptions = new String[routes.size() + 1];
        routeOptions[0] = "Not Assigned (ID: 0)";
        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            routeOptions[i + 1] = route.getRouteName() + " (ID: " + route.getRouteID() + ")";
        }
        
        String newRouteSelection = (String) JOptionPane.showInputDialog(
            this,
            "Select new route for bus " + plateNumber + " (ID: " + busID + ")\n" +
            "Current route: " + currentRouteName,
            "Reassign Route",
            JOptionPane.QUESTION_MESSAGE,
            null,
            routeOptions,
            null
        );
        
        if (newRouteSelection != null) {
            // Parse route ID from selection
            int newRouteID = 0;
            if (newRouteSelection.contains("(ID: ")) {
                String idStr = newRouteSelection.substring(newRouteSelection.indexOf("(ID: ") + 5, newRouteSelection.indexOf(")"));
                newRouteID = Integer.parseInt(idStr);
            }
            
            // Only update if route changed
            if (newRouteID != bus.getRouteID()) {
                // Update bus using AdminRegister
                adminRegister.updateBus(busID, bus.getPlateNumber(), bus.getCapacity(), bus.getStatus(), newRouteID);
                
                // Reload data from database
                loadRealData();
                
                String newRouteName = "Not Assigned";
                for (Route route : routes) {
                    if (route.getRouteID() == newRouteID) {
                        newRouteName = route.getRouteName();
                        break;
                    }
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Bus " + plateNumber + " successfully reassigned to " + newRouteName + "!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
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
        String routeID;  // Changed from route name to route ID
        String capacity;
        String status;

        public BusData(String busId, String plateNumber, String routeID, String capacity, String status) {
            this.busId = busId;
            this.plateNumber = plateNumber;
            this.routeID = routeID;
            this.capacity = capacity;
            this.status = status;
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
        private boolean confirmed = false;
        private BusData busData;

        public BusDialog(JFrame parent, String title, BusData existingData) {
            super(parent, title, true);
            setSize(450, 450);
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

            // Route - Load from database dynamically
            formPanel.add(createLabel("Route:"));
            java.util.ArrayList<String> routeOptions = new java.util.ArrayList<>();
            routeOptions.add("Not Assigned (0)");
            for (Route route : routes) {
                routeOptions.add(route.getRouteName() + " (" + route.getRouteID() + ")");
            }
            cmbRoute = new JComboBox<>(routeOptions.toArray(new String[0]));
            cmbRoute.setFont(getCustomFont(Font.PLAIN, 13));
            cmbRoute.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            if (existingData != null && existingData.routeID != null) {
                // Select the route based on route ID
                int routeID = Integer.parseInt(existingData.routeID);
                if (routeID == 0) {
                    cmbRoute.setSelectedIndex(0);
                } else {
                    for (int i = 0; i < routes.size(); i++) {
                        if (routes.get(i).getRouteID() == routeID) {
                            cmbRoute.setSelectedIndex(i + 1);
                            break;
                        }
                    }
                }
            }
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
            cmbStatus = new JComboBox<>(new String[]{"Active", "UnderMaintenance", "Inactive", "OutOfService"});
            cmbStatus.setFont(getCustomFont(Font.PLAIN, 13));
            cmbStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            if (existingData != null) cmbStatus.setSelectedItem(existingData.status);
            formPanel.add(cmbStatus);
            formPanel.add(Box.createVerticalStrut(15));

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
                    // Extract route ID from selection
                    String selectedRoute = cmbRoute.getSelectedItem().toString();
                    String routeID = "0";
                    if (selectedRoute.contains("(") && selectedRoute.contains(")")) {
                        routeID = selectedRoute.substring(selectedRoute.lastIndexOf("(") + 1, selectedRoute.lastIndexOf(")"));
                    }
                    
                    busData = new BusData(
                        txtBusId.getText().trim(),
                        txtPlateNumber.getText().trim(),
                        routeID,
                        txtCapacity.getText().trim(),
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
            if (txtPlateNumber.getText().trim().isEmpty() ||
                txtCapacity.getText().trim().isEmpty()) {
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
