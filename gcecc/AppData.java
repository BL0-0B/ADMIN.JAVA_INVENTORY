package gcecc;

import java.util.*;

//AppData.java — In-memory data store.
 
public class AppData {

    //COLORS
    public static final java.awt.Color GREEN_DARK  = new java.awt.Color(0x06, 0x4e, 0x3b);
    public static final java.awt.Color GREEN       = new java.awt.Color(0x09, 0x79, 0x69);
    public static final java.awt.Color GREEN_MID   = new java.awt.Color(0x10, 0xb9, 0x81);
    public static final java.awt.Color GREEN_LIGHT = new java.awt.Color(0xd1, 0xfa, 0xe5);
    public static final java.awt.Color GREEN_PALE  = new java.awt.Color(0xec, 0xfd, 0xf5);
    public static final java.awt.Color GOLD        = new java.awt.Color(0xf5, 0x9e, 0x0b);
    public static final java.awt.Color GOLD_LIGHT  = new java.awt.Color(0xfe, 0xf3, 0xc7);
    public static final java.awt.Color RED         = new java.awt.Color(0xef, 0x44, 0x44);
    public static final java.awt.Color RED_LIGHT   = new java.awt.Color(0xfe, 0xe2, 0xe2);
    public static final java.awt.Color BLUE        = new java.awt.Color(0x00, 0xac, 0xee);
    public static final java.awt.Color BLUE_LIGHT  = new java.awt.Color(0xdb, 0xea, 0xfe);
    public static final java.awt.Color PURPLE      = new java.awt.Color(0x7c, 0x3a, 0xed);
    public static final java.awt.Color BG          = new java.awt.Color(0xf4, 0xf7, 0xf6);
    public static final java.awt.Color TEXT        = new java.awt.Color(0x15, 0x1a, 0x2d);
    public static final java.awt.Color TEXT_MUTED  = new java.awt.Color(0x6b, 0x72, 0x80);
    public static final java.awt.Color BORDER      = new java.awt.Color(0xe5, 0xe7, 0xeb);
    public static final java.awt.Color WHITE       = java.awt.Color.WHITE;

    //USER SESSION
    public static String sessionName     = "";
    public static String sessionUsername = "";

    //USERS
    public static List<String[]> users = new ArrayList<>(Arrays.asList(
        new String[]{"Administrator", "admin",   "gcecc2025"},
        new String[]{"Staff",         "scanner", "12345"}
    ));
    // Each user: [name, username, password]

    //CATEGORIES
    public static List<String[]> categories = new ArrayList<>(Arrays.asList(
        new String[]{"1", "PE Uniforms"},
        new String[]{"2", "NSTP Shirts"},
        new String[]{"3", "School Uniform"},
        new String[]{"4", "Books"},
        new String[]{"5", "ID Laces ccs"}
    ));
    public static int nextCatId = 6;

    //PRODUCTS
    // Each product: [id, name, catId, price, stock, lowStock, barcode, unit]
    public static List<String[]> products = new ArrayList<>(Arrays.asList(
        new String[]{"1","PE Jogging Pants (S)","1","650","30","5","8934670001","pcs"},
        new String[]{"2","PE Jogging Pants (M)","1","650","28","5","8934670002","pcs"},
        new String[]{"3","PE Jogging Pants (L)","1","650","20","5","8934670003","pcs"},
        new String[]{"4","PE Jogging Pants (XL)","1","650","15","5","8934670004","pcs"},
        new String[]{"5","PE T-Shirt (S)","1","650","40","5","8934670005","pcs"},
        new String[]{"6","PE T-Shirt (M)","1","650","35","5","8934670006","pcs"},
        new String[]{"7","PE T-Shirt (L)","1","650","30","5","8934670007","pcs"},
        new String[]{"8","PE T-Shirt (XL)","1","650","18","5","8934670008","pcs"},
        new String[]{"9","NSTP Shirt (XS)","2","650","15","5","8934670009","pcs"},
        new String[]{"10","NSTP Shirt (S)","2","650","25","5","8934670010","pcs"},
        new String[]{"11","NSTP Shirt (M)","2","650","22","5","8934670011","pcs"},
        new String[]{"12","NSTP Shirt (L)","2","650","20","5","8934670012","pcs"},
        new String[]{"13","NSTP Shirt (XL)","2","650","12","5","8934670013","pcs"},
        new String[]{"14","NSTP Shirt (XXL)","2","650","8","3","8934670014","pcs"},
        new String[]{"15","Uniform Set (XS) Girl","3","650","10","3","8934670015","set"},
        new String[]{"16","Uniform Set (S) Girl","3","650","15","3","8934670016","set"},
        new String[]{"17","Uniform Set (M) Girl","3","650","12","3","8934670017","set"},
        new String[]{"18","Uniform Set (L) Girl","3","650","10","3","8934670018","set"},
        new String[]{"19","Uniform Set (XS) Boy","3","650","10","3","8934670019","set"},
        new String[]{"20","Uniform Set (S) Boy","3","650","14","3","8934670020","set"},
        new String[]{"21","Uniform Set (M) Boy","3","650","11","3","8934670021","set"},
        new String[]{"22","Uniform Set (L) Boy","3","650","9","3","8934670022","set"},
        new String[]{"23","BSIT Prog Fund Book","4","480","20","3","8934670023","pcs"},
        new String[]{"24","BSBA Mgmt Book","4","450","18","3","8934670024","pcs"},
        new String[]{"25","BSED Math Methods","4","420","15","3","8934670025","pcs"},
        new String[]{"26","BSN Anatomy Book","4","590","10","3","8934670026","pcs"},
        new String[]{"27","BSCRIM Law Book","4","460","12","3","8934670027","pcs"},
        new String[]{"28","ID Lace css - BSIT","5","35","80","10","8934670028","pcs"},
        new String[]{"29","ID Lace css- BSCS","5","35","75","10","8934670029","pcs"},
        new String[]{"30","ID Lace css- BSEMC","5","35","60","10","8934670030","pcs"},
        new String[]{"31","ID Lace - BSSHANE(White)","5","35","55","10","8934670031","pcs"},
        new String[]{"32","ID Lace - BSGLAI (Red)","5","35","50","10","8934670032","pcs"}
    ));
    public static int nextProdId = 33;

