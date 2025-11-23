package com.metromanage.ui;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import com.metromanage.domain.Bus;
import com.metromanage.domain.Route;
import com.metromanage.domain.OperationRegister;
import com.metromanage.model.BusPersistanceHandler;
import com.metromanage.model.RoutePersistanceHandler;

/**
 * BusAllocation screen for MetroManage application.
 * Handles UC03 (Allocate Buses) and UC04 (Reallocate/Deallocate Buses).
 * 
 * Features:
 * - View high-level allocation metrics (total, allocated, unallocated buses)
 * - Allocate buses to routes
 * - Deallocate buses from routes
 * - View allocation details table with delay index
 * - Automatic periodic delay index recalculation (every 5 minutes)
 * - Manual delay index recalculation button
 * 
 * Note: Uses mock data only. No real database or backend integration yet.
 */
public class BusAllocation extends JFrame {
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
    
    // [PLACEHOLDER: for DELAY_INDEX_REFRESH_INTERVAL_MS]
    private static final int DELAY_INDEX_REFRESH_INTERVAL_MS = 5 * 60 * 1000; // 5 minutes
    
    // ==================== FONT PLACEHOLDERS ====================
    // [PLACEHOLDER: for FONT_FAMILY]
    private static final String FONT_FAMILY = "Inter";  // Fallback to Arial if not available
    
    // ==================== UI COMPONENTS ====================
    private JPanel rootPanel;
    private JPanel pnlSidebar;
    private JPanel pnlContent;
    private JPanel pnlSummaryCards;
    private JPanel pnlAllocationControls;
    private JPanel pnlTableCard;
    
    private JLabel lblGreeting;
    private JLabel lblRole;
    
    // Analytics card components
    private JLabel lblTotalBusesValue;
    private JLabel lblAllocatedBusesValue;
    private JLabel lblUnallocatedBusesValue;
    
    // Control components
    private JComboBox<ComboBoxItem> cmbRoute;
    private JComboBox<ComboBoxItem> cmbBus;
    private JButton btnAllocateBus;
    private JButton btnDeallocateBus;
    private JButton btnRecalculateDelayIndex;
    
    // Table components
    private JTable tblAllocation;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JLabel lblLastUpdated;
    
    // Navigation buttons
    private JPanel navDashboard;
    private JPanel navManageFleet;
    private JPanel navBoardingTotals;
    private JPanel navBusAllocation;
    
    // Data structures
    private ArrayList<Bus> buses;
    private ArrayList<Route> routes;
    private Map<Integer, Integer> busRouteAllocations; // busID -> routeID
    private Map<Integer, Double> routeDelayIndexes; // routeID -> delayIndex
    
    // Backend handlers
    private OperationRegister operationRegister;
    private BusPersistanceHandler busPersistanceHandler;
    private RoutePersistanceHandler routePersistanceHandler;
    
    // Timer for automatic delay index recalculation
    private javax.swing.Timer delayIndexTimer;
    
    public BusAllocation() {
        // Initialize backend handlers
        operationRegister = new OperationRegister();
        busPersistanceHandler = new BusPersistanceHandler();
        routePersistanceHandler = new RoutePersistanceHandler();
        
        initializeUI();
        loadRealData();
        startDelayIndexTimer();
    }

