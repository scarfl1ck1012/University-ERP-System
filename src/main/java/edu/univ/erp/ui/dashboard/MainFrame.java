package edu.univ.erp.ui.dashboard;

import edu.univ.erp.ui.auth.LoginWindow;
import edu.univ.erp.ui.auth.ChangePasswordDialog;

import edu.univ.erp.ui.student.*;
import edu.univ.erp.ui.instructor.*;
import edu.univ.erp.ui.admin.*;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    String u;
    String r;

    CardLayout cl;
    JPanel p_center;

    public MainFrame(String user, String role) {
        this.u = user;
        this.r = role;

        setTitle("University ERP - " + r);
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- TOP HEADER ---
        // Simple panel, default color
        JPanel head = new JPanel(new FlowLayout(FlowLayout.LEFT));
        head.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        JLabel l = new JLabel("Dashboard: " + r + " | User: " + u);
        l.setFont(new Font("Arial", Font.BOLD, 16));
        head.add(l);

        add(head, BorderLayout.NORTH);

        // --- LEFT SIDEBAR ---
        // Standard panel with a black line on the right
        JPanel p_side = new JPanel(new GridLayout(12, 1, 5, 5)); // Grid for buttons
        p_side.setPreferredSize(new Dimension(200, 0));
        p_side.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- CENTER (Screens) ---
        cl = new CardLayout();
        p_center = new JPanel(cl);

        // Add sidebar and center to a container to manage the divider line
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.add(p_side, BorderLayout.WEST);
        mainContainer.add(p_center, BorderLayout.CENTER);

        add(mainContainer, BorderLayout.CENTER);

        // --- BUTTON CREATION HELPER ---
        // We define a simple helper here to make standard buttons

        // Load screens based on role
        if(r.equals("STUDENT")) {
            nav(p_side, "Course Catalog", new StudentCatalogPanel(u));
            nav(p_side, "My Timetable", new StudentTimetablePanel(u));
            nav(p_side, "My Grades", new StudentGradesPanel(u));
        }
        else if(r.equals("INSTRUCTOR")) {
            nav(p_side, "My Sections", new InstructorSectionsPanel(u));
            nav(p_side, "Gradebook", new GradebookPanel(u));
        }
        else if(r.equals("ADMIN")) {
            nav(p_side, "System Settings", new AdminSettingsPanel());
            nav(p_side, "Manage Users", new AdminUsersPanel());
            nav(p_side, "Manage Courses", new AdminCoursesPanel());
        }

        // Spacer
        p_side.add(new JLabel(""));

        // Bottom Actions (Standard Buttons)
        JButton b_pass = new JButton("Change Password");
        JButton b_out = new JButton("Logout");

        b_pass.addActionListener(e -> new ChangePasswordDialog(this, u).setVisible(true));
        b_out.addActionListener(e -> logout());

        p_side.add(b_pass);
        p_side.add(b_out);
    }

    // Helper to add a button and panel
    void nav(JPanel side, String title, JPanel panel) {
        p_center.add(panel, title);

        JButton b = new JButton(title);
        // No setBackground, No setForeground -> Keeps the "Metal" look (Standard Gray/Blue)
        b.setFocusPainted(false);

        b.addActionListener(e -> cl.show(p_center, title));
        side.add(b);
    }

    void logout() {
        dispose();
        new LoginWindow().setVisible(true);
    }
}