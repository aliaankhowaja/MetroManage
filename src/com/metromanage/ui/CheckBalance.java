package com.metromanage.ui;
import javax.swing.*;
import java.awt.*;

public class CheckBalance extends JFrame {
    public CheckBalance() {
        setTitle("Check Balance");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(520, 320);
        setLocationRelativeTo(null);
        add(new JLabel("Check Balance form", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}

