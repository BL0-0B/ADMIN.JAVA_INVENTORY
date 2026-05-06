package gcecc;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import static gcecc.AppData.*;

public class AddStockPanel extends JPanel {

    MainWindow win;
    String[] foundProduct = null;

    JTextField barcodeField;
    JPanel     productCard;
    JLabel     prodNameLbl, prodCatLbl, prodPriceLbl, prodStockLbl, prodBarcodeLbl;
    JSpinner   qtySpinner;
    JButton    confirmBtn;
    JLabel     statusLbl;

    StringBuilder     barcodeBuffer = new StringBuilder();
    javax.swing.Timer barcodeTimer;

    public AddStockPanel(MainWindow win) {
        this.win = win;
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);
        setBorder(new EmptyBorder(22, 22, 22, 22));
        buildUI();
        setupBarcodeScanner();
    }

    void buildUI() {
        //Page Title
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(BG);
        titleRow.setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel title = new JLabel("➕  Add Stock");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(TEXT);

        JLabel sub = new JLabel("Scan a product barcode or type it manually to restock");
        sub.setFont(FONT_SMALL);
        sub.setForeground(TEXT_MUTED);

        titleRow.add(title, BorderLayout.NORTH);
        titleRow.add(sub,   BorderLayout.SOUTH);
        add(titleRow, BorderLayout.NORTH);

        //Center scroll area
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(BG);

        // Hero banner
        center.add(buildHeroBanner());
        center.add(Box.createVerticalStrut(16));

        //SCAN CARD
        JPanel scanCard = new JPanel(new BorderLayout(12, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(GREEN);
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
            @Override public boolean isOpaque() { return false; }
        };
        scanCard.setBorder(new EmptyBorder(18, 20, 18, 20));
        scanCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 82));
        scanCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel scanIcon = new JLabel("📷");
        scanIcon.setFont(new Font("SansSerif", Font.PLAIN, 28));

        JPanel scanMid = new JPanel(new BorderLayout(0, 4));
        scanMid.setOpaque(false);
        JLabel scanLbl = new JLabel("BARCODE / PRODUCT NAME");
        scanLbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        scanLbl.setForeground(TEXT_MUTED);

        barcodeField = new JTextField();
        barcodeField.setFont(new Font("SansSerif", Font.BOLD, 16));
        barcodeField.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        barcodeField.putClientProperty("JTextField.placeholderText", "Scan or type barcode here...");
        barcodeField.addActionListener(e -> searchByBarcode(barcodeField.getText().trim()));
        scanMid.add(scanLbl,      BorderLayout.NORTH);
        scanMid.add(barcodeField, BorderLayout.CENTER);

        JButton searchBtn = UI.primaryBtn("Search");
        searchBtn.addActionListener(e -> searchByBarcode(barcodeField.getText().trim()));

        scanCard.add(scanIcon,  BorderLayout.WEST);
        scanCard.add(scanMid,   BorderLayout.CENTER);
        scanCard.add(searchBtn, BorderLayout.EAST);

        center.add(scanCard);
        center.add(Box.createVerticalStrut(12));

        //Status label
        statusLbl = new JLabel("Ready — scan a barcode to begin");
        statusLbl.setFont(FONT_BOLD);
        statusLbl.setForeground(TEXT_MUTED);
        statusLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusLbl.setBorder(new EmptyBorder(2, 2, 2, 0));
        center.add(statusLbl);
        center.add(Box.createVerticalStrut(12));

        //Product card (shown after scan)
        productCard = new JPanel(new BorderLayout(16, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                // Green top accent line
                g2.setColor(GREEN);
                g2.setStroke(new BasicStroke(3f));
                g2.drawLine(16, 0, getWidth()-16, 0);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override public boolean isOpaque() { return false; }
        };
        productCard.setBorder(new CompoundBorder(
            new LineBorder(GREEN_LIGHT, 1, true),
            new EmptyBorder(20, 22, 20, 22)
        ));
        productCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 165));
        productCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        productCard.setVisible(false);

        // Left: product info
        JPanel infoPanel = new JPanel(new GridLayout(5, 1, 0, 5));
        infoPanel.setOpaque(false);

        prodNameLbl    = new JLabel("—");
        prodNameLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        prodNameLbl.setForeground(TEXT);

        prodCatLbl     = new JLabel("—");
        prodCatLbl.setFont(FONT_SMALL);
        prodCatLbl.setForeground(TEXT_MUTED);

        prodPriceLbl   = new JLabel("—");
        prodPriceLbl.setFont(FONT_BOLD);
        prodPriceLbl.setForeground(GREEN);

        prodStockLbl   = new JLabel("—");
        prodStockLbl.setFont(FONT_BOLD);

        prodBarcodeLbl = new JLabel("—");
        prodBarcodeLbl.setFont(FONT_MONO);
        prodBarcodeLbl.setForeground(TEXT_MUTED);

        infoPanel.add(prodNameLbl);
        infoPanel.add(prodCatLbl);
        infoPanel.add(prodPriceLbl);
        infoPanel.add(prodStockLbl);
        infoPanel.add(prodBarcodeLbl);

        // Right: qty + confirm
        JPanel qtyPanel = new JPanel();
        qtyPanel.setLayout(new BoxLayout(qtyPanel, BoxLayout.Y_AXIS));
        qtyPanel.setOpaque(false);
        qtyPanel.setPreferredSize(new Dimension(200, 0));

        JLabel qtyLbl = new JLabel("Quantity to Add:");
        qtyLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        qtyLbl.setForeground(TEXT_MUTED);
        qtyLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        qtySpinner.setFont(new Font("SansSerif", Font.BOLD, 24));
        qtySpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        ((JSpinner.DefaultEditor) qtySpinner.getEditor())
            .getTextField().setHorizontalAlignment(SwingConstants.CENTER);

        confirmBtn = UI.primaryBtn("✔  Confirm Add Stock");
        confirmBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        confirmBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        confirmBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmBtn.addActionListener(e -> confirmAddStock());

        qtyPanel.add(qtyLbl);
        qtyPanel.add(Box.createVerticalStrut(8));
        qtyPanel.add(qtySpinner);
        qtyPanel.add(Box.createVerticalStrut(10));
        qtyPanel.add(confirmBtn);

        productCard.add(infoPanel, BorderLayout.CENTER);
        productCard.add(qtyPanel,  BorderLayout.EAST);

        center.add(productCard);
        center.add(Box.createVerticalStrut(20));

        //Recent Stock-ins
        JPanel recentCard = new JPanel(new BorderLayout(0, 8)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
            @Override public boolean isOpaque() { return false; }
        };
        recentCard.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(16, 18, 16, 18)
        ));
        recentCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        //Section heade
        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setOpaque(false);
        recentHeader.setBorder(new MatteBorder(0, 0, 2, 0, GREEN_LIGHT));
        recentHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JLabel recentTitle = new JLabel("Today's Stock-Ins");
        recentTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        recentTitle.setForeground(TEXT);
        recentHeader.add(recentTitle, BorderLayout.WEST);

        String[] cols = {"#","Product","Qty Added","Barcode","Time"};
        DefaultTableModel recentModel = UI.tableModel(cols);
        JTable recentTable = UI.styledTable(recentModel);
        recentTable.getColumnModel().getColumn(0).setMaxWidth(55);
        recentTable.getColumnModel().getColumn(2).setMaxWidth(100);
        recentTable.getColumnModel().getColumn(3).setMaxWidth(120);
        recentTable.getColumnModel().getColumn(4).setMaxWidth(95);

        // Qty column
        recentTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                l.setForeground(GREEN);
                l.setFont(FONT_BOLD);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                return l;
            }
        });

        recentCard.add(recentHeader,              BorderLayout.NORTH);
        recentCard.add(UI.scrollPane(recentTable), BorderLayout.CENTER);

        putClientProperty("recentModel", recentModel);

        center.add(recentCard);

        JScrollPane centerScroll = new JScrollPane(center);
        centerScroll.setBorder(BorderFactory.createEmptyBorder());
        centerScroll.getViewport().setBackground(BG);
        UI.smoothScroll(centerScroll);
        add(centerScroll, BorderLayout.CENTER);
    }

    //HERO BANNER
    JPanel buildHeroBanner() {
        JPanel hero = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, GREEN_DARK, getWidth(), getHeight(), GREEN);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(255, 255, 255, 22));
                g2.fillOval(getWidth() - 110, -40, 160, 160);
                g2.fillOval(getWidth() - 60, getHeight() - 55, 100, 100);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override public boolean isOpaque() { return false; }
        };
        hero.setBorder(new EmptyBorder(18, 22, 18, 22));
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        hero.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLbl = new JLabel("Add Stock");
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLbl.setForeground(WHITE);

        JLabel subLbl = new JLabel("Scan or enter a barcode to restock products. Scanner-ready!");
        subLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subLbl.setForeground(new Color(255, 255, 255, 200));

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 3));
        info.setOpaque(false);
        info.add(titleLbl);
        info.add(subLbl);
        hero.add(info, BorderLayout.CENTER);
        return hero;
    }

    //BARCODE SCANNER
    void setupBarcodeScanner() {
        barcodeTimer = new javax.swing.Timer(80, e -> barcodeBuffer.setLength(0));
        barcodeTimer.setRepeats(false);

        KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .addKeyEventDispatcher(ev -> {
                if (ev.getID() != KeyEvent.KEY_TYPED) return false;
                char ch = ev.getKeyChar();
                if (ch == '\n' || ch == '\r') {
                    String code = barcodeBuffer.toString().trim();
                    barcodeBuffer.setLength(0);
                    if (code.length() >= 6) {
                        SwingUtilities.invokeLater(() -> {
                            barcodeField.setText(code);
                            searchByBarcode(code);
                        });
                    }
                } else {
                    barcodeBuffer.append(ch);
                    barcodeTimer.restart();
                }
                return false;
            });
    }

    //SEARCH
    void searchByBarcode(String barcode) {
        if (barcode.isEmpty()) return;
        String[] p = AppData.getProductByBarcode(barcode);
        if (p == null) {
            // Try partial name match
            p = products.stream()
                .filter(x -> x[1].toLowerCase().contains(barcode.toLowerCase()))
                .findFirst().orElse(null);
        }
        if (p == null) {
            statusLbl.setText("❌  Product not found for: " + barcode);
            statusLbl.setForeground(RED);
            productCard.setVisible(false);
            foundProduct = null;
            return;
        }
        foundProduct = p;
        showProductCard(p);
        barcodeField.selectAll();
    }

    void showProductCard(String[] p) {
        int stock = getStock(p);
        prodNameLbl   .setText(p[1]);
        prodCatLbl    .setText("Category: " + getCategoryName(p[2]));
        prodPriceLbl  .setText(String.format("₱ %.2f  per %s", getPrice(p), p[7]));
        prodStockLbl  .setText("Current Stock: " + stock + " " + p[7]);
        prodStockLbl  .setForeground(isOutOfStock(p) ? RED : isLowStock(p) ? GOLD : GREEN);
        prodBarcodeLbl.setText("Barcode: " + p[6]);

        statusLbl.setText("✅  Product found — enter quantity and confirm");
        statusLbl.setForeground(GREEN);
        productCard.setVisible(true);
        qtySpinner.setValue(1);
        confirmBtn.requestFocusInWindow();
        productCard.revalidate();
        productCard.repaint();
    }

    //CONFIRM
    void confirmAddStock() {
        if (foundProduct == null) {
            JOptionPane.showMessageDialog(win, "No product selected. Please scan first.");
            return;
        }
        int qty          = (int) qtySpinner.getValue();
        String prodName  = foundProduct[1];
        int oldStock     = getStock(foundProduct);

        AppData.addStock(foundProduct[0], qty);
        int newStock = getStock(foundProduct);

        prodStockLbl.setText("Current Stock: " + newStock + " " + foundProduct[7]);
        prodStockLbl.setForeground(GREEN);

        statusLbl.setText(String.format(
            "✅  Added %d %s to %s  (Stock: %d → %d)",
            qty, foundProduct[7], prodName, oldStock, newStock));
        statusLbl.setForeground(GREEN);

        refreshRecentTable();

        // Show success notification
        JOptionPane.showMessageDialog(win,
            String.format("Stock updated!\n\n%s\nAdded: +%d %s\nNew stock: %d %s",
                prodName, qty, foundProduct[7], newStock, foundProduct[7]),
            "✅  Stock Added", JOptionPane.INFORMATION_MESSAGE);

        // Reset for next scan
        barcodeField.setText("");
        barcodeField.requestFocusInWindow();
        productCard.setVisible(false);
        foundProduct = null;
    }

    //REFRESH RECENT TABLE
    void refreshRecentTable() {
        DefaultTableModel model = (DefaultTableModel) getClientProperty("recentModel");
        if (model == null) return;
        model.setRowCount(0);
        String today = new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date());
        stockRecords.stream()
            .filter(r -> r[4].equals(today))
            .forEach(r -> model.addRow(new Object[]{
                "#" + String.format("%04d", Integer.parseInt(r[0])),
                r[2], "+" + r[3], r[6], r[5]
            }));
    }

    public void onShow() {
        refreshRecentTable();
        barcodeField.setText("");
        productCard.setVisible(false);
        foundProduct = null;
        statusLbl.setText("Ready — scan a barcode to begin");
        statusLbl.setForeground(TEXT_MUTED);
        barcodeField.requestFocusInWindow();
    }
}