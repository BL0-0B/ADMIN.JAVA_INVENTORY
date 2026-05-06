package gcecc;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;

import static gcecc.AppData.*;

/*
POSPanel.java — Point of Sale screen.
 Product tile grid on left, cart on right.
 Checkout shows cash input + receipt popup.
 */
public class POSPanel extends JPanel {

    MainWindow win;
    List<String[]> cart = new ArrayList<>();

    JTextField        searchField;
    JComboBox<String> catCombo;
    JPanel            productGrid;
    JPanel            cartItemsPanel;
    JLabel            cartCountLabel;
    JLabel            totalLabel;

    StringBuilder     barcodeBuffer = new StringBuilder();
    javax.swing.Timer barcodeTimer;

    public POSPanel(MainWindow win) {
        this.win = win;
        setLayout(new BorderLayout(14, 0));
        setBackground(BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
        setupBarcodeScanner();
    }

    void buildUI() {

        // ── LEFT: Products
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBackground(BG);

        // Page title
        JLabel title = UI.sectionTitle("Inventory");
        leftPanel.add(title, BorderLayout.NORTH);

        // Search + filter bar
        JPanel filterBar = new JPanel(new BorderLayout(8, 0));
        filterBar.setBackground(WHITE);
        filterBar.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel searchIcon = new JLabel("🔍  ");
        searchField = UI.field("Search product or scan barcode...");
        searchField.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        searchField.addActionListener(e -> handleEnter());
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { refreshGrid(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { refreshGrid(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refreshGrid(); }
        });

        catCombo = new JComboBox<>();
        catCombo.setFont(FONT_REG);
        catCombo.setPreferredSize(new Dimension(155, 34));
        catCombo.addActionListener(e -> refreshGrid());

        JButton scanBtn = new JButton("📷 Scan");
        scanBtn.setFont(FONT_SMALL);
        scanBtn.setBackground(GREEN);
        scanBtn.setForeground(WHITE);
        scanBtn.setBorderPainted(false);
        scanBtn.setFocusPainted(false);
        scanBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        scanBtn.setBorder(new EmptyBorder(7, 14, 7, 14));
        scanBtn.addActionListener(e -> searchField.requestFocus());

        JPanel rightOfSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rightOfSearch.setBackground(WHITE);
        rightOfSearch.add(catCombo);
        rightOfSearch.add(scanBtn);

        filterBar.add(searchIcon,    BorderLayout.WEST);
        filterBar.add(searchField,   BorderLayout.CENTER);
        filterBar.add(rightOfSearch, BorderLayout.EAST);

        JPanel leftCenter = new JPanel(new BorderLayout(0, 8));
        leftCenter.setBackground(BG);
        leftCenter.add(filterBar, BorderLayout.NORTH);

        // Product grid
        productGrid = new JPanel(new GridLayout(0, 3, 10, 10));
        productGrid.setBackground(BG);
        productGrid.setBorder(new EmptyBorder(4, 0, 4, 0));

        JScrollPane gridScroll = new JScrollPane(productGrid);
        gridScroll.setBackground(BG);
        gridScroll.getViewport().setBackground(BG);
        gridScroll.setBorder(BorderFactory.createEmptyBorder());
        gridScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        UI.smoothScroll(gridScroll);
        leftCenter.add(gridScroll, BorderLayout.CENTER);

        leftPanel.add(leftCenter, BorderLayout.CENTER);

        //RIGHT: Cart
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setBackground(BG);
        rightPanel.setPreferredSize(new Dimension(350, 0));

        // Cart header
        JPanel cartHeaderCard = new JPanel(new BorderLayout());
        cartHeaderCard.setBackground(WHITE);
        cartHeaderCard.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(14, 16, 14, 16)
        ));
        JLabel orderTitle = new JLabel("🛒  Order");
        orderTitle.setFont(FONT_LG);
        orderTitle.setForeground(TEXT);

        cartCountLabel = new JLabel("0 item(s)");
        cartCountLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        cartCountLabel.setOpaque(true);
        cartCountLabel.setBackground(GREEN);
        cartCountLabel.setForeground(WHITE);
        cartCountLabel.setBorder(new EmptyBorder(4, 12, 4, 12));

        cartHeaderCard.add(orderTitle,    BorderLayout.WEST);
        cartHeaderCard.add(cartCountLabel, BorderLayout.EAST);

        // Cart items
        cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        cartItemsPanel.setBackground(WHITE);
        cartItemsPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane cartScroll = new JScrollPane(cartItemsPanel);
        cartScroll.setBorder(new LineBorder(BORDER, 1, true));
        cartScroll.getViewport().setBackground(WHITE);
        UI.smoothScroll(cartScroll);

        // Total card
        JPanel totalCard = new JPanel(new BorderLayout());
        totalCard.setBackground(WHITE);
        totalCard.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(16, 18, 16, 18)
        ));
        JLabel totalLbl = new JLabel("TOTAL");
        totalLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        totalLbl.setForeground(TEXT_MUTED);

        totalLabel = new JLabel("₱ 0.00");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        totalLabel.setForeground(GREEN);

        totalCard.add(totalLbl,   BorderLayout.WEST);
        totalCard.add(totalLabel, BorderLayout.EAST);

        // Action buttons
        JButton proceedBtn = UI.primaryBtn("✔  Proceed");
        proceedBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        proceedBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        proceedBtn.addActionListener(e -> proceedOrder());

        JButton clearBtn = UI.outlineBtn("✖  Clear Order");
        clearBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        clearBtn.addActionListener(e -> clearCart());

        JPanel actionPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        actionPanel.setBackground(BG);
        actionPanel.add(proceedBtn);
        actionPanel.add(clearBtn);

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 8));
        bottomPanel.setBackground(BG);
        bottomPanel.add(totalCard,   BorderLayout.NORTH);
        bottomPanel.add(actionPanel, BorderLayout.CENTER);

        rightPanel.add(cartHeaderCard, BorderLayout.NORTH);
        rightPanel.add(cartScroll,     BorderLayout.CENTER);
        rightPanel.add(bottomPanel,    BorderLayout.SOUTH);

        add(leftPanel,  BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
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
                        SwingUtilities.invokeLater(() -> addByBarcode(code));
                    }
                } else {
                    barcodeBuffer.append(ch);
                    barcodeTimer.restart();
                }
                return false;
            });
    }

    void handleEnter() {
        String text = searchField.getText().trim();
        if (text.length() >= 6) {
            String[] p = AppData.getProductByBarcode(text);
            if (p != null) { addToCart(p); searchField.setText(""); return; }
        }
        refreshGrid();
    }

    void addByBarcode(String code) {
        String[] p = AppData.getProductByBarcode(code);
        if (p != null) addToCart(p);
        else {
            searchField.setText(code);
            refreshGrid();
        }
    }

    //CATEGORIES
    void loadCategories() {
        Object prev = catCombo.getSelectedItem();
        catCombo.removeAllItems();
        catCombo.addItem("0|All Categories");
        for (String[] c : categories) catCombo.addItem(c[0] + "|" + c[1]);
        catCombo.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v != null) { String sv = v.toString(); setText(sv.contains("|") ? sv.split("\\|")[1] : sv); }
                return this;
            }
        });
        if (prev != null) catCombo.setSelectedItem(prev);
    }

    //PRODUCT GRID
    void refreshGrid() {
        productGrid.removeAll();
        String search = searchField.getText().trim().toLowerCase();
        String selCat = catCombo.getSelectedItem() != null ? catCombo.getSelectedItem().toString() : "";
        String catId  = selCat.contains("|") ? selCat.split("\\|")[0] : "0";

        List<String[]> filtered = new ArrayList<>();
        for (String[] p : products) {
            if (!catId.equals("0") && !p[2].equals(catId)) continue;
            if (!search.isEmpty() && !p[1].toLowerCase().contains(search) && !p[6].contains(search)) continue;
            filtered.add(p);
        }

        if (filtered.isEmpty()) {
            JLabel empty = new JLabel("No products found", SwingConstants.CENTER);
            empty.setFont(FONT_REG);
            empty.setForeground(TEXT_MUTED);
            productGrid.setLayout(new BorderLayout());
            productGrid.add(empty, BorderLayout.CENTER);
        } else {
            productGrid.setLayout(new GridLayout(0, 3, 10, 10));
            for (String[] p : filtered) productGrid.add(buildTile(p));
        }

        productGrid.revalidate();
        productGrid.repaint();
    }

    JPanel buildTile(String[] p) {
        boolean out  = isOutOfStock(p);
        boolean low  = isLowStock(p);

        JPanel tile = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(out ? new Color(0xfe, 0xf2, 0xf2) : WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
            @Override public boolean isOpaque() { return false; }
        };
        tile.setLayout(new BorderLayout(0, 4));
        tile.setBorder(new CompoundBorder(
            new LineBorder(out ? RED_LIGHT : low ? GOLD_LIGHT : BORDER, 1, true),
            new EmptyBorder(14, 10, 14, 10)
        ));

        // Product name
        JLabel nameLbl = new JLabel("<html><center>" + p[1] + "</center></html>", SwingConstants.CENTER);
        nameLbl.setFont(FONT_BOLD);
        nameLbl.setForeground(out ? TEXT_MUTED : TEXT);

        // Size pill (if product has a size)
        String sizeVal = p.length > 8 && p[8] != null && !p[8].isEmpty() ? p[8] : "";
        JLabel sizeLbl = UI.pill(sizeVal.isEmpty() ? "No Size" : sizeVal,
                                 sizeVal.isEmpty() ? new Color(0xf3,0xf4,0xf6) : BLUE_LIGHT,
                                 sizeVal.isEmpty() ? TEXT_MUTED : new Color(0x1d,0x4e,0xd8));
        sizeLbl.setHorizontalAlignment(SwingConstants.CENTER);

        // Price
        JLabel priceLbl = new JLabel(String.format("₱ %.2f", getPrice(p)), SwingConstants.CENTER);
        priceLbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        priceLbl.setForeground(out ? TEXT_MUTED : GREEN);

        // Stock pill
        String stockText = out ? "Out of Stock" : low ? "Low: " + p[4] : p[4] + " " + p[7];
        Color pillBg = out ? RED_LIGHT : low ? GOLD_LIGHT : GREEN_LIGHT;
        Color pillFg = out ? RED       : low ? GOLD       : GREEN_DARK;
        JLabel stockPill = UI.pill(stockText, pillBg, pillFg);
        stockPill.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel center = new JPanel(new GridLayout(4, 1, 0, 3));
        center.setOpaque(false);
        center.add(nameLbl);
        center.add(sizeLbl);
        center.add(priceLbl);
        center.add(stockPill);

        tile.add(center, BorderLayout.CENTER);

        if (!out) {
            tile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            tile.addMouseListener(new MouseAdapter() {
                final Border normal  = tile.getBorder();
                final Border hovered = new CompoundBorder(
                    new LineBorder(GREEN, 1, true), new EmptyBorder(14, 10, 14, 10));
                public void mouseClicked(MouseEvent e) { addToCart(p); }
                public void mouseEntered(MouseEvent e) { tile.setBorder(hovered); }
                public void mouseExited (MouseEvent e) { tile.setBorder(normal);  }
            });
        }
        return tile;
    }

    //CART
    void addToCart(String[] product) {
        if (isOutOfStock(product)) {
            JOptionPane.showMessageDialog(win, "This product is out of stock!", "Stock Alert", JOptionPane.WARNING_MESSAGE);
            return;
        }
        for (String[] item : cart) {
            if (item[0].equals(product[0])) {
                int curQty   = Integer.parseInt(item[3]);
                int maxStock = getStock(product);
                if (curQty >= maxStock) {
                    JOptionPane.showMessageDialog(win, "Not enough stock!", "Stock Alert", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                item[3] = String.valueOf(curQty + 1);
                renderCart(); return;
            }
        }
        cart.add(new String[]{ product[0], product[1], product[3], "1",
                               product.length > 8 && product[8] != null ? product[8] : "" });
        renderCart();
    }

    void renderCart() {
        cartItemsPanel.removeAll();
        double total = 0;

        if (cart.isEmpty()) {
            JLabel empty = new JLabel("<html><center>Tap a product to add it to<br>the order</center></html>", SwingConstants.CENTER);
            empty.setFont(FONT_REG);
            empty.setForeground(TEXT_MUTED);
            empty.setBorder(new EmptyBorder(40, 0, 40, 0));
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            cartItemsPanel.add(empty);
        } else {
            for (int i = 0; i < cart.size(); i++) {
                String[] item = cart.get(i);
                int    qty   = Integer.parseInt(item[3]);
                double price = Double.parseDouble(item[2]);
                total += price * qty;
                cartItemsPanel.add(buildCartRow(item, i));
                cartItemsPanel.add(Box.createVerticalStrut(6));
            }
        }

        totalLabel.setText(String.format("₱ %.2f", total));
        cartCountLabel.setText(cart.size() + " item(s)");
        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
    }

    JPanel buildCartRow(String[] item, int index) {
        JPanel row = new JPanel(new BorderLayout(8, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GREEN_PALE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
            @Override public boolean isOpaque() { return false; }
        };
        row.setBorder(new CompoundBorder(
            new LineBorder(GREEN_LIGHT, 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
        info.setOpaque(false);
        JLabel nameLbl = new JLabel(item[1] + (item.length > 4 && !item[4].isEmpty() ? "  [" + item[4] + "]" : ""));
        nameLbl.setFont(FONT_BOLD);
        nameLbl.setForeground(TEXT);
        int qty = Integer.parseInt(item[3]);
        double price = Double.parseDouble(item[2]);
        JLabel priceLbl = new JLabel(String.format("₱ %.2f  ×  %d", price, qty));
        priceLbl.setFont(FONT_SMALL);
        priceLbl.setForeground(GREEN);
        info.add(nameLbl);
        info.add(priceLbl);

        // Qty controls
        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        qtyPanel.setOpaque(false);

        JButton minus   = qtyBtn("−");
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(qty, 1, 999, 1));
        spinner.setPreferredSize(new Dimension(52, 28));
        spinner.setFont(FONT_BOLD);
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setHorizontalAlignment(SwingConstants.CENTER);
        JButton plus = qtyBtn("+");
        JButton del  = new JButton("✕");
        del.setFont(FONT_SMALL);
        del.setForeground(RED);
        del.setBackground(WHITE);
        del.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(4, 7, 4, 7)));
        del.setFocusPainted(false);
        del.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        minus.addActionListener(e -> {
            int v = (int) spinner.getValue();
            if (v > 1) { item[3] = String.valueOf(v - 1); renderCart(); }
        });
        plus.addActionListener(e -> {
            String[] prod = AppData.getProductById(item[0]);
            int maxStock  = prod != null ? getStock(prod) : 999;
            int v = (int) spinner.getValue();
            if (v < maxStock) { item[3] = String.valueOf(v + 1); renderCart(); }
            else JOptionPane.showMessageDialog(win, "Not enough stock!");
        });
        spinner.addChangeListener(e -> {
            String[] prod = AppData.getProductById(item[0]);
            int maxStock  = prod != null ? getStock(prod) : 999;
            int v = (int) spinner.getValue();
            item[3] = String.valueOf(Math.min(v, maxStock));
        });
        del.addActionListener(e -> { cart.remove(index); renderCart(); });

        qtyPanel.add(minus);
        qtyPanel.add(spinner);
        qtyPanel.add(plus);
        qtyPanel.add(del);

        row.add(info,     BorderLayout.CENTER);
        row.add(qtyPanel, BorderLayout.EAST);
        return row;
    }

    JButton qtyBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(WHITE);
        btn.setForeground(GREEN);
        btn.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(4, 8, 4, 8)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(30, 28));
        return btn;
    }

    void clearCart() { cart.clear(); renderCart(); }

    //PROCEED/RECEIPT
    void proceedOrder() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(win, "Cart is empty!", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = cart.stream()
            .mapToDouble(i -> Double.parseDouble(i[2]) * Integer.parseInt(i[3])).sum();

        // Cash input dialog
        JPanel cashPanel = new JPanel(new BorderLayout(0, 10));
        cashPanel.setBackground(WHITE);
        cashPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel totalInfo = new JLabel(String.format("Total:  ₱ %.2f", total), SwingConstants.CENTER);
        totalInfo.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalInfo.setForeground(GREEN);

        JTextField cashField = UI.field("Enter cash amount");
        cashField.setFont(new Font("SansSerif", Font.BOLD, 16));
        cashField.setText(String.format("%.2f", total));

        cashPanel.add(totalInfo, BorderLayout.NORTH);
        cashPanel.add(cashField, BorderLayout.CENTER);

        int res = JOptionPane.showConfirmDialog(win, cashPanel,
            "Cash Received", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res != JOptionPane.OK_OPTION) return;

        double cash;
        try {
            cash = Double.parseDouble(cashField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(win, "Invalid cash amount."); return;
        }

        if (cash < total) {
            JOptionPane.showMessageDialog(win, String.format(
                "Cash (₱ %.2f) is less than total (₱ %.2f).", cash, total),
                "Insufficient Cash", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double change = cash - total;

        // Deduct stock & build summary + itemDetails string
        StringBuilder itemsSummary = new StringBuilder();
        StringBuilder itemDetails  = new StringBuilder();
        for (String[] item : cart) {
            int qty = Integer.parseInt(item[3]);
            AppData.deductStock(item[0], qty);
            if (itemsSummary.length() > 0) itemsSummary.append(", ");
            itemsSummary.append(item[1]).append(" x").append(qty);
            // Format: "id|name|size|price|qty" separated by ";;"
            String size = item.length > 4 ? item[4] : "";
            if (itemDetails.length() > 0) itemDetails.append(";;");
            itemDetails.append(item[0]).append("|")
                       .append(item[1]).append("|")
                       .append(size).append("|")
                       .append(item[2]).append("|")
                       .append(qty);
        }

        String orNum = String.format("%06d", AppData.nextTxId);
        AppData.transactions.add(new String[]{
            String.valueOf(AppData.nextTxId++),
            String.valueOf(cart.size()),
            String.format("%.2f", total),
            new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date()),
            new java.text.SimpleDateFormat("hh:mm a").format(new java.util.Date()),
            itemsSummary.toString(),
            itemDetails.toString()
        });

        // Receipt dialog
        JPanel receiptPanel = UI.buildReceiptPanel(orNum, new ArrayList<>(cart), total, cash, change);
        JScrollPane receiptScroll = new JScrollPane(receiptPanel);
        receiptScroll.setPreferredSize(new Dimension(360, 460));
        receiptScroll.setBorder(BorderFactory.createEmptyBorder());

        JOptionPane.showMessageDialog(win, receiptScroll,
            "Receipt — OR #" + orNum, JOptionPane.PLAIN_MESSAGE);

        cart.clear();
        renderCart();
        refreshGrid();
    }

    public void onShow() {
        loadCategories();
        refreshGrid();
        searchField.requestFocusInWindow();
    }
}