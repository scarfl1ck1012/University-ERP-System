package edu.univ.erp.ui.student;

import edu.univ.erp.domain.SectionDetails;
import edu.univ.erp.service.StudentService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentTimetablePanel extends JPanel {

    JTable t_grid, t_list;
    DefaultTableModel m_grid, m_list;
    StudentService serv;
    String usr;

    public StudentTimetablePanel(String name) {
        this.usr = name;
        this.serv = new StudentService();
        setLayout(new BorderLayout());

        // --- TOP: Visual Schedule ---
        JPanel p1 = new JPanel(new BorderLayout());
        p1.setBorder(BorderFactory.createTitledBorder("Weekly View"));

        String[] c_grid = {"Day", "8-9", "9-10", "10-11", "11-12", "12-1", "1-2", "2-3", "3-4", "4-5"};
        Object[][] d_grid = {
                {"Mon", "", "", "", "", "", "", "", "", ""},
                {"Tue", "", "", "", "", "", "", "", "", ""},
                {"Wed", "", "", "", "", "", "", "", "", ""},
                {"Thu", "", "", "", "", "", "", "", "", ""},
                {"Fri", "", "", "", "", "", "", "", "", ""}
        };

        m_grid = new DefaultTableModel(d_grid, c_grid) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        t_grid = new JTable(m_grid);
        t_grid.setRowHeight(50);
        t_grid.setGridColor(Color.GRAY);
        t_grid.setShowGrid(true);
        // Fix width of Day column
        t_grid.getColumnModel().getColumn(0).setMaxWidth(50);

        p1.add(new JScrollPane(t_grid));
        p1.setPreferredSize(new Dimension(800, 300));

        // --- BOTTOM: List ---
        JPanel p2 = new JPanel(new BorderLayout());
        p2.setBorder(BorderFactory.createTitledBorder("My Registered Courses"));

        String[] c_list = {"ID", "Code", "Title", "Instructor", "Room"};
        m_list = new DefaultTableModel(c_list, 0);
        t_list = new JTable(m_list);

        // Hide ID
        t_list.getColumnModel().getColumn(0).setMinWidth(0);
        t_list.getColumnModel().getColumn(0).setMaxWidth(0);

        p2.add(new JScrollPane(t_list));

        JPanel bp = new JPanel();
        JButton b1 = new JButton("Refresh");
        JButton b2 = new JButton("Drop Selected");
        bp.add(b1); bp.add(b2);
        p2.add(bp, BorderLayout.SOUTH);

        add(p1, BorderLayout.NORTH);
        add(p2, BorderLayout.CENTER);

        b1.addActionListener(e -> refresh());
        b2.addActionListener(e -> drop());

        refresh();
    }

    void refresh() {
        // Clear Grid
        for(int i=0; i<5; i++)
            for(int j=1; j<10; j++)
                m_grid.setValueAt("", i, j);

        // Clear List
        m_list.setRowCount(0);

        try {
            List<SectionDetails> list = serv.getStudentSchedule(usr);

            for (SectionDetails s : list) {
                // 1. Fill Grid
                String txt = s.getDayTime();
                String cell = s.getCourseCode() + " (" + s.getRoom() + ")";

                fillGrid(txt, cell);

                // 2. Fill List
                m_list.addRow(new Object[]{
                        s.getSectionId(), s.getCourseCode(), s.getCourseTitle(),
                        s.getInstructorName(), s.getRoom()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void fillGrid(String t, String v) {
        int c = -1;
        // Robust Parsing for "8-9", "08:00", "8 AM"
        if (t.contains("8") && !t.contains("18")) c = 1;
        else if (t.contains("9")) c = 2;
        else if (t.contains("10")) c = 3;
        else if (t.contains("11")) c = 4;
        else if (t.contains("12")) c = 5;
        else if (t.contains("1") && !t.contains("11") && !t.contains("12")) c = 6;
        else if (t.contains("2") && !t.contains("12")) c = 7;
        else if (t.contains("3")) c = 8;
        else if (t.contains("4")) c = 9;

        if (c != -1) {
            if (t.contains("M")) m_grid.setValueAt(v, 0, c);
            if (t.contains("T") && !t.contains("Th")) m_grid.setValueAt(v, 1, c);
            if (t.contains("W")) m_grid.setValueAt(v, 2, c);
            if (t.contains("Th")) m_grid.setValueAt(v, 3, c);
            if (t.contains("F")) m_grid.setValueAt(v, 4, c);
        }
    }

    void drop() {
        int r = t_list.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Select a course from the list below.");
            return;
        }
        try {
            int sid = (int) m_list.getValueAt(r, 0);
            serv.dropCourse(usr, sid);
            JOptionPane.showMessageDialog(this, "Dropped!");
            refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}