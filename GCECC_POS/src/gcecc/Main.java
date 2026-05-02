package gcecc;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;


public class Main {
    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc",        10);
            UIManager.put("Component.arc",     8);
            UIManager.put("ScrollBar.width",   8);
            UIManager.put("ScrollBar.thumbArc",999);
            UIManager.put("TabbedPane.tabHeight", 36);
        } catch (Exception e) {
            // Falls back to default Java look if FlatLaf not found
            System.err.println("FlatLaf not loaded, using default look.");
        }

        SwingUtilities.invokeLater(() -> new LoginWindow());
    }
}
