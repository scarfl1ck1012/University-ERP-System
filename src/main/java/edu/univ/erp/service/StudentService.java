package edu.univ.erp.service;

import edu.univ.erp.data.DatabaseConnector;
import edu.univ.erp.domain.SectionDetails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StudentService {

    // show all courses
    public List<SectionDetails> getAllSections() throws Exception {
        List<SectionDetails> l = new ArrayList<>();

        String query = "SELECT s.section_id, c.code, c.title, u.username as instructor, " +
                     "s.day_time, s.room, s.capacity " +
                     "FROM sections s " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "JOIN auth_db.users_auth u ON s.instructor_id = u.user_id";

        try (Connection c = DatabaseConnector.getErpConnection();
             PreparedStatement p = c.prepareStatement(query);
             ResultSet r = p.executeQuery()) {
            while(r.next()) {
                l.add(new SectionDetails(
                    r.getInt("section_id"),
                    r.getString("code"),
                    r.getString("title"),
                    r.getString("instructor"),
                    r.getString("day_time"),
                    r.getString("room"),
                    r.getInt("capacity")
                ));
            }
        }
        return l;
    }

    // register for a class
    public void register(String u_name, int s_id) throws Exception {

        // checking admin lock
        if(new AdminService().isMaintenanceMode()) {
            throw new Exception("System is in Maintenance Mode. Registration is disabled.");
        }

        int stu_id = getUserId(u_name);

        // validation check
        String sql_check = "SELECT s.capacity, " +
                   "(SELECT COUNT(*) FROM enrollments WHERE section_id = ?) as count, " +
                   "(SELECT COUNT(*) FROM enrollments WHERE section_id = ? AND student_id = ?) as enrolled " +
                   "FROM sections s WHERE s.section_id = ?";

        try (Connection con = DatabaseConnector.getErpConnection()) {
            try (PreparedStatement ps = con.prepareStatement(sql_check)) {
                ps.setInt(1, s_id);
                ps.setInt(2, s_id);
                ps.setInt(3, stu_id);
                ps.setInt(4, s_id);

                try (ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        int cap = rs.getInt("capacity");
                        int filled = rs.getInt("count");
                        int is_in = rs.getInt("enrolled");

                        if (is_in > 0) {
                            throw new Exception("You are already registered for this section.");
                        }
                        if (filled >= cap) {
                            throw new Exception("Registration failed: Section is full.");
                        }
                    }
                }
            }

            // do the insert
            String i = "INSERT INTO enrollments (student_id, section_id) VALUES (?, ?)";
            try (PreparedStatement pp = con.prepareStatement(i)) {
                pp.setInt(1, stu_id);
                pp.setInt(2, s_id);
                pp.executeUpdate();
            }
        }
    }

    // delete the course
    public void dropCourse(String user, int sec_id) throws Exception {
        if(new AdminService().isMaintenanceMode()) {
            throw new Exception("System is in Maintenance Mode.");
        }

        int uid = getUserId(user);
        String d = "DELETE FROM enrollments WHERE student_id = ? AND section_id = ?";

        try (Connection c = DatabaseConnector.getErpConnection();
             PreparedStatement st = c.prepareStatement(d)) {
            st.setInt(1, uid);
            st.setInt(2, sec_id);
            int rows = st.executeUpdate();

            if(rows == 0) {
                throw new Exception("Course not found in your schedule.");
            }
        }
    }

    // getting my schedule
    public List<SectionDetails> getStudentSchedule(String name) throws Exception {
        List<SectionDetails> my_list = new ArrayList<>();
        int id = getUserId(name);

        String q = "SELECT s.section_id, c.code, c.title, u.username as instructor, " +
                     "s.day_time, s.room, s.capacity " +
                     "FROM enrollments e " +
                     "JOIN sections s ON e.section_id = s.section_id " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "JOIN auth_db.users_auth u ON s.instructor_id = u.user_id " +
                     "WHERE e.student_id = ?";

        try (Connection c = DatabaseConnector.getErpConnection();
             PreparedStatement p = c.prepareStatement(q)) {
            p.setInt(1, id);
            try (ResultSet res = p.executeQuery()) {
                while(res.next()) {
                    my_list.add(new SectionDetails(
                        res.getInt("section_id"),
                        res.getString("code"),
                        res.getString("title"),
                        res.getString("instructor"),
                        res.getString("day_time"),
                        res.getString("room"),
                        res.getInt("capacity")
                    ));
                }
            }
        }
        return my_list;
    }

    public List<String[]> getGrades(String u) throws Exception {
        List<String[]> g = new ArrayList<>();
        int u_id = getUserId(u);

        String sql = "SELECT c.code, c.title, g.component, g.score " +
                     "FROM grades g " +
                     "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                     "JOIN sections s ON e.section_id = s.section_id " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "WHERE e.student_id = ?";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, u_id);
            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    String[] temp = new String[4];
                    temp[0] = rs.getString("code");
                    temp[1] = rs.getString("title");
                    temp[2] = rs.getString("component");
                    temp[3] = String.valueOf(rs.getDouble("score"));
                    g.add(temp);
                }
            }
        }
        return g;
    }

    private int getUserId(String n) throws Exception {
        String q = "SELECT user_id FROM users_auth WHERE username = ?";
        try (Connection c = DatabaseConnector.getAuthConnection();
             PreparedStatement p = c.prepareStatement(q)) {
            p.setString(1, n);
            try (ResultSet r = p.executeQuery()) {
                if(r.next()) {
                    return r.getInt("user_id");
                }
            }
        }
        throw new Exception("User not found");
    }
}