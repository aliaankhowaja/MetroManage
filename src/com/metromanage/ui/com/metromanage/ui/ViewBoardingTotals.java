package com.metromanage.ui;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.metromanage.domain.Route;
import com.metromanage.model.RidePersistanceHandler;
import com.metromanage.model.RidePersistanceHandler.BoardingData;
import com.metromanage.model.RoutePersistanceHandler;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Map;

/**
 * ViewBoardingTotals - Admin dashboard for viewing boarding totals with modern UI.
 * Features: Analytics cards (Total Boardings, Busiest Route, Active Trips), trip table with filtering.
 */
public class ViewBoardingTotals extends JFrame {

    // ==================== BACKEND HANDLERS ====================
    private RidePersistanceHandler ridePersistanceHandler;
    private RoutePersistanceHandler routePersistanceHandler;

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
    private JPanel pnlTableCard;
    
    private JLabel lblGreeting;
    private JLabel lblRole;
    
    // Analytics card components
    private JLabel lblTotalBoardingsValue;
    private JLabel lblTotalBoardingsSubtext;
    
    private JLabel lblBusiestRouteValue;
    private JLabel lblBusiestRouteSubtext;
    
    private JLabel lblActiveTripsValue;
    private JLabel lblActiveTripsSubtext;
    
    // Filter components
    private JComboBox<String> cmbRoute;
    private JComboBox<String> cmbDateRange;
    private JComboBox<String> cmbTimeWindow;
    private JButton btnRefresh;
    
    // Table components
    private JTable tblBoardings;
    private DefaultTableModel tableModel;
    private JScrollPane scrollBoardings;
    private JLabel lblShowingTrips;
    
    // Navigation buttons
    private JPanel navDashboard;
    private JPanel navManageUsers;
    private JPanel navManageFleet;
    private JPanel navBoardingTotals;
    private JPanel navLogout;
    
    // Data storage
    private ArrayList<BoardingData> boardingDataList;
    private ArrayList<Route> routes;

    public ViewBoardingTotals() {
        // Initialize backend handlers
        ridePersistanceHandler = new RidePersistanceHandler();
        routePersistanceHandler = new RoutePersistanceHandler();
        
        initializeUI();
        loadRoutes();
        applyFilters();
    }

    private void initializeUI() {
        setTitle("View Boarding Totals - MetroManage");
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

        navManageFleet = createNavItem("Manage Fleet", false);
        pnlSidebar.add(navManageFleet);
        pnlSidebar.add(Box.createVerticalStrut(8));

        navBoardingTotals = createNavItem("Boarding Totals", true);  // Active
        pnlSidebar.add(navBoardingTotals);
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

        // Top section wrapper for title + cards + filters
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);

        // Page title
        JLabel lblTitle = new JLabel("View Boarding Totals");
        lblTitle.setFont(getCustomFont(Font.BOLD, 28));
        lblTitle.setForeground(TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        topSection.add(lblTitle);
        topSection.add(Box.createVerticalStrut(25));

        // Analytics cards
        createAnalyticsCards();
        topSection.add(pnlSummaryCards);
        topSection.add(Box.createVerticalStrut(20));

        // Filter bar
        createFilterBar();
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.setOpaque(false);
        filterPanel.add(createFilterBar(), BorderLayout.CENTER);
        topSection.add(filterPanel);
        topSection.add(Box.createVerticalStrut(20));

        pnlContent.add(topSection, BorderLayout.NORTH);

        // Bottom section: Boarding totals table
        createBoardingTable();
        pnlContent.add(pnlTableCard, BorderLayout.CENTER);

        rootPanel.add(pnlContent, BorderLayout.CENTER);
    }

