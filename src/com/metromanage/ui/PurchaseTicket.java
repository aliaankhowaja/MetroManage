package com.metromanage.ui;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.metromanage.model.RoutePersistanceHandler;
import com.metromanage.model.PassengerPersistanceHandler;
import com.metromanage.domain.Route;
import com.metromanage.domain.StationRegister;
import com.metromanage.domain.Ticket;
import com.metromanage.domain.Passenger;

import java.awt.*;
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
    // private static final String[] ROUTES = {"R1", "R2", "R3", "R4", "R5", "R6"};
    private ArrayList<Route> routes;
    private String[] routeNames;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy • HH:mm", Locale.ENGLISH);

    // ==================== UI COMPONENTS ====================
    private JLabel lblTicketsTodayValue;
    private JLabel lblRevenueTodayValue;
    private JLabel lblPopularRouteValue;

    private JLabel lblAverageFareValue;

    // private JComboBox<String> cmbTicketType;
    // private JComboBox<String> cmbPassengerCategory;
    private JComboBox<String> cmbRoute;
    // private JComboBox<String> cmbDirection;
    // private JSpinner spnPassengerCount;
    // private JComboBox<String> cmbValidity;
    private JComboBox<String> cmbPaymentMethod;
    private JTextField txtPassengerID;
    private JTextField txtCardDetails;
    // private JTextField txtGuestPhone;
    
    // Check-in/Check-out fields
    private JTextField txtCheckInTicketID;
    private JTextField txtCheckInBusID;
    private JTextField txtCheckInStationID;
    private JTextField txtCheckOutTicketID;
    private JTextField txtCheckOutStationID;

    // ==================== IN-MEMORY DATA ====================
    private double lastCalculatedFare = 0.0;
    private int ticketSequence = 200;
    private StationRegister stationRegister;
    private Ticket lastIssuedTicket;

    public PurchaseTicket() {
        stationRegister = new StationRegister();
        initializeUI();
        setVisible(true);
    }

    private void initializeUI() {
        RoutePersistanceHandler rph = new RoutePersistanceHandler();
        routes = rph.getAllRoutes();
        routeNames = routes.stream().map(Route::getRouteName).toArray(String[]::new);
        setTitle("Station Management - MetroManage");
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
        sidebar.add(createSidebarButton("Station Management", null));
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
        
        JComponent checkInOutSection = createCheckInOutSection();
        enforceSectionWidth(checkInOutSection);
        stacked.add(checkInOutSection);

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

        JLabel lblTitle = new JLabel("Station Management");
        lblTitle.setFont(getCustomFont(Font.BOLD, 30));
        lblTitle.setForeground(TEXT_PRIMARY);
        wrapper.add(lblTitle);
        wrapper.add(Box.createVerticalStrut(18));

        JPanel cards = new JPanel(new GridLayout(1, 4, 18, 0));
        cards.setOpaque(false);
        // cards.add(createSummaryCard("Guest Tickets Today", "0", new Color(86, 124, 141), label -> lblTicketsTodayValue = label));
        // cards.add(createSummaryCard("Estimated Revenue Today", "PKR 0", new Color(76, 175, 80), label -> lblRevenueTodayValue = label));
        // cards.add(createSummaryCard("Most Popular Guest Route", "-", new Color(255, 183, 77), label -> lblPopularRouteValue = label));
        // cards.add(createSummaryCard("Average Fare per Ticket", "PKR 0", new Color(220, 120, 120), label -> lblAverageFareValue = label));
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
        // addLabeledField(formColumn, gbc, 1, "Ticket Type", cmbTicketType = new JComboBox<>(new String[]{"Single Ride", "Return", "[PLACEHOLDER: for FUTURE_PASS_TYPE]"}));
        // addLabeledField(formColumn, gbc, 2, "Passenger Category", cmbPassengerCategory = new JComboBox<>(new String[]{"Adult", "Child", "Senior", "Student"}));
        addLabeledField(formColumn, gbc, 1, "Route", cmbRoute = new JComboBox<>(routeNames));
        // addLabeledField(formColumn, gbc, 2, "Direction", cmbDirection = new JComboBox<>(new String[]{"Outbound", "Inbound", "Loop"}));
        // addLabeledField(formColumn, gbc, 5, "Passenger Count", spnPassengerCount = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1)));
        // addLabeledField(formColumn, gbc, 6, "Validity", cmbValidity = new JComboBox<>(new String[]{"Today only", "24 hours", "[PLACEHOLDER: FUTURE_VALIDITY_OPTION]"}));
        addLabeledField(formColumn, gbc, 2, "Payment Method", cmbPaymentMethod = new JComboBox<>(new String[]{"Cash", "Card", "Wallet"}));
        addLabeledField(formColumn, gbc, 3, "Passenger ID (optional)", txtPassengerID = new JTextField());
        addLabeledField(formColumn, gbc, 4, "Card Details (if applicable)", txtCardDetails = new JTextField());
        // addLabeledField(formColumn, gbc, 9, "Guest Phone (optional)", txtGuestPhone = new JTextField());

        // JPanel fareSummary = createFareSummaryPanel();
        // gbc.gridx = 0;
        // gbc.gridy = 10;
        // gbc.gridwidth = 2;
        // formColumn.add(fareSummary, gbc);

        JButton btnIssue = new JButton("Issue Ticket");
        stylePrimaryButton(btnIssue);
        btnIssue.addActionListener(e -> issueGuestTicket());
        // buttonRow.add(btnIssue);

        gbc.gridy = 11;
        formColumn.add(btnIssue, gbc);

        card.add(formColumn, BorderLayout.CENTER);
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

    private JPanel createCheckInOutSection() {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        
        JLabel sectionTitle = new JLabel("Check-In / Check-Out Operations");
        sectionTitle.setFont(getCustomFont(Font.BOLD, 24));
        sectionTitle.setForeground(TEXT_PRIMARY);
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.add(sectionTitle);
        wrapper.add(Box.createVerticalStrut(18));
        
        JPanel cardsRow = new JPanel(new GridLayout(1, 2, 18, 0));
        cardsRow.setOpaque(false);
        cardsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        
        // Check-in card
        cardsRow.add(createCheckInCard());
        
        // Check-out card
        cardsRow.add(createCheckOutCard());
        
        wrapper.add(cardsRow);
        
        return wrapper;
    }
    
    private JPanel createCheckInCard() {
        JPanel card = new RoundedPanel(18);
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        card.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        
        JLabel cardTitle = new JLabel("Check-In");
        cardTitle.setFont(getCustomFont(Font.BOLD, 20));
        cardTitle.setForeground(TEXT_PRIMARY);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        card.add(cardTitle, gbc);
        
        gbc.gridwidth = 1;
        addLabeledField(card, gbc, 1, "Ticket ID", txtCheckInTicketID = new JTextField());
        addLabeledField(card, gbc, 2, "Bus ID", txtCheckInBusID = new JTextField());
        addLabeledField(card, gbc, 3, "Boarding Station ID", txtCheckInStationID = new JTextField());
        
        JButton btnCheckIn = new JButton("Check In");
        stylePrimaryButton(btnCheckIn);
        btnCheckIn.addActionListener(e -> performCheckIn());
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 6, 6, 6);
        card.add(btnCheckIn, gbc);
        
        return card;
    }
    
    private JPanel createCheckOutCard() {
        JPanel card = new RoundedPanel(18);
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        card.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        
        JLabel cardTitle = new JLabel("Check-Out");
        cardTitle.setFont(getCustomFont(Font.BOLD, 20));
        cardTitle.setForeground(TEXT_PRIMARY);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        card.add(cardTitle, gbc);
        
        gbc.gridwidth = 1;
        addLabeledField(card, gbc, 1, "Ticket ID", txtCheckOutTicketID = new JTextField());
        addLabeledField(card, gbc, 2, "Arrival Station ID", txtCheckOutStationID = new JTextField());
        
        JButton btnCheckOut = new JButton("Check Out");
        stylePrimaryButton(btnCheckOut);
        btnCheckOut.addActionListener(e -> performCheckOut());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 6, 6, 6);
        card.add(btnCheckOut, gbc);
        
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

        // lblBaseFare = createSummaryLine(panel, "Base Fare", "PKR 0");
        // lblPassengerMultiplier = createSummaryLine(panel, "Passenger Multiplier", "x1.0");
        // lblDiscountInfo = createSummaryLine(panel, "Ticket/Validity Multipliers", "x1.0");

        panel.add(Box.createVerticalStrut(12));
        // lblTotalFare = new JLabel("PKR 0");
        // lblTotalFare.setFont(getCustomFont(Font.BOLD, 26));
        // lblTotalFare.setForeground(PRIMARY_COLOR);
        // panel.add(lblTotalFare);

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
        btn.setFont(getCustomFont("Station Management".equals(text) ? Font.BOLD : Font.PLAIN, 14));
        btn.setForeground("Station Management".equals(text) ? Color.WHITE : SKY);
        btn.setBackground("Station Management".equals(text) ? SIDEBAR_ACTIVE : SIDEBAR_BACKGROUND);
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



    private void issueGuestTicket() {
        if (!validateRequiredFields(true)) {
            return;
        }

        try {
            // Get selected route
            String routeName = (String) cmbRoute.getSelectedItem();
            Route selectedRoute = routes.stream()
                    .filter(r -> r.getRouteName().equals(routeName))
                    .findFirst()
                    .orElse(null);
                    if (selectedRoute == null) {
                JOptionPane.showMessageDialog(this, "Selected route is invalid.", "Validation",
                JOptionPane.WARNING_MESSAGE);
                return;
            }
            System.out.println("Selected Route: " + selectedRoute.getRouteName() + ", Cost: " + selectedRoute.getCost());

            // Get payment details
            String paymentMethod = (String) cmbPaymentMethod.getSelectedItem();
            String paymentDetails = "";
            
            if ("Card".equals(paymentMethod)) {
                paymentDetails = txtCardDetails.getText().trim();
            }

            // Get passenger ID if provided
            String passengerIDText = txtPassengerID.getText().trim();
            Ticket ticket;
            
            // TODO: Get actual boarding station ID from UI or session
            // For now, using station ID 1 as default
            int boardingStationID = 1;
            
            if (!passengerIDText.isEmpty()) {
                // Request ticket for registered passenger
                try {
                    int passengerID = Integer.parseInt(passengerIDText);
                    PassengerPersistanceHandler pph = new PassengerPersistanceHandler();
                    Passenger passenger = (Passenger) pph.find(passengerID);
                  
                    if (passenger == null) {
                        JOptionPane.showMessageDialog(this, "Passenger not found with ID: " + passengerID,
                                "Validation", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    ticket = stationRegister.requestTicket(selectedRoute, paymentMethod, passenger,
                            paymentDetails, boardingStationID);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid Passenger ID format.",
                            "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else {
                // Request guest ticket (no passenger)
                
                ticket = stationRegister.requestTicket(selectedRoute, paymentMethod,
                        paymentDetails, boardingStationID);
            }
            
            // Store the issued ticket
            lastIssuedTicket = ticket;
            lastCalculatedFare = selectedRoute.getCost();
            
            // Update summary cards
            updateSummaryCards();
            
            JOptionPane.showMessageDialog(this, "Ticket issued successfully!\nTicket ID: " + ticket.getTicketID(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                    
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Payment Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred while issuing the ticket: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private boolean validateRequiredFields(boolean showDialog) {
        String paymentMethod = (String) cmbPaymentMethod.getSelectedItem();

        if (cmbRoute.getSelectedItem() == null || paymentMethod == null) {
            if (showDialog) {
                JOptionPane.showMessageDialog(this, "Please select a route and payment method.", "Validation", JOptionPane.WARNING_MESSAGE);
            }
            return false;
        }

        if ("Card".equals(paymentMethod)) {
            String cardDetails = txtCardDetails.getText().trim();
            if (cardDetails.isEmpty()) {
                if (showDialog) {
                    JOptionPane.showMessageDialog(this, "Card details cannot be empty for card payments.", "Validation", JOptionPane.WARNING_MESSAGE);
                }
                return false;
            }
            long commaCount = cardDetails.chars().filter(ch -> ch == ',').count();
            if (commaCount != 2) {
                if (showDialog) {
                    JOptionPane.showMessageDialog(this, "Card details must contain exactly two commas.", "Validation", JOptionPane.WARNING_MESSAGE);
                }
                return false;
            }
        } else if ("Walllet".equals(paymentMethod)) { // Typo in original code
            if (txtPassengerID.getText().trim().isEmpty()) {
                if (showDialog) {
                    JOptionPane.showMessageDialog(this, "Passenger ID is required for Wallet payment.", "Validation", JOptionPane.WARNING_MESSAGE);
                }
                return false;
            }
        } else if ("Wallet".equals(paymentMethod)) {
            if (txtPassengerID.getText().trim().isEmpty()) {
                if (showDialog) {
                    JOptionPane.showMessageDialog(this, "Passenger ID is required for Wallet payment.", "Validation", JOptionPane.WARNING_MESSAGE);
                }
                return false;
            }
        }

        return true;
    }

    private void refreshAllViews() {
        updateSummaryCards();
    }



    private void updateSummaryCards() {
        // TODO: Update this with real data
    }
    
    private void performCheckIn() {
        try {
            // Validate inputs
            String ticketIDText = txtCheckInTicketID.getText().trim();
            String busIDText = txtCheckInBusID.getText().trim();
            String stationIDText = txtCheckInStationID.getText().trim();
            
            if (ticketIDText.isEmpty() || busIDText.isEmpty() || stationIDText.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please fill in all required fields (Ticket ID, Bus ID, and Boarding Station ID).", 
                    "Validation Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int ticketID = Integer.parseInt(ticketIDText);
            int busID = Integer.parseInt(busIDText);
            int boardingStationID = Integer.parseInt(stationIDText);
            
            // Perform check-in
            stationRegister.checkIn(ticketID, busID, boardingStationID);
            
            // Clear fields after successful check-in
            txtCheckInTicketID.setText("");
            txtCheckInBusID.setText("");
            txtCheckInStationID.setText("");
            
            JOptionPane.showMessageDialog(this, 
                "Check-in completed successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numeric values for Ticket ID, Bus ID, and Station ID.", 
                "Invalid Input", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Check-in failed: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void performCheckOut() {
        try {
            // Validate inputs
            String ticketIDText = txtCheckOutTicketID.getText().trim();
            String stationIDText = txtCheckOutStationID.getText().trim();
            
            if (ticketIDText.isEmpty() || stationIDText.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please fill in all required fields (Ticket ID and Arrival Station ID).", 
                    "Validation Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int ticketID = Integer.parseInt(ticketIDText);
            int arrivalStationID = Integer.parseInt(stationIDText);
            
            // Perform check-out
            stationRegister.checkOut(ticketID, arrivalStationID);
            
            // Clear fields after successful check-out
            txtCheckOutTicketID.setText("");
            txtCheckOutStationID.setText("");
            
            JOptionPane.showMessageDialog(this, 
                "Check-out completed successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numeric values for Ticket ID and Station ID.", 
                "Invalid Input", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Check-out failed: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
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


}