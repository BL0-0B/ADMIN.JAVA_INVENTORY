package gcecc;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

import static gcecc.AppData.*;

public class UI {

    public static JButton primaryBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(GREEN);
        btn.setForeground(WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(GREEN_DARK); }
            public void mouseExited (MouseEvent e) { btn.setBackground(GREEN); }
        });
        return btn;
    }

    public static JButton dangerBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(RED);
        btn.setForeground(WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        return btn;
    }

    public static JButton outlineBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(WHITE);
        btn.setForeground(RED);
        btn.setBorder(new CompoundBorder(
            new LineBorder(RED, 1, true),
            new EmptyBorder(8, 18, 8, 18)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JTextField field(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(FONT_REG);
        tf.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        tf.putClientProperty("JTextField.placeholderText", placeholder);
        return tf;
    }

    public static JPasswordField pwField(String placeholder) {
        JPasswordField pf = new JPasswordField();
        pf.setFont(FONT_REG);
        pf.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        pf.putClientProperty("JTextField.placeholderText", placeholder);
        return pf;
    }

    public static JLabel formLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }

    public static JLabel sectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        lbl.setForeground(TEXT);
        lbl.setBorder(new EmptyBorder(0, 0, 12, 0));
        return lbl;
    }

    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(WHITE);
        p.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(16, 18, 16, 18)
        ));
        return p;
    }

    public static DefaultTableModel tableModel(String[] cols) {
        return new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    public static JTable styledTable(DefaultTableModel model) {
        JTable tbl = new JTable(model);
        tbl.setFont(FONT_REG);
        tbl.setRowHeight(36);
        tbl.setShowHorizontalLines(true);
        tbl.setGridColor(BORDER);
        tbl.setBackground(WHITE);
        tbl.setSelectionBackground(GREEN_PALE);
        tbl.setSelectionForeground(GREEN_DARK);
        tbl.setFocusable(false);
        tbl.setIntercellSpacing(new Dimension(0, 0));
        JTableHeader header = tbl.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 11));
        header.setBackground(GREEN_PALE);
        header.setForeground(GREEN_DARK);
        header.setBorder(new MatteBorder(0, 0, 2, 0, GREEN_LIGHT));
        return tbl;
    }

    public static JScrollPane scrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new LineBorder(BORDER, 1, true));
        sp.getViewport().setBackground(WHITE);
        return sp;
    }

    public static JPanel statCard(String label, String value, Color accent, String id) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(18, 20, 18, 20)
        ));

        JPanel colorBar = new JPanel();
        colorBar.setBackground(accent);
        colorBar.setPreferredSize(new Dimension(6, 0));
        card.add(colorBar, BorderLayout.WEST);

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 4));
        info.setBackground(WHITE);
        info.setBorder(new EmptyBorder(0, 14, 0, 0));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(TEXT_MUTED);

        JLabel val = new JLabel(value);
        val.setFont(new Font("SansSerif", Font.BOLD, 28));
        val.setForeground(TEXT);
        val.setName(id); // used to find and update later

        info.add(lbl);
        info.add(val);
        card.add(info, BorderLayout.CENTER);
        return card;
    }

    // Finds a label by its name inside a container tree 
    public static JLabel findLabel(Container root, String name) {
        for (Component c : root.getComponents()) {
            if (c instanceof JLabel && name.equals(c.getName())) return (JLabel) c;
            if (c instanceof Container) {
                JLabel found = findLabel((Container) c, name);
                if (found != null) return found;
            }
        }
        return null;
    }
}
