package com.metromanage.ui;
import javax.swing.*;
import java.awt.*;

public class ViewPeakHours extends JFrame {
    public ViewPeakHours() {
        setTitle("View Peak Hours");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(640, 420);
        setLocationRelativeTo(null);
        add(new JLabel("View Peak Hours form", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
