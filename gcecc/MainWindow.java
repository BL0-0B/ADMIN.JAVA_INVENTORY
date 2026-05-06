package gcecc;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

import static gcecc.AppData.*;

// MainWindow.java — Main application frame.
 
public class MainWindow extends JFrame {

    CardLayout  cardLayout;
    JPanel      contentPanel;

    POSPanel       posPanel;
    InventoryPanel inventoryPanel;
    DashboardPanel dashboardPanel;

    JButton btnDash, btnPOS, btnInv;

    public MainWindow() {
        buildUI();
        setVisible(true);
        showPage("dashboard");
    }

    void buildUI() {
        setTitle("GCECC INVENTORY System — " + sessionName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 680));
        setSize(1280, 760);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);

        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG);

        dashboardPanel = new DashboardPanel(this);
        posPanel       = new POSPanel(this);
        inventoryPanel = new InventoryPanel(this);

        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(posPanel,       "pos");
        contentPanel.add(inventoryPanel, "inventory");

        add(contentPanel, BorderLayout.CENTER);
    }

    JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(WHITE);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(new MatteBorder(0, 0, 0, 1, BORDER));

        //Logo/Header
        JPanel logoArea = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, GREEN_DARK, getWidth(), getHeight(), GREEN);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        logoArea.setLayout(new BoxLayout(logoArea, BoxLayout.Y_AXIS));
        logoArea.setBorder(new EmptyBorder(22, 20, 18, 20));
        logoArea.setOpaque(false);

        JLabel logoLbl = new JLabel("⬡ GCECC");
        logoLbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        logoLbl.setForeground(WHITE);
        logoLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLbl = new JLabel("Admin POS System");
        subLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        subLbl.setForeground(new Color(255, 255, 255, 180));
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoArea.add(logoLbl);
        logoArea.add(Box.createVerticalStrut(2));
        logoArea.add(subLbl);

        sidebar.add(logoArea, BorderLayout.NORTH);

        //Nav
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(WHITE);
        nav.setBorder(new EmptyBorder(14, 10, 10, 10));

        JLabel menuLabel = navSectionLabel("MAIN MENU");
        nav.add(menuLabel);
        nav.add(Box.createVerticalStrut(4));

        btnDash = navBtn("📊", "Dashboard");
        btnPOS  = navBtn("🛒", "Inventory");
        btnInv  = navBtn("📁", "Category & Product");

        btnDash.addActionListener(e -> showPage("dashboard"));
        btnPOS .addActionListener(e -> showPage("pos"));
        btnInv .addActionListener(e -> showPage("inventory"));

        nav.add(btnDash);
        nav.add(Box.createVerticalStrut(2));
        nav.add(btnPOS);
        nav.add(Box.createVerticalStrut(2));
        nav.add(btnInv);

        sidebar.add(nav, BorderLayout.CENTER);

        //Footer
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(WHITE);
        footer.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, BORDER),
            new EmptyBorder(14, 16, 14, 16)
        ));

        // User avatar circle
        JLabel avatar = new JLabel(sessionName.substring(0, 1).toUpperCase(), SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GREEN_LIGHT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setFont(new Font("SansSerif", Font.BOLD, 14));
        avatar.setForeground(GREEN_DARK);
        avatar.setPreferredSize(new Dimension(34, 34));
        avatar.setOpaque(false);

        JLabel userLbl = new JLabel(sessionName);
        userLbl.setFont(FONT_BOLD);
        userLbl.setForeground(TEXT);
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLbl = new JLabel("Administrator");
        roleLbl.setFont(FONT_SMALL);
        roleLbl.setForeground(TEXT_MUTED);
        roleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton logoutBtn = new JButton("🚪  Logout") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? RED_LIGHT : WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoutBtn.setFont(FONT_REG);
        logoutBtn.setForeground(RED);
        logoutBtn.setOpaque(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.setHorizontalAlignment(SwingConstants.LEFT);
        logoutBtn.setBorder(new EmptyBorder(9, 10, 9, 10));
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        logoutBtn.addActionListener(e -> doLogout());

        footer.add(userLbl);
        footer.add(Box.createVerticalStrut(1));
        footer.add(roleLbl);
        footer.add(Box.createVerticalStrut(10));
        footer.add(logoutBtn);

        // Footer info
        JPanel footerInfo = new JPanel();
        footerInfo.setLayout(new BoxLayout(footerInfo, BoxLayout.Y_AXIS));
        footerInfo.setBackground(GREEN_PALE);
        footerInfo.setBorder(new EmptyBorder(8, 14, 10, 14));

        JLabel gcLabel = new JLabel("Gordon College, Olongapo City");
        gcLabel.setFont(new Font("SansSerif", Font.PLAIN, 9));
        gcLabel.setForeground(new Color(0x06, 0x6c, 0x56));
        gcLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel gcLabel2 = new JLabel("Mon–Fri  ·  8:00 AM – 5:00 PM");
        gcLabel2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        gcLabel2.setForeground(new Color(0x06, 0x6c, 0x56));
        gcLabel2.setAlignmentX(Component.LEFT_ALIGNMENT);
        footerInfo.add(gcLabel);
        footerInfo.add(gcLabel2);

        JPanel footerWrap = new JPanel(new BorderLayout());
        footerWrap.setBackground(WHITE);
        footerWrap.add(footer,     BorderLayout.CENTER);
        footerWrap.add(footerInfo, BorderLayout.SOUTH);

        sidebar.add(footerWrap, BorderLayout.SOUTH);
        return sidebar;
    }

    JLabel navSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(new Color(0xaa, 0xaa, 0xaa));
        lbl.setBorder(new EmptyBorder(6, 6, 4, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    JButton navBtn(String icon, String label) {
        JButton btn = new JButton(icon + "  " + label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getBackground();
                if (!WHITE.equals(bg)) {
                    g2.setColor(bg);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                } else if (getModel().isRollover()) {
                    g2.setColor(GREEN_PALE);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_REG);
        btn.setForeground(TEXT);
        btn.setBackground(WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(11, 14, 11, 14));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!GREEN_LIGHT.equals(btn.getBackground())) btn.setForeground(GREEN);
            }
            public void mouseExited(MouseEvent e) {
                if (!GREEN_LIGHT.equals(btn.getBackground())) btn.setForeground(TEXT);
            }
        });
        return btn;
    }

    void setActiveNav(JButton active) {
        for (JButton b : new JButton[]{btnDash, btnPOS, btnInv}) {
            b.setBackground(WHITE);
            b.setForeground(TEXT);
            b.setFont(FONT_REG);
        }
        active.setBackground(GREEN_LIGHT);
        active.setForeground(GREEN_DARK);
        active.setFont(FONT_BOLD);
    }

    void showPage(String page) {
        cardLayout.show(contentPanel, page);
        switch (page) {
            case "dashboard" -> { setActiveNav(btnDash); dashboardPanel.refresh(); }
            case "pos"       -> { setActiveNav(btnPOS);  posPanel.onShow(); }
            case "inventory" -> { setActiveNav(btnInv);  inventoryPanel.onShow(); }
        }
    }

    void doLogout() {
        int r = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            dispose();
            new LoginWindow();
        }
    }
}