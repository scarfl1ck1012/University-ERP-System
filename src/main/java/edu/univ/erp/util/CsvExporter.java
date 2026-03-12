package edu.univ.erp.util;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

public class CsvExporter {

    // saving to file
    public static void exportGradesToCsv(List<String[]> data, String path) throws Exception {

        FileWriter fw = new FileWriter(path);
        PrintWriter pw = new PrintWriter(fw);

        // FIX: Updated header to match the new Student Grades Table
        pw.println("Course Code,Course Title,Quiz,MidSem,EndSem,Total,Grade");

        // loop data
        for (String[] row : data) {
            // FIX: Now printing all 7 columns instead of just 4
            // row structure: [Code, Title, Quiz, Mid, End, Total, Grade]

            pw.print("\"" + row[0] + "\","); // Code
            pw.print("\"" + row[1] + "\","); // Title

            // Check if null to avoid "null" text in csv, simpler for student code
            pw.print("\"" + (row[2]==null?"":row[2]) + "\","); // Quiz
            pw.print("\"" + (row[3]==null?"":row[3]) + "\","); // Mid
            pw.print("\"" + (row[4]==null?"":row[4]) + "\","); // End
            pw.print("\"" + (row[5]==null?"":row[5]) + "\","); // Total

            // Last one gets println
            pw.println("\"" + (row[6]==null?"":row[6]) + "\""); // Grade
        }

        pw.flush();
        pw.close();
        fw.close();
    }
}