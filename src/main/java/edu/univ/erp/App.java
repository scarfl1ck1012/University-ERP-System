package edu.univ.erp;

import edu.univ.erp.ui.auth.LoginWindow;
import javax.swing.*;

public class App {
    public static void main(String[] args) {
        try {
            // This forces the standard "Java Metal" or System look
            // which gives you those standard rectangular 3D buttons.
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LoginWindow loginWindow = new LoginWindow();
            loginWindow.setVisible(true);
        });
    }
}