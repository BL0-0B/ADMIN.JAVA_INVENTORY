package gcecc;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

import static gcecc.AppData.*;

//UI.java — Reusable UI component factory.

public class UI {

    //BUTTONS

    public static JButton primaryBtn(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, GREEN_DARK, getWidth(), getHeight(), GREEN);
                if (getModel().isRollover()) gp = new GradientPaint(0, 0, GREEN, getWidth(), getHeight(), GREEN_DARK);
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BOLD);
        btn.setForeground(WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        return btn;
    }

    public static JButton dangerBtn(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? RED.darker() : RED;
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BOLD);
        btn.setForeground(WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        return btn;
    }

    public static JButton outlineBtn(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? GREEN_PALE : WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(RED);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BOLD);
        btn.setForeground(RED);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9, 18, 9, 18));
        return btn;
    }

    public static JButton outlineGreenBtn(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? GREEN_PALE : WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(GREEN);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BOLD);
        btn.setForeground(GREEN);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9, 18, 9, 18));
        return btn;
    }

    //FIELDS

    public static JTextField field(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(FONT_REG);
        tf.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(9, 12, 9, 12)
        ));
        tf.putClientProperty("JTextField.placeholderText", placeholder);
        return tf;
    }

    public static JPasswordField pwField(String placeholder) {
        JPasswordField pf = new JPasswordField();
        pf.setFont(FONT_REG);
        pf.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(9, 12, 9, 12)
        ));
        pf.putClientProperty("JTextField.placeholderText", placeholder);
        return pf;
    }

    //LABELS

    public static JLabel formLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }

    public static JLabel sectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        lbl.setForeground(TEXT);
        lbl.setBorder(new EmptyBorder(0, 0, 4, 0));
        return lbl;
    }

    //PILL BADGE

    public static JLabel pill(String text, Color bg, Color fg) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(fg);
        lbl.setBackground(bg);
        lbl.setOpaque(false);
        lbl.setBorder(new EmptyBorder(3, 10, 3, 10));
        return lbl;
    }

    //STAT CARD (Dashboard)

    public static JPanel statCard(String label, String value, Color accent, String id) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Subtle top accent line
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(3f));
                g2.drawLine(18, 0, getWidth() - 18, 0);
                g2.dispose();
            }
        };
        card.setBackground(WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(18, 20, 18, 20)
        ));

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 6));
        info.setBackground(WHITE);

        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(TEXT_MUTED);

        JLabel val = new JLabel(value);
        val.setFont(new Font("SansSerif", Font.BOLD, 30));
        val.setForeground(accent);
        val.setName(id);

        info.add(lbl);
        info.add(val);
        card.add(info, BorderLayout.CENTER);

        // Color dot top-right
        JLabel dot = new JLabel("●");
        dot.setFont(new Font("SansSerif", Font.PLAIN, 18));
        dot.setForeground(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 60));
        dot.setHorizontalAlignment(SwingConstants.RIGHT);
        card.add(dot, BorderLayout.NORTH);

        return card;
    }

    //TABLE 

    public static DefaultTableModel tableModel(String[] cols) {
        return new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    public static JTable styledTable(DefaultTableModel model) {
        JTable tbl = new JTable(model);
        tbl.setFont(FONT_REG);
        tbl.setRowHeight(38);
        tbl.setShowHorizontalLines(true);
        tbl.setShowVerticalLines(false);
        tbl.setGridColor(new Color(0xf3, 0xf4, 0xf6));
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
        header.setPreferredSize(new Dimension(0, 38));
        return tbl;
    }

    public static JScrollPane scrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new LineBorder(BORDER, 1, true));
        sp.getViewport().setBackground(WHITE);
        smoothScroll(sp);
        return sp;
    }

    /**smooth-scrolling JScrollPane */
    public static JScrollPane smoothScrollPane(JComponent content) {
        JScrollPane sp = new JScrollPane(content);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(content.getBackground());
        smoothScroll(sp);
        return sp;
    }
  
    public static void smoothScroll(JScrollPane sp) {
        sp.getVerticalScrollBar()  .setUnitIncrement(16);
        sp.getHorizontalScrollBar().setUnitIncrement(16);
        sp.getVerticalScrollBar()  .setBlockIncrement(64);

        sp.addMouseWheelListener(e -> {
            JScrollBar bar = sp.getVerticalScrollBar();
            int amount  = e.getUnitsToScroll() * bar.getUnitIncrement();
            int current = bar.getValue();
            int newVal  = current + amount;

            boolean atTop    = current <= bar.getMinimum() && amount < 0;
            boolean atBottom = current >= bar.getMaximum() - bar.getVisibleAmount() && amount > 0;

            if ((atTop || atBottom) && sp.getParent() != null) {
                sp.getParent().dispatchEvent(
                    new java.awt.event.MouseWheelEvent(
                        sp.getParent(),
                        e.getID(), e.getWhen(), e.getModifiersEx(),
                        e.getX(), e.getY(), e.getXOnScreen(), e.getYOnScreen(),
                        e.getClickCount(), e.isPopupTrigger(),
                        e.getScrollType(), e.getScrollAmount(),
                        e.getWheelRotation(), e.getPreciseWheelRotation()
                    )
                );
                return;
            }
            bar.setValue(newVal);
        });
    }

    //FIND LABEL BY NAME

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

    //RECEIPT PANEL (for POS dialog)

    public static JPanel buildReceiptPanel(String orNumber, java.util.List<String[]> items,
                                           double total, double cash, double change) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(WHITE);
        panel.setBorder(new EmptyBorder(20, 28, 20, 28));

        // Store header
        JLabel store = new JLabel("GCECC", SwingConstants.CENTER);
        store.setFont(new Font("SansSerif", Font.BOLD, 18));
        store.setForeground(GREEN);
        store.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel storeSub = new JLabel("Gordon College Employees Consumers Cooperative", SwingConstants.CENTER);
        storeSub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        storeSub.setForeground(TEXT_MUTED);
        storeSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel addr = new JLabel("Gordon College, Olongapo City, Zambales", SwingConstants.CENTER);
        addr.setFont(new Font("SansSerif", Font.PLAIN, 10));
        addr.setForeground(TEXT_MUTED);
        addr.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep1 = new JSeparator();
        sep1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep1.setForeground(BORDER);

        JLabel orLbl = new JLabel("OR #: " + orNumber, SwingConstants.CENTER);
        orLbl.setFont(new Font("Monospaced", Font.BOLD, 12));
        orLbl.setForeground(TEXT);
        orLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        String dateStr = new java.text.SimpleDateFormat("MM/dd/yyyy  hh:mm a").format(new java.util.Date());
        JLabel dateLbl = new JLabel(dateStr, SwingConstants.CENTER);
        dateLbl.setFont(new Font("Monospaced", Font.PLAIN, 10));
        dateLbl.setForeground(TEXT_MUTED);
        dateLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep2 = new JSeparator();
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep2.setForeground(BORDER);

        panel.add(store);
        panel.add(Box.createVerticalStrut(2));
        panel.add(storeSub);
        panel.add(addr);
        panel.add(Box.createVerticalStrut(10));
        panel.add(sep1);
        panel.add(Box.createVerticalStrut(8));
        panel.add(orLbl);
        panel.add(dateLbl);
        panel.add(Box.createVerticalStrut(10));
        panel.add(sep2);
        panel.add(Box.createVerticalStrut(10));

        // Items
        for (String[] item : items) {
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(WHITE);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
            int qty = Integer.parseInt(item[3]);
            double price = Double.parseDouble(item[2]);
            JLabel name = new JLabel(item[1] + " x" + qty);
            name.setFont(new Font("Monospaced", Font.PLAIN, 11));
            name.setForeground(TEXT);
            JLabel amt = new JLabel(String.format("₱ %.2f", price * qty));
            amt.setFont(new Font("Monospaced", Font.BOLD, 11));
            amt.setForeground(TEXT);
            row.add(name, BorderLayout.WEST);
            row.add(amt,  BorderLayout.EAST);
            panel.add(row);
        }

        panel.add(Box.createVerticalStrut(10));
        JSeparator sep3 = new JSeparator();
        sep3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep3.setForeground(BORDER);
        panel.add(sep3);
        panel.add(Box.createVerticalStrut(8));

        // Total / Cash / Change
        panel.add(receiptRow("TOTAL",  String.format("₱ %.2f", total),  FONT_BOLD, GREEN));
        panel.add(Box.createVerticalStrut(4));
        panel.add(receiptRow("CASH",   String.format("₱ %.2f", cash),   FONT_REG,  TEXT));
        panel.add(Box.createVerticalStrut(4));
        panel.add(receiptRow("CHANGE", String.format("₱ %.2f", change), FONT_BOLD, change > 0 ? GREEN : TEXT));

        panel.add(Box.createVerticalStrut(14));
        JSeparator sep4 = new JSeparator();
        sep4.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep4.setForeground(BORDER);
        panel.add(sep4);
        panel.add(Box.createVerticalStrut(10));

        JLabel thanks = new JLabel("Thank you for shopping at GCECC!", SwingConstants.CENTER);
        thanks.setFont(new Font("SansSerif", Font.BOLD, 11));
        thanks.setForeground(GREEN);
        thanks.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(thanks);

        JLabel hours = new JLabel("Mon–Fri  ·  8:00 AM – 5:00 PM", SwingConstants.CENTER);
        hours.setFont(new Font("SansSerif", Font.PLAIN, 10));
        hours.setForeground(TEXT_MUTED);
        hours.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(hours);

        return panel;
    }

    private static JPanel receiptRow(String label, String value, Font font, Color valueColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        JLabel l = new JLabel(label);
        l.setFont(font);
        l.setForeground(TEXT_MUTED);
        JLabel v = new JLabel(value);
        v.setFont(font);
        v.setForeground(valueColor);
        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        return row;
    }
}