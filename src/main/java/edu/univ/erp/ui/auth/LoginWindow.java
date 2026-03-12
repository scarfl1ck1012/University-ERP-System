package edu.univ.erp.ui.auth;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.ui.dashboard.MainFrame;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class LoginWindow extends JFrame {

    JTextField tf_u;
    JPasswordField tf_p;
    JButton btn;
    AuthService as;

    public LoginWindow() {
        as = new AuthService();
        setTitle("University ERP");
        // Made window much smaller since we removed the left side
        setSize(450, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout: Single column, centered
        setLayout(new MigLayout("insets 20, fill"));

        // --- MAIN FORM BOX ---
        JPanel p = new JPanel(new MigLayout("insets 20, wrap 1, fillx", "[center]"));
        p.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Basic black border

        // Logo
        URL logoUrl = getClass().getResource("/images/logo.png");
        if (logoUrl != null) {
            ImageIcon icon = new ImageIcon(logoUrl);
            Image img = icon.getImage().getScaledInstance(180, -1, Image.SCALE_SMOOTH);
            p.add(new JLabel(new ImageIcon(img)), "gapbottom 10");
        }

        JLabel title = new JLabel("Login");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        p.add(title, "gapbottom 10");

        tf_u = new JTextField(20);
        tf_p = new JPasswordField(20);
        btn = new JButton("Submit");

        // Inputs
        p.add(new JLabel("Username:"), "align left");
        p.add(tf_u, "growx, gapbottom 5");

        p.add(new JLabel("Password:"), "align left");
        p.add(tf_p, "growx, gapbottom 10");

        p.add(btn, "growx, h 30!");

        // Add the form panel to the center of the window
        add(p, "grow, align center");

        btn.addActionListener(e -> go());
        getRootPane().setDefaultButton(btn);
    }

    void go() {
        String u = tf_u.getText();
        String p = new String(tf_p.getPassword());

        btn.setEnabled(false);
        btn.setText("Logging in...");

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return as.login(u, p);
            }

            @Override
            protected void done() {
                try {
                    btn.setEnabled(true);
                    btn.setText("Submit");
                    String r = get();
                    dispose();
                    new MainFrame(u, r).setVisible(true);
                } catch (Exception ex) {
                    btn.setEnabled(true);
                    btn.setText("Submit");
                    String msg = ex.getMessage();
                    if (ex.getCause() != null) {
                        msg = ex.getCause().getMessage();
                    }
                    JOptionPane.showMessageDialog(LoginWindow.this, "Login Failed: " + msg);
                }
            }
        };
        worker.execute();
    }
}