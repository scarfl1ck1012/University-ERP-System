package edu.univ.erp.ui.student;

import edu.univ.erp.service.StudentService;
import edu.univ.erp.util.CsvExporter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StudentGradesPanel extends JPanel {

    JTable t;
    DefaultTableModel m;
    StudentService s_serv;
    String u;

    // This list will hold the "Table View" data for the CSV export
    List<String[]> exportData;

    public StudentGradesPanel(String name) {
        this.u = name;
        this.s_serv = new StudentService();
        this.exportData = new ArrayList<>();

        setLayout(new BorderLayout());

        JLabel head = new JLabel("My Grades");
        head.setFont(new Font("Arial", Font.BOLD, 18));
        head.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(head, BorderLayout.NORTH);

        // UPDATED COLUMNS as requested
        String[] c = {"Code", "Title", "Quiz", "MidSem", "EndSem", "Total", "Grade"};

        m = new DefaultTableModel(c, 0) {
            public boolean isCellEditable(int r, int col) { return false; }
        };

        t = new JTable(m);
        t.setRowHeight(30);
        t.getTableHeader().setReorderingAllowed(false);

        add(new JScrollPane(t), BorderLayout.CENTER);

        JPanel p = new JPanel(new FlowLayout());
        JButton b1 = new JButton("Refresh");
        JButton b2 = new JButton("Download CSV");

        p.add(b1);
        p.add(b2);
        add(p, BorderLayout.SOUTH);

        b1.addActionListener(e -> load());
        b2.addActionListener(e -> csv());

        load();
    }

    void load() {
        m.setRowCount(0);
        exportData.clear();

        SwingWorker<List<String[]>, Void> worker = new SwingWorker<List<String[]>, Void>() {
            @Override
            protected List<String[]> doInBackground() throws Exception {
                // Get raw data: [Code, Title, Component, Score]
                return s_serv.getGrades(u);
            }

            @Override
            protected void done() {
                try {
                    List<String[]> raw = get();
                    // 1. Get unique courses
                    List<String> codes = new ArrayList<>();
                    for(String[] r : raw) {
                        if(!codes.contains(r[0])) codes.add(r[0]);
                    }

                    // 2. Pivot data (Group by course)
                    for(String code : codes) {
                        String title = "";
                        double q = 0, mid = 0, end = 0;

                        // Find marks for this code
                        for(String[] r : raw) {
                            if(r[0].equals(code)) {
                                title = r[1];
                                double sc = Double.parseDouble(r[3]);
                                if(r[2].equalsIgnoreCase("Quiz")) q = sc;
                                else if(r[2].equalsIgnoreCase("Midterm")) mid = sc;
                                else if(r[2].equalsIgnoreCase("Final")) end = sc;
                            }
                        }

                        double tot = q + mid + end;
                        String grade = getLetter(tot);

                        // Add to table
                        String[] rowObj = {
                                code, title,
                                String.valueOf(q),
                                String.valueOf(mid),
                                String.valueOf(end),
                                String.valueOf(tot),
                                grade
                        };

                        m.addRow(rowObj);
                        exportData.add(rowObj);
                    }
                } catch(Exception e) {
                    JOptionPane.showMessageDialog(StudentGradesPanel.this, "Error: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    String getLetter(double d) {
        if (d >= 90) return "A+";
        if (d >= 80) return "A";
        if (d >= 70) return "B+";
        if (d >= 60) return "B";
        if (d >= 50) return "C";
        return "F";
    }

    void csv() {
        if (exportData.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No grades to export.");
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("transcript.csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                // We pass the "Pivoted" data to the exporter now
                CsvExporter.exportGradesToCsv(exportData, fc.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Saved!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}