    private void createAnalyticsCards() {
        pnlSummaryCards = new JPanel(new GridLayout(1, 3, 20, 0));
        pnlSummaryCards.setOpaque(false);
        pnlSummaryCards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        pnlSummaryCards.setPreferredSize(new Dimension(1000, 140));

        // Card 1: Total Boardings
        JPanel card1 = createAnalyticsCard(
            "Total Boardings",
            "3,240",
            "Selected period",
            PRIMARY_COLOR
        );
        pnlSummaryCards.add(card1);

        // Card 2: Busiest Route
        JPanel card2 = createAnalyticsCard(
            "Busiest Route",
            "R1",
            "Boardings: 1,120",
            new Color(76, 175, 80)
        );
        pnlSummaryCards.add(card2);

        // Card 3: Active Trips
        JPanel card3 = createAnalyticsCard(
            "Active Trips",
            "42",
            "Running now",
            new Color(255, 152, 0)
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
        
        // Store references to update later
        if (title.equals("Total Boardings")) {
            lblTotalBoardingsValue = lblValue;
        } else if (title.equals("Busiest Route")) {
            lblBusiestRouteValue = lblValue;
        } else if (title.equals("Active Trips")) {
            lblActiveTripsValue = lblValue;
        }
        
        content.add(lblValue);
        content.add(Box.createVerticalStrut(8));

        // Subtext
        JLabel lblSubtext = new JLabel(subtext);
        lblSubtext.setFont(getCustomFont(Font.PLAIN, 12));
        lblSubtext.setForeground(TEXT_MUTED);
        lblSubtext.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Store references to update later
        if (title.equals("Total Boardings")) {
            lblTotalBoardingsSubtext = lblSubtext;
        } else if (title.equals("Busiest Route")) {
            lblBusiestRouteSubtext = lblSubtext;
        } else if (title.equals("Active Trips")) {
            lblActiveTripsSubtext = lblSubtext;
        }
        
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

    private JPanel createFilterBar() {
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterBar.setOpaque(false);

        // Route filter
        JLabel lblRouteFilter = new JLabel("Route:");
        lblRouteFilter.setFont(getCustomFont(Font.BOLD, 13));
        lblRouteFilter.setForeground(TEXT_PRIMARY);
        filterBar.add(lblRouteFilter);

        cmbRoute = new JComboBox<>();
        cmbRoute.addItem("All Routes");
        cmbRoute.setFont(getCustomFont(Font.PLAIN, 13));
        cmbRoute.setBackground(Color.WHITE);
        cmbRoute.setForeground(TEXT_PRIMARY);
        cmbRoute.setPreferredSize(new Dimension(140, 35));
        filterBar.add(cmbRoute);

        filterBar.add(Box.createHorizontalStrut(10));

        // Date Range filter
        JLabel lblDateRange = new JLabel("Date Range:");
        lblDateRange.setFont(getCustomFont(Font.BOLD, 13));
        lblDateRange.setForeground(TEXT_PRIMARY);
        filterBar.add(lblDateRange);

        cmbDateRange = new JComboBox<>(new String[]{"All Time", "Today", "Last 7 Days", "Last 30 Days"});
        cmbDateRange.setFont(getCustomFont(Font.PLAIN, 13));
        cmbDateRange.setBackground(Color.WHITE);
        cmbDateRange.setForeground(TEXT_PRIMARY);
        cmbDateRange.setPreferredSize(new Dimension(140, 35));
        filterBar.add(cmbDateRange);

        filterBar.add(Box.createHorizontalStrut(10));

        // Time Window filter
        JLabel lblTimeWindow = new JLabel("Time:");
        lblTimeWindow.setFont(getCustomFont(Font.BOLD, 13));
        lblTimeWindow.setForeground(TEXT_PRIMARY);
        filterBar.add(lblTimeWindow);

        cmbTimeWindow = new JComboBox<>(new String[]{"All Day", "Morning", "Evening"});
        cmbTimeWindow.setFont(getCustomFont(Font.PLAIN, 13));
        cmbTimeWindow.setBackground(Color.WHITE);
        cmbTimeWindow.setForeground(TEXT_PRIMARY);
        cmbTimeWindow.setPreferredSize(new Dimension(140, 35));
        filterBar.add(cmbTimeWindow);

        filterBar.add(Box.createHorizontalStrut(20));

        // Refresh button
        btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(getCustomFont(Font.BOLD, 13));
        btnRefresh.setBackground(PRIMARY_COLOR);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorder(new EmptyBorder(10, 25, 10, 25));
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> applyFilters());
        filterBar.add(btnRefresh);

        return filterBar;
    }

    private void createBoardingTable() {
        pnlTableCard = new RoundedPanel(15);
        pnlTableCard.setBackground(CARD_BACKGROUND);
        pnlTableCard.setLayout(new BorderLayout());
        pnlTableCard.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header section with title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel tableTitle = new JLabel("Trip Boarding Details");
        tableTitle.setFont(getCustomFont(Font.BOLD, 20));
        tableTitle.setForeground(TEXT_PRIMARY);
        headerPanel.add(tableTitle, BorderLayout.WEST);

        // Right side: showing trips count
        lblShowingTrips = new JLabel("Showing 0 trips");
        lblShowingTrips.setFont(getCustomFont(Font.PLAIN, 13));
        lblShowingTrips.setForeground(TEXT_MUTED);
        headerPanel.add(lblShowingTrips, BorderLayout.EAST);

        pnlTableCard.add(headerPanel, BorderLayout.NORTH);

        // Create table
        String[] columns = {"Trip ID", "Bus ID", "Route", "Departure Time", "Passenger Count"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblBoardings = new JTable(tableModel);
        tblBoardings.setFont(getCustomFont(Font.PLAIN, 13));
        tblBoardings.setRowHeight(45);
        tblBoardings.setShowGrid(false);
        tblBoardings.setIntercellSpacing(new Dimension(0, 0));
        tblBoardings.setSelectionBackground(new Color(240, 240, 240));
        tblBoardings.setSelectionForeground(TEXT_PRIMARY);

        // Style table header
        JTableHeader header = tblBoardings.getTableHeader();
        header.setFont(getCustomFont(Font.BOLD, 13));
        header.setBackground(new Color(250, 250, 250));
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        
        // Center align header text
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        // Center align all columns by default
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tblBoardings.getColumnCount(); i++) {
            tblBoardings.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Custom renderer for Passenger Count column (with colors)
        tblBoardings.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                JLabel label = (JLabel) c;
                label.setFont(getCustomFont(Font.BOLD, 13));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                
                int count = Integer.parseInt(value.toString());
                if (count >= 40) {
                    label.setForeground(new Color(76, 175, 80));  // Green for high
                } else if (count >= 25) {
                    label.setForeground(new Color(255, 152, 0));  // Orange for medium
                } else {
                    label.setForeground(new Color(158, 158, 158));  // Gray for low
                }
                
                return label;
            }
        });

