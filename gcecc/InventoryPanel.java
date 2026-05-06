package gcecc;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;

import static gcecc.AppData.*;

/*
 InentoryPanel.java — View and manage products and categories.
 Styled to match the website: section heroes, table cards, pill badges,
 green section headers, category description field.
 */
public class InventoryPanel extends JPanel {

    MainWindow win;
    DefaultTableModel prodModel, catModel;
    JTable prodTable, catTable;
    JTextField prodSearch;
    JComboBox<String> prodCatFilter;

    public InventoryPanel(MainWindow win) {
        this.win = win;
        setLayout(new BorderLayout(0, 12));
        setBackground(BG);
        setBorder(new EmptyBorder(22, 22, 22, 22));
        buildUI();
    }

    void buildUI() {
        // Page title
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(BG);
        titleRow.setBorder(new EmptyBorder(0, 0, 12, 0));
        JLabel title = new JLabel("📦  Inventory Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(TEXT);
        JLabel sub = new JLabel("Manage products, categories and stock levels");
        sub.setFont(FONT_SMALL);
        sub.setForeground(TEXT_MUTED);
        titleRow.add(title, BorderLayout.NORTH);
        titleRow.add(sub,   BorderLayout.SOUTH);
        add(titleRow, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_BOLD);
        tabs.setBackground(WHITE);
        tabs.addTab("  Products  ",   buildProductsTab());
        tabs.addTab("  Categories  ", buildCategoriesTab());
        add(tabs, BorderLayout.CENTER);
    }

    //PRODUCTS TAB
    JPanel buildProductsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(14, 0, 0, 0));