    private void initializeUI() {
        setTitle("Bus Allocation - MetroManage");
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
                String initials = "AM";  // Admin Metro
                FontMetrics fm = g2d.getFontMetrics();
                int x = (60 - fm.stringWidth(initials)) / 2;
                int y = ((60 - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(initials, x, y);
            }
        };
        avatarPanel.setPreferredSize(new Dimension(60, 60));
        avatarPanel.setMaximumSize(new Dimension(60, 60));
        avatarPanel.setOpaque(false);
        avatarPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userInfoPanel.add(avatarPanel);
        userInfoPanel.add(Box.createVerticalStrut(15));

        lblGreeting = new JLabel("Admin User");
        lblGreeting.setFont(getCustomFont(Font.BOLD, 18));
        lblGreeting.setForeground(Color.WHITE);
        lblGreeting.setAlignmentX(Component.LEFT_ALIGNMENT);
        userInfoPanel.add(lblGreeting);
        userInfoPanel.add(Box.createVerticalStrut(5));

        lblRole = new JLabel("Administrator");
        lblRole.setFont(getCustomFont(Font.PLAIN, 13));
        lblRole.setForeground(new Color(203, 214, 230));
        lblRole.setAlignmentX(Component.LEFT_ALIGNMENT);
        userInfoPanel.add(lblRole);

        pnlSidebar.add(userInfoPanel);
        pnlSidebar.add(Box.createVerticalStrut(10));

        // Navigation section label
        JLabel navLabel = new JLabel("NAVIGATION");
        navLabel.setFont(getCustomFont(Font.BOLD, 11));
        navLabel.setForeground(new Color(150, 160, 170));
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        navLabel.setBorder(new EmptyBorder(0, 10, 8, 0));
        pnlSidebar.add(navLabel);

        // Navigation items
        navDashboard = createSidebarNavItem("Dashboard", false);
        navManageFleet = createSidebarNavItem("Manage Fleet", false);
        navBoardingTotals = createSidebarNavItem("Boarding Totals", false);
        navBusAllocation = createSidebarNavItem("Bus Allocation", true);  // Active

        pnlSidebar.add(navDashboard);
        pnlSidebar.add(Box.createVerticalStrut(5));
        pnlSidebar.add(navManageFleet);
        pnlSidebar.add(Box.createVerticalStrut(5));
        pnlSidebar.add(navBoardingTotals);
        pnlSidebar.add(Box.createVerticalStrut(5));
        pnlSidebar.add(navBusAllocation);

        pnlSidebar.add(Box.createVerticalGlue());

        rootPanel.add(pnlSidebar, BorderLayout.WEST);
    }

