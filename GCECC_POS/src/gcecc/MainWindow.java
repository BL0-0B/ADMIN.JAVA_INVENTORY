package gcecc;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import static gcecc.AppData.*;

// MainWindow.java — Main application frame with sidebar + content area.
 
public class MainWindow extends JFrame {

    CardLayout  cardLayout;
    JPanel      contentPanel;

    POSPanel       posPanel;
    InventoryPanel inventoryPanel;
    DashboardPanel dashboardPanel;

    // Sidebar nav buttons 
    JButton btnDash, btnPOS, btnInv;

    public MainWindow() {
        buildUI();
        setVisible(true);
        showPage("dashboard");
    }

    void buildUI() {
        setTitle("GCECC POS System — " + sessionName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 680));
        setSize(1280, 760);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);

        // Content area
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
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new MatteBorder(0, 0, 0, 1, BORDER));

        // Logo
        JPanel logoArea = new JPanel();
        logoArea.setLayout(new BoxLayout(logoArea, BoxLayout.Y_AXIS));
        logoArea.setBackground(WHITE);
        logoArea.setBorder(new EmptyBorder(20, 18, 16, 18));

        JLabel logoLbl = new JLabel("GCECC");
        logoLbl.setFont(new Font("SansSerif", Font.BOLD, 22));
        logoLbl.setForeground(GREEN);

        JLabel subLbl = new JLabel("Admin POS");
        subLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subLbl.setForeground(TEXT_MUTED);

        logoArea.add(logoLbl);
        logoArea.add(subLbl);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);

        JPanel topArea = new JPanel(new BorderLayout());
        topArea.setBackground(WHITE);
        topArea.add(logoArea, BorderLayout.CENTER);
        topArea.add(sep,      BorderLayout.SOUTH);

        sidebar.add(topArea, BorderLayout.NORTH);

        // Nav links
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(WHITE);
        nav.setBorder(new EmptyBorder(10, 8, 8, 8));

        JLabel menuLabel = new JLabel("MAIN MENU");
        menuLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        menuLabel.setForeground(new Color(0xaa, 0xaa, 0xaa));
        menuLabel.setBorder(new EmptyBorder(8, 6, 6, 0));
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnDash = navBtn("📊   Dashboard");
        btnPOS  = navBtn("🛒   POS / Order");
        btnInv  = navBtn("📦   Inventory");

        btnDash.addActionListener(e -> showPage("dashboard"));
        btnPOS .addActionListener(e -> showPage("pos"));
        btnInv .addActionListener(e -> showPage("inventory"));

        nav.add(menuLabel);
        nav.add(Box.createVerticalStrut(2));
        nav.add(btnDash);
        nav.add(Box.createVerticalStrut(2));
        nav.add(btnPOS);
        nav.add(Box.createVerticalStrut(2));
        nav.add(btnInv);

        sidebar.add(nav, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(WHITE);
        footer.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, BORDER),
            new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel userLbl = new JLabel("👤  " + sessionName);
        userLbl.setFont(FONT_BOLD);
        userLbl.setForeground(GREEN);
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLbl = new JLabel("Administrator");
        roleLbl.setFont(FONT_SMALL);
        roleLbl.setForeground(TEXT_MUTED);
        roleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton logoutBtn = new JButton("🚪   Logout");
        logoutBtn.setFont(FONT_REG);
        logoutBtn.setForeground(RED);
        logoutBtn.setBackground(WHITE);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.setHorizontalAlignment(SwingConstants.LEFT);
        logoutBtn.setBorder(new EmptyBorder(8, 0, 0, 0));
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        logoutBtn.addActionListener(e -> doLogout());
        logoutBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { logoutBtn.setBackground(RED_LIGHT); }
            public void mouseExited (MouseEvent e) { logoutBtn.setBackground(WHITE); }
        });

        footer.add(userLbl);
        footer.add(Box.createVerticalStrut(2));
        footer.add(roleLbl);
        footer.add(logoutBtn);

        sidebar.add(footer, BorderLayout.SOUTH);
        return sidebar;
    }

    JButton navBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_REG);
        btn.setForeground(TEXT);
        btn.setBackground(WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 14, 10, 14));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!btn.getBackground().equals(GREEN_LIGHT)) {
                    btn.setBackground(GREEN_PALE);
                    btn.setForeground(GREEN);
                }
            }
            public void mouseExited(MouseEvent e) {
                if (!btn.getBackground().equals(GREEN_LIGHT)) {
                    btn.setBackground(WHITE);
                    btn.setForeground(TEXT);
                }
            }
        });
        return btn;
    }

    void setActiveNav(JButton active) {
        for (JButton b : new JButton[]{btnDash, btnPOS, btnInv}) {
            b.setBackground(WHITE);
            b.setForeground(TEXT);
        }
        active.setBackground(GREEN_LIGHT);
        active.setForeground(GREEN_DARK);
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
            "Logout", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            dispose();
            new LoginWindow();
        }
    }
}
