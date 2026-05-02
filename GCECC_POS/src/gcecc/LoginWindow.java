package gcecc;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import static gcecc.AppData.*;

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
        setTitle("GCECC POS — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(440, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        // Green gradient background panel
        JPanel bg = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, GREEN_DARK, getWidth(), getHeight(), GREEN_MID);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(bg);

        // White login card
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(255, 255, 255, 80), 1, true),
            new EmptyBorder(36, 36, 36, 36)
        ));
        card.setPreferredSize(new Dimension(350, 430));

        // Logo
        JLabel logo = new JLabel("GCECC", SwingConstants.CENTER);
        logo.setFont(new Font("SansSerif", Font.BOLD, 30));
        logo.setForeground(GREEN);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub1 = new JLabel("Admin POS System", SwingConstants.CENTER);
        sub1.setFont(new Font("SansSerif", Font.BOLD, 13));
        sub1.setForeground(TEXT);
        sub1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub2 = new JLabel("Gordon College Employees Consumers Cooperative", SwingConstants.CENTER);
        sub2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        sub2.setForeground(TEXT_MUTED);
        sub2.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Error label
        errorLabel = new JLabel(" ", SwingConstants.CENTER);
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        errorLabel.setForeground(RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        // Username
        JLabel uLbl = UI.formLabel("USERNAME");
        uLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField = UI.field("Enter username");
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        usernameField.addActionListener(e -> passwordField.requestFocus());

        // Password
        JLabel pLbl = UI.formLabel("PASSWORD");
        pLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField = UI.pwField("Enter password");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        passwordField.addActionListener(e -> doLogin());

        // Login button
        loginBtn = UI.primaryBtn("Login");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        loginBtn.addActionListener(e -> doLogin());

        // Hint
        JLabel hint = new JLabel("Default: admin / gcecc2025", SwingConstants.CENTER);
        hint.setFont(new Font("SansSerif", Font.PLAIN, 10));
        hint.setForeground(TEXT_MUTED);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(logo);
        card.add(Box.createVerticalStrut(4));
        card.add(sub1);
        card.add(Box.createVerticalStrut(2));
        card.add(sub2);
        card.add(Box.createVerticalStrut(24));
        card.add(uLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(12));
        card.add(pLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(6));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(16));
        card.add(hint);

        bg.add(card);
    }

    void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            return;
        }

        if (AppData.loginUser(username, password)) {
            AppData.sessionName     = AppData.getUserName(username);
            AppData.sessionUsername = username;
            dispose();
            new MainWindow();
        } else {
            errorLabel.setText("Invalid username or password.");
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }
}
