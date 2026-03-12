package edu.univ.erp.auth;

import edu.univ.erp.data.DatabaseConnector;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthService {

    public String login(String u, String p) throws Exception {
        String q = "SELECT user_id, password_hash, role FROM users_auth WHERE username = ?";

        try (Connection c = DatabaseConnector.getAuthConnection();
             PreparedStatement ps = c.prepareStatement(q)) {
             
            ps.setString(1, u);

            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    String h = rs.getString("password_hash");

                    // checking hash
                    if (BCrypt.checkpw(p, h)) {
                        return rs.getString("role");
                    } else {
                        throw new Exception("wrong pass");
                    }
                }
            }
        }
        throw new Exception("user not found");
    }

    // update password
    public void changePassword(String user, String old_p, String new_p) throws Exception {
        String cur_hash = "";
        
        try (Connection con = DatabaseConnector.getAuthConnection()) {
            // get old hash
            String s1 = "SELECT password_hash FROM users_auth WHERE username = ?";
            try (PreparedStatement st1 = con.prepareStatement(s1)) {
                st1.setString(1, user);
                try (ResultSet r = st1.executeQuery()) {
                    if(r.next()) {
                        cur_hash = r.getString("password_hash");
                    } else {
                        throw new Exception("no user found");
                    }
                }
            }

            // verify
            if(!BCrypt.checkpw(old_p, cur_hash)) {
                throw new Exception("Old password wrong");
            }

            // make new hash
            String h_new = BCrypt.hashpw(new_p, BCrypt.gensalt(12));
            String s2 = "UPDATE users_auth SET password_hash = ? WHERE username = ?";

            try (PreparedStatement st2 = con.prepareStatement(s2)) {
                st2.setString(1, h_new);
                st2.setString(2, user);
                st2.executeUpdate();
            }
        }
    }
}