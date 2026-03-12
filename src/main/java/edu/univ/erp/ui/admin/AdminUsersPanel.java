package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;

public class AdminUsersPanel extends JPanel {

    AdminService serv;
    JTextField tf_u;
    JPasswordField tf_p;
    JComboBox<String> cb_role;

    JTextField tf_roll, tf_prog, tf_yr;
    JPanel p_stu;

    JTextField tf_dept, tf_tle;
    JPanel p_inst;

    Color blue = new Color(0, 102, 204);

    public AdminUsersPanel() {
        this.serv = new AdminService();
        setLayout(new MigLayout("insets 20", "[right]10[fill, grow]"));
        setBackground(Color.WHITE);

        JLabel l = new JLabel("Add New User");
        l.setFont(new Font("Segoe UI", Font.BOLD, 20));
        l.setForeground(new Color(50, 50, 50));
        add(l, "span 2, align center, wrap 20");

        tf_u = new JTextField(20);
        tf_p = new JPasswordField(20);
        cb_role = new JComboBox<>(new String[]{"STUDENT", "INSTRUCTOR"});

        add(new JLabel("Username:")); add(tf_u, "wrap");
        add(new JLabel("Password:")); add(tf_p, "wrap");
        add(new JLabel("Role:")); add(cb_role, "wrap 20");

        // dynamic parts
        stu_ui();
        inst_ui();

        JPanel cards = new JPanel(new CardLayout());
        cards.setOpaque(false);
        cards.add(p_stu, "STUDENT");
        cards.add(p_inst, "INSTRUCTOR");
        add(cards, "span 2, growx, wrap 20");

        JButton b = new JButton("Create User");
        b.setBackground(new Color(40, 167, 69)); // green
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);

        add(b, "span 2, align center, h 40!");

        cb_role.addActionListener(e -> {
            CardLayout cl = (CardLayout) cards.getLayout();
            cl.show(cards, (String) cb_role.getSelectedItem());
        });

        b.addActionListener(e -> save());
    }

    void stu_ui() {
        p_stu = new JPanel(new MigLayout("insets 10", "[right]10[fill, grow]"));
        p_stu.setBackground(new Color(245, 247, 250)); // light gray bg
        p_stu.setBorder(BorderFactory.createTitledBorder("Student Details"));

        tf_roll = new JTextField();
        tf_prog = new JTextField();
        tf_yr = new JTextField();

        p_stu.add(new JLabel("Roll No:")); p_stu.add(tf_roll, "wrap");
        p_stu.add(new JLabel("Program:")); p_stu.add(tf_prog, "wrap");
        p_stu.add(new JLabel("Year:")); p_stu.add(tf_yr, "wrap");
    }

    void inst_ui() {
        p_inst = new JPanel(new MigLayout("insets 10", "[right]10[fill, grow]"));
        p_inst.setBackground(new Color(245, 247, 250));
        p_inst.setBorder(BorderFactory.createTitledBorder("Instructor Details"));

        tf_dept = new JTextField();
        tf_tle = new JTextField();

        p_inst.add(new JLabel("Department:")); p_inst.add(tf_dept, "wrap");
        p_inst.add(new JLabel("Title:")); p_inst.add(tf_tle, "wrap");
    }

    void save() {
        String u = tf_u.getText();
        String p = new String(tf_p.getPassword());
        String r = (String) cb_role.getSelectedItem();

        try {
            if (u.isEmpty() || p.isEmpty()) throw new Exception("Fill all fields.");

            if ("STUDENT".equals(r)) {
                int y = Integer.parseInt(tf_yr.getText());
                serv.createStudent(u, p, tf_roll.getText(), tf_prog.getText(), y);
            } else {
                serv.createInstructor(u, p, tf_dept.getText(), tf_tle.getText());
            }
            JOptionPane.showMessageDialog(this, "User Created!");
            // clear
            tf_u.setText(""); tf_p.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}