        // Set column widths
        tblBoardings.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblBoardings.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblBoardings.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblBoardings.getColumnModel().getColumn(3).setPreferredWidth(150);
        tblBoardings.getColumnModel().getColumn(4).setPreferredWidth(150);

        scrollBoardings = new JScrollPane(tblBoardings);
        scrollBoardings.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollBoardings.getViewport().setBackground(Color.WHITE);
        
        // Apply custom modern scrollbar
        scrollBoardings.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollBoardings.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        scrollBoardings.getVerticalScrollBar().setUnitIncrement(16);

        pnlTableCard.add(scrollBoardings, BorderLayout.CENTER);
    }

    private void loadRoutes() {
        // Load all routes from database
        routes = routePersistanceHandler.getAllRoutes();
        
        // Populate route dropdown
        for (Route route : routes) {
            cmbRoute.addItem(route.getRouteName());
        }
    }

    private void applyFilters() {
        String selectedRoute = (String) cmbRoute.getSelectedItem();
        String selectedDateRange = (String) cmbDateRange.getSelectedItem();
        String selectedTimeWindow = (String) cmbTimeWindow.getSelectedItem();
        
        // Determine route ID filter (0 for all routes)
        int routeID = 0;
        if (!selectedRoute.equals("All Routes")) {
            for (Route route : routes) {
                if (route.getRouteName().equals(selectedRoute)) {
                    routeID = route.getRouteID();
                    break;
                }
            }
        }
        
        // Determine days back filter
        int daysBack = -1; // Default to all time
        if (selectedDateRange.equals("Today")) {
            daysBack = 0;
        } else if (selectedDateRange.equals("Last 7 Days")) {
            daysBack = 7;
        } else if (selectedDateRange.equals("Last 30 Days")) {
            daysBack = 30;
        } else if (selectedDateRange.equals("All Time")) {
            daysBack = -1; // Show all data
        }
        
        // Determine time window filter
        String timeWindow = "All";
        if (selectedTimeWindow.equals("Morning")) {
            timeWindow = "Morning";
        } else if (selectedTimeWindow.equals("Evening")) {
            timeWindow = "Evening";
        }
        
        // Fetch data from database with filters
        boardingDataList = ridePersistanceHandler.getBoardingData(routeID, daysBack, timeWindow);
        
        // Update table
        updateTable();
        
        // Update summary cards
        updateSummaryCards(routeID, daysBack, timeWindow);
    }

    private void updateTable() {
        // Clear existing rows
        tableModel.setRowCount(0);
        
        // Add boarding data
        int tripCounter = 1;
        for (BoardingData data : boardingDataList) {
            // Format boarding time (extract time part only)
            String timeStr = data.boardingTime;
            if (timeStr != null && timeStr.length() >= 16) {
                // Extract HH:MM from datetime string
                timeStr = timeStr.substring(11, 16);
            }
            
            tableModel.addRow(new Object[]{
                "T" + String.format("%04d", tripCounter++),
                "B" + String.format("%03d", data.busID),
                data.routeName != null ? data.routeName : "N/A",
                timeStr,
                data.passengerCount
            });
        }
        
        // Update showing trips label
        lblShowingTrips.setText("Showing " + boardingDataList.size() + " trips");
    }

    private void updateSummaryCards(int routeID, int daysBack, String timeWindow) {
        // Get total boardings from database
        int totalBoardings = ridePersistanceHandler.getTotalBoardings(routeID, daysBack, timeWindow);
        lblTotalBoardingsValue.setText(String.format("%,d", totalBoardings));
        lblTotalBoardingsSubtext.setText("Selected period");
        
        // Get busiest route from database
        Map<String, Integer> busiestRouteData = ridePersistanceHandler.getBusiestRoute(daysBack, timeWindow);
        String busiestRoute = "N/A";
        int maxBoardings = 0;
        
        if (!busiestRouteData.isEmpty()) {
            Map.Entry<String, Integer> entry = busiestRouteData.entrySet().iterator().next();
            busiestRoute = entry.getKey();
            maxBoardings = entry.getValue();
        }
        
        lblBusiestRouteValue.setText(busiestRoute != null ? busiestRoute : "N/A");
        lblBusiestRouteSubtext.setText("Boardings: " + String.format("%,d", maxBoardings));
        
        // Active trips = number of trips in the table
        lblActiveTripsValue.setText(String.valueOf(boardingDataList.size()));
        lblActiveTripsSubtext.setText("Trips shown");
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

 
}

