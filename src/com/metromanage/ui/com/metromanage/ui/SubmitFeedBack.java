package com.metromanage.ui;
import javax.swing.*;
import java.awt.*;

public class SubmitFeedBack extends JFrame {
    public SubmitFeedBack() {
        setTitle("Submit Feedback");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(560, 380);
        setLocationRelativeTo(null);
        add(new JLabel("SubmitFeedback form (implement fields here)", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
