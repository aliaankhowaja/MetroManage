package com.metromanage.ui;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.metromanage.domain.Route;
import com.metromanage.domain.Station;
import com.metromanage.domain.LoginHandler;
import com.metromanage.model.RoutePersistanceHandler;
import com.metromanage.model.RouteStationPersistanceHandler;
import com.metromanage.model.StationPersistanceHandler;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * ViewSchedule - Admin dashboard for viewing route schedules with modern UI.
 * Features: Analytics cards, route selection, detailed stop information.
 */
public class ViewSchedule extends JFrame {

    // ==================== BACKEND HANDLERS ====================
    private RoutePersistanceHandler routePersistanceHandler;
    private StationPersistanceHandler stationPersistanceHandler;
    private RouteStationPersistanceHandler routeStationPersistanceHandler;

    // ==================== COLOR PLACEHOLDERS ====================
    private static final Color PRIMARY_COLOR = new Color(86, 124, 141);  // Teal accent
    private static final Color SIDEBAR_BACKGROUND = new Color(47, 65, 86);  // Navy
    private static final Color SIDEBAR_ACTIVE = new Color(86, 124, 141);  // Teal for active
    private static final Color MAIN_BACKGROUND = new Color(245, 239, 232);  // Light beige
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(47, 65, 86);  // Navy
    private static final Color TEXT_MUTED = new Color(120, 120, 120);  // Gray
    
    // Supporting colors
    private static final Color SKY = new Color(203, 214, 230);
    private static final Color BEIGE = new Color(245, 239, 232);

    // ==================== FONT PLACEHOLDERS ====================
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
    private JLabel lblTotalRoutesValue;
    private JLabel lblTotalRoutesSubtext;
    
    private JLabel lblActiveBusesValue;
    private JLabel lblActiveBusesSubtext;
    
    private JLabel lblTotalStationsValue;
    private JLabel lblTotalStationsSubtext;
    
    // Route info display
    private JLabel lblSelectedRouteName;
    private JLabel lblRouteDistance;
    private JLabel lblRouteTime;
    private JLabel lblRouteCost;
    
    // Filter components
    private JComboBox<String> cmbRoute;
    private JButton btnRefresh;
    
    // Table components
    private JTable tblSchedule;
    private DefaultTableModel tableModel;
    private JScrollPane scrollSchedule;
    private JLabel lblShowingStops;
    
    // Navigation buttons
    private JPanel navDashboard;
    private JPanel navManageUsers;
    private JPanel navManageFleet;
    private JPanel navSchedule;
    private JPanel navLogout;

    // Data storage
    private ArrayList<Route> routes;
    private Route selectedRoute;

    public ViewSchedule() {
        // Initialize backend handlers
        routePersistanceHandler = new RoutePersistanceHandler();
        stationPersistanceHandler = new StationPersistanceHandler();
        routeStationPersistanceHandler = new RouteStationPersistanceHandler();
        
        initializeUI();
        loadRoutes();
        if (!routes.isEmpty()) {
            cmbRoute.setSelectedIndex(0);
            applyFilters();
        }
    }

    private void initializeUI() {
        setTitle("View Schedule - MetroManage");
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

        navSchedule = createNavItem("View Schedule", true);  // Active
        pnlSidebar.add(navSchedule);
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
        JLabel lblTitle = new JLabel("Route Schedules");
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
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.setOpaque(false);
        filterPanel.add(createFilterBar(), BorderLayout.CENTER);
        topSection.add(filterPanel);
        topSection.add(Box.createVerticalStrut(15));

        // Route info panel
        JPanel routeInfoPanel = createRouteInfoPanel();
        topSection.add(routeInfoPanel);
        topSection.add(Box.createVerticalStrut(20));

        pnlContent.add(topSection, BorderLayout.NORTH);

        // Bottom section: Schedule table
        createScheduleTable();
        pnlContent.add(pnlTableCard, BorderLayout.CENTER);

        rootPanel.add(pnlContent, BorderLayout.CENTER);
    }

