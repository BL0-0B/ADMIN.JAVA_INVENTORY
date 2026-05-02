package gcecc;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import static gcecc.AppData.*;


public class POSPanel extends JPanel {

    MainWindow win;

    // Cart
    List<String[]> cart = new ArrayList<>(); // [productId, name, price, qty]

    // UI components
    JTextField        searchField;
    JComboBox<String> catCombo;
    JPanel            productGrid;
    JPanel            cartItemsPanel;
    JLabel            cartCountLabel;
    JLabel            totalLabel;

    // Barcode scanner buffer
    StringBuilder     barcodeBuffer  = new StringBuilder();
    javax.swing.Timer barcodeTimer;
    boolean           scannerReady   = false;

    public POSPanel(MainWindow win) {
        this.win = win;
        setLayout(new BorderLayout(14, 0));
        setBackground(BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
        setupBarcodeScanner();
    }

    void buildUI() {

        // ── LEFT PANEL (Products) ────────────────────────────
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBackground(BG);

        // Search + filter bar
        JPanel filterBar = new JPanel(new BorderLayout(8, 0));
        filterBar.setBackground(WHITE);
        filterBar.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel searchIcon = new JLabel("🔍  ");
        searchIcon.setFont(FONT_REG);

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

        // Scan button
        JButton scanBtn = new JButton("📷 Scan");
        scanBtn.setFont(FONT_SMALL);
        scanBtn.setBackground(GREEN);
        scanBtn.setForeground(WHITE);
        scanBtn.setBorderPainted(false);
        scanBtn.setFocusPainted(false);
        scanBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        scanBtn.setBorder(new EmptyBorder(6, 12, 6, 12));
        scanBtn.addActionListener(e -> activateScanner());

        JPanel rightOfSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rightOfSearch.setBackground(WHITE);
        rightOfSearch.add(catCombo);
        rightOfSearch.add(scanBtn);

        filterBar.add(searchIcon,   BorderLayout.WEST);
        filterBar.add(searchField,  BorderLayout.CENTER);
        filterBar.add(rightOfSearch,BorderLayout.EAST);

        leftPanel.add(filterBar, BorderLayout.NORTH);

        // Product grid
        productGrid = new JPanel(new GridLayout(0, 3, 10, 10));
        productGrid.setBackground(BG);
        productGrid.setBorder(new EmptyBorder(2, 0, 2, 0));

        JScrollPane gridScroll = new JScrollPane(productGrid);
        gridScroll.setBackground(BG);
        gridScroll.getViewport().setBackground(BG);
        gridScroll.setBorder(BorderFactory.createEmptyBorder());
        gridScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        leftPanel.add(gridScroll, BorderLayout.CENTER);

        // RIGHT PANEL (Cart)
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setBackground(BG);
        rightPanel.setPreferredSize(new Dimension(340, 0));

        // Cart header card
        JPanel cartHeaderCard = new JPanel(new BorderLayout());
        cartHeaderCard.setBackground(WHITE);
        cartHeaderCard.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(12, 16, 12, 16)
        ));
        JLabel orderTitle = new JLabel("🛒  Order");
        orderTitle.setFont(FONT_LG);
        orderTitle.setForeground(TEXT);
        cartCountLabel = new JLabel("0 item(s)");
        cartCountLabel.setFont(FONT_SMALL);
        cartCountLabel.setOpaque(true);
        cartCountLabel.setBackground(GREEN);
        cartCountLabel.setForeground(WHITE);
        cartCountLabel.setBorder(new EmptyBorder(3, 10, 3, 10));
        cartHeaderCard.add(orderTitle,    BorderLayout.WEST);
        cartHeaderCard.add(cartCountLabel, BorderLayout.EAST);

        // Cart items scrollable area
        cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        cartItemsPanel.setBackground(WHITE);

        JScrollPane cartScroll = new JScrollPane(cartItemsPanel);
        cartScroll.setBorder(new LineBorder(BORDER, 1, true));
        cartScroll.getViewport().setBackground(WHITE);

