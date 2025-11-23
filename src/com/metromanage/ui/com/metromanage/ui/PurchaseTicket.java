package com.metromanage.ui;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Adjustable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PurchaseTicket - Admin-only guest ticket issuance console (mock UI/data only).
 */
public class PurchaseTicket extends JFrame {

    // ==================== COLOR PLACEHOLDERS ====================
    // [PLACEHOLDER: for PRIMARY_COLOR_HEX]
    private static final Color PRIMARY_COLOR = new Color(86, 124, 141);

    // [PLACEHOLDER: for SIDEBAR_BACKGROUND_COLOR]
    private static final Color SIDEBAR_BACKGROUND = new Color(47, 65, 86);

    // [PLACEHOLDER: for SIDEBAR_ACTIVE_ITEM_COLOR]
    private static final Color SIDEBAR_ACTIVE = new Color(86, 124, 141);

    // [PLACEHOLDER: for MAIN_BACKGROUND_COLOR]
    private static final Color MAIN_BACKGROUND = new Color(245, 239, 232);

    // [PLACEHOLDER: for CARD_BACKGROUND_COLOR]
    private static final Color CARD_BACKGROUND = Color.WHITE;

    // [PLACEHOLDER: for TEXT_PRIMARY_COLOR]
    private static final Color TEXT_PRIMARY = new Color(47, 65, 86);

    // [PLACEHOLDER: for TEXT_MUTED_COLOR]
    private static final Color TEXT_MUTED = new Color(120, 120, 120);

    // Supporting palette
    private static final Color SKY = new Color(203, 214, 230);
    private static final Color LIGHT_BORDER = new Color(230, 230, 230);

    // ==================== FONT PLACEHOLDERS ====================
    // [PLACEHOLDER: for FONT_FAMILY]
    private static final String FONT_FAMILY = "Inter";

    // ==================== DATA / FORMATTERS ====================
    private static final String[] ROUTES = {"R1", "R2", "R3", "R4", "R5", "R6"};
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy • HH:mm", Locale.ENGLISH);

    // ==================== UI COMPONENTS ====================
    private JLabel lblTicketsTodayValue;
    private JLabel lblRevenueTodayValue;
    private JLabel lblPopularRouteValue;

    private JLabel lblAverageFareValue;

    private JComboBox<String> cmbTicketType;
    private JComboBox<String> cmbPassengerCategory;
    private JComboBox<String> cmbRoute;
    private JComboBox<String> cmbDirection;
    private JSpinner spnPassengerCount;
    private JComboBox<String> cmbValidity;
    private JComboBox<String> cmbPaymentMethod;
    private JTextField txtGuestName;
    private JTextField txtGuestPhone;

    private JLabel lblBaseFare;
    private JLabel lblPassengerMultiplier;
    private JLabel lblDiscountInfo;
    private JLabel lblTotalFare;

    private JLabel lblPreviewTicketId;
    private JLabel lblPreviewRoute;
    private JLabel lblPreviewTicketType;
    private JLabel lblPreviewPassengerCount;
    private JLabel lblPreviewTotalFare;
    private JLabel lblPreviewValidity;
    private JLabel lblPreviewIssuedAt;

    private JTable tblGuestTickets;
    private DefaultTableModel guestTicketTableModel;
    private JComboBox<String> cmbRecentRouteFilter;

    // ==================== IN-MEMORY DATA ====================
    private final List<GuestTicket> guestTickets = new ArrayList<>();
    private double lastCalculatedFare = 0.0;
    private int ticketSequence = 200;

    public PurchaseTicket() {
        initializeUI();
        seedMockTickets();
        refreshAllViews();
        setVisible(true);
    }

    private void initializeUI() {
        setTitle("Issue Guest Ticket - MetroManage");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1350, 860);
        setLocationRelativeTo(null);

        JPanel rootPanel = new GradientPanel();
        rootPanel.setLayout(new BorderLayout());
        setContentPane(rootPanel);