    private void createAnalyticsCards() {
        pnlSummaryCards = new JPanel(new GridLayout(1, 3, 20, 0));
        pnlSummaryCards.setOpaque(false);
        pnlSummaryCards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        pnlSummaryCards.setPreferredSize(new Dimension(1000, 140));

        // Card 1: Total Routes
        JPanel card1 = createAnalyticsCard(
            "Total Routes",
            "0",
            "Active routes",
            PRIMARY_COLOR
        );
        pnlSummaryCards.add(card1);

        // Card 2: Active Buses
        JPanel card2 = createAnalyticsCard(
            "Active Buses",
            "0",
            "Currently running",
            new Color(76, 175, 80)
        );
        pnlSummaryCards.add(card2);

        // Card 3: Total Stations
        JPanel card3 = createAnalyticsCard(
            "Total Stations",
            "0",
            "All stops",
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
        if (title.equals("Total Routes")) {
            lblTotalRoutesValue = lblValue;
        } else if (title.equals("Active Buses")) {
            lblActiveBusesValue = lblValue;
        } else if (title.equals("Total Stations")) {
            lblTotalStationsValue = lblValue;
        }
        
        content.add(lblValue);
        content.add(Box.createVerticalStrut(8));

        // Subtext
        JLabel lblSubtext = new JLabel(subtext);
        lblSubtext.setFont(getCustomFont(Font.PLAIN, 12));
        lblSubtext.setForeground(TEXT_MUTED);
        lblSubtext.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Store references to update later
        if (title.equals("Total Routes")) {
            lblTotalRoutesSubtext = lblSubtext;
        } else if (title.equals("Active Buses")) {
            lblActiveBusesSubtext = lblSubtext;
        } else if (title.equals("Total Stations")) {
            lblTotalStationsSubtext = lblSubtext;
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
        JLabel lblRouteFilter = new JLabel("Select Route:");
        lblRouteFilter.setFont(getCustomFont(Font.BOLD, 13));
        lblRouteFilter.setForeground(TEXT_PRIMARY);
        filterBar.add(lblRouteFilter);

        cmbRoute = new JComboBox<>();
        cmbRoute.setFont(getCustomFont(Font.PLAIN, 13));
        cmbRoute.setBackground(Color.WHITE);
        cmbRoute.setForeground(TEXT_PRIMARY);
        cmbRoute.setPreferredSize(new Dimension(200, 35));
        filterBar.add(cmbRoute);

        filterBar.add(Box.createHorizontalStrut(20));

        // Refresh button
        btnRefresh = new JButton("Load Schedule");
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

    private JPanel createRouteInfoPanel() {
        JPanel infoPanel = new RoundedPanel(10);
        infoPanel.setBackground(new Color(240, 248, 255));
        infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 15));
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        infoPanel.setPreferredSize(new Dimension(1000, 70));

        // Route Name
        lblSelectedRouteName = new JLabel("Route: ---");
        lblSelectedRouteName.setFont(getCustomFont(Font.BOLD, 15));
        lblSelectedRouteName.setForeground(TEXT_PRIMARY);
        infoPanel.add(lblSelectedRouteName);

        // Distance
        lblRouteDistance = new JLabel("Distance: ---");
        lblRouteDistance.setFont(getCustomFont(Font.PLAIN, 14));
        lblRouteDistance.setForeground(TEXT_MUTED);
        infoPanel.add(lblRouteDistance);

        // Time
        lblRouteTime = new JLabel("Est. Time: ---");
        lblRouteTime.setFont(getCustomFont(Font.PLAIN, 14));
        lblRouteTime.setForeground(TEXT_MUTED);
        infoPanel.add(lblRouteTime);

        // Cost
        lblRouteCost = new JLabel("Cost: ---");
        lblRouteCost.setFont(getCustomFont(Font.PLAIN, 14));
        lblRouteCost.setForeground(TEXT_MUTED);
        infoPanel.add(lblRouteCost);

        return infoPanel;
    }

    private void createScheduleTable() {
        pnlTableCard = new RoundedPanel(15);
        pnlTableCard.setBackground(CARD_BACKGROUND);
        pnlTableCard.setLayout(new BorderLayout());
        pnlTableCard.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header section with title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel tableTitle = new JLabel("Station Schedule");
        tableTitle.setFont(getCustomFont(Font.BOLD, 20));
        tableTitle.setForeground(TEXT_PRIMARY);
        headerPanel.add(tableTitle, BorderLayout.WEST);

        // Right side: showing stops count
        lblShowingStops = new JLabel("Select a route");
        lblShowingStops.setFont(getCustomFont(Font.PLAIN, 13));
        lblShowingStops.setForeground(TEXT_MUTED);
        headerPanel.add(lblShowingStops, BorderLayout.EAST);

        pnlTableCard.add(headerPanel, BorderLayout.NORTH);

        // Create table
        String[] columns = {"Stop #", "Station Name", "Distance (km)", "Time (min)", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblSchedule = new JTable(tableModel);
        tblSchedule.setFont(getCustomFont(Font.PLAIN, 13));
        tblSchedule.setRowHeight(45);
        tblSchedule.setShowGrid(false);
        tblSchedule.setIntercellSpacing(new Dimension(0, 0));
        tblSchedule.setSelectionBackground(new Color(240, 240, 240));
        tblSchedule.setSelectionForeground(TEXT_PRIMARY);

        // Style table header
        JTableHeader header = tblSchedule.getTableHeader();
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
        for (int i = 0; i < tblSchedule.getColumnCount(); i++) {
            tblSchedule.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Custom renderer for Stop # column (with bold text)
        tblSchedule.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                JLabel label = (JLabel) c;
                label.setFont(getCustomFont(Font.BOLD, 14));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setForeground(PRIMARY_COLOR);
                
                return label;
            }
        });

        // Custom renderer for Station Name column (left-aligned)
        tblSchedule.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                JLabel label = (JLabel) c;
                label.setFont(getCustomFont(Font.BOLD, 13));
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setForeground(TEXT_PRIMARY);
                label.setBorder(new EmptyBorder(0, 15, 0, 0));
                
                return label;
            }
        });

        // Custom renderer for Status column (with colors)
        tblSchedule.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                JLabel label = (JLabel) c;
                label.setFont(getCustomFont(Font.BOLD, 12));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                
                String status = value.toString();
                if (status.equals("Active")) {
                    label.setForeground(new Color(76, 175, 80));  // Green for active
                } else if (status.equals("Inactive")) {
                    label.setForeground(new Color(158, 158, 158));  // Gray for inactive
                } else {
                    label.setForeground(TEXT_PRIMARY);
                }
                
                return label;
            }
        });

        // Set column widths
        tblSchedule.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblSchedule.getColumnModel().getColumn(1).setPreferredWidth(300);
        tblSchedule.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblSchedule.getColumnModel().getColumn(3).setPreferredWidth(150);
        tblSchedule.getColumnModel().getColumn(4).setPreferredWidth(120);

        scrollSchedule = new JScrollPane(tblSchedule);
        scrollSchedule.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollSchedule.getViewport().setBackground(Color.WHITE);
        
        // Apply custom modern scrollbar
        scrollSchedule.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollSchedule.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        scrollSchedule.getVerticalScrollBar().setUnitIncrement(16);

        pnlTableCard.add(scrollSchedule, BorderLayout.CENTER);
    }

    private void loadRoutes() {
        // Load all routes from database
        routes = routePersistanceHandler.getAllRoutes();
        
        // Populate route dropdown
        for (Route route : routes) {
            cmbRoute.addItem(route.getRouteName());
        }

        // Update analytics
        refreshAnalyticsCards();
    }

    private void refreshAnalyticsCards() {
        // Total routes
        lblTotalRoutesValue.setText(String.valueOf(routes.size()));
        int activeRoutes = 0;
        for (Route route : routes) {
            if (route.getActive()) {
                activeRoutes++;
            }
        }
        lblTotalRoutesSubtext.setText(activeRoutes + " active routes");
        
        // Total buses (count from all routes)
        int totalBuses = routePersistanceHandler.getTotalActiveBuses();
        lblActiveBusesValue.setText(String.valueOf(totalBuses));
        lblActiveBusesSubtext.setText("Currently running");
        
        // Total stations
        int totalStations = stationPersistanceHandler.getTotalStations();
        lblTotalStationsValue.setText(String.valueOf(totalStations));
        lblTotalStationsSubtext.setText("All stops");
    }

    private void applyFilters() {
        String selectedRouteName = (String) cmbRoute.getSelectedItem();
        if (selectedRouteName == null || selectedRouteName.isEmpty()) {
            return;
        }
        
        // Find selected route
        selectedRoute = null;
        for (Route route : routes) {
            if (route.getRouteName().equals(selectedRouteName)) {
                selectedRoute = route;
                break;
            }
        }
        
        if (selectedRoute == null) {
            return;
        }
        
        // Update route info
        updateRouteInfo();
        
        // Update table
        updateTable();
    }

    private void updateRouteInfo() {
        if (selectedRoute != null) {
            lblSelectedRouteName.setText("Route: " + selectedRoute.getRouteName());
            lblRouteDistance.setText(String.format("Distance: %.1f km", selectedRoute.getTotalDistance()));
            lblRouteTime.setText(String.format("Est. Time: %d min", selectedRoute.getEstimatedTime()));
            lblRouteCost.setText(String.format("Cost: PKR%.2f", selectedRoute.getCost()));
        }
    }

    private void updateTable() {
        // Clear existing rows
        tableModel.setRowCount(0);
        
        if (selectedRoute == null) {
            lblShowingStops.setText("No route selected");
            return;
        }
        
        // Get stations for the route
        ArrayList<String> stationIDs = routeStationPersistanceHandler.getStationsByRoute(selectedRoute.getRouteID());
        
        if (stationIDs.isEmpty()) {
            lblShowingStops.setText("No stations found");
            return;
        }
        
        // Calculate distance and time per stop (evenly distributed)
        float distancePerStop = selectedRoute.getTotalDistance() / stationIDs.size();
        int timePerStop = selectedRoute.getEstimatedTime() / stationIDs.size();
        
        float cumulativeDistance = 0;
        int cumulativeTime = 0;
        
        // Add station data
        for (int i = 0; i < stationIDs.size(); i++) {
            int stationID = Integer.parseInt(stationIDs.get(i));
            Station station = (Station) stationPersistanceHandler.find(stationID);
            
            if (station != null) {
                if (i > 0) {
                    cumulativeDistance += distancePerStop;
                    cumulativeTime += timePerStop;
                }
                
                String stopNumber = String.valueOf(i + 1);
                String stationName = station.getName();
                String distance = String.format("%.1f", cumulativeDistance);
                String time = String.valueOf(cumulativeTime);
                String status = station.getStatus() != null ? station.getStatus() : "Active";
                
                tableModel.addRow(new Object[]{
                    stopNumber,
                    stationName,
                    distance,
                    time,
                    status
                });
            }
        }
        
        // Update showing stops label
        lblShowingStops.setText("Showing " + stationIDs.size() + " stops");
    }

    private void handleNavigation(String destination) {
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
                    new LoginHandler().adminLogout();
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
