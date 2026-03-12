package edu.univ.erp.ui.instructor;

import edu.univ.erp.domain.SectionDetails;
import edu.univ.erp.service.InstructorService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class GradebookPanel extends JPanel {

    JComboBox<SectionDetails> box;
    JTable tbl;
    DefaultTableModel model;
    InstructorService is;
    String u_name;

    Color blue = new Color(0, 102, 204);

    public GradebookPanel(String name) {
        this.u_name = name;
        this.is = new InstructorService();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // header
        JLabel l = new JLabel("Class Gradebook");
        l.setFont(new Font("Segoe UI", Font.BOLD, 18));
        l.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(l, BorderLayout.NORTH);

        // top controls
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(Color.WHITE);

        box = new JComboBox<>();
        box.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof SectionDetails) {
                    SectionDetails sec = (SectionDetails) v;
                    setText(sec.getCourseCode() + " - " + sec.getCourseTitle());
                }
                return this;
            }
        });

        JButton b_load = mkBtn("Load Students", blue);

        top.add(new JLabel("Select Class:"));
        top.add(box);
        top.add(b_load);

        JPanel headWrap = new JPanel(new BorderLayout());
        headWrap.setBackground(Color.WHITE);
        headWrap.add(l, BorderLayout.NORTH);
        headWrap.add(top, BorderLayout.CENTER);
        add(headWrap, BorderLayout.NORTH);

        // table columns (ID, Name, Roll, Scores, Total, Grade)
        String[] cols = {"Enroll ID", "Name", "Roll No", "Quiz (20)", "Midterm (30)", "Final (50)", "Total", "Grade"};

        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                // edit only marks (3, 4, 5)
                return c >= 3 && c <= 5;
            }
        };

        tbl = new JTable(model);
        styleT(tbl);

        // hide id
        tbl.getColumnModel().getColumn(0).setMinWidth(0);
        tbl.getColumnModel().getColumn(0).setMaxWidth(0);

        add(new JScrollPane(tbl), BorderLayout.CENTER);

        // bottom buttons
        JPanel bot = new JPanel();
        bot.setBackground(Color.WHITE);

        JButton b_calc = mkBtn("Compute Grades", Color.GRAY);
        JButton b_save = mkBtn("Save Scores", new Color(100, 200, 100)); // green
        JButton b_stat = mkBtn("Show Class Stats", new Color(255, 140, 0)); // orange

        bot.add(b_calc);
        bot.add(b_save);
        bot.add(b_stat);
        add(bot, BorderLayout.SOUTH);

        b_load.addActionListener(e -> loadStu());
        b_calc.addActionListener(e -> calc());
        b_save.addActionListener(e -> save());
        b_stat.addActionListener(e -> stats());

        loadSec();
    }

    JButton mkBtn(String t, Color c) {
        JButton b = new JButton(t);
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        return b;
    }

    void styleT(JTable t) {
        t.setRowHeight(30);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setGridColor(Color.LIGHT_GRAY);
        t.setShowVerticalLines(false);
        JTableHeader h = t.getTableHeader();
        h.setBackground(blue);
        h.setForeground(Color.WHITE);
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    void loadSec() {
        try {
            List<SectionDetails> l = is.getMySections(u_name);
            for (SectionDetails s : l) box.addItem(s);
        } catch (Exception e) { e.printStackTrace(); }
    }

    void loadStu() {
        SectionDetails sel = (SectionDetails) box.getSelectedItem();
        if (sel == null) return;

        try {
            model.setRowCount(0);
            List<String[]> list = is.getGradeSheet(sel.getSectionId());

            for (String[] r : list) {
                model.addRow(new Object[]{
                        r[0], r[1], r[2],
                        r[3], r[4], r[5],
                        "", ""
                });
            }
            if (list.isEmpty()) JOptionPane.showMessageDialog(this, "No students found.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    void calc() {
        if (tbl.isEditing()) tbl.getCellEditor().stopCellEditing();

        for (int i = 0; i < model.getRowCount(); i++) {
            double q = getVal(model.getValueAt(i, 3));
            double m = getVal(model.getValueAt(i, 4));
            double f = getVal(model.getValueAt(i, 5));

            double tot = q + m + f;

            String l;
            if (tot >= 90) l = "A+";
            else if (tot >= 80) l = "A";
            else if (tot >= 70) l = "B+";
            else if (tot >= 60) l = "B";
            else if (tot >= 50) l = "C";
            else l = "F";

            model.setValueAt(String.format("%.1f", tot), i, 6);
            model.setValueAt(l, i, 7);
        }
    }

    void stats() {
        calc();
        double s = 0;
        int c = 0;

        for (int i = 0; i < model.getRowCount(); i++) {
            String t = (String) model.getValueAt(i, 6);
            if (t != null && !t.isEmpty()) {
                s += Double.parseDouble(t);
                c++;
            }
        }

        if (c == 0) {
            JOptionPane.showMessageDialog(this, "Compute grades first.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Class Average: " + String.format("%.1f", s / c));
    }

    void save() {
        if (tbl.isEditing()) tbl.getCellEditor().stopCellEditing();

        int cnt = 0;
        String err = null;

        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                int eid = Integer.parseInt((String) model.getValueAt(i, 0));

                saveOne(eid, "Quiz", model.getValueAt(i, 3));
                saveOne(eid, "Midterm", model.getValueAt(i, 4));
                saveOne(eid, "Final", model.getValueAt(i, 5));

                cnt++;
            } catch (Exception e) {
                err = e.getMessage();
            }
        }

        if (err != null) JOptionPane.showMessageDialog(this, "Error: " + err);
        else JOptionPane.showMessageDialog(this, "Saved " + cnt + " valid scores!");
    }

    void saveOne(int id, String type, Object v) throws Exception {
        if (v != null && !v.toString().isEmpty()) {
            is.saveGrade(id, type, Double.parseDouble(v.toString()));
        }
    }

    double getVal(Object o) {
        try { return Double.parseDouble(o.toString()); } catch (Exception e) { return 0.0; }
    }
}