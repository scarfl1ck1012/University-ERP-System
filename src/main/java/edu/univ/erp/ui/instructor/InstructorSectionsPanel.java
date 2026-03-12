package edu.univ.erp.ui.instructor;

import edu.univ.erp.domain.SectionDetails;
import edu.univ.erp.service.InstructorService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InstructorSectionsPanel extends JPanel {

    JTable t;
    DefaultTableModel tm;
    InstructorService serv;
    String user;

    public InstructorSectionsPanel(String u) {
        this.user = u;
        this.serv = new InstructorService();

        setLayout(new BorderLayout());

        // Header
        JLabel l = new JLabel("My Teaching Schedule");
        l.setFont(new Font("Arial", Font.BOLD, 18));
        l.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(l, BorderLayout.NORTH);

        // Table Setup
        String[] c = {"ID", "Code", "Title", "Schedule", "Room", "Capacity"};

        tm = new DefaultTableModel(c, 0) {
            public boolean isCellEditable(int r, int col) { return false; }
        };

        t = new JTable(tm);
        t.setRowHeight(40); // Make rows big enough to read
        t.getTableHeader().setReorderingAllowed(false);
        t.setFont(new Font("Arial", Font.PLAIN, 14));
        t.setGridColor(Color.GRAY);
        t.setShowGrid(true); // Ensure grid lines show

        // Hide ID Column
        t.getColumnModel().getColumn(0).setMinWidth(0);
        t.getColumnModel().getColumn(0).setMaxWidth(0);
        t.getColumnModel().getColumn(0).setWidth(0);

        add(new JScrollPane(t), BorderLayout.CENTER);

        // Refresh Button
        JPanel p = new JPanel();
        JButton b = new JButton("Refresh List");
        b.setBackground(Color.LIGHT_GRAY);

        p.add(b);
        add(p, BorderLayout.SOUTH);

        b.addActionListener(e -> getData());

        // Load data immediately
        getData();
    }

    void getData() {
        try {
            tm.setRowCount(0); // Clear old data
            List<SectionDetails> list = serv.getMySections(user);

            // DEBUG PRINT: Check your VS Code Terminal when this runs!
            System.out.println("Loading sections for " + user + "... Found: " + list.size());

            for (SectionDetails s : list) {
                Object[] row = {
                        s.getSectionId(),
                        s.getCourseCode(),
                        s.getCourseTitle(),
                        s.getDayTime(),
                        s.getRoom(),
                        s.getCapacity()
                };
                tm.addRow(row);
            }

            // If list is empty, show a message so we know it ran
            if (list.isEmpty()) {
                // Optional: You can uncomment this if you want a popup when empty
                // JOptionPane.showMessageDialog(this, "No sections found for " + user);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}