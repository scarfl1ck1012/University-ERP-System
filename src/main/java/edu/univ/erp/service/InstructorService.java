package edu.univ.erp.service;

import edu.univ.erp.data.DatabaseConnector;
import edu.univ.erp.domain.SectionDetails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class InstructorService {

    // get the sections for the teacher
    public List<SectionDetails> getMySections(String u_name) throws Exception {
        List<SectionDetails> my_list = new ArrayList<>();
        int t_id = getUserId(u_name);

        String q = "SELECT s.section_id, c.code, c.title, u.username as instructor, " +
                     "s.day_time, s.room, s.capacity " +
                     "FROM sections s " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "JOIN auth_db.users_auth u ON s.instructor_id = u.user_id " +
                     "WHERE s.instructor_id = ?";

        try (Connection c = DatabaseConnector.getErpConnection();
             PreparedStatement p = c.prepareStatement(q)) {

            p.setInt(1, t_id);

            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                my_list.add(new SectionDetails(
                    rs.getInt("section_id"),
                    rs.getString("code"),
                    rs.getString("title"),
                    rs.getString("instructor"),
                    rs.getString("day_time"),
                    rs.getString("room"),
                    rs.getInt("capacity")
                ));
            }
        }
        return my_list;
    }

    // find students in the section
    public List<String[]> getEnrolledStudents(int sec_id) throws Exception {
        List<String[]> s_list = new ArrayList<>();

        String sql_query = "SELECT e.enrollment_id, u.username, st.roll_no " +
                     "FROM enrollments e " +
                     "JOIN students st ON e.student_id = st.user_id " +
                     "JOIN auth_db.users_auth u ON st.user_id = u.user_id " +
                     "WHERE e.section_id = ?";

        try(Connection con = DatabaseConnector.getErpConnection();
            PreparedStatement ps = con.prepareStatement(sql_query)) {

            ps.setInt(1, sec_id);

            ResultSet r = ps.executeQuery();
            while(r.next()) {
                String[] temp = new String[3];
                temp[0] = String.valueOf(r.getInt("enrollment_id"));
                temp[1] = r.getString("username");
                temp[2] = r.getString("roll_no");
                s_list.add(temp);
            }
        }
        return s_list;
    }

    // saving the marks to db
    public void saveGrade(int e_id, String type, double marks) throws Exception {
        // checking if admin locked the system
        AdminService admin = new AdminService();
        if(admin.isMaintenanceMode()) {
            throw new Exception("Maintenance Mode is ON. Grades cannot be saved.");
        }

        // using upsert just in case
        String s = "INSERT INTO grades (enrollment_id, component, score) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE score = VALUES(score)";

        try (Connection c = DatabaseConnector.getErpConnection();
             PreparedStatement p = c.prepareStatement(s)) {
            p.setInt(1, e_id);
            p.setString(2, type);
            p.setDouble(3, marks);
            p.executeUpdate();
        }
    }

    // get the whole sheet
    public List<String[]> getGradeSheet(int sec_id) throws Exception {
        List<String[]> sheet = new ArrayList<>();

        String q = "SELECT e.enrollment_id, u.username, st.roll_no " +
                             "FROM enrollments e " +
                             "JOIN students st ON e.student_id = st.user_id " +
                             "JOIN auth_db.users_auth u ON st.user_id = u.user_id " +
                             "WHERE e.section_id = ?";

        try (Connection con = DatabaseConnector.getErpConnection();
             PreparedStatement st = con.prepareStatement(q)) {
            st.setInt(1, sec_id);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    int eid = rs.getInt("enrollment_id");
                    String name = rs.getString("username");
                    String roll = rs.getString("roll_no");

                    double q_marks = getScore(con, eid, "Quiz");
                    double m_marks = getScore(con, eid, "Midterm");
                    double f_marks = getScore(con, eid, "Final");

                    String[] row = {
                        String.valueOf(eid), name, roll,
                        (q_marks == -1) ? "" : String.valueOf(q_marks),
                        (m_marks == -1) ? "" : String.valueOf(m_marks),
                        (f_marks == -1) ? "" : String.valueOf(f_marks)
                    };
                    sheet.add(row);
                }
            }
        }
        return sheet;
    }

    private double getScore(Connection c, int enroll_id, String comp) throws java.sql.SQLException {
        String sql = "SELECT score FROM grades WHERE enrollment_id = ? AND component = ?";
        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, enroll_id);
            p.setString(2, comp);
            try (ResultSet r = p.executeQuery()) {
                if(r.next()) {
                    return r.getDouble("score");
                }
            }
        }
        return -1;
    }

    private int getUserId(String u) throws Exception {
        String q = "SELECT user_id FROM users_auth WHERE username = ?";
        try (Connection c = DatabaseConnector.getAuthConnection();
             PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, u);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) return rs.getInt("user_id");
            }
        }
        throw new Exception("User not found: " + u);
    }
}