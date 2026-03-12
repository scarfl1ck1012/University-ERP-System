package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;

public class AdminSettingsPanel extends JPanel {

    AdminService as;
    JLabel stat;
    JButton btn;

    public AdminSettingsPanel() {
        this.as = new AdminService();
        setLayout(new MigLayout("fill, insets 50"));
        setBackground(Color.WHITE);

        JLabel l = new JLabel("System Controls");
        l.setFont(new Font("Segoe UI", Font.BOLD, 24));
        l.setForeground(new Color(50, 50, 50));
        add(l, "wrap, align center, gapbottom 30");

        // box
        JPanel p = new JPanel(new MigLayout("insets 20"));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        stat = new JLabel("Checking...");
        stat.setFont(new Font("Segoe UI", Font.BOLD, 18));

        btn = new JButton("Change Status");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(0, 102, 204)); // blue
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);

        p.add(new JLabel("Maintenance Mode: "), "split 2");
        p.add(stat, "wrap, gapbottom 20");
        p.add(btn, "align center, w 150!, h 40!");

        add(p, "align center");

        btn.addActionListener(e -> toggle());
        check();
    }

    void check() {
        try {
            boolean on = as.isMaintenanceMode();
            if (on) {
                stat.setText("ON (LOCKED)");
                stat.setForeground(Color.RED);
                btn.setText("Turn OFF");
                btn.setBackground(new Color(220, 53, 69)); // red
            } else {
                stat.setText("OFF (NORMAL)");
                stat.setForeground(new Color(40, 167, 69)); // green
                btn.setText("Turn ON");
                btn.setBackground(new Color(0, 102, 204)); // blue
            }
        } catch (Exception e) {
            stat.setText("Error");
        }
    }

    void toggle() {
        try {
            boolean cur = as.isMaintenanceMode();
            as.setMaintenanceMode(!cur);
            check();
            JOptionPane.showMessageDialog(this, "Settings Updated.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}