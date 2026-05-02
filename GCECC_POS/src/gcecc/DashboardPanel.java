package gcecc;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static gcecc.AppData.*;

//Dashboard with calendar, period summary, items sold detail
 
public class DashboardPanel extends JPanel {

    MainWindow win;
    JLabel statProducts, statLow, statTx, statRevenue;
    JComboBox<String> periodCombo;
    JSpinner datePicker;
    SpinnerDateModel dateModel;
    DefaultTableModel txModel, summaryModel, lowModel;
    JTable txTable, summaryTable, lowTable;

    public DashboardPanel(MainWindow win) {
        this.win = win;
        setLayout(new BorderLayout(0, 14));
        setBackground(BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
    }

    void buildUI() {
        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(TEXT);
        title.setBorder(new EmptyBorder(0, 0, 4, 0));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setBackground(BG);

        // Stat cards
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 12, 0));
        statsRow.setBackground(BG);
        statsRow.add(UI.statCard("Total Products",     "0",   GREEN,               "sp"));
        statsRow.add(UI.statCard("Low / Out of Stock", "0",   RED,                 "sl"));
        statsRow.add(UI.statCard("Transactions Today", "0",   new Color(0,172,238),"st"));
        statsRow.add(UI.statCard("Revenue Today",      "0.00",new Color(16,185,129),"sr"));
        statProducts = UI.findLabel(statsRow, "sp");
        statLow      = UI.findLabel(statsRow, "sl");
        statTx       = UI.findLabel(statsRow, "st");
        statRevenue  = UI.findLabel(statsRow, "sr");
        center.add(statsRow, BorderLayout.NORTH);

