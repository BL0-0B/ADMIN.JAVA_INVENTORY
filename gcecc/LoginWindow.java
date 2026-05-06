package gcecc;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

import static gcecc.AppData.*;

//LoginWindow.java — Login screen matching the website's green gradient login card.

public class LoginWindow extends JFrame {

    JTextField     usernameField;
    JPasswordField passwordField;
    JLabel         errorLabel;
    JButton        loginBtn;

    public LoginWindow() {
        buildUI();
        setVisible(true);
    }

    void buildUI() {
        setTitle("GCECC — Admin Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(false);

        // Green gradient background
        JPanel bg = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, GREEN_DARK,
                    getWidth(), getHeight(), new Color(0x10, 0xb9, 0x81)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Decorative circles like website
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillOval(getWidth() - 160, -60, 260, 260);
                g2.fillOval(-80, getHeight() - 160, 220, 220);
                g2.dispose();
            }
        };
        setContentPane(bg);

        // White login card
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
            }
            @Override public boolean isOpaque() { return false; }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 40, 40, 40));
        card.setPreferredSize(new Dimension(370, 470));
        card.setBackground(WHITE);

        //Logo area 
        JLabel logoMark = new JLabel("GCECC", SwingConstants.CENTER);
        logoMark.setFont(new Font("SansSerif", Font.BOLD, 32));
        logoMark.setForeground(GREEN);
        logoMark.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLbl = new JLabel("Admin Portal", SwingConstants.CENTER);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLbl.setForeground(TEXT);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLbl = new JLabel("Gordon College Employees Consumers Cooperative", SwingConstants.CENTER);
        subLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        subLbl.setForeground(TEXT_MUTED);
        subLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Error
        errorLabel = new JLabel(" ", SwingConstants.CENTER);
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        errorLabel.setForeground(RED);
        errorLabel.setOpaque(true);
        errorLabel.setBackground(RED_LIGHT);
        errorLabel.setBorder(new CompoundBorder(
            new LineBorder(new Color(0xfc, 0xa5, 0xa5), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        errorLabel.setVisible(false);

        //Username
        JLabel uLbl = UI.formLabel("USERNAME");
        uLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = UI.field("Enter username");
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        usernameField.addActionListener(e -> passwordField.requestFocus());
        usernameField.addFocusListener(focusHighlight(usernameField));

        //Password
        JLabel pLbl = UI.formLabel("PASSWORD");
        pLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = UI.pwField("Enter password");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        passwordField.addActionListener(e -> doLogin());
        passwordField.addFocusListener(focusHighlight(passwordField));

        //Login Button
        loginBtn = UI.primaryBtn("  Login  ");
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        loginBtn.addActionListener(e -> doLogin());

        //Footer
        JLabel footerLbl = new JLabel("GCECC · Gordon College, Olongapo City, Zambales", SwingConstants.CENTER);
        footerLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        footerLbl.setForeground(TEXT_MUTED);
        footerLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hoursLbl = new JLabel("Mon–Fri  ·  8:00 AM – 5:00 PM", SwingConstants.CENTER);
        hoursLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        hoursLbl.setForeground(TEXT_MUTED);
        hoursLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Assemble
        card.add(logoMark);
        card.add(Box.createVerticalStrut(4));
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(2));
        card.add(subLbl);
        card.add(Box.createVerticalStrut(18));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(uLbl);
        card.add(Box.createVerticalStrut(5));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));
        card.add(pLbl);
        card.add(Box.createVerticalStrut(5));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(18));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(22));
        card.add(footerLbl);
        card.add(Box.createVerticalStrut(2));
        card.add(hoursLbl);

        bg.add(card);

        // Focus username
        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) { usernameField.requestFocusInWindow(); }
        });
    }

    // Focus highlight
    FocusListener focusHighlight(JComponent comp) {
        return new FocusAdapter() {
            final Border normal = comp.getBorder();
            final Border focused = new CompoundBorder(
                new LineBorder(GREEN, 1, true),
                new EmptyBorder(9, 12, 9, 12)
            );
            public void focusGained(FocusEvent e) { comp.setBorder(focused); }
            public void focusLost  (FocusEvent e) { comp.setBorder(normal);  }
        };
    }

    void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        errorLabel.setVisible(false);

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        loginBtn.setEnabled(false);
        loginBtn.setText("  Logging in...  ");

        Timer t = new Timer(500, e -> {
            if (AppData.loginUser(username, password)) {
                AppData.sessionName     = AppData.getUserName(username);
                AppData.sessionUsername = username;
                dispose();
                new MainWindow();
            } else {
                showError("Invalid username or password. Please try again.");
                passwordField.setText("");
                passwordField.requestFocus();
                loginBtn.setEnabled(true);
                loginBtn.setText("  Login  ");
            }
        });
        t.setRepeats(false);
        t.start();
    }

    void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}