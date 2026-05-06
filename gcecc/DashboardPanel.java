package gcecc;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static gcecc.AppData.*;

//DashboardPanel.java — Dashboard with stat cards, period filter, tabs.

public class DashboardPanel extends JPanel {

    MainWindow win;
    JLabel statProducts, statCategories, statLow, statTx;
    JComboBox<String> periodCombo;
    JSpinner datePicker, weekYearPicker, monthYearPicker;
    SpinnerDateModel dateModel;
    SpinnerNumberModel weekYearModel, monthYearModel, yearModel;
    JSpinner yearPicker;
    DefaultTableModel soldModel, addedModel, lowModel;
    JTable soldTable, addedTable, lowTable;

    public DashboardPanel(MainWindow win) {
        this.win = win;
        setLayout(new BorderLayout(0, 14));
        setBackground(BG);
        setBorder(new EmptyBorder(22, 22, 22, 22));
        buildUI();
    }

    void buildUI() {
        // Page title + subtitle
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(BG);
        titleRow.setBorder(new EmptyBorder(0, 0, 8, 0));

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(TEXT);

        String dateStr = new SimpleDateFormat("EEEE, MMMM d, yyyy").format(new Date());
        JLabel dateLbl = new JLabel(dateStr);
        dateLbl.setFont(FONT_SMALL);
        dateLbl.setForeground(TEXT_MUTED);

        titleRow.add(title,   BorderLayout.NORTH);
        titleRow.add(dateLbl, BorderLayout.SOUTH);
        add(titleRow, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setBackground(BG);

        // ── Stat cards (4): Products, Categories, Low/Out of Stock, Transactions ─
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 14, 0));
        statsRow.setBackground(BG);
        statsRow.add(UI.statCard("Total Products",      "0", GREEN,  "sp"));
        statsRow.add(UI.statCard("Categories",          "0", BLUE,   "sc"));
        statsRow.add(UI.statCard("Low / Out of Stock",  "0", RED,    "sl"));
        statsRow.add(UI.statCard("Transactions Today",  "0", GOLD,   "st"));
        statProducts   = UI.findLabel(statsRow, "sp");
        statCategories = UI.findLabel(statsRow, "sc");
        statLow        = UI.findLabel(statsRow, "sl");
        statTx         = UI.findLabel(statsRow, "st");
        center.add(statsRow, BorderLayout.NORTH);

