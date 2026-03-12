package edu.univ.erp.ui.admin;

import edu.univ.erp.domain.SectionDetails;
import edu.univ.erp.service.AdminService;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class AdminCoursesPanel extends JPanel {

    AdminService as;

    JTextField c_code, c_title, c_cred;
    JComboBox<String> cb_c, cb_i;
    JTextField tf_t, tf_r, tf_cap, tf_sem, tf_y;

    JTable t;
    DefaultTableModel tm;
    int sel_id = -1;

    Color blue = new Color(0, 102, 204);
    Color bg = new Color(245, 247, 250);

    public AdminCoursesPanel() {
        this.as = new AdminService();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // header
        JLabel l = new JLabel("Manage Courses & Sections");
        l.setFont(new Font("Segoe UI", Font.BOLD, 20));
        l.setForeground(new Color(50, 50, 50));
        l.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(l, BorderLayout.NORTH);

        // forms panel (Holds the two forms)
        JPanel p_forms = new JPanel(new GridLayout(1, 2, 20, 0));
        p_forms.setBackground(Color.WHITE);
        p_forms.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Course Form
        JPanel p1 = mkPanel("1. Course Actions");
        c_code = new JTextField();
        c_title = new JTextField();
        c_cred = new JTextField();
        JButton b_mk = mkBtn("Create", new Color(100, 200, 100));
        JButton b_del = mkBtn("Delete", new Color(220, 53, 69)); // red

        addItem(p1, "Code:", c_code);
        addItem(p1, "Title:", c_title);
        addItem(p1, "Credits:", c_cred);

        JPanel bp1 = new JPanel(new FlowLayout());
        bp1.setOpaque(false);
        bp1.add(b_mk); bp1.add(b_del);
        p1.add(bp1, "span 2, align center, gaptop 5"); // Reduced gap

        // 2. Section Form
        JPanel p2 = mkPanel("2. Section Actions");
        cb_c = new JComboBox<>();
        cb_i = new JComboBox<>();
        tf_t = new JTextField();
        tf_r = new JTextField();
        tf_cap = new JTextField();
        tf_sem = new JTextField("Spring");
        tf_y = new JTextField("2026");

        JButton b_sec = mkBtn("Create Section", new Color(100, 200, 100));
        JButton b_up = mkBtn("Update Selected", blue);

        addItem(p2, "Course:", cb_c);
        addItem(p2, "Instructor:", cb_i);
        addItem(p2, "Time:", tf_t);
        addItem(p2, "Room:", tf_r);
        addItem(p2, "Capacity:", tf_cap);

        p2.add(new JLabel("Sem/Year:"));
        JPanel sub = new JPanel(new GridLayout(1, 2, 5, 0));
        sub.setOpaque(false);
        sub.add(tf_sem); sub.add(tf_y);
        p2.add(sub, "wrap");

        JPanel bp2 = new JPanel(new FlowLayout());
        bp2.setOpaque(false);
        bp2.add(b_sec); bp2.add(b_up);
        p2.add(bp2, "span 2, align center, gaptop 5"); // Reduced gap

        p_forms.add(p1);
        p_forms.add(p2);

        // --- SCROLL PANE FIX ---
        // Wrap the forms in a ScrollPane so buttons are always reachable
        JScrollPane scroll = new JScrollPane(p_forms);
        scroll.setBorder(null); // Remove ugly border
        scroll.getVerticalScrollBar().setUnitIncrement(16); // Make scrolling smoother
        add(scroll, BorderLayout.CENTER);

        // 3. Table (Bottom)
        String[] c = {"ID", "Code", "Title", "Instructor", "Schedule", "Room", "Cap"};
        tm = new DefaultTableModel(c, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        t = new JTable(tm);
        styleT(t);

        // hide id
        t.getColumnModel().getColumn(0).setMinWidth(0);
        t.getColumnModel().getColumn(0).setMaxWidth(0);

        t.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && t.getSelectedRow() != -1) {
                loadSel();
            }
        });

        JPanel p_bot = new JPanel(new BorderLayout());
        p_bot.setBorder(BorderFactory.createTitledBorder("Existing Sections"));
        p_bot.setBackground(Color.WHITE);
        p_bot.add(new JScrollPane(t));

        // Reduced height slightly to give more room to the top forms
        p_bot.setPreferredSize(new Dimension(800, 200));

        add(p_bot, BorderLayout.SOUTH);

        // actions
        b_mk.addActionListener(e -> saveC());
        b_del.addActionListener(e -> delC());
        b_sec.addActionListener(e -> saveS());
        b_up.addActionListener(e -> upS());

        refresh();
    }

    // helpers
    JPanel mkPanel(String t) {
        // Reduced gap between components
        JPanel p = new JPanel(new MigLayout("wrap 2, gap 5", "[right][fill, grow]"));
        p.setBackground(bg);
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), t));
        return p;
    }

    void addItem(JPanel p, String l, JComponent c) {
        p.add(new JLabel(l)); p.add(c);
    }

    JButton mkBtn(String t, Color c) {
        JButton b = new JButton(t);
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return b;
    }

    void styleT(JTable tbl) {
        tbl.setRowHeight(30);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl.setGridColor(Color.LIGHT_GRAY);
        tbl.setShowVerticalLines(false);
        JTableHeader h = tbl.getTableHeader();
        h.setBackground(blue);
        h.setForeground(Color.WHITE);
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    void refresh() {
        try {
            cb_c.removeAllItems();
            cb_i.removeAllItems();
            for (String s : as.getAllCourseCodes()) cb_c.addItem(s);
            for (String s : as.getAllInstructors()) cb_i.addItem(s);

            tm.setRowCount(0);
            List<SectionDetails> list = as.getAllSections();
            for (SectionDetails s : list) {
                tm.addRow(new Object[]{
                        s.getSectionId(), s.getCourseCode(), s.getCourseTitle(),
                        s.getInstructorName(), s.getDayTime(), s.getRoom(), s.getCapacity()
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    void loadSel() {
        int r = t.getSelectedRow();
        if (r == -1) return;
        sel_id = (int) tm.getValueAt(r, 0);
        cb_c.setSelectedItem(tm.getValueAt(r, 1));
        cb_i.setSelectedItem(tm.getValueAt(r, 3));
        tf_t.setText((String) tm.getValueAt(r, 4));
        tf_r.setText((String) tm.getValueAt(r, 5));
        tf_cap.setText(String.valueOf(tm.getValueAt(r, 6)));
    }

    void saveC() {
        try {
            as.createCourse(c_code.getText(), c_title.getText(), Integer.parseInt(c_cred.getText()));
            JOptionPane.showMessageDialog(this, "Course Created!");
            refresh();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    void delC() {
        String code = c_code.getText();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Course Code to delete.");
            return;
        }
        try {
            as.deleteCourse(code);
            JOptionPane.showMessageDialog(this, "Deleted!");
            refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    void saveS() {
        try {
            int cap = Integer.parseInt(tf_cap.getText());
            if (cap < 1) throw new Exception("Capacity must be positive.");

            as.createSection(
                    (String)cb_c.getSelectedItem(), (String)cb_i.getSelectedItem(),
                    tf_t.getText(), tf_r.getText(), cap, tf_sem.getText(), Integer.parseInt(tf_y.getText()));
            JOptionPane.showMessageDialog(this, "Section Created!");
            refresh();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    void upS() {
        if (sel_id == -1) {
            JOptionPane.showMessageDialog(this, "Select a section first.");
            return;
        }
        try {
            int cap = Integer.parseInt(tf_cap.getText());
            if (cap < 1) throw new Exception("Capacity must be positive.");

            as.updateSection(sel_id, tf_r.getText(), cap);
            JOptionPane.showMessageDialog(this, "Updated!");
            refresh();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }
}