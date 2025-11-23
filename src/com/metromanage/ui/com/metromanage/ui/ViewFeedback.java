package com.metromanage.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Admin feedback dashboard using in-memory mock data.
 */
public class ViewFeedback extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(86, 124, 141);
    private static final Color SIDEBAR_BACKGROUND = new Color(47, 65, 86);
    private static final Color SIDEBAR_ACTIVE = new Color(86, 124, 141);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(47, 65, 86);
    private static final Color TEXT_MUTED = new Color(120, 120, 120);
    private static final Color SKY = new Color(203, 214, 230);
    private static final Color BEIGE = new Color(245, 239, 232);
    private static final String FONT_FAMILY = "Inter";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd • HH:mm");

    private JLabel lblOverallRatingValue;
    private JLabel lblTotalFeedbackValue;
    private JLabel lblTotalFeedbackSubtext;
    private JLabel lblBadRidesValue;
    private JLabel lblTopIssueValue;
    private JLabel lblTopIssueSubtext;

    private JComboBox<String> cmbRoute;
    private JComboBox<String> cmbDateRange;
    private JComboBox<String> cmbMinRating;
    private JComboBox<String> cmbLabelFilter;

    private JTable tblRides;
    private DefaultTableModel rideTableModel;
    private JLabel lblRideTableSummary;

    private JTable tblFeedbackEntries;
    private DefaultTableModel feedbackTableModel;
    private JLabel lblFeedbackSummary;
    private JTextArea txtFullComment;

    private JLabel lblRideHeaderTitle;
    private JLabel lblRideMeta;
    private JLabel lblRideLabelCounts;

    private JCheckBox chkComplaints;
    private JCheckBox chkSuggestions;
    private JCheckBox chkCompliments;
    private JCheckBox chkOther;

    private final List<FeedbackEntry> allFeedbackEntries = new ArrayList<>();
    private List<FeedbackEntry> filteredEntries = new ArrayList<>();
    private List<RideAggregate> currentRideAggregates = new ArrayList<>();
    private List<FeedbackEntry> currentDetailEntries = new ArrayList<>();
    private String selectedRideId;

    public ViewFeedback() {
        seedMockData();
        initializeUI();
        populateRouteFilter();
        applyFilters();
    }

    private void initializeUI() {
        setTitle("View Feedback - MetroManage");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1450, 900);
        setLocationRelativeTo(null);

        JPanel rootPanel = new GradientPanel();
        rootPanel.setLayout(new BorderLayout(0, 0));
        setContentPane(rootPanel);

        rootPanel.add(createSidebar(), BorderLayout.WEST);
        rootPanel.add(createContentArea(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createSidebar() {
        JPanel pnlSidebar = new JPanel();
        pnlSidebar.setLayout(new BoxLayout(pnlSidebar, BoxLayout.Y_AXIS));
        pnlSidebar.setBackground(SIDEBAR_BACKGROUND);
        pnlSidebar.setPreferredSize(new Dimension(280, 0));
        pnlSidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setOpaque(false);
        userInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userInfoPanel.setBorder(new EmptyBorder(0, 10, 30, 10));

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
        avatarPanel.setOpaque(false);
        userInfoPanel.add(avatarPanel);
        userInfoPanel.add(Box.createVerticalStrut(15));

        JLabel lblGreeting = new JLabel("Hello, AdminUser");
        lblGreeting.setFont(getCustomFont(Font.BOLD, 18));
        lblGreeting.setForeground(Color.WHITE);
        userInfoPanel.add(lblGreeting);
        userInfoPanel.add(Box.createVerticalStrut(5));

        JLabel lblRole = new JLabel("Admin");
        lblRole.setFont(getCustomFont(Font.PLAIN, 13));
        lblRole.setForeground(SKY);
        userInfoPanel.add(lblRole);

        pnlSidebar.add(userInfoPanel);
        pnlSidebar.add(Box.createVerticalStrut(20));

        JLabel navLabel = new JLabel("QUICK ACCESS");
        navLabel.setFont(getCustomFont(Font.BOLD, 11));
        navLabel.setForeground(SKY);
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        navLabel.setBorder(new EmptyBorder(0, 12, 12, 0));
        pnlSidebar.add(navLabel);

        pnlSidebar.add(createQuickAccessButton("Dashboard", false, () -> handleNavigation("Dashboard")));
        pnlSidebar.add(Box.createVerticalStrut(8));
        pnlSidebar.add(createQuickAccessButton("Manage Fleet", false, () -> handleNavigation("Manage Fleet")));
        pnlSidebar.add(Box.createVerticalStrut(8));
        pnlSidebar.add(createQuickAccessButton("Boarding Totals", false, () -> handleNavigation("Boarding Totals")));
        pnlSidebar.add(Box.createVerticalStrut(8));
        pnlSidebar.add(createQuickAccessButton("View Feedback", true, null));
        pnlSidebar.add(Box.createVerticalStrut(8));
        pnlSidebar.add(createQuickAccessButton("Logout", false, () -> handleNavigation("Logout")));
        pnlSidebar.add(Box.createVerticalGlue());

        return pnlSidebar;
    }

    private JButton createQuickAccessButton(String text, boolean active, Runnable action) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.setCursor(active ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(getCustomFont(active ? Font.BOLD : Font.PLAIN, 13));
        btn.setForeground(active ? Color.WHITE : SKY);
        btn.setBackground(active ? SIDEBAR_ACTIVE : SIDEBAR_BACKGROUND);
        btn.setBorder(new EmptyBorder(14, 18, 14, 18));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);

        if (!active && action != null) {
            btn.addActionListener(e -> action.run());
        }

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!active) {
                    btn.setBackground(new Color(65, 85, 110));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!active) {
                    btn.setBackground(SIDEBAR_BACKGROUND);
                }
            }
        });

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(btn.getBackground());
                g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 14, 14);
                if (active) {
                    g2d.setColor(Color.WHITE);
                    g2d.fillRoundRect(0, 0, 6, c.getHeight(), 12, 12);
                }
                g2d.dispose();
                super.paint(g, c);
            }
        });
        return btn;
    }

    private JPanel createContentArea() {
        JPanel pnlContent = new JPanel(new BorderLayout());
        pnlContent.setOpaque(false);
        pnlContent.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);

        JLabel lblTitle = new JLabel("View Feedback");
        lblTitle.setFont(getCustomFont(Font.BOLD, 30));
        lblTitle.setForeground(TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        topSection.add(lblTitle);
        topSection.add(Box.createVerticalStrut(25));

        JPanel pnlSummaryCards = new JPanel(new GridLayout(1, 4, 20, 0));
        pnlSummaryCards.setOpaque(false);
        pnlSummaryCards.add(createSummaryCard(
                "Overall Rating",
                "4.2 / 5",
                "Average across selected rides",
                PRIMARY_COLOR,
                label -> lblOverallRatingValue = label,
                null
        ));
        pnlSummaryCards.add(createSummaryCard(
                "Total Feedback",
                "0",
                "Complaints: 0, Suggestions: 0",
                new Color(76, 175, 80),
                label -> lblTotalFeedbackValue = label,
                sub -> lblTotalFeedbackSubtext = sub
        ));
        pnlSummaryCards.add(createSummaryCard(
                "Bad Rides",
                "0",
                "Rating < 3.0",
                new Color(255, 152, 0),
                label -> lblBadRidesValue = label,
                null
        ));
        pnlSummaryCards.add(createSummaryCard(
                "Top Issue",
                "Overcrowding",
                "Most frequent complaint",
                new Color(220, 120, 120),
                label -> lblTopIssueValue = label,
                sub -> lblTopIssueSubtext = sub
        ));
        topSection.add(pnlSummaryCards);
        topSection.add(Box.createVerticalStrut(20));

        topSection.add(createFilterBar());
        topSection.add(Box.createVerticalStrut(20));

        pnlContent.add(topSection, BorderLayout.NORTH);
        pnlContent.add(createRideOverviewPanel(), BorderLayout.CENTER);
        pnlContent.add(createRideDetailsPanel(), BorderLayout.SOUTH);
        return pnlContent;
    }

    private JPanel createSummaryCard(String title, String value, String subtext, Color accentColor,
                                     Consumer<JLabel> valueConsumer, Consumer<JLabel> subtextConsumer) {
        JPanel card = new RoundedPanel(18);
        card.setBackground(CARD_BACKGROUND);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(getCustomFont(Font.PLAIN, 14));
        lblTitle.setForeground(TEXT_MUTED);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblTitle);
        content.add(Box.createVerticalStrut(10));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(getCustomFont(Font.BOLD, 32));
        lblValue.setForeground(TEXT_PRIMARY);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblValue);
        content.add(Box.createVerticalStrut(8));

        JLabel lblSubtext = new JLabel(subtext);
        lblSubtext.setFont(getCustomFont(Font.PLAIN, 12));
        lblSubtext.setForeground(TEXT_MUTED);
        lblSubtext.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblSubtext);

        if (valueConsumer != null) {
            valueConsumer.accept(lblValue);
        }
        if (subtextConsumer != null) {
            subtextConsumer.accept(lblSubtext);
        }

        JPanel indicator = new JPanel();
        indicator.setBackground(accentColor);
        indicator.setPreferredSize(new Dimension(4, 0));
        card.add(indicator, BorderLayout.WEST);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 10, 0, 0));
        wrapper.add(content, BorderLayout.CENTER);
        card.add(wrapper, BorderLayout.CENTER);
        return card;
    }

    private JPanel createFilterBar() {
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterBar.setOpaque(false);

        JLabel lblRouteFilter = new JLabel("Route:");
        lblRouteFilter.setFont(getCustomFont(Font.BOLD, 13));
        lblRouteFilter.setForeground(TEXT_PRIMARY);
        filterBar.add(lblRouteFilter);

        cmbRoute = new JComboBox<>();
        cmbRoute.addItem("All Routes");
        cmbRoute.setPreferredSize(new Dimension(140, 36));
        cmbRoute.setFont(getCustomFont(Font.PLAIN, 13));
        filterBar.add(cmbRoute);

        JLabel lblDateRange = new JLabel("Date Range:");
        lblDateRange.setFont(getCustomFont(Font.BOLD, 13));
        lblDateRange.setForeground(TEXT_PRIMARY);
        filterBar.add(lblDateRange);

        cmbDateRange = new JComboBox<>(new String[]{"All Time", "Today", "Last 7 Days", "Last 30 Days"});
        cmbDateRange.setPreferredSize(new Dimension(130, 36));
        cmbDateRange.setFont(getCustomFont(Font.PLAIN, 13));
        filterBar.add(cmbDateRange);

        JLabel lblMinRating = new JLabel("Min Rating:");
        lblMinRating.setFont(getCustomFont(Font.BOLD, 13));
        lblMinRating.setForeground(TEXT_PRIMARY);
        filterBar.add(lblMinRating);

        cmbMinRating = new JComboBox<>(new String[]{"All", "1+", "2+", "3+", "4+"});
        cmbMinRating.setPreferredSize(new Dimension(110, 36));
        cmbMinRating.setFont(getCustomFont(Font.PLAIN, 13));
        filterBar.add(cmbMinRating);

        JLabel lblLabelFilter = new JLabel("Label:");
        lblLabelFilter.setFont(getCustomFont(Font.BOLD, 13));
        lblLabelFilter.setForeground(TEXT_PRIMARY);
        filterBar.add(lblLabelFilter);

        cmbLabelFilter = new JComboBox<>(new String[]{"All", "Complaint", "Suggestion", "Compliment", "Other"});
        cmbLabelFilter.setPreferredSize(new Dimension(150, 36));
        cmbLabelFilter.setFont(getCustomFont(Font.PLAIN, 13));
        filterBar.add(cmbLabelFilter);

        JButton btnRefresh = new JButton("Apply Filters");
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

    private JPanel createRideOverviewPanel() {
        JPanel pnlRideOverview = new RoundedPanel(18);
        pnlRideOverview.setBackground(CARD_BACKGROUND);
        pnlRideOverview.setLayout(new BorderLayout());
        pnlRideOverview.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel lblHeader = new JLabel("Ride Overview");
        lblHeader.setFont(getCustomFont(Font.BOLD, 20));
        lblHeader.setForeground(TEXT_PRIMARY);
        header.add(lblHeader, BorderLayout.WEST);

        lblRideTableSummary = new JLabel("Showing 0 rides");
        lblRideTableSummary.setFont(getCustomFont(Font.PLAIN, 13));
        lblRideTableSummary.setForeground(TEXT_MUTED);
        header.add(lblRideTableSummary, BorderLayout.EAST);
        pnlRideOverview.add(header, BorderLayout.NORTH);

        String[] columns = {"Ride ID", "Route", "Bus", "Departure", "Avg Rating", "Feedback", "Labels", "Status"};
        rideTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblRides = new JTable(rideTableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                int modelRow = convertRowIndexToModel(row);
                boolean badRide = false;
                Object value = rideTableModel.getValueAt(modelRow, 4);
                if (value != null) {
                    try {
                        badRide = Double.parseDouble(value.toString()) < 3.0;
                    } catch (NumberFormatException ignored) {
                        badRide = false;
                    }
                }
                if (!isRowSelected(row)) {
                    c.setBackground(badRide ? new Color(255, 235, 230) : Color.WHITE);
                }
                return c;
            }
        };
        tblRides.setRowHeight(48);
        tblRides.setFont(getCustomFont(Font.PLAIN, 13));
        tblRides.setShowGrid(false);
        tblRides.setIntercellSpacing(new Dimension(0, 0));
        tblRides.setSelectionBackground(new Color(230, 240, 250));
        tblRides.setSelectionForeground(TEXT_PRIMARY);
        tblRides.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    updateRideDetailsFromSelection();
                }
            }
        });

        JTableHeader tableHeader = tblRides.getTableHeader();
        tableHeader.setFont(getCustomFont(Font.BOLD, 13));
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 42));
        tableHeader.setBackground(new Color(250, 250, 250));
        tableHeader.setForeground(TEXT_PRIMARY);
        ((DefaultTableCellRenderer) tableHeader.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tblRides.getColumnCount(); i++) {
            tblRides.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tblRides);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());

        pnlRideOverview.add(scrollPane, BorderLayout.CENTER);
        return pnlRideOverview;
    }

    private JPanel createRideDetailsPanel() {
        JPanel pnlRideDetails = new RoundedPanel(18);
        pnlRideDetails.setBackground(CARD_BACKGROUND);
        pnlRideDetails.setBorder(new EmptyBorder(25, 25, 25, 25));
        pnlRideDetails.setLayout(new BorderLayout(0, 20));
        pnlRideDetails.setPreferredSize(new Dimension(0, 320));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        lblRideHeaderTitle = new JLabel("Select a ride to view details");
        lblRideHeaderTitle.setFont(getCustomFont(Font.BOLD, 18));
        lblRideHeaderTitle.setForeground(TEXT_PRIMARY);
        header.add(lblRideHeaderTitle, BorderLayout.WEST);

        lblRideMeta = new JLabel("");
        lblRideMeta.setFont(getCustomFont(Font.PLAIN, 13));
        lblRideMeta.setForeground(TEXT_MUTED);
        header.add(lblRideMeta, BorderLayout.CENTER);

        lblRideLabelCounts = new JLabel("Complaints: 0, Suggestions: 0, Compliments: 0, Other: 0");
        lblRideLabelCounts.setFont(getCustomFont(Font.PLAIN, 13));
        lblRideLabelCounts.setForeground(TEXT_MUTED);
        header.add(lblRideLabelCounts, BorderLayout.SOUTH);
        pnlRideDetails.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(20, 0));
        body.setOpaque(false);

        JPanel leftColumn = new JPanel(new BorderLayout(0, 10));
        leftColumn.setOpaque(false);

        JPanel labelFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        labelFilters.setOpaque(false);
        chkComplaints = createDetailFilterCheckBox("Complaints");
        chkSuggestions = createDetailFilterCheckBox("Suggestions");
        chkCompliments = createDetailFilterCheckBox("Compliments");
        chkOther = createDetailFilterCheckBox("Other");
        labelFilters.add(chkComplaints);
        labelFilters.add(chkSuggestions);
        labelFilters.add(chkCompliments);
        labelFilters.add(chkOther);
        leftColumn.add(labelFilters, BorderLayout.NORTH);

        String[] feedbackColumns = {"Passenger", "Rating", "Labels", "Snippet", "Time"};
        feedbackTableModel = new DefaultTableModel(feedbackColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblFeedbackEntries = new JTable(feedbackTableModel);
        tblFeedbackEntries.setFont(getCustomFont(Font.PLAIN, 12));
        tblFeedbackEntries.setRowHeight(36);
        tblFeedbackEntries.setShowGrid(false);
        tblFeedbackEntries.setSelectionBackground(new Color(235, 244, 255));
        tblFeedbackEntries.setSelectionForeground(TEXT_PRIMARY);
        tblFeedbackEntries.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateFullCommentFromSelection();
            }
        });

        JTableHeader feedbackHeader = tblFeedbackEntries.getTableHeader();
        feedbackHeader.setFont(getCustomFont(Font.BOLD, 12));
        feedbackHeader.setPreferredSize(new Dimension(feedbackHeader.getWidth(), 36));
        ((DefaultTableCellRenderer) feedbackHeader.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer feedbackCenter = new DefaultTableCellRenderer();
        feedbackCenter.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tblFeedbackEntries.getColumnCount(); i++) {
            if (i != 3) {
                tblFeedbackEntries.getColumnModel().getColumn(i).setCellRenderer(feedbackCenter);
            }
        }
        tblFeedbackEntries.getColumnModel().getColumn(0).setPreferredWidth(110);
        tblFeedbackEntries.getColumnModel().getColumn(3).setPreferredWidth(260);

        JScrollPane feedbackScroll = new JScrollPane(tblFeedbackEntries);
        feedbackScroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        feedbackScroll.getViewport().setBackground(Color.WHITE);
        feedbackScroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        leftColumn.add(feedbackScroll, BorderLayout.CENTER);

        lblFeedbackSummary = new JLabel("0 feedback entries");
        lblFeedbackSummary.setFont(getCustomFont(Font.PLAIN, 12));
        lblFeedbackSummary.setForeground(TEXT_MUTED);
        leftColumn.add(lblFeedbackSummary, BorderLayout.SOUTH);

        body.add(leftColumn, BorderLayout.CENTER);

        JPanel rightColumn = new JPanel(new BorderLayout());
        rightColumn.setOpaque(false);
        JLabel lblFullComment = new JLabel("Full Comment");
        lblFullComment.setFont(getCustomFont(Font.BOLD, 14));
        lblFullComment.setForeground(TEXT_PRIMARY);
        rightColumn.add(lblFullComment, BorderLayout.NORTH);

        txtFullComment = new JTextArea();
        txtFullComment.setFont(getCustomFont(Font.PLAIN, 13));
        txtFullComment.setLineWrap(true);
        txtFullComment.setWrapStyleWord(true);
        txtFullComment.setEditable(false);
        txtFullComment.setBackground(new Color(252, 252, 252));
        txtFullComment.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        JScrollPane commentScroll = new JScrollPane(txtFullComment);
        commentScroll.setBorder(BorderFactory.createEmptyBorder());
        commentScroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        rightColumn.add(commentScroll, BorderLayout.CENTER);

        body.add(rightColumn, BorderLayout.EAST);
        pnlRideDetails.add(body, BorderLayout.CENTER);
        return pnlRideDetails;
    }

    private JCheckBox createDetailFilterCheckBox(String text) {
        JCheckBox box = new JCheckBox(text, true);
        box.setFont(getCustomFont(Font.PLAIN, 12));
        box.setForeground(TEXT_PRIMARY);
        box.setOpaque(false);
        box.addItemListener(this::handleDetailFilterChange);
        return box;
    }

    private void populateRouteFilter() {
        Set<String> routes = new TreeSet<>();
        for (FeedbackEntry entry : allFeedbackEntries) {
            routes.add(entry.routeId);
        }
        for (String route : routes) {
            cmbRoute.addItem(route);
        }
    }

    private void applyFilters() {
        filteredEntries = new ArrayList<>(allFeedbackEntries);

        String routeFilter = (String) cmbRoute.getSelectedItem();
        if (routeFilter != null && !routeFilter.equals("All Routes")) {
            filteredEntries = filteredEntries.stream()
                    .filter(entry -> routeFilter.equals(entry.routeId))
                    .collect(Collectors.toList());
        }

        String dateRange = (String) cmbDateRange.getSelectedItem();
        if (dateRange != null && !dateRange.equals("All Time")) {
            filteredEntries = filteredEntries.stream()
                    .filter(entry -> withinRange(entry.timestamp, dateRange))
                    .collect(Collectors.toList());
        }

        String minRatingStr = (String) cmbMinRating.getSelectedItem();
        int minRating = parseMinRating(minRatingStr);
        if (minRating > 0) {
            filteredEntries = filteredEntries.stream()
                    .filter(entry -> entry.rating >= minRating)
                    .collect(Collectors.toList());
        }

        String labelFilter = (String) cmbLabelFilter.getSelectedItem();
        if (labelFilter != null && !labelFilter.equals("All")) {
            filteredEntries = filteredEntries.stream()
                    .filter(entry -> entry.labels.contains(labelFilter))
                    .collect(Collectors.toList());
        }

        currentRideAggregates = buildRideAggregates(filteredEntries);
        updateRideTable();
        updateSummaryCards();

        if (!currentRideAggregates.isEmpty()) {
            tblRides.setRowSelectionInterval(0, 0);
        } else {
            clearRideDetails();
        }
    }

    private boolean withinRange(LocalDateTime timestamp, String dateRange) {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = timestamp.toLocalDate();
        switch (dateRange) {
            case "Today":
                return targetDate.isEqual(today);
            case "Last 7 Days":
                return !targetDate.isBefore(today.minusDays(7));
            case "Last 30 Days":
                return !targetDate.isBefore(today.minusDays(30));
            default:
                return true;
        }
    }

    private int parseMinRating(String selection) {
        if (selection == null || selection.equals("All")) {
            return 0;
        }
        try {
            return Integer.parseInt(selection.substring(0, 1));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private List<RideAggregate> buildRideAggregates(List<FeedbackEntry> entries) {
        Map<String, RideAggregate> aggregates = new LinkedHashMap<>();
        for (FeedbackEntry entry : entries) {
            RideAggregate aggregate = aggregates.computeIfAbsent(entry.rideId, id -> new RideAggregate(
                    entry.rideId,
                    entry.routeId,
                    entry.busId,
                    entry.departureTime
            ));
            aggregate.add(entry);
        }
        return aggregates.values().stream()
                .sorted(Comparator
                        .comparingDouble(RideAggregate::getAvgRating)
                        .thenComparing(RideAggregate::getFeedbackCount, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    private void updateRideTable() {
        rideTableModel.setRowCount(0);
        for (RideAggregate aggregate : currentRideAggregates) {
            rideTableModel.addRow(new Object[]{
                    aggregate.rideId,
                    aggregate.routeId,
                    aggregate.busId,
                    aggregate.departureTime,
                    String.format("%.1f", aggregate.getAvgRating()),
                    aggregate.feedbackCount,
                    aggregate.getLabelSummary(),
                    aggregate.status
            });
        }
        lblRideTableSummary.setText("Showing " + currentRideAggregates.size() + " rides");
    }

    private void updateSummaryCards() {
        if (filteredEntries.isEmpty()) {
            lblOverallRatingValue.setText("N/A");
            lblTotalFeedbackValue.setText("0");
            lblTotalFeedbackSubtext.setText("Complaints: 0, Suggestions: 0");
            lblBadRidesValue.setText("0");
            lblTopIssueValue.setText("No data");
            lblTopIssueSubtext.setText("Add more filters to view");
            return;
        }

        double avgRating = filteredEntries.stream().mapToInt(entry -> entry.rating).average().orElse(0.0);
        lblOverallRatingValue.setText(String.format("%.1f / 5", avgRating));

        long suggestions = countLabelOccurrences(filteredEntries, "Suggestion");
        long complaints = countLabelOccurrences(filteredEntries, "Complaint");
        lblTotalFeedbackValue.setText(String.valueOf(filteredEntries.size()));
        lblTotalFeedbackSubtext.setText(String.format("Complaints: %d, Suggestions: %d", complaints, suggestions));

        long badRides = currentRideAggregates.stream().filter(agg -> agg.getAvgRating() < 3.0).count();
        lblBadRidesValue.setText(String.valueOf(badRides));

        String topIssue = filteredEntries.stream()
                .flatMap(entry -> entry.labels.stream())
                .filter(label -> !label.equals("Complaint") && !label.equals("Suggestion") && !label.equals("Compliment") && !label.equals("Other"))
                .collect(Collectors.groupingBy(label -> label, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Overcrowding");
        lblTopIssueValue.setText(topIssue);
        lblTopIssueSubtext.setText("Most cited issue");
    }

    private long countLabelOccurrences(List<FeedbackEntry> entries, String label) {
        return entries.stream().filter(entry -> entry.labels.contains(label)).count();
    }

    private void updateRideDetailsFromSelection() {
        int selectedRow = tblRides.getSelectedRow();
        if (selectedRow < 0) {
            clearRideDetails();
            return;
        }
        int modelRow = tblRides.convertRowIndexToModel(selectedRow);
        selectedRideId = (String) rideTableModel.getValueAt(modelRow, 0);
        RideAggregate aggregate = currentRideAggregates.stream()
                .filter(agg -> agg.rideId.equals(selectedRideId))
                .findFirst()
                .orElse(null);
        if (aggregate == null) {
            clearRideDetails();
            return;
        }

        lblRideHeaderTitle.setText(String.format("Ride %s • Route %s", aggregate.rideId, aggregate.routeId));
        lblRideMeta.setText(String.format("Bus %s | Departure %s | Avg Rating %.1f",
                aggregate.busId, aggregate.departureTime, aggregate.getAvgRating()));
        lblRideLabelCounts.setText(String.format("Complaints: %d, Suggestions: %d, Compliments: %d, Other: %d",
                aggregate.complaintCount, aggregate.suggestionCount, aggregate.complimentCount, aggregate.otherCount));

        refreshFeedbackEntries();
    }

    private void clearRideDetails() {
        selectedRideId = null;
        lblRideHeaderTitle.setText("Select a ride to view details");
        lblRideMeta.setText("");
        lblRideLabelCounts.setText("Complaints: 0, Suggestions: 0, Compliments: 0, Other: 0");
        feedbackTableModel.setRowCount(0);
        lblFeedbackSummary.setText("0 feedback entries");
        txtFullComment.setText("");
        currentDetailEntries = new ArrayList<>();
    }

    private void refreshFeedbackEntries() {
        if (selectedRideId == null) {
            clearRideDetails();
            return;
        }

        List<String> activeLabels = new ArrayList<>();
        if (chkComplaints.isSelected()) activeLabels.add("Complaint");
        if (chkSuggestions.isSelected()) activeLabels.add("Suggestion");
        if (chkCompliments.isSelected()) activeLabels.add("Compliment");
        if (chkOther.isSelected()) activeLabels.add("Other");
        boolean applyLabelFilter = !activeLabels.isEmpty();

        currentDetailEntries = filteredEntries.stream()
                .filter(entry -> entry.rideId.equals(selectedRideId))
                .filter(entry -> !applyLabelFilter || entry.labels.stream().anyMatch(activeLabels::contains))
                .collect(Collectors.toList());

        feedbackTableModel.setRowCount(0);
        for (FeedbackEntry entry : currentDetailEntries) {
            feedbackTableModel.addRow(new Object[]{
                maskUserId(entry.userId),
                entry.rating,
                String.join(", ", entry.labels),
                entry.comment.length() > 80 ? entry.comment.substring(0, 77) + "..." : entry.comment,
                entry.time
            });
        }
        lblFeedbackSummary.setText(currentDetailEntries.size() + " feedback entries");
        if (!currentDetailEntries.isEmpty()) {
            tblFeedbackEntries.setRowSelectionInterval(0, 0);
        } else {
            txtFullComment.setText("");
        }
    }

    private void updateFullCommentFromSelection() {
        int selectedRow = tblFeedbackEntries.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= currentDetailEntries.size()) {
            txtFullComment.setText("");
            return;
        }
        int modelRow = tblFeedbackEntries.convertRowIndexToModel(selectedRow);
        if (modelRow >= 0 && modelRow < currentDetailEntries.size()) {
            txtFullComment.setText(currentDetailEntries.get(modelRow).comment);
        }
    }

    private void handleDetailFilterChange(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED || event.getStateChange() == ItemEvent.DESELECTED) {
            refreshFeedbackEntries();
        }
    }

    private String maskUserId(String userId) {
        if (userId == null || userId.length() <= 4) {
            return "***" + (userId == null ? "" : userId);
        }
        return userId.substring(0, 2) + "***" + userId.substring(userId.length() - 2);
    }

    private void handleNavigation(String destination) {
        // TODO: plug in real backend later
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
            default:
                break;
        }
    }

    private void seedMockData() {
        // TODO: replace with persistence hook
        LocalDateTime now = LocalDateTime.now();
        allFeedbackEntries.add(new FeedbackEntry("R-101", "R1", "Bus-12", "USER1234", 2,
            Arrays.asList("Complaint", "Overcrowding"),
                "Bus was extremely crowded and AC was not working properly.", now.minusHours(2), "06:30"));
        allFeedbackEntries.add(new FeedbackEntry("R-101", "R1", "Bus-12", "USER9876", 3,
            Arrays.asList("Suggestion", "Scheduling"),
                "Please add more morning trips on R1 to handle rush.", now.minusHours(3), "06:45"));
        allFeedbackEntries.add(new FeedbackEntry("R-101", "R1", "Bus-12", "USER4455", 4,
            Arrays.asList("Compliment", "Driver"),
                "Driver was polite and helped elderly passengers.", now.minusHours(3), "07:10"));
        allFeedbackEntries.add(new FeedbackEntry("R-220", "R2", "Bus-08", "USER7412", 5,
            Arrays.asList("Compliment", "Punctuality"),
                "Ride was smooth and on time.", now.minusHours(6), "05:50"));
        allFeedbackEntries.add(new FeedbackEntry("R-220", "R2", "Bus-08", "USER1599", 2,
            Arrays.asList("Complaint", "Delays"),
                "Departure delayed by 20 minutes without announcement.", now.minusDays(1), "18:30"));
        allFeedbackEntries.add(new FeedbackEntry("R-312", "R3", "Bus-18", "USER3001", 1,
            Arrays.asList("Complaint", "Safety"),
                "Broken seat belts and harsh braking made the ride unsafe.", now.minusDays(2), "09:20"));
        allFeedbackEntries.add(new FeedbackEntry("R-312", "R3", "Bus-18", "USER2700", 2,
            Arrays.asList("Complaint", "Cleanliness"),
                "Bus was dirty and smelled terrible.", now.minusDays(2), "09:25"));
        allFeedbackEntries.add(new FeedbackEntry("R-400", "R4", "Bus-09", "USER6510", 4,
            Arrays.asList("Suggestion", "Amenities"),
                "Consider adding USB charging ports for commuters.", now.minusDays(4), "12:30"));
        allFeedbackEntries.add(new FeedbackEntry("R-450", "R4", "Bus-09", "USER2222", 3,
            Arrays.asList("Other", "Ticketing"),
                "Ticket scanner malfunctioned but conductor helped.", now.minusDays(5), "15:40"));
        allFeedbackEntries.add(new FeedbackEntry("R-510", "R5", "Bus-20", "USER9898", 5,
            Arrays.asList("Compliment", "Comfort"),
                "Very comfortable seats and clean bus.", now.minusDays(6), "17:15"));
        allFeedbackEntries.add(new FeedbackEntry("R-510", "R5", "Bus-20", "USER1188", 2,
            Arrays.asList("Complaint", "Overcrowding"),
                "Evening trip was fully packed, need more buses.", now.minusDays(6), "17:45"));
        allFeedbackEntries.add(new FeedbackEntry("R-620", "R6", "Bus-34", "USER4421", 4,
            Arrays.asList("Compliment", "Driver"),
                "Great driver, smooth ride despite rain.", now.minusDays(8), "08:10"));
        allFeedbackEntries.add(new FeedbackEntry("R-620", "R6", "Bus-34", "USER9551", 3,
            Arrays.asList("Suggestion", "Announcements"),
                "Announce station names a bit louder please.", now.minusDays(8), "08:20"));
        allFeedbackEntries.add(new FeedbackEntry("R-700", "R7", "Bus-02", "USER1677", 1,
            Arrays.asList("Complaint", "Overcrowding"),
                "This was the worst ride, constantly overcrowded.", now.minusDays(10), "19:00"));
        allFeedbackEntries.add(new FeedbackEntry("R-700", "R7", "Bus-02", "USER7612", 2,
            Arrays.asList("Complaint", "Delays"),
                "Peak hour trip delayed every day.", now.minusDays(10), "19:05"));
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), BEIGE);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private Font getCustomFont(int style, float size) {
        try {
            return new Font(FONT_FAMILY, style, (int) size);
        } catch (Exception e) {
            return new Font("Arial", style, (int) size);
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;

        RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(0, 0, 0, 15));
            g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, radius, radius);
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, radius, radius);
        }
    }

    private static class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        private static final int THUMB_SIZE = 8;

        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(180, 180, 180);
            this.trackColor = new Color(245, 245, 245);
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
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int x = thumbBounds.x;
            int y = thumbBounds.y;
            int width = thumbBounds.width;
            int height = thumbBounds.height;
            if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
                x += (width - THUMB_SIZE) / 2;
                width = THUMB_SIZE;
            } else {
                y += (height - THUMB_SIZE) / 2;
                height = THUMB_SIZE;
            }
            g2.setColor(thumbColor);
            g2.fillRoundRect(x, y, width, height, THUMB_SIZE, THUMB_SIZE);
        }
    }

    private static class FeedbackEntry {
        private final String rideId;
        private final String routeId;
        private final String busId;
        private final String userId;
        private final int rating;
        private final List<String> labels;
        private final String comment;
        private final String time;
        private final String departureTime;
        private final LocalDateTime timestamp;

        private FeedbackEntry(String rideId, String routeId, String busId, String userId, int rating,
                              List<String> labels, String comment, LocalDateTime timestamp, String departureTime) {
            this.rideId = rideId;
            this.routeId = routeId;
            this.busId = busId;
            this.userId = userId;
            this.rating = rating;
            this.labels = new ArrayList<>(labels);
            this.comment = comment;
            this.timestamp = timestamp;
            this.time = timestamp.format(TIME_FORMATTER);
            this.departureTime = departureTime;
        }
    }

    private static class RideAggregate {
        private final String rideId;
        private final String routeId;
        private final String busId;
        private final String departureTime;
        private int feedbackCount;
        private double ratingSum;
        private int complaintCount;
        private int suggestionCount;
        private int complimentCount;
        private int otherCount;
        private String status = "Stable";

        private RideAggregate(String rideId, String routeId, String busId, String departureTime) {
            this.rideId = rideId;
            this.routeId = routeId;
            this.busId = busId;
            this.departureTime = departureTime;
        }

        private void add(FeedbackEntry entry) {
            feedbackCount++;
            ratingSum += entry.rating;
            for (String label : entry.labels) {
                switch (label) {
                    case "Complaint":
                        complaintCount++;
                        break;
                    case "Suggestion":
                        suggestionCount++;
                        break;
                    case "Compliment":
                        complimentCount++;
                        break;
                    default:
                        otherCount++;
                        break;
                }
            }
            double avg = getAvgRating();
            if (avg < 3.0) {
                status = "Attention";
            } else if (avg >= 4.5) {
                status = "Great";
            } else {
                status = "Stable";
            }
        }

        private double getAvgRating() {
            return feedbackCount == 0 ? 0 : ratingSum / feedbackCount;
        }

        private int getFeedbackCount() {
            return feedbackCount;
        }

        private String getLabelSummary() {
            return String.format("C:%d | S:%d | P:%d | O:%d", complaintCount, suggestionCount, complimentCount, otherCount);
        }
    }
}
