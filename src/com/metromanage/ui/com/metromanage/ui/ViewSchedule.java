package com.metromanage.ui;
import javax.swing.*;
import java.awt.*;

public class ViewSchedule extends JFrame {
    public ViewSchedule() {
        setTitle("View Schedule");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(720, 500);
        setLocationRelativeTo(null);
        add(new JLabel("View Schedule form", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}