    //TRANSACTIONS
    public static List<String[]> transactions = new ArrayList<>();
    public static int nextTxId = 1;

    //TOCK RECORDS
    public static List<String[]> stockRecords = new ArrayList<>();
    public static int nextStockId = 1;

    public static void addStock(String productId, int qty) {
        String[] p = getProductById(productId);
        if (p == null) return;
        p[4] = String.valueOf(getStock(p) + qty);
        String date  = new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date());
        String time  = new java.text.SimpleDateFormat("hh:mm a").format(new java.util.Date());
        String size  = p.length > 8 && p[8] != null ? p[8] : "";
        String price = p[3];
        // stockRecord: [id, productId, productName, qty, date, time, barcode, size, price]
        stockRecords.add(new String[]{
            String.valueOf(nextStockId++), productId, p[1],
            String.valueOf(qty), date, time, p[6], size, price
        });
    }

    //ELPERS
    public static String[] getProductById(String id) {
        return products.stream().filter(p -> p[0].equals(id)).findFirst().orElse(null);
    }
    public static String[] getProductByBarcode(String barcode) {
        return products.stream().filter(p -> p[6].equals(barcode.trim())).findFirst().orElse(null);
    }
    public static String getCategoryName(String catId) {
        return categories.stream().filter(c -> c[0].equals(catId)).map(c -> c[1]).findFirst().orElse("—");
    }
    public static int    getStock   (String[] p) { return Integer.parseInt(p[4]); }
    public static int    getLowStock(String[] p) { return Integer.parseInt(p[5]); }
    public static double getPrice   (String[] p) { return Double.parseDouble(p[3]); }
    public static boolean isOutOfStock(String[] p) { return getStock(p) <= 0; }
    public static boolean isLowStock  (String[] p) { return getStock(p) > 0 && getStock(p) <= getLowStock(p); }
    public static void deductStock(String productId, int qty) {
        products.stream().filter(p -> p[0].equals(productId)).findFirst()
            .ifPresent(p -> p[4] = String.valueOf(Math.max(0, getStock(p) - qty)));
    }
    public static boolean loginUser(String username, String password) {
        return users.stream().anyMatch(u -> u[1].equals(username) && u[2].equals(password));
    }
    public static String getUserName(String username) {
        return users.stream().filter(u -> u[1].equals(username)).map(u -> u[0]).findFirst().orElse(username);
    }

    //FONTS
    public static java.awt.Font FONT_REG   = new java.awt.Font("SansSerif", java.awt.Font.PLAIN,  13);
    public static java.awt.Font FONT_BOLD  = new java.awt.Font("SansSerif", java.awt.Font.BOLD,   13);
    public static java.awt.Font FONT_SMALL = new java.awt.Font("SansSerif", java.awt.Font.PLAIN,  11);
    public static java.awt.Font FONT_LG    = new java.awt.Font("SansSerif", java.awt.Font.BOLD,   16);
    public static java.awt.Font FONT_MONO  = new java.awt.Font("Monospaced",java.awt.Font.PLAIN,  12);
}