        // Filter bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        filterBar.setBackground(WHITE);
        filterBar.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true), new EmptyBorder(4, 12, 4, 12)));

        dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        datePicker = new JSpinner(dateModel);
        datePicker.setEditor(new JSpinner.DateEditor(datePicker, "MM/dd/yyyy"));
        datePicker.setPreferredSize(new Dimension(130, 32));
        datePicker.addChangeListener(e -> refresh());

        JButton todayBtn = UI.primaryBtn("Today");
        todayBtn.setFont(FONT_SMALL);
        todayBtn.setBorder(new EmptyBorder(5, 12, 5, 12));
        todayBtn.addActionListener(e -> { dateModel.setValue(new Date()); refresh(); });

        periodCombo = new JComboBox<>(new String[]{"Day","Week","Month","Year"});
        periodCombo.setFont(FONT_REG);
        periodCombo.setPreferredSize(new Dimension(100, 32));
        periodCombo.addActionListener(e -> refresh());

        filterBar.add(new JLabel("  Date:"));
        filterBar.add(datePicker);
        filterBar.add(todayBtn);
        filterBar.add(new JLabel("    View by:"));
        filterBar.add(periodCombo);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_BOLD);
        tabs.addTab("  Transactions  ", buildTxTab());
        tabs.addTab("  Summary       ", buildSummaryTab());
        tabs.addTab("  Low Stock     ", buildLowStockTab());

        JPanel mainArea = new JPanel(new BorderLayout(0, 10));
        mainArea.setBackground(BG);
        mainArea.add(filterBar, BorderLayout.NORTH);
        mainArea.add(tabs,      BorderLayout.CENTER);

        center.add(mainArea, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    JPanel buildTxTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(WHITE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        txModel = UI.tableModel(new String[]{"OR #","Count","Total","Date & Time","Items Sold"});
        txTable = UI.styledTable(txModel);
        txTable.setRowHeight(40);
        txTable.getColumnModel().getColumn(0).setMaxWidth(80);
        txTable.getColumnModel().getColumn(1).setMaxWidth(60);
        txTable.getColumnModel().getColumn(2).setMaxWidth(90);
        txTable.getColumnModel().getColumn(3).setMaxWidth(130);

        // Green total
        txTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t,v,s,f,r,c);
                l.setForeground(GREEN); l.setFont(FONT_BOLD); return l;
            }
        });
        // Items sold — smaller text, wrap
        txTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t,v,s,f,r,c);
                l.setFont(FONT_SMALL); l.setForeground(TEXT_MUTED); return l;
            }
        });

        p.add(UI.scrollPane(txTable), BorderLayout.CENTER);
        return p;
    }

    JPanel buildSummaryTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(WHITE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        summaryModel = UI.tableModel(new String[]{"Period","Transactions","Items Sold","Revenue"});
        summaryTable = UI.styledTable(summaryModel);
        summaryTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t,v,s,f,r,c);
                l.setForeground(GREEN); l.setFont(FONT_BOLD); return l;
            }
        });

        p.add(UI.scrollPane(summaryTable), BorderLayout.CENTER);
        return p;
    }

    JPanel buildLowStockTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(WHITE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        lowModel = UI.tableModel(new String[]{"Product","Category","Stock","Status"});
        lowTable = UI.styledTable(lowModel);
        lowTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t,v,s,f,r,c);
                l.setOpaque(true);
                String sv = v != null ? v.toString() : "";
                if ("Out of Stock".equals(sv)) { l.setBackground(RED_LIGHT); l.setForeground(RED); }
                else { l.setBackground(GOLD_LIGHT); l.setForeground(GOLD); }
                l.setFont(FONT_BOLD); return l;
            }
        });

        p.add(UI.scrollPane(lowTable), BorderLayout.CENTER);
        return p;
    }

    // ── MAIN REFRESH ──────────────────────────────────────────
    public void refresh() {
        if (dateModel == null) return;
        Date selected = dateModel.getDate();
        String period = periodCombo != null ? periodCombo.getSelectedItem().toString() : "Day";
        String today  = new SimpleDateFormat("MM/dd/yyyy").format(new Date());

        // Stat cards (always today)
        long totalProds = products.size();
        long lowOrOut   = products.stream().filter(p -> isLowStock(p) || isOutOfStock(p)).count();
        List<String[]> todayTx = transactions.stream()
            .filter(t -> t[3].equals(today)).collect(Collectors.toList());
        double revToday = todayTx.stream().mapToDouble(t -> Double.parseDouble(t[2])).sum();

        if (statProducts != null) statProducts.setText(String.valueOf(totalProds));
        if (statLow      != null) statLow     .setText(String.valueOf(lowOrOut));
        if (statTx       != null) statTx      .setText(String.valueOf(todayTx.size()));
        if (statRevenue  != null) statRevenue .setText(String.format("%.2f", revToday));

        // Transactions tab
        txModel.setRowCount(0);
        List<String[]> filtered = filterByPeriod(transactions, selected, period);
        filtered.forEach(t -> txModel.addRow(new Object[]{
            "#" + String.format("%06d", Integer.parseInt(t[0])),
            t[1] + " item(s)",
            "P " + t[2],
            t[3] + "  " + t[4],
            t.length > 5 && !t[5].isEmpty() ? t[5] : "—"
        }));

        // Summary tab
        summaryModel.setRowCount(0);
        buildSummary(selected, period);

        // Low stock tab
        lowModel.setRowCount(0);
        products.stream().filter(p -> isLowStock(p) || isOutOfStock(p)).forEach(p ->
            lowModel.addRow(new Object[]{
                p[1], getCategoryName(p[2]), p[4] + " " + p[7],
                isOutOfStock(p) ? "Out of Stock" : "Low Stock"
            })
        );
    }

    // ── FILTER BY PERIOD ─────────────────────────────────────
    List<String[]> filterByPeriod(List<String[]> list, Date sel, String period) {
        Calendar sc = Calendar.getInstance(); sc.setTime(sel);
        return list.stream().filter(t -> {
            try {
                Date d = new SimpleDateFormat("MM/dd/yyyy").parse(t[3]);
                Calendar tc = Calendar.getInstance(); tc.setTime(d);
                return switch (period) {
                    case "Day"   -> sameDay(sc,tc);
                    case "Week"  -> sameWeek(sc,tc);
                    case "Month" -> sameMonth(sc,tc);
                    case "Year"  -> sameYear(sc,tc);
                    default      -> sameDay(sc,tc);
                };
            } catch (Exception e) { return false; }
        }).collect(Collectors.toList());
    }

    boolean sameDay  (Calendar a, Calendar b) { return a.get(Calendar.YEAR)==b.get(Calendar.YEAR) && a.get(Calendar.DAY_OF_YEAR)==b.get(Calendar.DAY_OF_YEAR); }
    boolean sameWeek (Calendar a, Calendar b) { return a.get(Calendar.YEAR)==b.get(Calendar.YEAR) && a.get(Calendar.WEEK_OF_YEAR)==b.get(Calendar.WEEK_OF_YEAR); }
    boolean sameMonth(Calendar a, Calendar b) { return a.get(Calendar.YEAR)==b.get(Calendar.YEAR) && a.get(Calendar.MONTH)==b.get(Calendar.MONTH); }
    boolean sameYear (Calendar a, Calendar b) { return a.get(Calendar.YEAR)==b.get(Calendar.YEAR); }

    // ── BUILD SUMMARY ─────────────────────────────────────────
    void buildSummary(Date selected, String period) {
        Calendar sel = Calendar.getInstance(); sel.setTime(selected);

        switch (period) {
            case "Day" -> {
                summaryModel.setColumnIdentifiers(new Object[]{"Time","Items","Revenue","Items Sold"});
                List<String[]> dayTx = filterByPeriod(transactions, selected, "Day");
                dayTx.forEach(t -> summaryModel.addRow(new Object[]{
                    t[4], t[1]+" item(s)", "P "+t[2],
                    t.length>5 && !t[5].isEmpty() ? t[5] : "—"
                }));
                double total = dayTx.stream().mapToDouble(t->Double.parseDouble(t[2])).sum();
                summaryModel.addRow(new Object[]{"TOTAL", dayTx.size()+" tx", String.format("P %.2f",total),""});
            }
            case "Week" -> {
                summaryModel.setColumnIdentifiers(new Object[]{"Day","Transactions","Items Sold","Revenue"});
                Calendar start = (Calendar) sel.clone();
                start.set(Calendar.DAY_OF_WEEK, start.getFirstDayOfWeek());
                double wTot=0; int wTx=0; int wIt=0;
                for (int d=0; d<7; d++) {
                    Date day = start.getTime();
                    List<String[]> dt = filterByPeriod(transactions, day, "Day");
                    double rev = dt.stream().mapToDouble(t->Double.parseDouble(t[2])).sum();
                    int it  = dt.stream().mapToInt(t->Integer.parseInt(t[1])).sum();
                    summaryModel.addRow(new Object[]{
                        new SimpleDateFormat("EEE MM/dd").format(day),
                        dt.size()+" tx", it+" items", String.format("P %.2f",rev)
                    });
                    wTot+=rev; wTx+=dt.size(); wIt+=it;
                    start.add(Calendar.DAY_OF_MONTH,1);
                }
                summaryModel.addRow(new Object[]{"WEEK TOTAL",wTx+" tx",wIt+" items",String.format("P %.2f",wTot)});
            }
            case "Month" -> {
                summaryModel.setColumnIdentifiers(new Object[]{"Date","Transactions","Items Sold","Revenue"});
                Calendar dc = (Calendar) sel.clone(); dc.set(Calendar.DAY_OF_MONTH,1);
                int days = sel.getActualMaximum(Calendar.DAY_OF_MONTH);
                double mTot=0; int mTx=0; int mIt=0;
                for (int d=0; d<days; d++) {
                    Date day = dc.getTime();
                    List<String[]> dt = filterByPeriod(transactions, day, "Day");
                    if (!dt.isEmpty()) {
                        double rev = dt.stream().mapToDouble(t->Double.parseDouble(t[2])).sum();
                        int it  = dt.stream().mapToInt(t->Integer.parseInt(t[1])).sum();
                        summaryModel.addRow(new Object[]{
                            new SimpleDateFormat("MM/dd (EEE)").format(day),
                            dt.size()+" tx", it+" items", String.format("P %.2f",rev)
                        });
                        mTot+=rev; mTx+=dt.size(); mIt+=it;
                    }
                    dc.add(Calendar.DAY_OF_MONTH,1);
                }
                if (summaryModel.getRowCount()==0)
                    summaryModel.addRow(new Object[]{new SimpleDateFormat("MMMM yyyy").format(selected),"No data","—","P 0.00"});
                summaryModel.addRow(new Object[]{"MONTH TOTAL",mTx+" tx",mIt+" items",String.format("P %.2f",mTot)});
            }
            case "Year" -> {
                summaryModel.setColumnIdentifiers(new Object[]{"Month","Transactions","Items Sold","Revenue"});
                String[] months = {"January","February","March","April","May","June",
                                   "July","August","September","October","November","December"};
                double yTot=0; int yTx=0; int yIt=0;
                for (int m=0; m<12; m++) {
                    Calendar mc = (Calendar) sel.clone(); mc.set(Calendar.MONTH,m); mc.set(Calendar.DAY_OF_MONTH,1);
                    List<String[]> mt = filterByPeriod(transactions, mc.getTime(), "Month");
                    double rev = mt.stream().mapToDouble(t->Double.parseDouble(t[2])).sum();
                    int it  = mt.stream().mapToInt(t->Integer.parseInt(t[1])).sum();
                    summaryModel.addRow(new Object[]{
                        months[m]+" "+sel.get(Calendar.YEAR),
                        mt.size()+" tx", it+" items", String.format("P %.2f",rev)
                    });
                    yTot+=rev; yTx+=mt.size(); yIt+=it;
                }
                summaryModel.addRow(new Object[]{"YEAR "+sel.get(Calendar.YEAR),yTx+" tx",yIt+" items",String.format("P %.2f",yTot)});
            }
        }
    }
}
