package edu.univ.erp.ui.auth;

import edu.univ.erp.auth.AuthService;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;

public class ChangePasswordDialog extends JDialog {
    JPasswordField tf_old, tf_new, tf_con;
    AuthService as;
    String u;

    public ChangePasswordDialog(Frame p, String user) {
        super(p, "Change Password", true);
        this.u = user;
        this.as = new AuthService();

        setLayout(new MigLayout("insets 10, fill", "[][grow]"));
        setSize(350, 200);
        setLocationRelativeTo(p);

        add(new JLabel("Old Password:"));
        tf_old = new JPasswordField(15); add(tf_old, "wrap");

        add(new JLabel("New Password:"));
        tf_new = new JPasswordField(15); add(tf_new, "wrap");

        add(new JLabel("Confirm:"));
        tf_con = new JPasswordField(15); add(tf_con, "wrap");

        JButton b_ok = new JButton("Update");
        JButton b_can = new JButton("Cancel");

        JPanel bp = new JPanel();
        bp.add(b_ok); bp.add(b_can);
        add(bp, "span 2, align center");

        b_ok.addActionListener(e -> change());
        b_can.addActionListener(e -> dispose());
    }

    void change() {
        // Logic remains same, visuals stripped
        try {
            String o = new String(tf_old.getPassword());
            String n = new String(tf_new.getPassword());
            String c = new String(tf_con.getPassword());
            if(!n.equals(c)) { JOptionPane.showMessageDialog(this, "Mismatch"); return; }
            as.changePassword(u, o, n);
            JOptionPane.showMessageDialog(this, "Done");
            dispose();
        } catch(Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }
}