        // Hero banne
        JPanel hero = buildHeroBanner(
            "Products",
            "View, add, edit and delete products from your inventory"
        );
        panel.add(hero, BorderLayout.NORTH);

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(8, 0));
        toolbar.setBackground(BG);
        toolbar.setBorder(new EmptyBorder(0, 0, 8, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setBackground(BG);

        // Search bar
        JPanel searchWrap = new JPanel(new BorderLayout(6, 0));
        searchWrap.setBackground(WHITE);
        searchWrap.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(6, 12, 6, 12)
        ));
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(FONT_SMALL);
        prodSearch = UI.field("");
        prodSearch.setBorder(BorderFactory.createEmptyBorder());
        prodSearch.putClientProperty("JTextField.placeholderText", "Search products...");
        prodSearch.setPreferredSize(new Dimension(200, 26));
        prodSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { loadProducts(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { loadProducts(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { loadProducts(); }
        });
        searchWrap.add(searchIcon,  BorderLayout.WEST);
        searchWrap.add(prodSearch,  BorderLayout.CENTER);
        searchWrap.setPreferredSize(new Dimension(240, 38));

        prodCatFilter = new JComboBox<>();
        prodCatFilter.setFont(FONT_REG);
        prodCatFilter.setPreferredSize(new Dimension(165, 38));
        prodCatFilter.addActionListener(e -> loadProducts());

        left.add(searchWrap);
        left.add(prodCatFilter);

        JButton addBtn = UI.primaryBtn("＋  Add Product");
        toolbar.add(left,   BorderLayout.WEST);
        toolbar.add(addBtn, BorderLayout.EAST);
        addBtn.addActionListener(e -> showProductDialog(null));

        // Table card
        prodModel = UI.tableModel(new String[]{"#","Product Name","Size","Category","Price","Stock","Barcode","Status"});
        prodTable = UI.styledTable(prodModel);
        prodTable.getColumnModel().getColumn(0).setMaxWidth(44);
        prodTable.getColumnModel().getColumn(2).setMaxWidth(75);
        prodTable.getColumnModel().getColumn(4).setMaxWidth(95);
        prodTable.getColumnModel().getColumn(5).setMaxWidth(80);
        prodTable.getColumnModel().getColumn(6).setMaxWidth(115);
        prodTable.getColumnModel().getColumn(7).setMaxWidth(110);

        // Alternating row colours
        prodTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, s, f, r, c);
                if (s) { setBackground(GREEN_PALE); setForeground(GREEN_DARK); }
                else   { setBackground(r % 2 == 0 ? WHITE : new Color(0xf9, 0xff, 0xfe)); setForeground(TEXT); }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
        // Size column — blue pill (must be set after setDefaultRenderer)
        prodTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                String sv = v != null ? v.toString() : "";
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if (!sv.isEmpty() && !sv.equals("—")) {
                    lbl.setOpaque(true);
                    lbl.setBackground(s ? GREEN_PALE : BLUE_LIGHT);
                    lbl.setForeground(s ? GREEN_DARK : new Color(0x1d, 0x4e, 0xd8));
                    lbl.setFont(FONT_BOLD);
                } else {
                    lbl.setForeground(TEXT_MUTED);
                }
                return lbl;
            }
        });
        // Status column — coloured pill (must be set after setDefaultRenderer)
        prodTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                lbl.setOpaque(true);
                String sv = v != null ? v.toString() : "";
                switch (sv) {
                    case "Out of Stock" -> { lbl.setBackground(RED_LIGHT);   lbl.setForeground(RED);       }
                    case "Low Stock"    -> { lbl.setBackground(GOLD_LIGHT);  lbl.setForeground(GOLD);      }
                    default             -> { lbl.setBackground(GREEN_LIGHT); lbl.setForeground(GREEN_DARK);}
                }
                lbl.setFont(FONT_BOLD);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                return lbl;
            }
        });

        // Action buttons row
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        actions.setBackground(BG);
        JButton editBtn = UI.outlineGreenBtn("✏  Edit");
        JButton delBtn  = UI.dangerBtn("🗑  Delete");
        editBtn.addActionListener(e -> editSelectedProduct());
        delBtn .addActionListener(e -> deleteSelectedProduct());
        actions.add(editBtn);
        actions.add(delBtn);

        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setBackground(BG);
        center.add(toolbar,                 BorderLayout.NORTH);
        center.add(UI.scrollPane(prodTable),BorderLayout.CENTER);
        center.add(actions,                 BorderLayout.SOUTH);

        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    void loadProducts() {
        Object prevSel = prodCatFilter.getSelectedItem();
        prodCatFilter.removeAllItems();
        prodCatFilter.addItem("0|All Categories");
        for (String[] c : categories) prodCatFilter.addItem(c[0] + "|" + c[1]);
        prodCatFilter.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v != null) { String sv = v.toString(); setText(sv.contains("|") ? sv.split("\\|")[1] : sv); }
                return this;
            }
        });
        if (prevSel != null) prodCatFilter.setSelectedItem(prevSel);

        String search = prodSearch.getText().trim().toLowerCase();
        String selCat = prodCatFilter.getSelectedItem() != null ? prodCatFilter.getSelectedItem().toString() : "";
        String catId  = selCat.contains("|") ? selCat.split("\\|")[0] : "0";

        prodModel.setRowCount(0);
        int i = 1;
        for (String[] p : products) {
            if (!catId.equals("0") && !p[2].equals(catId)) continue;
            if (!search.isEmpty() && !p[1].toLowerCase().contains(search) && !p[6].contains(search)) continue;
            String size   = p.length > 8 && p[8] != null && !p[8].isEmpty() ? p[8] : "—";
            String status = isOutOfStock(p) ? "Out of Stock"
                          : isLowStock(p)   ? "Low Stock"
                          :                   "In Stock";
            prodModel.addRow(new Object[]{
                i++, p[1], size, getCategoryName(p[2]),
                String.format("₱ %.2f", getPrice(p)),
                p[4] + " " + p[7], p[6], status
            });
        }
    }

    void editSelectedProduct() {
        int row = prodTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(win, "Select a product first."); return; }
        String barcode = prodModel.getValueAt(row, 6).toString();
        String[] p = products.stream().filter(x -> x[6].equals(barcode)).findFirst().orElse(null);
        showProductDialog(p);
    }

    void deleteSelectedProduct() {
        int row = prodTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(win, "Select a product first."); return; }
        String name = prodModel.getValueAt(row, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(win,
            "Delete \"" + name + "\"?\nThis cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        String barcode = prodModel.getValueAt(row, 6).toString();
        products.removeIf(p -> p[6].equals(barcode));
        loadProducts();
        showToast("Product deleted.");
    }

    void showProductDialog(String[] existing) {
        JDialog dlg = new JDialog(win, existing == null ? "Add Product" : "Edit Product", true);
        dlg.setSize(440, 620);
        dlg.setLocationRelativeTo(win);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(WHITE);
        form.setBorder(new EmptyBorder(24, 28, 24, 28));

        // Dialog title bar
        JLabel dlgTitle = new JLabel(existing == null ? "＋  Add New Product" : "✏  Edit Product");
        dlgTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        dlgTitle.setForeground(GREEN);
        dlgTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        dlgTitle.setBorder(new EmptyBorder(0, 0, 14, 0));

        JTextField nameF     = UI.field("Product name");
        JTextField priceF    = UI.field("e.g. 650.00");
        JTextField stockF    = UI.field("Current stock");
        JTextField lowStockF = UI.field("Alert threshold (default: 5)");
        JTextField barcodeF  = UI.field("Leave blank to auto-generate");
        JTextField customSizeF = UI.field("Enter custom size");
        customSizeF.setVisible(false);

        JComboBox<String> catBox  = new JComboBox<>();
        JComboBox<String> unitBox = new JComboBox<>(new String[]{"pcs","set","kg","g","L","mL","pack","box"});
        JComboBox<String> sizeBox = new JComboBox<>(new String[]{
            "No Size","XS","S","M","L","XL","2XL","3XL","4XL","5XL","Custom"
        });
        sizeBox.addActionListener(e -> {
            boolean custom = "Custom".equals(sizeBox.getSelectedItem());
            customSizeF.setVisible(custom);
            customSizeF.getParent().revalidate();
        });

        catBox.addItem("0|Select Category");
        for (String[] c : categories) catBox.addItem(c[0] + "|" + c[1]);
        catBox.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v != null) { String sv = v.toString(); setText(sv.contains("|") ? sv.split("\\|")[1] : sv); }
                return this;
            }
        });

        if (existing != null) {
            nameF.setText(existing[1]);
            priceF.setText(existing[3]);
            stockF.setText(existing[4]);
            lowStockF.setText(existing[5]);
            barcodeF.setText(existing[6]);
            unitBox.setSelectedItem(existing[7]);
            // Size (index 8 if present)
            if (existing.length > 8 && existing[8] != null && !existing[8].isEmpty()) {
                String sz = existing[8];
                boolean found = false;
                for (int i = 0; i < sizeBox.getItemCount(); i++) {
                    if (sizeBox.getItemAt(i).equals(sz)) { sizeBox.setSelectedIndex(i); found = true; break; }
                }
                if (!found) { sizeBox.setSelectedItem("Custom"); customSizeF.setText(sz); customSizeF.setVisible(true); }
            }
            for (int i = 0; i < catBox.getItemCount(); i++) {
                if (catBox.getItemAt(i).startsWith(existing[2] + "|")) { catBox.setSelectedIndex(i); break; }
            }
        }

        String[][] fieldDefs = {
            {"Product Name *"}, {"Category *"},
            {"Price (₱) *"}, {"Stock *"},
            {"Low Stock Alert Qty"}, {"Barcode"}, {"Unit"}, {"Size"}, {"Custom Size"}
        };
        JComponent[] comps = {nameF, catBox, priceF, stockF, lowStockF, barcodeF, unitBox, sizeBox, customSizeF};

        form.add(dlgTitle);

        for (int i = 0; i < comps.length; i++) {
            JLabel lbl = UI.formLabel(fieldDefs[i][0]);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            comps[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            comps[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            if (comps[i] instanceof JTextField) {
                ((JTextField) comps[i]).setBorder(new CompoundBorder(
                    new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
            }
            form.add(lbl);
            form.add(Box.createVerticalStrut(4));
            form.add(comps[i]);
            form.add(Box.createVerticalStrut(12));
        }

        // Numeric-only fields for price and stock
        addNumericFilter(priceF, true);   // decimals allowed
        addNumericFilter(stockF, false);  // integers only
        addNumericFilter(lowStockF, false);

        JButton saveBtn = UI.primaryBtn(existing == null ? "Save Product" : "Update Product");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        saveBtn.addActionListener(e -> {
            String name   = nameF.getText().trim();
            String catSel = catBox.getSelectedItem().toString();
            String catId  = catSel.split("\\|")[0];

            if (name.isEmpty() || catId.equals("0")) {
                JOptionPane.showMessageDialog(dlg, "Please fill in all required (*) fields."); return;
            }
            if (priceF.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Please enter a valid price."); return;
            }
            if (stockF.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Please enter the stock quantity."); return;
            }

            double price  = Double.parseDouble(priceF.getText().trim());
            int    stock  = Integer.parseInt(stockF.getText().trim());
            int    low    = lowStockF.getText().trim().isEmpty() ? 5 : Integer.parseInt(lowStockF.getText().trim());
            String barcode = barcodeF.getText().trim().isEmpty()
                ? "8934" + (System.currentTimeMillis() % 1000000) : barcodeF.getText().trim();
            String unit = unitBox.getSelectedItem().toString();
            String sizeVal = "Custom".equals(sizeBox.getSelectedItem())
                ? customSizeF.getText().trim()
                : ("No Size".equals(sizeBox.getSelectedItem()) ? "" : sizeBox.getSelectedItem().toString());

            String date = new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date());
            String time = new java.text.SimpleDateFormat("hh:mm a").format(new java.util.Date());

            if (existing == null) {
                String newId = String.valueOf(nextProdId++);
                products.add(new String[]{
                    newId, name, catId,
                    String.valueOf(price), String.valueOf(stock),
                    String.valueOf(low), barcode, unit, sizeVal
                });
                // Record in stockRecords so Dashboard Items Added tab shows it
                // stockRecord: [id, productId, productName, qty, date, time, barcode, size, price]
                stockRecords.add(new String[]{
                    String.valueOf(nextStockId++),
                    newId, name,
                    String.valueOf(stock),
                    date, time, barcode, sizeVal,
                    String.valueOf(price)
                });
            } else {
                existing[1] = name; existing[2] = catId;
                existing[3] = String.valueOf(price); existing[4] = String.valueOf(stock);
                existing[5] = String.valueOf(low);   existing[6] = barcode;
                existing[7] = unit;
                if (existing.length > 8) existing[8] = sizeVal;
                // Record edit as a stock adjustment too
                stockRecords.add(new String[]{
                    String.valueOf(nextStockId++),
                    existing[0], name,
                    String.valueOf(stock),
                    date, time, barcode, sizeVal,
                    String.valueOf(price)
                });
            }
            dlg.dispose();
            loadProducts();
            showToast(existing == null ? "Product added!" : "Product updated!");
        });

        form.add(Box.createVerticalStrut(4));
        form.add(saveBtn);

        JScrollPane formScroll = new JScrollPane(form);
        formScroll.setBorder(BorderFactory.createEmptyBorder());
        formScroll.getViewport().setBackground(WHITE);
        UI.smoothScroll(formScroll);
        dlg.add(formScroll);
        dlg.setVisible(true);
    }

    //Allow only numbers in a text field
    void addNumericFilter(JTextField field, boolean decimals) {
        ((javax.swing.text.AbstractDocument) field.getDocument())
            .setDocumentFilter(new javax.swing.text.DocumentFilter() {
                public void insertString(FilterBypass fb, int off, String str, javax.swing.text.AttributeSet a)
                        throws javax.swing.text.BadLocationException {
                    if (isValid(fb.getDocument().getText(0, fb.getDocument().getLength()) + str)) super.insertString(fb, off, str, a);
                }
                public void replace(FilterBypass fb, int off, int len, String str, javax.swing.text.AttributeSet a)
                        throws javax.swing.text.BadLocationException {
                    String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
                    String result = cur.substring(0, off) + (str == null ? "" : str) + cur.substring(off + len);
                    if (isValid(result)) super.replace(fb, off, len, str, a);
                }
                public void remove(FilterBypass fb, int off, int len)
                        throws javax.swing.text.BadLocationException { super.remove(fb, off, len); }
                boolean isValid(String s) {
                    if (s.isEmpty()) return true;
                    if (decimals) return s.matches("\\d*\\.?\\d*");
                    return s.matches("\\d*");
                }
            });
    }

    //CATEGORIES TAB
    JPanel buildCategoriesTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(14, 0, 0, 0));

        // Hero banner
        JPanel hero = buildHeroBanner(
            "Categories",
            "Organise products into categories for easier navigation"
        );
        panel.add(hero, BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(BG);
        JButton addBtn = UI.primaryBtn("＋  Add Category");
        addBtn.addActionListener(e -> showCategoryDialog(null));
        toolbar.add(addBtn);

        catModel = UI.tableModel(new String[]{"#","Category Name","Description","Products"});
        catTable = UI.styledTable(catModel);
        catTable.getColumnModel().getColumn(0).setMaxWidth(44);
        catTable.getColumnModel().getColumn(3).setMaxWidth(90);

        // Products count — green bold
        catTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                l.setForeground(GREEN); l.setFont(FONT_BOLD);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                return l;
            }
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        actions.setBackground(BG);
        JButton editBtn = UI.outlineGreenBtn("✏  Edit");
        JButton delBtn  = UI.dangerBtn("🗑  Delete");
        editBtn.addActionListener(e -> editSelectedCategory());
        delBtn .addActionListener(e -> deleteSelectedCategory());
        actions.add(editBtn);
        actions.add(delBtn);

        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setBackground(BG);
        center.add(toolbar,                 BorderLayout.NORTH);
        center.add(UI.scrollPane(catTable), BorderLayout.CENTER);
        center.add(actions,                 BorderLayout.SOUTH);

        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    void loadCategories() {
        catModel.setRowCount(0);
        int i = 1;
        for (String[] c : categories) {
            long count = products.stream().filter(p -> p[2].equals(c[0])).count();
            catModel.addRow(new Object[]{ i++, c[1], c.length > 2 ? c[2] : "—", count });
        }
    }

    void editSelectedCategory() {
        int row = catTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(win, "Select a category first."); return; }
        showCategoryDialog(categories.get(row));
    }

    void deleteSelectedCategory() {
        int row = catTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(win, "Select a category first."); return; }
        String name = catModel.getValueAt(row, 1).toString();
        String catId = categories.get(row)[0];
        if (products.stream().anyMatch(p -> p[2].equals(catId))) {
            JOptionPane.showMessageDialog(win, "Cannot delete — this category has products.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(win,
            "Delete category \"" + name + "\"?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        categories.remove(row);
        loadCategories();
        showToast("Category deleted.");
    }

    void showCategoryDialog(String[] existing) {
        JDialog dlg = new JDialog(win, existing == null ? "Add Category" : "Edit Category", true);
        dlg.setSize(380, 230);
        dlg.setLocationRelativeTo(win);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(WHITE);
        form.setBorder(new EmptyBorder(24, 28, 20, 28));

        JLabel dlgTitle = new JLabel(existing == null ? "＋  New Category" : "✏  Edit Category");
        dlgTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        dlgTitle.setForeground(GREEN);
        dlgTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        dlgTitle.setBorder(new EmptyBorder(0, 0, 16, 0));

        JTextField nameF = UI.field("Category name");
        nameF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        nameF.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField descF = UI.field("Short description (optional)");
        descF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        descF.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (existing != null) {
            nameF.setText(existing[1]);
            if (existing.length > 2) descF.setText(existing[2]);
        }

        JButton saveBtn = UI.primaryBtn(existing == null ? "Save" : "Update");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        saveBtn.addActionListener(e -> {
            String name = nameF.getText().trim();
            if (name.isEmpty()) { JOptionPane.showMessageDialog(dlg, "Category name is required."); return; }
            if (existing == null) {
                String[] newCat = new String[]{ String.valueOf(nextCatId++), name, descF.getText().trim() };
                categories.add(newCat);
            } else {
                existing[1] = name;
                if (existing.length > 2) existing[2] = descF.getText().trim();
            }
            dlg.dispose();
            loadCategories();
            showToast(existing == null ? "Category added!" : "Category updated!");
        });

        form.add(dlgTitle);
        form.add(UI.formLabel("Category Name *"));
        form.add(Box.createVerticalStrut(4));
        form.add(nameF);
        form.add(Box.createVerticalStrut(12));
        form.add(UI.formLabel("Description"));
        form.add(Box.createVerticalStrut(4));
        form.add(descF);
        form.add(Box.createVerticalStrut(16));
        form.add(saveBtn);

        dlg.add(form);
        dlg.setVisible(true);
    }

    //HERO BANNER
    JPanel buildHeroBanner(String title, String sub) {
        JPanel hero = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, GREEN_DARK, getWidth(), getHeight(), GREEN);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                // Decorative circles
                g2.setColor(new Color(255, 255, 255, 22));
                g2.fillOval(getWidth() - 110, -40, 160, 160);
                g2.fillOval(getWidth() - 60, getHeight() - 55, 100, 100);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override public boolean isOpaque() { return false; }
        };
        hero.setBorder(new EmptyBorder(20, 22, 20, 22));
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 17));
        titleLbl.setForeground(WHITE);

        JLabel subLbl = new JLabel(sub);
        subLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subLbl.setForeground(new Color(255, 255, 255, 200));

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 3));
        info.setOpaque(false);
        info.add(titleLbl);
        info.add(subLbl);
        hero.add(info, BorderLayout.CENTER);
        return hero;
    }

    //TOAST
    void showToast(String msg) {
        JOptionPane.showMessageDialog(win, msg, "  Done", JOptionPane.INFORMATION_MESSAGE);
    }

    public void onShow() {
        loadProducts();
        loadCategories();
    }
}