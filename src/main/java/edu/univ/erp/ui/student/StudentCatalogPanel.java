package edu.univ.erp.ui.student;

import edu.univ.erp.domain.SectionDetails;
import edu.univ.erp.service.StudentService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StudentCatalogPanel extends JPanel {
    JTable t;
    DefaultTableModel tm;
    StudentService ss;
    String u_name;

    public StudentCatalogPanel(String user) {
        this.u_name = user;
        this.ss = new StudentService();
        setLayout(new BorderLayout());

        // Basic Label
        add(new JLabel("Course Catalog List"), BorderLayout.NORTH);

        String[] c = {"ID", "Code", "Title", "Instructor", "Room", "Cap"};
        tm = new DefaultTableModel(c, 0);
        t = new JTable(tm);

        // No fancy styling, just default grid
        add(new JScrollPane(t), BorderLayout.CENTER);

        JPanel p = new JPanel();
        JButton b_ref = new JButton("Refresh");
        JButton b_reg = new JButton("Register");
        p.add(b_ref); p.add(b_reg);
        add(p, BorderLayout.SOUTH);

        b_ref.addActionListener(e -> getData());
        b_reg.addActionListener(e -> reg());
        getData();
    }

    void getData() {
        try {
            tm.setRowCount(0);
            for(SectionDetails s : ss.getAllSections()) {
                tm.addRow(new Object[]{s.getSectionId(), s.getCourseCode(), s.getCourseTitle(), s.getInstructorName(), s.getRoom(), s.getCapacity()});
            }
        } catch(Exception e){}
    }

    void reg() {
        int r = t.getSelectedRow();
        if(r == -1) return;
        try {
            ss.register(u_name, (int)tm.getValueAt(r, 0));
            JOptionPane.showMessageDialog(this, "Registered");
        } catch(Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }
}