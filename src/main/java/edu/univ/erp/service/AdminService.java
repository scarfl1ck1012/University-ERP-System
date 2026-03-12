package edu.univ.erp.service;

import edu.univ.erp.data.DatabaseConnector;
import edu.univ.erp.domain.SectionDetails;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminService {

    public boolean isMaintenanceMode() throws Exception {
        String q = "SELECT setting_value FROM settings WHERE setting_key = 'maintenance_on'";
        try (Connection c = DatabaseConnector.getErpConnection();
             Statement stmt = c.createStatement();
             ResultSet r = stmt.executeQuery(q)) {
            if (r.next()) {
                String val = r.getString("setting_value");
                return "true".equalsIgnoreCase(val);
            }
        }
        return false;
    }

    public void setMaintenanceMode(boolean flag) throws Exception {
        String v = flag ? "true" : "false";
        String s = "UPDATE settings SET setting_value = ? WHERE setting_key = 'maintenance_on'";
        try (Connection con = DatabaseConnector.getErpConnection();
             PreparedStatement p = con.prepareStatement(s)) {
            p.setString(1, v);
            if(p.executeUpdate() == 0) {
                createSetting("maintenance_on", v);
            }
        }
    }

    private void createSetting(String k, String v) throws Exception {
        String q = "INSERT INTO settings (setting_key, setting_value) VALUES (?, ?)";
        try (Connection c = DatabaseConnector.getErpConnection();
             PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, k); ps.setString(2, v);
            ps.executeUpdate();
        }
    }

    public void createStudent(String u, String p, String roll, String prog, int yr) throws Exception {
        int id = createUserInAuth(u, p, "STUDENT");
        String q = "INSERT INTO students (user_id, roll_no, program, year) VALUES (?, ?, ?, ?)";
        try (Connection c = DatabaseConnector.getErpConnection();
             PreparedStatement st = c.prepareStatement(q)) {
            st.setInt(1, id); st.setString(2, roll); st.setString(3, prog); st.setInt(4, yr);
            st.executeUpdate();
        }
    }

    public void createInstructor(String u, String p, String dept, String title) throws Exception {
        int id = createUserInAuth(u, p, "INSTRUCTOR");
        String q = "INSERT INTO instructors (user_id, department, title) VALUES (?, ?, ?)";
        try (Connection c = DatabaseConnector.getErpConnection();
             PreparedStatement st = c.prepareStatement(q)) {
            st.setInt(1, id); st.setString(2, dept); st.setString(3, title);
            st.executeUpdate();
        }
    }

    private int createUserInAuth(String u, String pass, String role) throws Exception {
        String h = BCrypt.hashpw(pass, BCrypt.gensalt(12));
        String q = "INSERT INTO users_auth (username, password_hash, role) VALUES (?, ?, ?)";
        try (Connection c = DatabaseConnector.getAuthConnection();
             PreparedStatement p = c.prepareStatement(q, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, u); p.setString(2, h); p.setString(3, role);
            p.executeUpdate();
            try (ResultSet r = p.getGeneratedKeys()) {
                if(r.next()) {
                    return r.getInt(1);
                }
            }
        }
        throw new Exception("fail user");
    }

    public void createCourse(String c, String t, int cr) throws Exception {
        String q = "INSERT INTO courses (code, title, credits) VALUES (?, ?, ?)";
        try (Connection con = DatabaseConnector.getErpConnection();
             PreparedStatement ps = con.prepareStatement(q)) {
            ps.setString(1, c); ps.setString(2, t); ps.setInt(3, cr);
            ps.executeUpdate();
        }
    }

    public void createSection(String cCode, String iUser, String dt, String rm, int cap, String sem, int y) throws Exception {
        int cid = getCourseId(cCode); int iid = getUserIdFromAuth(iUser);
        String sql = "INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnector.getErpConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, cid); p.setInt(2, iid); p.setString(3, dt); p.setString(4, rm);
            p.setInt(5, cap); p.setString(6, sem); p.setInt(7, y);
            p.executeUpdate();
        }
    }

    public List<String> getAllCourseCodes() throws Exception {
        List<String> l = new ArrayList<>();
        try (Connection c = DatabaseConnector.getErpConnection();
             Statement stmt = c.createStatement();
             ResultSet r = stmt.executeQuery("SELECT code FROM courses")) {
            while(r.next()) l.add(r.getString("code"));
        }
        return l;
    }

    public List<String> getAllInstructors() throws Exception {
        List<String> l = new ArrayList<>();
        try (Connection c = DatabaseConnector.getAuthConnection();
             Statement stmt = c.createStatement();
             ResultSet r = stmt.executeQuery("SELECT username FROM users_auth WHERE role = 'INSTRUCTOR'")) {
            while(r.next()) l.add(r.getString("username"));
        }
        return l;
    }

    public List<SectionDetails> getAllSections() throws Exception {
        List<SectionDetails> l = new ArrayList<>();
        String q = "SELECT s.section_id, c.code, c.title, u.username as instructor, s.day_time, s.room, s.capacity " +
                "FROM sections s JOIN courses c ON s.course_id = c.course_id JOIN auth_db.users_auth u ON s.instructor_id = u.user_id";
        try (Connection c = DatabaseConnector.getErpConnection();
             Statement stmt = c.createStatement();
             ResultSet r = stmt.executeQuery(q)) {
            while(r.next()) {
                l.add(new SectionDetails(r.getInt("section_id"), r.getString("code"), r.getString("title"),
                    r.getString("instructor"), r.getString("day_time"), r.getString("room"), r.getInt("capacity")));
            }
        }
        return l;
    }

    public void updateSection(int id, String rm, int cp) throws Exception {
        String q = "UPDATE sections SET room = ?, capacity = ? WHERE section_id = ?";
        try (Connection c = DatabaseConnector.getErpConnection();
             PreparedStatement p = c.prepareStatement(q)) {
            p.setString(1, rm); p.setInt(2, cp); p.setInt(3, id);
            p.executeUpdate();
        }
    }

    // Delete Logic (Bonus Check)
    public void deleteCourse(String c) throws Exception {
        int id = getCourseId(c);
        String chk = "SELECT COUNT(*) FROM sections WHERE course_id = ?";
        try (Connection con = DatabaseConnector.getErpConnection()) {
            try (PreparedStatement p = con.prepareStatement(chk)) {
                p.setInt(1, id);
                try (ResultSet r = p.executeQuery()) {
                    if(r.next() && r.getInt(1) > 0) {
                        throw new Exception("Cannot delete course. Sections exist.");
                    }
                }
            }

            String del = "DELETE FROM courses WHERE course_id = ?";
            try (PreparedStatement ps = con.prepareStatement(del)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        }
    }

    private int getCourseId(String c) throws Exception {
        String q = "SELECT course_id FROM courses WHERE code = ?";
        try (Connection con = DatabaseConnector.getErpConnection();
             PreparedStatement p = con.prepareStatement(q)) {
            p.setString(1, c);
            try (ResultSet r = p.executeQuery()) {
                if(r.next()) {
                    return r.getInt(1);
                }
            }
        }
        throw new Exception("Course not found");
    }

    private int getUserIdFromAuth(String u) throws Exception {
        String q = "SELECT user_id FROM users_auth WHERE username = ?";
        try (Connection con = DatabaseConnector.getAuthConnection();
             PreparedStatement p = con.prepareStatement(q)) {
            p.setString(1, u);
            try (ResultSet r = p.executeQuery()) {
                if(r.next()) {
                    return r.getInt(1);
                }
            }
        }
        throw new Exception("User not found");
    }
}