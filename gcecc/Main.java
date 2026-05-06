package gcecc;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

// Main.java — GCECC POS System Entry Point

public class Main {
    public static void main(String[] args) {
        // Apply FlatLaf modern look
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc",           10);
            UIManager.put("Component.arc",         8);
            UIManager.put("ScrollPane.smoothScrolling", true);
            UIManager.put("ScrollBar.width",       8);
            UIManager.put("ScrollBar.thumbArc",  999);
            UIManager.put("ScrollBar.track",     new javax.swing.plaf.ColorUIResource(AppData.BG));

            javax.swing.JScrollBar.setDefaultLocale(java.util.Locale.getDefault());
            UIManager.put("ScrollBar.unitIncrement",  16);
            UIManager.put("ScrollBar.blockIncrement", 64);
            UIManager.put("TabbedPane.tabHeight",  38);
            UIManager.put("TabbedPane.selectedBackground", new Color(0xec, 0xfd, 0xf5));
            UIManager.put("TabbedPane.underlineColor",     new Color(0x09, 0x79, 0x69));
            UIManager.put("TextField.arc",         6);
            UIManager.put("ComboBox.arc",          6);
            UIManager.put("Spinner.arc",           6);
            UIManager.put("ProgressBar.arc",       6);
        } catch (Exception e) {
            System.err.println("FlatLaf not loaded, using default look.");
        }

        SwingUtilities.invokeLater(() -> new LoginWindow());
    }
}