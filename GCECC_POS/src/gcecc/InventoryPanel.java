package gcecc;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import static gcecc.AppData.*;

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
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
    }

    void buildUI() {
        add(UI.sectionTitle("📦  Inventory Management"), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_BOLD);
        tabs.addTab("  Products  ",   buildProductsTab());
        tabs.addTab("  Categories  ", buildCategoriesTab());
        add(tabs, BorderLayout.CENTER);
    }

    // PRODUCTS TAB
    JPanel buildProductsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(8, 0));
        toolbar.setBackground(BG);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setBackground(BG);
        prodSearch = UI.field("Search product...");
        prodSearch.setPreferredSize(new Dimension(220, 36));
        prodSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { loadProducts(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { loadProducts(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { loadProducts(); }
        });
        prodCatFilter = new JComboBox<>();
        prodCatFilter.setFont(FONT_REG);
        prodCatFilter.setPreferredSize(new Dimension(155, 36));
        prodCatFilter.addActionListener(e -> loadProducts());
        left.add(new JLabel("🔍"));
        left.add(prodSearch);
        left.add(prodCatFilter);

        JButton addBtn = UI.primaryBtn("+ Add Product");
        addBtn.addActionListener(e -> showProductDialog(null));

        toolbar.add(left,   BorderLayout.WEST);
        toolbar.add(addBtn, BorderLayout.EAST);

        // Table
        prodModel = UI.tableModel(new String[]{"#","Name","Category","Price","Stock","Barcode","Status"});
        prodTable = UI.styledTable(prodModel);
        prodTable.getColumnModel().getColumn(0).setMaxWidth(40);
        prodTable.getColumnModel().getColumn(3).setMaxWidth(90);
        prodTable.getColumnModel().getColumn(4).setMaxWidth(75);
        prodTable.getColumnModel().getColumn(6).setMaxWidth(105);

        // Color status column
        prodTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t,v,s,f,r,c);
                lbl.setOpaque(true);
                String sv = v != null ? v.toString() : "";
                switch (sv) {
                    case "Out of Stock" -> { lbl.setBackground(RED_LIGHT);   lbl.setForeground(RED); }
                    case "Low Stock"    -> { lbl.setBackground(GOLD_LIGHT);  lbl.setForeground(GOLD); }
                    default             -> { lbl.setBackground(GREEN_LIGHT); lbl.setForeground(GREEN_DARK); }
                }
                lbl.setFont(FONT_BOLD);
                return lbl;
            }
        });

        // Action buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actions.setBackground(BG);
        JButton editBtn = UI.primaryBtn("✏  Edit");
        JButton delBtn  = UI.dangerBtn("🗑  Delete");
        editBtn.addActionListener(e -> editSelectedProduct());
        delBtn .addActionListener(e -> deleteSelectedProduct());
        actions.add(editBtn); actions.add(delBtn);

        panel.add(toolbar,            BorderLayout.NORTH);
        panel.add(UI.scrollPane(prodTable), BorderLayout.CENTER);
        panel.add(actions,            BorderLayout.SOUTH);
        return panel;
    }

    void loadProducts() {
        // Refresh category filter
        Object prevSel = prodCatFilter.getSelectedItem();
        prodCatFilter.removeAllItems();
        prodCatFilter.addItem("0|All Categories");
        for (String[] c : categories) prodCatFilter.addItem(c[0] + "|" + c[1]);
        if (prevSel != null) prodCatFilter.setSelectedItem(prevSel);

        String search = prodSearch.getText().trim().toLowerCase();
        String selCat = prodCatFilter.getSelectedItem() != null ? prodCatFilter.getSelectedItem().toString() : "";
        String catId  = selCat.contains("|") ? selCat.split("\\|")[0] : "0";

        prodModel.setRowCount(0);
        int i = 1;
        for (String[] p : products) {
            if (!catId.equals("0") && !p[2].equals(catId)) continue;
            if (!search.isEmpty() && !p[1].toLowerCase().contains(search)
                && !p[6].contains(search)) continue;
            String status = isOutOfStock(p) ? "Out of Stock"
                          : isLowStock(p)   ? "Low Stock"
                          :                   "In Stock";
            prodModel.addRow(new Object[]{
                i++, p[1], getCategoryName(p[2]),
                String.format("₱ %.2f", getPrice(p)),
                p[4] + " " + p[7], p[6], status
            });
        }
    }

    void editSelectedProduct() {
        int row = prodTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(win, "Select a product first."); return; }
        String barcode = prodModel.getValueAt(row, 5).toString();
        String[] p = products.stream().filter(x -> x[6].equals(barcode)).findFirst().orElse(null);
        showProductDialog(p);
    }

    void deleteSelectedProduct() {
        int row = prodTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(win, "Select a product first."); return; }
        int confirm = JOptionPane.showConfirmDialog(win,
            "Delete \"" + prodModel.getValueAt(row, 1) + "\"?",
            "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        String barcode = prodModel.getValueAt(row, 5).toString();
        products.removeIf(p -> p[6].equals(barcode));
        loadProducts();
    }

    void showProductDialog(String[] existing) {
        JDialog dlg = new JDialog(win, existing == null ? "Add Product" : "Edit Product", true);
        dlg.setSize(400, 500);
        dlg.setLocationRelativeTo(win);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(WHITE);
        form.setBorder(new EmptyBorder(20, 24, 20, 24));

        JTextField nameF     = UI.field("Product name");
        JTextField priceF    = UI.field("e.g. 650.00");
        JTextField stockF    = UI.field("Current stock");
        JTextField lowStockF = UI.field("Alert when stock reaches (default 5)");
        JTextField barcodeF  = UI.field("Leave blank to auto-generate");

        JComboBox<String> catBox  = new JComboBox<>();
        JComboBox<String> unitBox = new JComboBox<>(new String[]{"pcs","set","kg","g","L","mL","pack","box"});

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
            for (int i = 0; i < catBox.getItemCount(); i++) {
                if (catBox.getItemAt(i).startsWith(existing[2] + "|")) { catBox.setSelectedIndex(i); break; }
            }
        }

        String[][] fields = {
            {"Product Name *", null}, {"Category *", null},
            {"Price (₱) *", null},   {"Stock *", null},
            {"Low Stock Alert", null},{"Barcode", null},
            {"Unit", null}
        };
        JComponent[] comps = {nameF, catBox, priceF, stockF, lowStockF, barcodeF, unitBox};
        for (int i = 0; i < comps.length; i++) {
            JLabel lbl = UI.formLabel(fields[i][0]);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            comps[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            comps[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            if (comps[i] instanceof JTextField) {
                ((JTextField) comps[i]).setBorder(new CompoundBorder(
                    new LineBorder(BORDER, 1, true), new EmptyBorder(7, 10, 7, 10)));
            }
            form.add(lbl);
            form.add(Box.createVerticalStrut(3));
            form.add(comps[i]);
            form.add(Box.createVerticalStrut(10));
        }

        JButton saveBtn = UI.primaryBtn(existing == null ? "Save Product" : "Update Product");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            try {
                String name    = nameF.getText().trim();
                String catSel  = catBox.getSelectedItem().toString();
                String catId   = catSel.split("\\|")[0];
                double price   = Double.parseDouble(priceF.getText().trim());
                int    stock   = Integer.parseInt(stockF.getText().trim());
                int    low     = lowStockF.getText().isEmpty() ? 5 : Integer.parseInt(lowStockF.getText().trim());
                String barcode = barcodeF.getText().trim().isEmpty()
                    ? "8934" + (System.currentTimeMillis() % 1000000) : barcodeF.getText().trim();
                String unit    = unitBox.getSelectedItem().toString();

                if (name.isEmpty() || catId.equals("0")) {
                    JOptionPane.showMessageDialog(dlg, "Fill all required fields (*)."); return;
                }

                if (existing == null) {
                    products.add(new String[]{
                        String.valueOf(nextProdId++), name, catId,
                        String.valueOf(price), String.valueOf(stock),
                        String.valueOf(low), barcode, unit
                    });
                } else {
                    existing[1] = name; existing[2] = catId;
                    existing[3] = String.valueOf(price); existing[4] = String.valueOf(stock);
                    existing[5] = String.valueOf(low);   existing[6] = barcode;
                    existing[7] = unit;
                }
                dlg.dispose();
                loadProducts();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "Invalid number format.");
            }
        });

        form.add(Box.createVerticalStrut(4));
        form.add(saveBtn);

        dlg.add(new JScrollPane(form));
        dlg.setVisible(true);
    }

    // CATEGORIES TAB
    JPanel buildCategoriesTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        JButton addBtn = UI.primaryBtn("+ Add Category");
        addBtn.addActionListener(e -> showCategoryDialog(null));
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(BG);
        toolbar.add(addBtn);

        catModel = UI.tableModel(new String[]{"#","Category Name","Products"});
        catTable = UI.styledTable(catModel);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actions.setBackground(BG);
        JButton editBtn = UI.primaryBtn("✏  Edit");
        JButton delBtn  = UI.dangerBtn("🗑  Delete");
        editBtn.addActionListener(e -> editSelectedCategory());
        delBtn .addActionListener(e -> deleteSelectedCategory());
        actions.add(editBtn); actions.add(delBtn);

        panel.add(toolbar,                BorderLayout.NORTH);
        panel.add(UI.scrollPane(catTable),BorderLayout.CENTER);
        panel.add(actions,                BorderLayout.SOUTH);
        return panel;
    }

    void loadCategories() {
        catModel.setRowCount(0);
        int i = 1;
        for (String[] c : categories) {
            long count = products.stream().filter(p -> p[2].equals(c[0])).count();
            catModel.addRow(new Object[]{ i++, c[1], count });
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
        int confirm = JOptionPane.showConfirmDialog(win,
            "Delete this category?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        categories.remove(row);
        loadCategories();
    }

    void showCategoryDialog(String[] existing) {
        JDialog dlg = new JDialog(win, existing == null ? "Add Category" : "Edit Category", true);
        dlg.setSize(350, 180);
        dlg.setLocationRelativeTo(win);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(WHITE);
        form.setBorder(new EmptyBorder(20, 24, 16, 24));

        JTextField nameF = UI.field("Category name");
        nameF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        if (existing != null) nameF.setText(existing[1]);

        form.add(UI.formLabel("Category Name *"));
        form.add(Box.createVerticalStrut(4));
        form.add(nameF);
        form.add(Box.createVerticalStrut(14));

        JButton saveBtn = UI.primaryBtn(existing == null ? "Save" : "Update");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            String name = nameF.getText().trim();
            if (name.isEmpty()) { JOptionPane.showMessageDialog(dlg, "Name required."); return; }
            if (existing == null) {
                categories.add(new String[]{ String.valueOf(nextCatId++), name });
            } else {
                existing[1] = name;
            }
            dlg.dispose();
            loadCategories();
        });

        form.add(saveBtn);
        dlg.add(form);
        dlg.setVisible(true);
    }

    public void onShow() {
        loadProducts();
        loadCategories();
    }
}