    private JPanel createSidebarNavItem(String text, boolean active) {
        JPanel navItem = new JPanel(new BorderLayout());
        navItem.setOpaque(false);
        navItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        navItem.setPreferredSize(new Dimension(240, 45));
        navItem.setAlignmentX(Component.LEFT_ALIGNMENT);
        navItem.setBorder(new EmptyBorder(0, 10, 0, 10));

        JLabel label = new JLabel(text);
        label.setFont(getCustomFont(active ? Font.BOLD : Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
        label.setBorder(new EmptyBorder(0, 15, 0, 0));
        navItem.add(label, BorderLayout.CENTER);

        if (active) {
            navItem.setOpaque(true);
            navItem.setBackground(SIDEBAR_ACTIVE);
        } else {
            navItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
            navItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    navItem.setOpaque(true);
                    navItem.setBackground(new Color(60, 80, 100));
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

        // Top section wrapper
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);

        // Page title
        JLabel lblTitle = new JLabel("Bus Allocation");
        lblTitle.setFont(getCustomFont(Font.BOLD, 28));
        lblTitle.setForeground(TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        topSection.add(lblTitle);
        topSection.add(Box.createVerticalStrut(25));

        // Analytics cards
        createAnalyticsCards();
        topSection.add(pnlSummaryCards);
        topSection.add(Box.createVerticalStrut(15));

        // Allocation controls
        createAllocationControls();
        topSection.add(pnlAllocationControls);
        topSection.add(Box.createVerticalStrut(15));

        pnlContent.add(topSection, BorderLayout.NORTH);

        // Bottom section: Table
        createAllocationTable();
        pnlContent.add(pnlTableCard, BorderLayout.CENTER);

        rootPanel.add(pnlContent, BorderLayout.CENTER);
    }

    private void createAnalyticsCards() {
        pnlSummaryCards = new JPanel(new GridLayout(1, 3, 20, 0));
        pnlSummaryCards.setOpaque(false);
        pnlSummaryCards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        pnlSummaryCards.setPreferredSize(new Dimension(1000, 140));

        // Card 1: Total Buses
        lblTotalBusesValue = new JLabel();
        JPanel card1 = createAnalyticsCard("Total Buses", "80", "In fleet", PRIMARY_COLOR, lblTotalBusesValue);
        pnlSummaryCards.add(card1);

        // Card 2: Allocated Buses
        lblAllocatedBusesValue = new JLabel();
        JPanel card2 = createAnalyticsCard("Allocated Buses", "60", "Currently assigned", new Color(76, 175, 80), lblAllocatedBusesValue);
        pnlSummaryCards.add(card2);

        // Card 3: Unallocated Buses
        lblUnallocatedBusesValue = new JLabel();
        JPanel card3 = createAnalyticsCard("Unallocated Buses", "20", "Available", new Color(255, 152, 0), lblUnallocatedBusesValue);
        pnlSummaryCards.add(card3);
    }

    private JPanel createAnalyticsCard(String title, String value, String subtext, Color accentColor, JLabel valueLabel) {
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

        // Value - use provided label reference
        valueLabel.setText(value);
        valueLabel.setFont(getCustomFont(Font.BOLD, 36));
        valueLabel.setForeground(TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(valueLabel);
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

    private void createAllocationControls() {
        pnlAllocationControls = new RoundedPanel(15);
        pnlAllocationControls.setBackground(CARD_BACKGROUND);
        pnlAllocationControls.setLayout(new BorderLayout());
        pnlAllocationControls.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Title
        JLabel controlTitle = new JLabel("Allocate / Deallocate Buses");
        controlTitle.setFont(getCustomFont(Font.BOLD, 20));
        controlTitle.setForeground(TEXT_PRIMARY);
        
        // Recalculate button on the right
        btnRecalculateDelayIndex = new JButton("Recalculate Delay Index");
        btnRecalculateDelayIndex.setFont(getCustomFont(Font.BOLD, 13));
        btnRecalculateDelayIndex.setBackground(PRIMARY_COLOR);
        btnRecalculateDelayIndex.setForeground(Color.WHITE);
        btnRecalculateDelayIndex.setFocusPainted(false);
        btnRecalculateDelayIndex.setBorderPainted(false);
        btnRecalculateDelayIndex.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRecalculateDelayIndex.setPreferredSize(new Dimension(200, 40));
        btnRecalculateDelayIndex.addActionListener(e -> handleRecalculateDelayIndex());
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        headerPanel.add(controlTitle, BorderLayout.WEST);
        headerPanel.add(btnRecalculateDelayIndex, BorderLayout.EAST);
        pnlAllocationControls.add(headerPanel, BorderLayout.NORTH);

        // Form panel with better layout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Bus label and combo
        JLabel lblBus = new JLabel("Bus:");
        lblBus.setFont(getCustomFont(Font.PLAIN, 14));
        lblBus.setForeground(TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblBus, gbc);
        
        cmbBus = new JComboBox<ComboBoxItem>();
        cmbBus.setFont(getCustomFont(Font.PLAIN, 14));
        cmbBus.setPreferredSize(new Dimension(350, 40));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(cmbBus, gbc);

        // Row 2: Route label and combo
        JLabel lblRoute = new JLabel("Route:");
        lblRoute.setFont(getCustomFont(Font.PLAIN, 14));
        lblRoute.setForeground(TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblRoute, gbc);
        
        cmbRoute = new JComboBox<ComboBoxItem>();
        cmbRoute.setFont(getCustomFont(Font.PLAIN, 14));
        cmbRoute.setPreferredSize(new Dimension(350, 40));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(cmbRoute, gbc);

        // Row 3: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonPanel.setOpaque(false);

        btnDeallocateBus = new JButton("Deallocate");
        btnDeallocateBus.setFont(getCustomFont(Font.BOLD, 14));
        btnDeallocateBus.setBackground(new Color(244, 67, 54));  // Red
        btnDeallocateBus.setForeground(Color.WHITE);
        btnDeallocateBus.setFocusPainted(false);
        btnDeallocateBus.setBorderPainted(false);
        btnDeallocateBus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDeallocateBus.setPreferredSize(new Dimension(140, 40));
        btnDeallocateBus.addActionListener(e -> handleDeallocateBus());
        buttonPanel.add(btnDeallocateBus);

        btnAllocateBus = new JButton("Allocate");
        btnAllocateBus.setFont(getCustomFont(Font.BOLD, 14));
        btnAllocateBus.setBackground(new Color(76, 175, 80));  // Green
        btnAllocateBus.setForeground(Color.WHITE);
        btnAllocateBus.setFocusPainted(false);
        btnAllocateBus.setBorderPainted(false);
        btnAllocateBus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAllocateBus.setPreferredSize(new Dimension(140, 40));
        btnAllocateBus.addActionListener(e -> handleAllocateBus());
        buttonPanel.add(btnAllocateBus);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 5, 5, 5);
        formPanel.add(buttonPanel, gbc);

        pnlAllocationControls.add(formPanel, BorderLayout.CENTER);
    }

    private void createAllocationTable() {
        pnlTableCard = new RoundedPanel(15);
        pnlTableCard.setBackground(CARD_BACKGROUND);
        pnlTableCard.setLayout(new BorderLayout());
        pnlTableCard.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header section
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel tableTitle = new JLabel("Bus Allocation Details");
        tableTitle.setFont(getCustomFont(Font.BOLD, 20));
        tableTitle.setForeground(TEXT_PRIMARY);
        headerPanel.add(tableTitle, BorderLayout.WEST);

        lblLastUpdated = new JLabel("Updated: --:--");
        lblLastUpdated.setFont(getCustomFont(Font.PLAIN, 12));
        lblLastUpdated.setForeground(TEXT_MUTED);
        headerPanel.add(lblLastUpdated, BorderLayout.EAST);

        pnlTableCard.add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Bus ID", "Route Number", "Total Buses on Route", "Delay Index"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblAllocation = new JTable(tableModel);
        tblAllocation.setFont(getCustomFont(Font.PLAIN, 13));
        tblAllocation.setRowHeight(35);
        tblAllocation.setSelectionBackground(SKY);
        tblAllocation.setSelectionForeground(TEXT_PRIMARY);
        tblAllocation.setGridColor(new Color(230, 230, 230));
        tblAllocation.setShowVerticalLines(true);
        tblAllocation.setShowHorizontalLines(true);

        // Table header styling
        JTableHeader header = tblAllocation.getTableHeader();
        header.setFont(getCustomFont(Font.BOLD, 13));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

        scrollPane = new JScrollPane(tblAllocation);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        
        // Apply custom modern scrollbar
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        pnlTableCard.add(scrollPane, BorderLayout.CENTER);
    }

    private void loadRealData() {
        buses = new ArrayList<>();
        routes = new ArrayList<>();
        busRouteAllocations = new HashMap<>();
        routeDelayIndexes = new HashMap<>();

        // Load buses from database
        buses = busPersistanceHandler.getAllBuses();
        if (buses == null) {
            buses = new ArrayList<>();
        }

        // Load routes from database
        routes = routePersistanceHandler.getAllRoutes();
        if (routes == null) {
            routes = new ArrayList<>();
        }

        // Initialize allocations map from database
        for (Bus bus : buses) {
            int routeID = bus.getRouteID();
            if (routeID != 0) {
                busRouteAllocations.put(bus.getBusID(), routeID);
            }
        }

        // Initialize delay indexes for all routes
        for (Route route : routes) {
            routeDelayIndexes.put(route.getRouteID(), 0.0);
        }

        // Populate combo boxes with formatted display strings
        cmbRoute.removeAllItems();
        cmbRoute.addItem(new ComboBoxItem(0, "-- Select Route --"));
        for (Route route : routes) {
            String displayText = String.format("R%d - %s", route.getRouteID(), route.getRouteName());
            cmbRoute.addItem(new ComboBoxItem(route.getRouteID(), displayText));
        }

        cmbBus.removeAllItems();
        cmbBus.addItem(new ComboBoxItem(0, "-- Select Bus --"));
        for (Bus bus : buses) {
            String displayText = String.format("B%03d - %s (%s)", bus.getBusID(), bus.getPlateNumber(), bus.getStatus());
            cmbBus.addItem(new ComboBoxItem(bus.getBusID(), displayText));
        }

        // Initial calculations
        recalculateDelayIndex();
        refreshSummaryCards();
        refreshAllocationTable();
    }

    private void startDelayIndexTimer() {
        delayIndexTimer = new javax.swing.Timer(DELAY_INDEX_REFRESH_INTERVAL_MS, e -> {
            recalculateDelayIndex();
            refreshSummaryCards();
            refreshAllocationTable();
            System.out.println("Automatic delay index recalculation at " + getCurrentTimestamp());
        });
        delayIndexTimer.start();
    }

    private void recalculateDelayIndex() {
        Random random = new Random();
        
        for (Route route : routes) {
            int busesOnRoute = getBusCountForRoute(route.getRouteID());
            
            // Delay index calculation based on buses on route
            double baseDelay = 2.0 + (random.nextDouble() * 3.0);
            double busEfficiencyFactor = busesOnRoute * 0.1;
            double delayIndex = Math.max(0.5, baseDelay - busEfficiencyFactor + (random.nextDouble() * 0.5));
            delayIndex = Math.round(delayIndex * 10.0) / 10.0;
            
            routeDelayIndexes.put(route.getRouteID(), delayIndex);
        }
        
        updateLastUpdatedLabel();
    }

    private int getBusCountForRoute(int routeID) {
        int count = 0;
        for (Integer allocatedRouteID : busRouteAllocations.values()) {
            if (allocatedRouteID != null && allocatedRouteID == routeID) {
                count++;
            }
        }
        return count;
    }

    private void refreshSummaryCards() {
        int totalBuses = buses.size();
        int allocatedBuses = busRouteAllocations.size();
        int unallocatedBuses = totalBuses - allocatedBuses;
        
        lblTotalBusesValue.setText(String.valueOf(totalBuses));
        lblAllocatedBusesValue.setText(String.valueOf(allocatedBuses));
        lblUnallocatedBusesValue.setText(String.valueOf(unallocatedBuses));
    }

    private void refreshAllocationTable() {
        tableModel.setRowCount(0);
        
        Map<Integer, List<Integer>> routeToBuses = new HashMap<>();
        
        for (Map.Entry<Integer, Integer> entry : busRouteAllocations.entrySet()) {
            int busID = entry.getKey();
            int routeID = entry.getValue();
            
            routeToBuses.putIfAbsent(routeID, new ArrayList<>());
            routeToBuses.get(routeID).add(busID);
        }
        
        for (Map.Entry<Integer, List<Integer>> entry : routeToBuses.entrySet()) {
            int routeID = entry.getKey();
            List<Integer> busesOnRoute = entry.getValue();
            int totalBusesOnRoute = busesOnRoute.size();
            double delayIndex = routeDelayIndexes.getOrDefault(routeID, 0.0);
            
            // Get route name for display
            String routeDisplay = "";
            for (Route route : routes) {
                if (route.getRouteID() == routeID) {
                    routeDisplay = String.format("R%d - %s", route.getRouteID(), route.getRouteName());
                    break;
                }
            }
            
            for (int busID : busesOnRoute) {
                // Get bus plate number for display
                String busDisplay = "";
                for (Bus bus : buses) {
                    if (bus.getBusID() == busID) {
                        busDisplay = String.format("B%03d - %s", bus.getBusID(), bus.getPlateNumber());
                        break;
                    }
                }
                tableModel.addRow(new Object[]{busDisplay, routeDisplay, totalBusesOnRoute, delayIndex});
            }
        }
        
        updateLastUpdatedLabel();
    }

    private void updateLastUpdatedLabel() {
        lblLastUpdated.setText("Updated: " + getCurrentTimestamp());
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    private void handleRecalculateDelayIndex() {
        recalculateDelayIndex();
        refreshSummaryCards();
        refreshAllocationTable();
        JOptionPane.showMessageDialog(this, 
            "Delay index recalculated successfully!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleAllocateBus() {
        Object selectedRouteObj = cmbRoute.getSelectedItem();
        Object selectedBusObj = cmbBus.getSelectedItem();
        
        if (!(selectedRouteObj instanceof ComboBoxItem) || ((ComboBoxItem) selectedRouteObj).getId() == 0) {
            JOptionPane.showMessageDialog(this, "Please select a route.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!(selectedBusObj instanceof ComboBoxItem) || ((ComboBoxItem) selectedBusObj).getId() == 0) {
            JOptionPane.showMessageDialog(this, "Please select a bus.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int busID = ((ComboBoxItem) selectedBusObj).getId();
        int routeID = ((ComboBoxItem) selectedRouteObj).getId();
        
        // Get route name for display
        String routeName = "";
        for (Route route : routes) {
            if (route.getRouteID() == routeID) {
                routeName = route.getRouteName();
                break;
            }
        }
        
        // Get bus plate number for display
        String busPlate = "";
        for (Bus bus : buses) {
            if (bus.getBusID() == busID) {
                busPlate = bus.getPlateNumber();
                break;
            }
        }
        
        if (busRouteAllocations.containsKey(busID)) {
            int currentRouteID = busRouteAllocations.get(busID);
            String currentRouteName = "";
            for (Route route : routes) {
                if (route.getRouteID() == currentRouteID) {
                    currentRouteName = route.getRouteName();
                    break;
                }
            }
            int response = JOptionPane.showConfirmDialog(this,
                String.format("Bus %s is already allocated to %s. Reallocate to %s?", busPlate, currentRouteName, routeName),
                "Reallocate Bus", JOptionPane.YES_NO_OPTION);
            
            if (response != JOptionPane.YES_OPTION) return;
        }
        
        // Allocate bus to route using backend
        operationRegister.allocateBusToRoute(busID, routeID);
        
        // Update local data structures
        busRouteAllocations.put(busID, routeID);
        
        // Reload buses to get updated status
        buses = busPersistanceHandler.getAllBuses();
        if (buses == null) {
            buses = new ArrayList<>();
        }
        
        refreshSummaryCards();
        refreshAllocationTable();
        recalculateDelayIndex();
        
        JOptionPane.showMessageDialog(this, 
            String.format("Bus %s successfully allocated to %s!", busPlate, routeName), 
            "Success", JOptionPane.INFORMATION_MESSAGE);
        
        cmbRoute.setSelectedIndex(0);
        cmbBus.setSelectedIndex(0);
    }

    private void handleDeallocateBus() {
        Object selectedBusObj = cmbBus.getSelectedItem();
        
        if (!(selectedBusObj instanceof ComboBoxItem) || ((ComboBoxItem) selectedBusObj).getId() == 0) {
            JOptionPane.showMessageDialog(this, "Please select a bus.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int busID = ((ComboBoxItem) selectedBusObj).getId();
        
        if (!busRouteAllocations.containsKey(busID)) {
            // Get bus plate number for display
            String busPlate = "";
            for (Bus bus : buses) {
                if (bus.getBusID() == busID) {
                    busPlate = bus.getPlateNumber();
                    break;
                }
            }
            JOptionPane.showMessageDialog(this, 
                String.format("Bus %s is not currently allocated to any route.", busPlate), 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int currentRouteID = busRouteAllocations.get(busID);
        String currentRouteName = "";
        for (Route route : routes) {
            if (route.getRouteID() == currentRouteID) {
                currentRouteName = route.getRouteName();
                break;
            }
        }
        
        // Get bus plate number for display
        String busPlate = "";
        for (Bus bus : buses) {
            if (bus.getBusID() == busID) {
                busPlate = bus.getPlateNumber();
                break;
            }
        }
        
        int response = JOptionPane.showConfirmDialog(this,
            String.format("Deallocate bus %s from %s?", busPlate, currentRouteName),
            "Confirm Deallocation", JOptionPane.YES_NO_OPTION);
        
        if (response != JOptionPane.YES_OPTION) return;
        
        // Deallocate bus by setting routeID to 0
        operationRegister.allocateBusToRoute(busID, 0);
        
        // Update local data structures
        busRouteAllocations.remove(busID);
        
        // Reload buses to get updated status
        buses = busPersistanceHandler.getAllBuses();
        if (buses == null) {
            buses = new ArrayList<>();
        }
        
        refreshSummaryCards();
        refreshAllocationTable();
        recalculateDelayIndex();
        
        JOptionPane.showMessageDialog(this, 
            String.format("Bus %s successfully deallocated from %s!", busPlate, currentRouteName), 
            "Success", JOptionPane.INFORMATION_MESSAGE);
        
        cmbRoute.setSelectedIndex(0);
        cmbBus.setSelectedIndex(0);
    }

    private void handleNavigation(String destination) {
        SwingUtilities.invokeLater(() -> {
            switch (destination) {
                case "Dashboard":
                    new DashBoard();
                    dispose();
                    break;
                case "Manage Fleet":
                    new ManageFleet();
                    dispose();
                    break;
                case "Boarding Totals":
                    new ViewBoardingTotals();
                    dispose();
                    break;
            }
        });
    }

    private Font getCustomFont(int style, float size) {
        try {
            return new Font(FONT_FAMILY, style, (int)size);
        } catch (Exception e) {
            return new Font("Arial", style, (int)size);
        }
    }

    // ==================== INNER CLASSES ====================

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
            
            g2d.setColor(new Color(0, 0, 0, 20));
            g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, cornerRadius, cornerRadius);
            
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


    // Helper class for combo box items
    private static class ComboBoxItem {
        private int id;
        private String displayText;
        
        public ComboBoxItem(int id, String displayText) {
            this.id = id;
            this.displayText = displayText;
        }
        
        public int getId() {
            return id;
        }
        
        @Override
        public String toString() {
            return displayText;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new BusAllocation();
        });
    }
}