        //Period filter bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterBar.setBackground(WHITE);
        filterBar.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(2, 10, 2, 10)
        ));

        JLabel viewLbl = new JLabel("View by:");
        viewLbl.setFont(FONT_BOLD);
        viewLbl.setForeground(TEXT_MUTED);

        periodCombo = new JComboBox<>(new String[]{"Day", "Week", "Month", "Year"});
        periodCombo.setFont(FONT_REG);
        periodCombo.setPreferredSize(new Dimension(110, 34));
        periodCombo.addActionListener(e -> updatePeriodPickers());

        // Day picker
        dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        datePicker = new JSpinner(dateModel);
        datePicker.setEditor(new JSpinner.DateEditor(datePicker, "MM/dd/yyyy"));
        datePicker.setPreferredSize(new Dimension(130, 34));
        datePicker.addChangeListener(e -> refresh());

        JButton todayBtn = UI.primaryBtn("Today");
        todayBtn.setFont(FONT_SMALL);
        todayBtn.setBorder(new EmptyBorder(6, 14, 6, 14));
        todayBtn.addActionListener(e -> { dateModel.setValue(new Date()); refresh(); });

        // Week picker — week number spinner + year spinner
        JSpinner weekSpinner = new JSpinner(new SpinnerNumberModel(
            Calendar.getInstance().get(Calendar.WEEK_OF_YEAR), 1, 53, 1));
        weekSpinner.setPreferredSize(new Dimension(70, 34));
        weekSpinner.putClientProperty("role", "week");

        weekYearModel = new SpinnerNumberModel(Calendar.getInstance().get(Calendar.YEAR), 2000, 2100, 1);
        weekYearPicker = new JSpinner(weekYearModel);
        weekYearPicker.setPreferredSize(new Dimension(80, 34));
        weekYearPicker.putClientProperty("role", "weekYear");

        JLabel weekLbl = new JLabel("Wk:");
        weekLbl.setFont(FONT_BOLD); weekLbl.setForeground(TEXT_MUTED);
        JLabel yrLbl1 = new JLabel("Year:");
        yrLbl1.setFont(FONT_BOLD); yrLbl1.setForeground(TEXT_MUTED);

        weekSpinner.addChangeListener(e -> refresh());
        weekYearPicker.addChangeListener(e -> refresh());
        putClientProperty("weekSpinner", weekSpinner);

        // Month picker — month combo + year spinner
        String[] months = {"January","February","March","April","May","June",
                           "July","August","September","October","November","December"};
        JComboBox<String> monthCombo = new JComboBox<>(months);
        monthCombo.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH));
        monthCombo.setPreferredSize(new Dimension(115, 34));
        monthCombo.setFont(FONT_REG);

        monthYearModel = new SpinnerNumberModel(Calendar.getInstance().get(Calendar.YEAR), 2000, 2100, 1);
        monthYearPicker = new JSpinner(monthYearModel);
        monthYearPicker.setPreferredSize(new Dimension(80, 34));

        JLabel yrLbl2 = new JLabel("Year:");
        yrLbl2.setFont(FONT_BOLD); yrLbl2.setForeground(TEXT_MUTED);

        monthCombo.addActionListener(e -> refresh());
        monthYearPicker.addChangeListener(e -> refresh());
        putClientProperty("monthCombo", monthCombo);

        // Year picker
        yearModel = new SpinnerNumberModel(Calendar.getInstance().get(Calendar.YEAR), 2000, 2100, 1);
        yearPicker = new JSpinner(yearModel);
        yearPicker.setPreferredSize(new Dimension(90, 34));
        yearPicker.addChangeListener(e -> refresh());

        // Assemble filter bar — store picker panels for show/hide
        JPanel dayPanel   = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0)); dayPanel.setOpaque(false);
        JPanel weekPanel  = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0)); weekPanel.setOpaque(false);
        JPanel monthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0)); monthPanel.setOpaque(false);
        JPanel yearPanel  = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0)); yearPanel.setOpaque(false);

        dayPanel  .add(datePicker); dayPanel.add(todayBtn);
        weekPanel .add(weekLbl); weekPanel.add(weekSpinner); weekPanel.add(yrLbl1); weekPanel.add(weekYearPicker);
        monthPanel.add(monthCombo); monthPanel.add(yrLbl2); monthPanel.add(monthYearPicker);
        yearPanel .add(yearPicker);

        weekPanel .setVisible(false);
        monthPanel.setVisible(false);
        yearPanel .setVisible(false);

        putClientProperty("dayPanel",   dayPanel);
        putClientProperty("weekPanel",  weekPanel);
        putClientProperty("monthPanel", monthPanel);
        putClientProperty("yearPanel",  yearPanel);

        filterBar.add(viewLbl);
        filterBar.add(periodCombo);
        filterBar.add(dayPanel);
        filterBar.add(weekPanel);
        filterBar.add(monthPanel);
        filterBar.add(yearPanel);

        //Tabs: Items Sold, Items Added, Low Stock
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_BOLD);
        tabs.setBackground(WHITE);
        tabs.addTab("  Items Sold  ",  buildSoldTab());
        tabs.addTab("  Items Added  ", buildAddedTab());
        tabs.addTab("  Low Stock   ",  buildLowStockTab());

        JPanel mainArea = new JPanel(new BorderLayout(0, 10));
        mainArea.setBackground(BG);
        mainArea.add(filterBar, BorderLayout.NORTH);
        mainArea.add(tabs,      BorderLayout.CENTER);

        center.add(mainArea, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    //ITEMS SOLD TAB
    JPanel buildSoldTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(WHITE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        soldModel = UI.tableModel(new String[]{"OR #","Product","Size","Qty","Unit Price","Total","Date","Time"});
        soldTable = UI.styledTable(soldModel);
        soldTable.getColumnModel().getColumn(0).setMaxWidth(90);
        soldTable.getColumnModel().getColumn(2).setMaxWidth(70);
        soldTable.getColumnModel().getColumn(3).setMaxWidth(55);
        soldTable.getColumnModel().getColumn(4).setMaxWidth(100);
        soldTable.getColumnModel().getColumn(5).setMaxWidth(110);
        soldTable.getColumnModel().getColumn(6).setMaxWidth(100);
        soldTable.getColumnModel().getColumn(7).setMaxWidth(85);

        // Total column — green bold
        soldTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                l.setForeground(GREEN); l.setFont(FONT_BOLD); return l;
            }
        });

        p.add(UI.scrollPane(soldTable), BorderLayout.CENTER);
        return p;
    }

    //ITEMS ADDED TAB
    JPanel buildAddedTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(WHITE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        addedModel = UI.tableModel(new String[]{"OR #","Product","Size","Qty","Unit Price","Total","Date","Time"});
        addedTable = UI.styledTable(addedModel);
        addedTable.getColumnModel().getColumn(0).setMaxWidth(90);
        addedTable.getColumnModel().getColumn(2).setMaxWidth(70);
        addedTable.getColumnModel().getColumn(3).setMaxWidth(55);
        addedTable.getColumnModel().getColumn(4).setMaxWidth(100);
        addedTable.getColumnModel().getColumn(5).setMaxWidth(110);
        addedTable.getColumnModel().getColumn(6).setMaxWidth(100);
        addedTable.getColumnModel().getColumn(7).setMaxWidth(85);

        // Total column — green bold
        addedTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                l.setForeground(GREEN); l.setFont(FONT_BOLD); return l;
            }
        });

        p.add(UI.scrollPane(addedTable), BorderLayout.CENTER);
        return p;
    }

    //LOW STOCK TAB
    JPanel buildLowStockTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(WHITE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        lowModel = UI.tableModel(new String[]{"Product","Category","Stock","Status"});
        lowTable = UI.styledTable(lowModel);
        lowTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t,v,s,f,r,c);
                l.setOpaque(true);
                String sv = v != null ? v.toString() : "";
                if ("Out of Stock".equals(sv)) { l.setBackground(RED_LIGHT);  l.setForeground(RED); }
                else { l.setBackground(GOLD_LIGHT); l.setForeground(GOLD); }
                l.setFont(FONT_BOLD);
                return l;
            }
        });

        p.add(UI.scrollPane(lowTable), BorderLayout.CENTER);
        return p;
    }

    //MAIN REFRESH
    public void refresh() {
        if (periodCombo == null) return;
        String period = periodCombo.getSelectedItem().toString();
        String today  = new SimpleDateFormat("MM/dd/yyyy").format(new Date());

        // Stat cards
        if (statProducts   != null) statProducts  .setText(String.valueOf(products.size()));
        if (statCategories != null) statCategories.setText(String.valueOf(categories.size()));
        long lowOrOut = products.stream().filter(p -> isLowStock(p) || isOutOfStock(p)).count();
        if (statLow != null) statLow.setText(String.valueOf(lowOrOut));
        long todayTxCount = transactions.stream().filter(t -> t[3].equals(today)).count();
        if (statTx  != null) statTx .setText(String.valueOf(todayTxCount));

        // Items Sold tab — one row per item per transaction
        if (soldModel != null) {
            soldModel.setRowCount(0);
            List<String[]> filteredTx = filterByPeriod(transactions, period);
            for (String[] tx : filteredTx) {
                // tx: [id, itemCount, total, date, time, itemsSummary, itemDetails(JSON-like)]
                // itemDetails stored as repeated entries in stockRecords-style or as structured list
                // We stored each cart item separately in itemDetails (index 6+)
                // Format from POSPanel: tx[6] = "id|name|size|price|qty" per item, separated by ";;"
                if (tx.length > 6 && !tx[6].isEmpty()) {
                    String orNum = "#" + String.format("%06d", Integer.parseInt(tx[0]));
                    for (String itemStr : tx[6].split(";;")) {
                        String[] parts = itemStr.split("\\|", -1);
                        if (parts.length >= 5) {
                            String name  = parts[1];
                            String size  = parts[2].isEmpty() ? "—" : parts[2];
                            int    qty   = Integer.parseInt(parts[4]);
                            double price = Double.parseDouble(parts[3]);
                            soldModel.addRow(new Object[]{
                                orNum, name, size, qty,
                                String.format("₱ %.2f", price),
                                String.format("₱ %.2f", price * qty),
                                tx[3], tx[4]
                            });
                        }
                    }
                }
            }
        }

        // Items Added tab — from stockRecords
        if (addedModel != null) {
            addedModel.setRowCount(0);
            List<String[]> filteredAdded = filterStockByPeriod(period);
            for (String[] r : filteredAdded) {
                // stockRecord: [id, productId, productName, qtyAdded, date, time, barcode, size, price]
                String size  = r.length > 7 ? (r[7].isEmpty() ? "—" : r[7]) : "—";
                double price = r.length > 8 ? Double.parseDouble(r[8]) : 0.0;
                int    qty   = Integer.parseInt(r[3]);
                String orNum = "#" + String.format("%04d", Integer.parseInt(r[0]));
                addedModel.addRow(new Object[]{
                    orNum, r[2], size, qty,
                    price > 0 ? String.format("₱ %.2f", price) : "—",
                    price > 0 ? String.format("₱ %.2f", price * qty) : "—",
                    r[4], r[5]
                });
            }
        }

        // Low Stock tab
        if (lowModel != null) {
            lowModel.setRowCount(0);
            products.stream().filter(p -> isLowStock(p) || isOutOfStock(p)).forEach(p ->
                lowModel.addRow(new Object[]{
                    p[1], getCategoryName(p[2]), p[4] + " " + p[7],
                    isOutOfStock(p) ? "Out of Stock" : "Low Stock"
                })
            );
        }
    }

    //PERIOD FILTER
    List<String[]> filterByPeriod(List<String[]> list, String period) {
        Calendar now = Calendar.getInstance();
        return list.stream().filter(t -> {
            try {
                Date d = new SimpleDateFormat("MM/dd/yyyy").parse(t[3]);
                Calendar tc = Calendar.getInstance(); tc.setTime(d);
                return matchesPeriod(tc, period, now);
            } catch (Exception e) { return false; }
        }).collect(Collectors.toList());
    }

    List<String[]> filterStockByPeriod(String period) {
        Calendar now = Calendar.getInstance();
        return stockRecords.stream().filter(r -> {
            try {
                Date d = new SimpleDateFormat("MM/dd/yyyy").parse(r[4]);
                Calendar tc = Calendar.getInstance(); tc.setTime(d);
                return matchesPeriod(tc, period, now);
            } catch (Exception e) { return false; }
        }).collect(Collectors.toList());
    }

    boolean matchesPeriod(Calendar tc, String period, Calendar now) {
        return switch (period) {
            case "Day" -> {
                if (dateModel == null) yield sameDay(now, tc);
                Calendar sel = Calendar.getInstance(); sel.setTime(dateModel.getDate());
                yield sameDay(sel, tc);
            }
            case "Week" -> {
                JSpinner ws = (JSpinner) getClientProperty("weekSpinner");
                int wk  = ws  != null ? (int) ws.getValue()         : now.get(Calendar.WEEK_OF_YEAR);
                int yr  = weekYearModel != null ? (int) weekYearModel.getValue() : now.get(Calendar.YEAR);
                yield tc.get(Calendar.YEAR) == yr && tc.get(Calendar.WEEK_OF_YEAR) == wk;
            }
            case "Month" -> {
                JComboBox<?> mc = (JComboBox<?>) getClientProperty("monthCombo");
                int mo = mc != null ? mc.getSelectedIndex() : now.get(Calendar.MONTH);
                int yr = monthYearModel != null ? (int) monthYearModel.getValue() : now.get(Calendar.YEAR);
                yield tc.get(Calendar.YEAR) == yr && tc.get(Calendar.MONTH) == mo;
            }
            case "Year" -> {
                int yr = yearModel != null ? (int) yearModel.getValue() : now.get(Calendar.YEAR);
                yield tc.get(Calendar.YEAR) == yr;
            }
            default -> sameDay(now, tc);
        };
    }

    void updatePeriodPickers() {
        String period = periodCombo.getSelectedItem().toString();
        JPanel dayP   = (JPanel) getClientProperty("dayPanel");
        JPanel weekP  = (JPanel) getClientProperty("weekPanel");
        JPanel monthP = (JPanel) getClientProperty("monthPanel");
        JPanel yearP  = (JPanel) getClientProperty("yearPanel");
        if (dayP   != null) dayP  .setVisible("Day"  .equals(period));
        if (weekP  != null) weekP .setVisible("Week" .equals(period));
        if (monthP != null) monthP.setVisible("Month".equals(period));
        if (yearP  != null) yearP .setVisible("Year" .equals(period));
        refresh();
    }

    boolean sameDay(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
            && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }
}