        rootPanel.add(createSidebar(), BorderLayout.WEST);
        rootPanel.add(createContentArea(), BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(SIDEBAR_BACKGROUND);
        sidebar.setBorder(new EmptyBorder(32, 20, 32, 20));

        // Avatar + greeting block
        JPanel userBlock = new JPanel();
        userBlock.setOpaque(false);
        userBlock.setLayout(new BoxLayout(userBlock, BoxLayout.Y_AXIS));
        userBlock.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_COLOR);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(getCustomFont(Font.BOLD, 24));
                FontMetrics fm = g2.getFontMetrics();
                String initials = "GA";
                g2.drawString(initials, (getWidth() - fm.stringWidth(initials)) / 2, (getHeight() + fm.getAscent()) / 2 - 4);
            }
        };
        avatar.setOpaque(false);
        avatar.setMaximumSize(new Dimension(64, 64));
        avatar.setPreferredSize(new Dimension(64, 64));
        userBlock.add(avatar);
        userBlock.add(Box.createVerticalStrut(14));

        JLabel lblGreeting = new JLabel("Hello, Ticket Admin");
        lblGreeting.setFont(getCustomFont(Font.BOLD, 18));
        lblGreeting.setForeground(Color.WHITE);
        userBlock.add(lblGreeting);

        JLabel lblRole = new JLabel("Guest Ticket Ops");
        lblRole.setFont(getCustomFont(Font.PLAIN, 13));
        lblRole.setForeground(SKY);
        userBlock.add(lblRole);
        userBlock.setBorder(new EmptyBorder(0, 10, 30, 10));

        sidebar.add(userBlock);

        JLabel navTitle = new JLabel("QUICK ACCESS");
        navTitle.setFont(getCustomFont(Font.BOLD, 11));
        navTitle.setForeground(SKY);
        navTitle.setBorder(new EmptyBorder(0, 12, 14, 0));
        navTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(navTitle);

        sidebar.add(createSidebarButton("Dashboard", () -> navigateTo(new DashBoard())));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createSidebarButton("Manage Fleet", () -> navigateTo(new ManageFleet())));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createSidebarButton("Boarding Totals", () -> navigateTo(new ViewBoardingTotals())));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createSidebarButton("View Feedback", () -> navigateTo(new ViewFeedback())));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createSidebarButton("Issue Guest Ticket", null));
        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private JPanel createContentArea() {
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);

        JPanel stacked = new JPanel();
        stacked.setOpaque(false);
        stacked.setLayout(new BoxLayout(stacked, BoxLayout.Y_AXIS));
        stacked.setBorder(new EmptyBorder(28, 24, 32, 24));

        JComponent summarySection = createSummarySection();
        enforceSectionWidth(summarySection);
        stacked.add(summarySection);
        stacked.add(Box.createVerticalStrut(24));

        JComponent ticketForm = createTicketFormSection();
        enforceSectionWidth(ticketForm);
        stacked.add(ticketForm);
        stacked.add(Box.createVerticalStrut(24));

        JComponent recentTickets = createRecentTicketsSection();
        enforceSectionWidth(recentTickets);
        stacked.add(recentTickets);

        JScrollPane scrollPane = new JScrollPane(stacked);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setUnitIncrement(24);
        verticalBar.setOpaque(false);
        verticalBar.setUI(new ModernScrollBarUI());
        JScrollBar horizontalBar = scrollPane.getHorizontalScrollBar();
        if (horizontalBar != null) {
            horizontalBar.setOpaque(false);
            horizontalBar.setUI(new ModernScrollBarUI());
        }
        content.add(scrollPane, BorderLayout.CENTER);
        return content;
    }

    private JPanel createSummarySection() {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Issue Guest Ticket");
        lblTitle.setFont(getCustomFont(Font.BOLD, 30));
        lblTitle.setForeground(TEXT_PRIMARY);
        wrapper.add(lblTitle);
        wrapper.add(Box.createVerticalStrut(18));

        JPanel cards = new JPanel(new GridLayout(1, 4, 18, 0));
        cards.setOpaque(false);
        cards.add(createSummaryCard("Guest Tickets Today", "230", new Color(86, 124, 141), label -> lblTicketsTodayValue = label));
        cards.add(createSummaryCard("Estimated Revenue Today", "PKR 18,400", new Color(76, 175, 80), label -> lblRevenueTodayValue = label));
        cards.add(createSummaryCard("Most Popular Guest Route", "R1 – City Center", new Color(255, 183, 77), label -> lblPopularRouteValue = label));
        cards.add(createSummaryCard("Average Fare per Ticket", "PKR 80", new Color(220, 120, 120), label -> lblAverageFareValue = label));
        wrapper.add(cards);

        return wrapper;
    }

    private JPanel createTicketFormSection() {
        JPanel card = new RoundedPanel(18);
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        card.setLayout(new BorderLayout(20, 0));

        JPanel formColumn = new JPanel(new GridBagLayout());
        formColumn.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel formTitle = new JLabel("Guest Ticket Details");
        formTitle.setFont(getCustomFont(Font.BOLD, 20));
        formTitle.setForeground(TEXT_PRIMARY);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        formColumn.add(formTitle, gbc);

        gbc.gridwidth = 1;
        addLabeledField(formColumn, gbc, 1, "Ticket Type", cmbTicketType = new JComboBox<>(new String[]{"Single Ride", "Return", "[PLACEHOLDER: for FUTURE_PASS_TYPE]"}));
        addLabeledField(formColumn, gbc, 2, "Passenger Category", cmbPassengerCategory = new JComboBox<>(new String[]{"Adult", "Child", "Senior", "Student"}));
        addLabeledField(formColumn, gbc, 3, "Route", cmbRoute = new JComboBox<>(ROUTES));
        addLabeledField(formColumn, gbc, 4, "Direction", cmbDirection = new JComboBox<>(new String[]{"Outbound", "Inbound", "Loop"}));
        addLabeledField(formColumn, gbc, 5, "Passenger Count", spnPassengerCount = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1)));
        addLabeledField(formColumn, gbc, 6, "Validity", cmbValidity = new JComboBox<>(new String[]{"Today only", "24 hours", "[PLACEHOLDER: FUTURE_VALIDITY_OPTION]"}));
        addLabeledField(formColumn, gbc, 7, "Payment Method", cmbPaymentMethod = new JComboBox<>(new String[]{"Cash", "Card", "Other"}));
        addLabeledField(formColumn, gbc, 8, "Guest Name (optional)", txtGuestName = new JTextField());
        addLabeledField(formColumn, gbc, 9, "Guest Phone (optional)", txtGuestPhone = new JTextField());

        JPanel fareSummary = createFareSummaryPanel();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        formColumn.add(fareSummary, gbc);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        buttonRow.setOpaque(false);
        JButton btnCalculate = new JButton("Calculate Fare");
        stylePrimaryButton(btnCalculate);
        btnCalculate.addActionListener(e -> calculateAndDisplayFare());
        JButton btnIssue = new JButton("Issue Ticket");
        styleSecondaryButton(btnIssue);
        btnIssue.addActionListener(e -> issueGuestTicket());
        buttonRow.add(btnCalculate);
        buttonRow.add(btnIssue);

        gbc.gridy = 11;
        formColumn.add(buttonRow, gbc);

        card.add(formColumn, BorderLayout.CENTER);
        card.add(createTicketPreviewPanel(), BorderLayout.EAST);
        return card;
    }

    private JPanel createRecentTicketsSection() {
        JPanel card = new RoundedPanel(18);
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        card.setLayout(new BorderLayout(0, 16));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel lblHeader = new JLabel("Recent Guest Tickets");
        lblHeader.setFont(getCustomFont(Font.BOLD, 20));
        lblHeader.setForeground(TEXT_PRIMARY);
        header.add(lblHeader, BorderLayout.WEST);

        cmbRecentRouteFilter = new JComboBox<>();
        cmbRecentRouteFilter.addItem("All Routes");
        for (String route : ROUTES) {
            cmbRecentRouteFilter.addItem(route);
        }
        styleInputField(cmbRecentRouteFilter);
        cmbRecentRouteFilter.setPreferredSize(new Dimension(150, 32));
        cmbRecentRouteFilter.addActionListener(e -> refreshRecentTicketsTable());
        header.add(cmbRecentRouteFilter, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);

        guestTicketTableModel = new DefaultTableModel(new Object[]{
                "Ticket ID", "Route", "Ticket Type", "Passenger Category", "Passenger Count", "Total Fare", "Payment Method", "Issued At"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblGuestTickets = new JTable(guestTicketTableModel);
        tblGuestTickets.setRowHeight(40);
        tblGuestTickets.setFont(getCustomFont(Font.PLAIN, 13));
        tblGuestTickets.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    handleTableSelection();
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(tblGuestTickets);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_BORDER));
        JScrollBar tableVBar = scrollPane.getVerticalScrollBar();
        tableVBar.setUnitIncrement(20);
        tableVBar.setUI(new ModernScrollBarUI());
        JScrollBar tableHBar = scrollPane.getHorizontalScrollBar();
        if (tableHBar != null) {
            tableHBar.setUI(new ModernScrollBarUI());
        }
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private JPanel createSummaryCard(String title, String defaultValue, Color accentColor, java.util.function.Consumer<JLabel> valueConsumer) {
        JPanel card = new RoundedPanel(18);
        card.setBackground(CARD_BACKGROUND);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 0, 18, 0));

        JPanel indicator = new JPanel();
        indicator.setBackground(accentColor);
        indicator.setPreferredSize(new Dimension(6, 0));
        card.add(indicator, BorderLayout.WEST);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel lblTitle = new JLabel(title.toUpperCase(Locale.ENGLISH));
        lblTitle.setFont(getCustomFont(Font.BOLD, 11));
        lblTitle.setForeground(TEXT_MUTED);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblTitle);
        content.add(Box.createVerticalStrut(8));

        JLabel lblValue = new JLabel(defaultValue);
        lblValue.setFont(getCustomFont(Font.BOLD, 28));
        lblValue.setForeground(TEXT_PRIMARY);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblValue);

        card.add(content, BorderLayout.CENTER);

        if (valueConsumer != null) {
            valueConsumer.accept(lblValue);
        }
        return card;
    }

    private JPanel createFareSummaryPanel() {
        JPanel panel = new RoundedPanel(16);
        panel.setBackground(new Color(250, 250, 250));
        panel.setBorder(new EmptyBorder(16, 18, 16, 18));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblHeader = new JLabel("Fare Summary");
        lblHeader.setFont(getCustomFont(Font.BOLD, 16));
        lblHeader.setForeground(TEXT_PRIMARY);
        panel.add(lblHeader);
        panel.add(Box.createVerticalStrut(8));

        lblBaseFare = createSummaryLine(panel, "Base Fare", "PKR 0");
        lblPassengerMultiplier = createSummaryLine(panel, "Passenger Multiplier", "x1.0");
        lblDiscountInfo = createSummaryLine(panel, "Ticket/Validity Multipliers", "x1.0");

        panel.add(Box.createVerticalStrut(12));
        lblTotalFare = new JLabel("PKR 0");
        lblTotalFare.setFont(getCustomFont(Font.BOLD, 26));
        lblTotalFare.setForeground(PRIMARY_COLOR);
        panel.add(lblTotalFare);

        return panel;
    }

    private JLabel createSummaryLine(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(getCustomFont(Font.PLAIN, 13));
        lbl.setForeground(TEXT_MUTED);
        row.add(lbl, BorderLayout.WEST);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(getCustomFont(Font.BOLD, 13));
        lblValue.setForeground(TEXT_PRIMARY);
        row.add(lblValue, BorderLayout.EAST);

        parent.add(row);
        return lblValue;
    }

    private JPanel createTicketPreviewPanel() {
        JPanel preview = new RoundedPanel(18);
        preview.setBackground(new Color(248, 248, 248));
        preview.setBorder(new EmptyBorder(18, 20, 18, 20));
        preview.setPreferredSize(new Dimension(360, 0));
        preview.setLayout(new BoxLayout(preview, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Ticket Preview");
        lblTitle.setFont(getCustomFont(Font.BOLD, 18));
        lblTitle.setForeground(TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        preview.add(lblTitle);
        preview.add(Box.createVerticalStrut(14));

        lblPreviewTicketId = createPreviewLine(preview, "Ticket ID", "—");
        lblPreviewRoute = createPreviewLine(preview, "Route / Direction", "—");
        lblPreviewTicketType = createPreviewLine(preview, "Ticket Type", "—");
        lblPreviewPassengerCount = createPreviewLine(preview, "Passengers", "—");
        lblPreviewTotalFare = createPreviewLine(preview, "Total Fare", "—");
        lblPreviewValidity = createPreviewLine(preview, "Validity", "—");
        lblPreviewIssuedAt = createPreviewLine(preview, "Issued At", "—");

        preview.add(Box.createVerticalStrut(16));
        JLabel qrPlaceholder = new JLabel("[PLACEHOLDER: QR CODE HERE]", SwingConstants.CENTER);
        qrPlaceholder.setFont(getCustomFont(Font.BOLD, 14));
        qrPlaceholder.setForeground(TEXT_MUTED);
        qrPlaceholder.setBorder(BorderFactory.createDashedBorder(TEXT_MUTED));
        qrPlaceholder.setPreferredSize(new Dimension(280, 160));
        qrPlaceholder.setAlignmentX(Component.LEFT_ALIGNMENT);
        preview.add(qrPlaceholder);

        return preview;
    }

    private JLabel createPreviewLine(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblKey = new JLabel(label);
        lblKey.setFont(getCustomFont(Font.PLAIN, 13));
        lblKey.setForeground(TEXT_MUTED);
        row.add(lblKey, BorderLayout.WEST);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(getCustomFont(Font.BOLD, 14));
        lblValue.setForeground(TEXT_PRIMARY);
        row.add(lblValue, BorderLayout.EAST);

        parent.add(row);
        parent.add(Box.createVerticalStrut(6));
        return lblValue;
    }

    private void enforceSectionWidth(JComponent component) {
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, component.getPreferredSize().height));
    }

    private void addLabeledField(JPanel container, GridBagConstraints gbc, int row, String label, JComponent field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(getCustomFont(Font.BOLD, 13));
        lbl.setForeground(TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        container.add(lbl, gbc);

        field.setFont(getCustomFont(Font.PLAIN, 13));
        styleInputField(field);
        gbc.gridx = 1;
        gbc.weightx = 1;
        container.add(field, gbc);
    }

    private void styleInputField(JComponent field) {
        if (field instanceof JComboBox) {
            JComboBox<?> combo = (JComboBox<?>) field;
            combo.setBackground(Color.WHITE);
            combo.setForeground(TEXT_PRIMARY);
            combo.setBorder(createInputBorder());
            combo.setPreferredSize(new Dimension(220, 36));
            combo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else if (field instanceof JTextField) {
            JTextField textField = (JTextField) field;
            textField.setBackground(Color.WHITE);
            textField.setForeground(TEXT_PRIMARY);
            textField.setBorder(createInputBorder());
            textField.setPreferredSize(new Dimension(220, 34));
        } else if (field instanceof JSpinner) {
            JSpinner spinner = (JSpinner) field;
            spinner.setBorder(createInputBorder());
            spinner.setPreferredSize(new Dimension(140, 36));
            spinner.setBackground(Color.WHITE);
            spinner.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            JComponent editorComponent = spinner.getEditor();
            if (editorComponent instanceof JSpinner.DefaultEditor) {
                JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) editorComponent;
                JFormattedTextField txt = editor.getTextField();
                txt.setHorizontalAlignment(SwingConstants.CENTER);
                txt.setBorder(BorderFactory.createEmptyBorder());
                txt.setBackground(Color.WHITE);
                txt.setForeground(TEXT_PRIMARY);
                txt.setFont(getCustomFont(Font.BOLD, 14));
            }
        }
    }

    private Border createInputBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_BORDER),
                new EmptyBorder(4, 10, 4, 10)
        );
    }

    private void stylePrimaryButton(JButton button) {
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 24, 10, 24));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton button) {
        button.setBackground(new Color(245, 245, 245));
        button.setForeground(TEXT_PRIMARY);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 24, 10, 24));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private JButton createSidebarButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.setFont(getCustomFont("Issue Guest Ticket".equals(text) ? Font.BOLD : Font.PLAIN, 14));
        btn.setForeground("Issue Guest Ticket".equals(text) ? Color.WHITE : SKY);
        btn.setBackground("Issue Guest Ticket".equals(text) ? SIDEBAR_ACTIVE : SIDEBAR_BACKGROUND);
        btn.setBorder(new EmptyBorder(14, 18, 14, 18));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        if (action != null) {
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> action.run());
        } else {
            btn.setCursor(Cursor.getDefaultCursor());
        }
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (action != null) {
                    btn.setBackground(new Color(65, 85, 110));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (action != null) {
                    btn.setBackground(SIDEBAR_BACKGROUND);
                }
            }
        });
        return btn;
    }

    private void navigateTo(JFrame frame) {
        frame.setVisible(true);
        dispose();
    }

    private void calculateAndDisplayFare() {
        if (!validateRequiredFields(false)) {
            return;
        }
        String route = (String) cmbRoute.getSelectedItem();
        String ticketType = (String) cmbTicketType.getSelectedItem();
        String category = (String) cmbPassengerCategory.getSelectedItem();
        String validity = (String) cmbValidity.getSelectedItem();
        int passengerCount = ((Number) spnPassengerCount.getValue()).intValue();

        double baseFare = getBaseFareForRoute(route);
        double ticketMultiplier = getTicketTypeMultiplier(ticketType);
        double categoryMultiplier = getCategoryMultiplier(category);
        double validityMultiplier = getValidityMultiplier(validity);

        double total = baseFare * passengerCount * ticketMultiplier * categoryMultiplier * validityMultiplier;
        // TODO: replace fare calculation with real pricing rules
        lastCalculatedFare = Math.max(50, Math.round(total / 10.0) * 10);

        lblBaseFare.setText(String.format("PKR %,.0f", baseFare));
        lblPassengerMultiplier.setText(String.format("x %.2f (Passengers %d • %s)", passengerCount * categoryMultiplier, passengerCount, category));
        lblDiscountInfo.setText(String.format("Ticket %.2f • Validity %.2f", ticketMultiplier, validityMultiplier));
        lblTotalFare.setText(String.format("PKR %,.0f", lastCalculatedFare));
    }

    private void issueGuestTicket() {
        if (!validateRequiredFields(true)) {
            return;
        }
        if (lastCalculatedFare <= 0) {
            calculateAndDisplayFare();
        }
        if (lastCalculatedFare <= 0) {
            JOptionPane.showMessageDialog(this, "Unable to compute fare. Please try again.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        GuestTicket ticket = new GuestTicket(
                generateTicketId(),
                (String) cmbTicketType.getSelectedItem(),
                (String) cmbPassengerCategory.getSelectedItem(),
                (String) cmbRoute.getSelectedItem(),
                (String) cmbDirection.getSelectedItem(),
                ((Number) spnPassengerCount.getValue()).intValue(),
                (String) cmbValidity.getSelectedItem(),
                (String) cmbPaymentMethod.getSelectedItem(),
                txtGuestName.getText().trim(),
                txtGuestPhone.getText().trim(),
                lastCalculatedFare,
                LocalDateTime.now()
        );

        // TODO: connect GuestTicket to backend service in future
        guestTickets.add(ticket);
        refreshAllViews();
        updateTicketPreview(ticket);
        JOptionPane.showMessageDialog(this, "Guest ticket issued successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean validateRequiredFields(boolean showDialog) {
        boolean valid = cmbRoute.getSelectedItem() != null
                && cmbTicketType.getSelectedItem() != null
                && cmbPaymentMethod.getSelectedItem() != null
                && ((Number) spnPassengerCount.getValue()).intValue() >= 1;
        if (!valid && showDialog) {
            JOptionPane.showMessageDialog(this, "Please fill in the mandatory fields (ticket type, route, passenger count, payment).", "Validation", JOptionPane.WARNING_MESSAGE);
        }
        return valid;
    }

    private void refreshAllViews() {
        updateSummaryCards();
        refreshRecentTicketsTable();
        autoSelectLatestTicket();
    }

    private void autoSelectLatestTicket() {
        if (tblGuestTickets.getRowCount() > 0) {
            tblGuestTickets.setRowSelectionInterval(0, 0);
            handleTableSelection();
        } else {
            clearTicketPreview();
        }
    }

    private void handleTableSelection() {
        int selectedRow = tblGuestTickets.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }
        String ticketId = (String) tblGuestTickets.getValueAt(selectedRow, 0);
        guestTickets.stream()
                .filter(ticket -> ticket.ticketId.equals(ticketId))
                .findFirst()
                .ifPresent(this::updateTicketPreview);
    }

    private void refreshRecentTicketsTable() {
        guestTicketTableModel.setRowCount(0);
        String routeFilter = cmbRecentRouteFilter.getSelectedItem() != null ? (String) cmbRecentRouteFilter.getSelectedItem() : "All Routes";
        List<GuestTicket> sorted = guestTickets.stream()
                .sorted(Comparator.comparing((GuestTicket t) -> t.issuedAt).reversed())
                .collect(Collectors.toList());

        for (GuestTicket ticket : sorted) {
            if (!"All Routes".equals(routeFilter) && !ticket.routeId.equals(routeFilter)) {
                continue;
            }
            guestTicketTableModel.addRow(new Object[]{
                    ticket.ticketId,
                    ticket.routeId,
                    ticket.ticketType,
                    ticket.passengerCategory,
                    ticket.passengerCount,
                    String.format("PKR %,.0f", ticket.totalFare),
                    ticket.paymentMethod,
                    DISPLAY_FORMATTER.format(ticket.issuedAt)
            });
        }
    }

    private void updateSummaryCards() {
        LocalDate today = LocalDate.now();
        long ticketsToday = guestTickets.stream().filter(t -> t.issuedAt.toLocalDate().isEqual(today)).count();
        double revenueToday = guestTickets.stream().filter(t -> t.issuedAt.toLocalDate().isEqual(today)).mapToDouble(t -> t.totalFare).sum();
        Map<String, Long> routeCounts = guestTickets.stream().collect(Collectors.groupingBy(t -> t.routeId, Collectors.counting()));
        String popularRoute = routeCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> entry.getKey() + " – " + getRouteDescriptor(entry.getKey()))
                .orElse("R1 – City Center");
        double avgFare = guestTickets.isEmpty() ? 0.0 : guestTickets.stream().mapToDouble(t -> t.totalFare).average().orElse(0.0);

        lblTicketsTodayValue.setText(String.valueOf(ticketsToday));
        lblRevenueTodayValue.setText(String.format("PKR %,.0f", revenueToday));
        lblPopularRouteValue.setText(popularRoute);
        lblAverageFareValue.setText(String.format("PKR %,.0f", avgFare));
    }

    private void updateTicketPreview(GuestTicket ticket) {
        lblPreviewTicketId.setText(ticket.ticketId);
        lblPreviewRoute.setText(ticket.routeId + " • " + ticket.direction);
        lblPreviewTicketType.setText(ticket.ticketType + " (" + ticket.passengerCategory + ")");
        String guestNameDisplay = (ticket.guestName == null || ticket.guestName.isBlank()) ? "Walk-up Guest" : ticket.guestName;
        String guestPhoneDisplay = (ticket.guestPhone == null || ticket.guestPhone.isBlank()) ? "N/A" : ticket.guestPhone;
        lblPreviewPassengerCount.setText(ticket.passengerCount + " passenger(s) • " + guestNameDisplay);
        lblPreviewPassengerCount.setToolTipText("Contact: " + guestPhoneDisplay);
        lblPreviewTotalFare.setText(String.format("PKR %,.0f", ticket.totalFare));
        lblPreviewValidity.setText(ticket.validity);
        lblPreviewIssuedAt.setText(DISPLAY_FORMATTER.format(ticket.issuedAt));
    }

    private void clearTicketPreview() {
        lblPreviewTicketId.setText("—");
        lblPreviewRoute.setText("—");
        lblPreviewTicketType.setText("—");
        lblPreviewPassengerCount.setText("—");
        lblPreviewTotalFare.setText("—");
        lblPreviewValidity.setText("—");
        lblPreviewIssuedAt.setText("—");
    }

    private String generateTicketId() {
        ticketSequence++;
        return String.format("GT-2025-%06d", ticketSequence);
    }

    private double getBaseFareForRoute(String route) {
        if (route == null) {
            return 80;
        }
        switch (route) {
            case "R1":
                return 80;
            case "R2":
                return 90;
            case "R3":
                return 70;
            case "R4":
                return 85;
            case "R5":
                return 60;
            case "R6":
                return 100;
            default:
                return 80;
        }
    }

    private double getTicketTypeMultiplier(String ticketType) {
        if (ticketType == null) {
            return 1.0;
        }
        switch (ticketType) {
            case "Return":
                return 1.75;
            case "[PLACEHOLDER: for FUTURE_PASS_TYPE]":
                return 2.2;
            default:
                return 1.0;
        }
    }

    private double getCategoryMultiplier(String category) {
        if (category == null) {
            return 1.0;
        }
        switch (category) {
            case "Child":
                return 0.5;
            case "Senior":
                return 0.7;
            case "Student":
                return 0.8;
            default:
                return 1.0;
        }
    }

    private double getValidityMultiplier(String validity) {
        if (validity == null) {
            return 1.0;
        }
        switch (validity) {
            case "24 hours":
                return 1.25;
            case "[PLACEHOLDER: FUTURE_VALIDITY_OPTION]":
                return 1.5;
            default:
                return 1.0;
        }
    }

    private String getRouteDescriptor(String route) {
        switch (route) {
            case "R1":
                return "City Center";
            case "R2":
                return "Tech Park";
            case "R3":
                return "Harbor";
            case "R4":
                return "University";
            case "R5":
                return "Airport";
            case "R6":
                return "Industrial";
            default:
                return "Metro";
        }
    }

    private void seedMockTickets() {
        guestTickets.clear();
        guestTickets.add(new GuestTicket("GT-2025-000101", "Single Ride", "Adult", "R1", "Outbound", 1,
                "Today only", "Cash", "Visitor One", "0300-0000001", 80, LocalDateTime.now().minusHours(1)));
        guestTickets.add(new GuestTicket("GT-2025-000102", "Return", "Student", "R2", "Inbound", 2,
                "24 hours", "Card", "Student Duo", "0300-0000002", 210, LocalDateTime.now().minusHours(3)));
        guestTickets.add(new GuestTicket("GT-2025-000103", "Single Ride", "Senior", "R3", "Outbound", 1,
                "Today only", "Cash", "", "", 60, LocalDateTime.now().minusDays(1)));
        guestTickets.add(new GuestTicket("GT-2025-000104", "Return", "Adult", "R1", "Inbound", 3,
                "24 hours", "Card", "Family", "0300-0000003", 360, LocalDateTime.now().minusHours(5)));
        guestTickets.add(new GuestTicket("GT-2025-000105", "Single Ride", "Child", "R4", "Loop", 2,
                "Today only", "Cash", "", "", 90, LocalDateTime.now().minusDays(2)));
    }

    private Font getCustomFont(int style, float size) {
        try {
            return new Font(FONT_FAMILY, style, (int) size);
        } catch (Exception ex) {
            return new Font("Arial", style, (int) size);
        }
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), MAIN_BACKGROUND);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
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
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 15));
            g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, radius, radius);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, radius, radius);
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
            if (scrollbar.getOrientation() == Adjustable.VERTICAL) {
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

    private static class GuestTicket {
        private final String ticketId;
        private final String ticketType;
        private final String passengerCategory;
        private final String routeId;
        private final String direction;
        private final int passengerCount;
        private final String validity;
        private final String paymentMethod;
        private final String guestName;
        private final String guestPhone;
        private final double totalFare;
        private final LocalDateTime issuedAt;

        private GuestTicket(String ticketId, String ticketType, String passengerCategory, String routeId,
                             String direction, int passengerCount, String validity, String paymentMethod,
                             String guestName, String guestPhone, double totalFare, LocalDateTime issuedAt) {
            this.ticketId = ticketId;
            this.ticketType = ticketType;
            this.passengerCategory = passengerCategory;
            this.routeId = routeId;
            this.direction = direction;
            this.passengerCount = passengerCount;
            this.validity = validity;
            this.paymentMethod = paymentMethod;
            this.guestName = guestName;
            this.guestPhone = guestPhone;
            this.totalFare = totalFare;
            this.issuedAt = issuedAt;
        }
    }
}