        // Total
        JPanel totalCard = new JPanel(new BorderLayout());
        totalCard.setBackground(WHITE);
        totalCard.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(14, 16, 14, 16)
        ));
        JLabel totalLbl = new JLabel("TOTAL");
        totalLbl.setFont(FONT_BOLD);
        totalLbl.setForeground(TEXT_MUTED);
        totalLabel = new JLabel("₱ 0.00");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        totalLabel.setForeground(GREEN);
        totalCard.add(totalLbl,   BorderLayout.WEST);
        totalCard.add(totalLabel, BorderLayout.EAST);

        // Action buttons
        JButton proceedBtn = UI.primaryBtn("✔  Proceed");
        proceedBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        proceedBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        proceedBtn.addActionListener(e -> proceedOrder());

        JButton clearBtn = UI.outlineBtn("✖  Clear Order");
        clearBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
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

    // BARCODE SCANNER
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

    void activateScanner() {
        searchField.requestFocusInWindow();
        searchField.setText("");
        // readonly trick: focus without keyboard popup (useful on touchscreens)
        searchField.setEditable(false);
        SwingUtilities.invokeLater(() -> {
            searchField.setEditable(true);
            searchField.requestFocusInWindow();
        });
        JOptionPane.showMessageDialog(win,
            "Scanner ready! Point your scanner at a barcode.",
            "Scanner Active", JOptionPane.INFORMATION_MESSAGE);
    }

    void handleEnter() {
        String query = searchField.getText().trim();
        if (query.length() >= 6) {
            addByBarcode(query);
            searchField.setText("");
        }
    }

    void addByBarcode(String barcode) {
        String[] p = AppData.getProductByBarcode(barcode);
        if (p == null) {
            JOptionPane.showMessageDialog(win,
                "No product found for barcode: " + barcode,
                "Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }
        addToCart(p);
        searchField.setText("");
    }

    //  PRODUCT GRID 
    void loadCategories() {
        catCombo.removeAllItems();
        catCombo.addItem("0|All Categories");
        for (String[] cat : categories) {
            catCombo.addItem(cat[0] + "|" + cat[1]);
        }
    }

    void refreshGrid() {
        String search = searchField.getText().trim().toLowerCase();
        String selCat = catCombo.getSelectedItem() != null ? catCombo.getSelectedItem().toString() : "";
        String catId  = selCat.contains("|") ? selCat.split("\\|")[0] : "0";

        List<String[]> filtered = new ArrayList<>();
        for (String[] p : products) {
            if (!catId.equals("0") && !p[2].equals(catId)) continue;
            if (!search.isEmpty() &&
                !p[1].toLowerCase().contains(search) &&
                !p[6].contains(search)) continue;
            filtered.add(p);
        }

        productGrid.removeAll();
        if (filtered.isEmpty()) {
            productGrid.setLayout(new BorderLayout());
            JLabel empty = new JLabel("No products found", SwingConstants.CENTER);
            empty.setFont(FONT_REG);
            empty.setForeground(TEXT_MUTED);
            productGrid.add(empty, BorderLayout.CENTER);
        } else {
            productGrid.setLayout(new GridLayout(0, 3, 10, 10));
            for (String[] p : filtered) productGrid.add(buildTile(p));
        }
        productGrid.revalidate();
        productGrid.repaint();
    }

    JPanel buildTile(String[] p) {
        boolean out = isOutOfStock(p);
        boolean low = isLowStock(p);

        JPanel tile = new JPanel(new GridBagLayout());
        tile.setBackground(out ? new Color(0xff, 0xf5, 0xf5) : WHITE);
        tile.setBorder(new CompoundBorder(
            new LineBorder(out ? RED_LIGHT : low ? GOLD_LIGHT : BORDER, 1, true),
            new EmptyBorder(12, 8, 12, 8)
        ));
        if (!out) tile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(2, 0, 2, 0);
        g.anchor = GridBagConstraints.CENTER;

        JLabel nameLbl = new JLabel("<html><center>" + p[1] + "</center></html>", SwingConstants.CENTER);
        nameLbl.setFont(FONT_BOLD);
        nameLbl.setForeground(out ? TEXT_MUTED : TEXT);
        nameLbl.setPreferredSize(new Dimension(125, 40));

        JLabel priceLbl = new JLabel(String.format("₱ %.2f", getPrice(p)), SwingConstants.CENTER);
        priceLbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        priceLbl.setForeground(out ? TEXT_MUTED : GREEN);

        String stockTxt = out ? "🚫 Out of Stock"
                        : low ? "⚠ Low: " + getStock(p) + " left"
                        :        getStock(p) + " " + p[7] + " left";
        JLabel stockLbl = new JLabel(stockTxt, SwingConstants.CENTER);
        stockLbl.setFont(FONT_SMALL);
        stockLbl.setForeground(out ? RED : low ? GOLD : TEXT_MUTED);

        g.gridy = 0; tile.add(nameLbl,  g);
        g.gridy = 1; tile.add(priceLbl, g);
        g.gridy = 2; tile.add(stockLbl, g);

        if (!out) {
            tile.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) { addToCart(p); }
                public void mouseEntered(MouseEvent e) {
                    tile.setBackground(GREEN_PALE);
                    tile.setBorder(new CompoundBorder(
                        new LineBorder(GREEN, 1, true), new EmptyBorder(12, 8, 12, 8)));
                }
                public void mouseExited(MouseEvent e) {
                    tile.setBackground(WHITE);
                    tile.setBorder(new CompoundBorder(
                        new LineBorder(BORDER, 1, true), new EmptyBorder(12, 8, 12, 8)));
                }
            });
        }
        return tile;
    }

    // ── CART ──────────────────────────────────────────────────
    void addToCart(String[] product) {
        if (isOutOfStock(product)) {
            JOptionPane.showMessageDialog(win, "Out of stock!", "Stock Alert", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Check if already in cart
        for (String[] item : cart) {
            if (item[0].equals(product[0])) {
                int curQty  = Integer.parseInt(item[3]);
                int maxStock = getStock(product);
                if (curQty >= maxStock) {
                    JOptionPane.showMessageDialog(win, "Not enough stock!", "Stock Alert", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                item[3] = String.valueOf(curQty + 1);
                renderCart();
                return;
            }
        }
        cart.add(new String[]{ product[0], product[1], product[3], "1" });
        renderCart();
    }

    void renderCart() {
        cartItemsPanel.removeAll();
        double total = 0;

        if (cart.isEmpty()) {
            JLabel empty = new JLabel("Tap a product to add it", SwingConstants.CENTER);
            empty.setFont(FONT_REG);
            empty.setForeground(TEXT_MUTED);
            empty.setBorder(new EmptyBorder(30, 0, 30, 0));
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            cartItemsPanel.add(empty);
        } else {
            for (int i = 0; i < cart.size(); i++) {
                String[] item = cart.get(i);
                int    qty    = Integer.parseInt(item[3]);
                double price  = Double.parseDouble(item[2]);
                double lineTotal = price * qty;
                total += lineTotal;
                cartItemsPanel.add(buildCartRow(item, i));
                cartItemsPanel.add(Box.createVerticalStrut(4));
            }
        }

        totalLabel.setText(String.format("₱ %.2f", total));
        cartCountLabel.setText(cart.size() + " item(s)");
        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
    }

    JPanel buildCartRow(String[] item, int index) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(GREEN_PALE);
        row.setBorder(new CompoundBorder(
            new LineBorder(GREEN_LIGHT, 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // Info
        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setBackground(GREEN_PALE);
        JLabel nameLbl = new JLabel(item[1]);
        nameLbl.setFont(FONT_BOLD);
        nameLbl.setForeground(TEXT);
        int qty = Integer.parseInt(item[3]);
        double price = Double.parseDouble(item[2]);
        JLabel priceLbl = new JLabel(String.format("₱ %.2f", price * qty));
        priceLbl.setFont(FONT_SMALL);
        priceLbl.setForeground(GREEN);
        info.add(nameLbl);
        info.add(priceLbl);

        // Qty controls
        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        qtyPanel.setBackground(GREEN_PALE);

        JButton minus = qtyBtn("−");
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(qty, 1, 999, 1));
        spinner.setPreferredSize(new Dimension(52, 28));
        spinner.setFont(FONT_BOLD);
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setHorizontalAlignment(SwingConstants.CENTER);
        JButton plus  = qtyBtn("+");
        JButton del   = new JButton("✕");
        del.setFont(FONT_SMALL);
        del.setForeground(RED);
        del.setBackground(WHITE);
        del.setBorder(new EmptyBorder(4, 8, 4, 8));
        del.setBorderPainted(false);
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
        btn.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(4, 8, 4, 8)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(30, 28));
        return btn;
    }

    void clearCart() { cart.clear(); renderCart(); }

    void proceedOrder() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(win, "Cart is empty!", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double total = 0;
        // Build items summary
        StringBuilder itemsSummary = new StringBuilder();

        for (String[] item : cart) {
            int    qty       = Integer.parseInt(item[3]);
            double price     = Double.parseDouble(item[2]);
            double lineTotal = price * qty;
            total += lineTotal;
            AppData.deductStock(item[0], qty);
            if (itemsSummary.length() > 0) itemsSummary.append(", ");
            itemsSummary.append(item[1]).append(" x").append(qty);
        }

        // Save transaction
        AppData.transactions.add(new String[]{
            String.valueOf(AppData.nextTxId++),
            String.valueOf(cart.size()),
            String.format("%.2f", total),
            new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date()),
            new java.text.SimpleDateFormat("hh:mm a").format(new java.util.Date()),
            itemsSummary.toString()   
        });

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
