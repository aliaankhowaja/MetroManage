package com.metromanage.ui;
import javax.swing.*;
import java.awt.*;

public class PurchaseTicket extends JFrame {
    public PurchaseTicket() {
        setTitle("Purchase Ticket");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 420);
        setLocationRelativeTo(null);
        add(new JLabel("PurchaseTicket form (implement fields here)", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}