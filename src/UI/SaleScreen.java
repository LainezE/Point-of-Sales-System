/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import PoS.PoS;
import PoS.classes.Customer;
import PoS.classes.Employee;
import PoS.classes.CustomerCart;
import PoS.classes.CartItem;
import PoS.classes.CustomerTransaction;
import PoS.classes.InventoryItem;
import java.util.ArrayList;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 *
 * @author aamer
 */
public class SaleScreen extends javax.swing.JPanel {
    private static final Color MAIN_BG_COLOR = new Color(210,109,63);
    private static final Color MAIN_FG_COLOR = new Color(247,241,227);
    private static final Color MENU_BTN_BG_COLOR = new Color(226,123,71);
    private static final Color MENU_BTN_SEL_COLOR = new Color(71,71,71);
    private final PoS pos;
    private final CardLayout cardLayout;
    private CustomerCart cart;
    private CustomerTransaction trans;
    private Timer updateTimer;
    
    /**
     * Creates new form PosScreen
     * @param pos
     */
    public SaleScreen(PoS pos) {
        initComponents();
        this.pos = pos;
        this.cart = null;
        
        if (pos.getAccessLevel() == PoS.ADMIN_LEVEL)
            btnMainMenu.setVisible(true);
        else
            btnMainMenu.setVisible(false);
        
        scanItemSerial.setFocusAccelerator('a');
        
        showDate();
        showDay();
        showTime();
        updateTimer = new Timer(1000, (ActionEvent e) -> { showTime(); });
        updateTimer.start();
        
        dialogCreateCustomer.getContentPane().setBackground(new Color(247,241,227));
        
        invTable.setAutoCreateRowSorter(true);
        invTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        updateInventoryTableData();
        
        invTable.getTableHeader().setFont(new Font("Maiandra GD",Font.BOLD,14));
        invTable.getTableHeader().setOpaque(false);
        invTable.getTableHeader().setBackground(MAIN_BG_COLOR);
        invTable.getTableHeader().setForeground(MAIN_FG_COLOR);
        invTable.setRowHeight(25);
        UIManager.getDefaults().put("TableHeader.cellBorder" , BorderFactory.createEmptyBorder(0,0,0,0));
        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) 
        invTable.getTableHeader().getDefaultRenderer();
        renderer.setHorizontalAlignment(JLabel.LEFT);
        
        custTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        updateCustomerTableData();
        
        custTable.setAutoCreateRowSorter(true);
        custTable.setVisible(false);
        custTable.getTableHeader().setFont(new Font("Maiandra GD",Font.BOLD,14));
        custTable.getTableHeader().setOpaque(false);
        custTable.getTableHeader().setBackground(MAIN_BG_COLOR);
        custTable.getTableHeader().setForeground(MAIN_FG_COLOR);
        custTable.setRowHeight(25);
        UIManager.getDefaults().put("TableHeader.cellBorder" , BorderFactory.createEmptyBorder(0,0,0,0));
        custTable.getTableHeader().getDefaultRenderer();
        renderer.setHorizontalAlignment(JLabel.LEFT);
        
        invoiceTable.getTableHeader().setFont(new Font("Maiandra GD",Font.BOLD,14));
        invoiceTable.getTableHeader().setOpaque(false);
        invoiceTable.getTableHeader().setBackground(MAIN_BG_COLOR);
        invoiceTable.getTableHeader().setForeground(MAIN_FG_COLOR);
        invoiceTable.setRowHeight(25);
        UIManager.getDefaults().put("TableHeader.cellBorder" , BorderFactory.createEmptyBorder(0,0,0,0));
        invoiceTable.getTableHeader().getDefaultRenderer();
        renderer.setHorizontalAlignment(JLabel.LEFT);
        
       try {
            //Font font = Font.createFont(Font.TRUETYPE_FONT, SaleScreen.class.getResourceAsStream("Font/digital-7.ttf"));
            InputStream is = getClass().getResourceAsStream("font/digital-7.ttf");
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            lblDiscount.setFont(font.deriveFont(Font.PLAIN, 28f));
            lblSubtotal.setFont(font.deriveFont(Font.PLAIN, 28f));
            lblTax.setFont(font.deriveFont(Font.PLAIN, 28f));
            lblTotal.setFont(font.deriveFont(Font.PLAIN, 36f));
            subtotalText.setFont(font.deriveFont(Font.PLAIN, 28f));
            discountText.setFont(font.deriveFont(Font.PLAIN, 28f));
            taxText.setFont(font.deriveFont(Font.PLAIN, 28f));
            totalText.setFont(font.deriveFont(Font.PLAIN, 36f));
            lblCardNumber.setFont(font.deriveFont(Font.PLAIN, 22f));
            lblExpiry.setFont(font.deriveFont(Font.PLAIN, 18f));
        }
        catch(FontFormatException | IOException e) {}
        
        cardLayout = (CardLayout) panelCards.getLayout();
        cardLayout.setVgap(0);
        
        //subtotalText.setText(cart.getSubtotal().toString());
        
        cardPaymentPnl.setVisible(false);
        cashPaymentPnl.setVisible(false);
        paymentOptions.setVisible(false);
        imgVisaLogo.setVisible(false);
        
        cardnumberText.setDocument(new LengthRestrictedDocument(19));
        expiryText.setDocument(new LengthRestrictedDocument(5));
        cvcText.setDocument(new LengthRestrictedDocument(3));
        
        UI_Tools.setFocusPainted(this, false);
        UI_Tools.setFocusPainted(dialogCreateCustomer, false);
        UI_Tools.setFocusPainted(dialogEditCustomer, false);
    
        if(pos.getAccessLevel()==PoS.SALES_LEVEL){
            Employee em = pos.employees.load(pos.getEmployeeID());
            lblUserName.setText(em.getFirstName()+" "+em.getLastName());
            lblUserType.setText("Employee");
        }
        
        else if(pos.getAccessLevel()==PoS.ADMIN_LEVEL){
            Employee em = pos.employees.load(pos.getEmployeeID());
            lblUserName.setText(em.getFirstName()+" "+em.getLastName());
            lblUserType.setText("Admin");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dialogCreateCustomer = new javax.swing.JDialog();
        jSeparator8 = new javax.swing.JSeparator();
        jSeparator9 = new javax.swing.JSeparator();
        jSeparator10 = new javax.swing.JSeparator();
        jSeparator11 = new javax.swing.JSeparator();
        jSeparator12 = new javax.swing.JSeparator();
        jSeparator13 = new javax.swing.JSeparator();
        jSeparator14 = new javax.swing.JSeparator();
        jSeparator15 = new javax.swing.JSeparator();
        createFirstName = new javax.swing.JTextField();
        createLastName = new javax.swing.JTextField();
        createEmail = new javax.swing.JTextField();
        createPhone = new javax.swing.JTextField();
        createStreet = new javax.swing.JTextField();
        createCity = new javax.swing.JTextField();
        createState = new javax.swing.JTextField();
        createZip = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        createSubmit = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        dialogCustomerId = new javax.swing.JDialog();
        dlgOkay = new javax.swing.JButton();
        lblCustId = new javax.swing.JLabel();
        custIdText = new javax.swing.JTextField();
        dialogRemoveItem = new javax.swing.JDialog();
        removeItems = new javax.swing.JTextField();
        btnOkay = new javax.swing.JButton();
        dialogEditCustomer = new javax.swing.JDialog();
        jSeparator16 = new javax.swing.JSeparator();
        jSeparator17 = new javax.swing.JSeparator();
        jSeparator18 = new javax.swing.JSeparator();
        jSeparator19 = new javax.swing.JSeparator();
        jSeparator20 = new javax.swing.JSeparator();
        jSeparator21 = new javax.swing.JSeparator();
        jSeparator22 = new javax.swing.JSeparator();
        jSeparator23 = new javax.swing.JSeparator();
        editFirstName = new javax.swing.JTextField();
        editLastName = new javax.swing.JTextField();
        editEmail = new javax.swing.JTextField();
        editPhone = new javax.swing.JTextField();
        editStreet = new javax.swing.JTextField();
        editCity = new javax.swing.JTextField();
        editState = new javax.swing.JTextField();
        editZip = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        editSubmit = new javax.swing.JButton();
        splitPaneMain = new javax.swing.JSplitPane();
        panelMenu = new javax.swing.JPanel();
        btnMainMenu = new javax.swing.JButton();
        btnLogoutButton = new javax.swing.JButton();
        btnCustomer = new javax.swing.JButton();
        btnExitButton = new javax.swing.JButton();
        btnInventory = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        panelCards = new javax.swing.JPanel();
        inventoryTab = new javax.swing.JPanel();
        lblInventorySearchIcon = new javax.swing.JLabel();
        textInventorySearch = new javax.swing.JTextField();
        textInventorySearch = new RoundJTextField(60);
        itemTable = new javax.swing.JScrollPane();
        invTable = new javax.swing.JTable();
        lblInventoryTitle = new javax.swing.JLabel();
        invSearchBy = new javax.swing.JComboBox<>();
        customerTab = new javax.swing.JPanel();
        scrollPaneCustomers = new javax.swing.JScrollPane();
        custTable = new javax.swing.JTable();
        customerCreate = new javax.swing.JButton();
        customerEdit = new javax.swing.JButton();
        lblInventorySearchIcon1 = new javax.swing.JLabel();
        customerDelete = new javax.swing.JButton();
        lblInventoryTitle1 = new javax.swing.JLabel();
        textCustomerSearch = new javax.swing.JTextField();
        textCustomerSearch = new RoundJTextField(60);
        custSearchBy = new javax.swing.JComboBox<>();
        cartPnl = new javax.swing.JPanel();
        scrollPaneCart = new javax.swing.JScrollPane();
        invoiceTable = new javax.swing.JTable();
        cartButtons = new javax.swing.JLayeredPane();
        addItem = new javax.swing.JButton();
        removeItem = new javax.swing.JButton();
        payButton = new javax.swing.JButton();
        customerReward = new javax.swing.JButton();
        couponDiscount = new javax.swing.JButton();
        cancelTransaction = new javax.swing.JButton();
        paymentOptions = new javax.swing.JLayeredPane();
        payByCashbtn = new javax.swing.JButton();
        payByCardbtn = new javax.swing.JButton();
        paymentBack = new javax.swing.JButton();
        cashPaymentPnl = new javax.swing.JLayeredPane();
        jSeparator5 = new javax.swing.JSeparator();
        jSeparator6 = new javax.swing.JSeparator();
        jSeparator7 = new javax.swing.JSeparator();
        cashAmountText = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        cashChangeText = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        cashCostText = new javax.swing.JTextField();
        cashBackbtn = new javax.swing.JButton();
        cashConfirmbtn = new javax.swing.JButton();
        cardPaymentPnl = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        cardholderText = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cardnumberText = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        expiryText = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        cvcText = new javax.swing.JTextField();
        cardConfirmbtn = new javax.swing.JButton();
        imgVisaLogo = new javax.swing.JLabel();
        lblExpiry = new javax.swing.JLabel();
        lblMonthYear = new javax.swing.JLabel();
        lblThru = new javax.swing.JLabel();
        lblCardholderName = new javax.swing.JLabel();
        lblValid = new javax.swing.JLabel();
        lblCardNumber = new javax.swing.JLabel();
        lblSmallNum = new javax.swing.JLabel();
        imgCreditCard = new javax.swing.JLabel();
        cardBack = new javax.swing.JButton();
        scanItemSerial = new javax.swing.JTextField();
        lblSubtotal = new javax.swing.JLabel();
        lblDiscount = new javax.swing.JLabel();
        lblTax = new javax.swing.JLabel();
        totalText = new javax.swing.JLabel();
        discountText = new javax.swing.JLabel();
        subtotalText = new javax.swing.JLabel();
        taxText = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        lblCashDisplayBackground = new javax.swing.JLabel();
        lblCashDisplayBorder = new javax.swing.JLabel();
        lblUserType = new javax.swing.JLabel();
        lblUserName = new javax.swing.JLabel();
        lblUserPicture = new javax.swing.JLabel();
        lblUserBackground = new javax.swing.JLabel();
        lblWeekDay = new javax.swing.JLabel();
        lblTime = new javax.swing.JLabel();
        lblDate = new javax.swing.JLabel();
        lblMonitorScreen = new javax.swing.JLabel();
        lblMonitorImg = new javax.swing.JLabel();
        lblStoreName = new javax.swing.JLabel();
        lblBanner = new javax.swing.JLabel();

        dialogCreateCustomer.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        dialogCreateCustomer.setTitle("Create Customer");
        dialogCreateCustomer.setAlwaysOnTop(true);
        dialogCreateCustomer.setBackground(new java.awt.Color(226, 123, 71));
        dialogCreateCustomer.setResizable(false);
        dialogCreateCustomer.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                dialogCreateCustomerWindowClosed(evt);
            }
        });
        dialogCreateCustomer.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        dialogCreateCustomer.getContentPane().add(jSeparator8, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 110, 120, 20));
        dialogCreateCustomer.getContentPane().add(jSeparator9, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, 120, 20));
        dialogCreateCustomer.getContentPane().add(jSeparator10, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 190, 50, 20));
        dialogCreateCustomer.getContentPane().add(jSeparator11, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 70, 120, 20));
        dialogCreateCustomer.getContentPane().add(jSeparator12, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 110, 120, 20));
        dialogCreateCustomer.getContentPane().add(jSeparator13, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 150, 120, 20));
        dialogCreateCustomer.getContentPane().add(jSeparator14, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 190, 70, 20));
        dialogCreateCustomer.getContentPane().add(jSeparator15, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 70, 120, 20));

        createFirstName.setBackground(new java.awt.Color(247, 241, 227));
        createFirstName.setFont(new java.awt.Font("Maiandra GD", 0, 14)); // NOI18N
        createFirstName.setForeground(new java.awt.Color(71, 71, 71));
        createFirstName.setBorder(null);
        dialogCreateCustomer.getContentPane().add(createFirstName, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 50, 120, 20));

        createLastName.setBackground(new java.awt.Color(247, 241, 227));
        createLastName.setFont(new java.awt.Font("Maiandra GD", 0, 14)); // NOI18N
        createLastName.setForeground(new java.awt.Color(71, 71, 71));
        createLastName.setBorder(null);
        dialogCreateCustomer.getContentPane().add(createLastName, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 50, 120, 20));

        createEmail.setBackground(new java.awt.Color(247, 241, 227));
        createEmail.setFont(new java.awt.Font("Maiandra GD", 0, 14)); // NOI18N
        createEmail.setForeground(new java.awt.Color(71, 71, 71));
        createEmail.setBorder(null);
        dialogCreateCustomer.getContentPane().add(createEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 90, 120, 20));

        createPhone.setBackground(new java.awt.Color(247, 241, 227));
        createPhone.setFont(new java.awt.Font("Maiandra GD", 0, 14)); // NOI18N
        createPhone.setForeground(new java.awt.Color(71, 71, 71));
        createPhone.setBorder(null);
        dialogCreateCustomer.getContentPane().add(createPhone, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 90, 120, -1));

        createStreet.setBackground(new java.awt.Color(247, 241, 227));
        createStreet.setFont(new java.awt.Font("Maiandra GD", 0, 14)); // NOI18N
        createStreet.setForeground(new java.awt.Color(71, 71, 71));
        createStreet.setBorder(null);
        dialogCreateCustomer.getContentPane().add(createStreet, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 130, 120, 20));

        createCity.setBackground(new java.awt.Color(247, 241, 227));
        createCity.setFont(new java.awt.Font("Maiandra GD", 0, 14)); // NOI18N
        createCity.setForeground(new java.awt.Color(71, 71, 71));
        createCity.setBorder(null);
        dialogCreateCustomer.getContentPane().add(createCity, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 130, 120, -1));

        createState.setBackground(new java.awt.Color(247, 241, 227));
        createState.setFont(new java.awt.Font("Maiandra GD", 0, 14)); // NOI18N
        createState.setForeground(new java.awt.Color(71, 71, 71));
        createState.setBorder(null);
        dialogCreateCustomer.getContentPane().add(createState, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 170, 50, 20));

        createZip.setBackground(new java.awt.Color(247, 241, 227));
        createZip.setFont(new java.awt.Font("Maiandra GD", 0, 14)); // NOI18N
        createZip.setForeground(new java.awt.Color(71, 71, 71));
        createZip.setBorder(null);
        dialogCreateCustomer.getContentPane().add(createZip, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 170, 70, -1));

        jLabel5.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(71, 71, 71));
        jLabel5.setText("First Name:");
        dialogCreateCustomer.getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        jLabel6.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(71, 71, 71));
        jLabel6.setText("Email:");
        dialogCreateCustomer.getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, -1, -1));

        jLabel7.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(71, 71, 71));
        jLabel7.setText("Street:");
        dialogCreateCustomer.getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, -1, -1));

        jLabel8.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(71, 71, 71));
        jLabel8.setText("State:");
        dialogCreateCustomer.getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, -1, -1));

        jLabel10.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(71, 71, 71));
        jLabel10.setText("Last Name:");
        dialogCreateCustomer.getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 50, -1, -1));

        jLabel11.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(71, 71, 71));
        jLabel11.setText("Phone:");
        dialogCreateCustomer.getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 90, -1, -1));

        jLabel12.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(71, 71, 71));
        jLabel12.setText("City:");
        dialogCreateCustomer.getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 130, -1, -1));

        jLabel13.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(71, 71, 71));
        jLabel13.setText("Zip:");
        dialogCreateCustomer.getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 170, -1, -1));

        createSubmit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/submitBtn.png"))); // NOI18N
        createSubmit.setBorder(null);
        createSubmit.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/submitClicked_1.png"))); // NOI18N
        createSubmit.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/submitClicked.png"))); // NOI18N
        createSubmit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                createSubmitMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                createSubmitMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                createSubmitMouseExited(evt);
            }
        });
        createSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createSubmitActionPerformed(evt);
            }
        });
        dialogCreateCustomer.getContentPane().add(createSubmit, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 228, 110, -1));

        jLabel1.setBackground(new java.awt.Color(210, 109, 63));
        jLabel1.setFont(new java.awt.Font("Maiandra GD", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(71, 71, 71));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Create a Customer");
        dialogCreateCustomer.getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 360, 30));

        dlgOkay.setText("Okay");
        dlgOkay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dlgOkayActionPerformed(evt);
            }
        });

        lblCustId.setText("Customer Id:");

        javax.swing.GroupLayout dialogCustomerIdLayout = new javax.swing.GroupLayout(dialogCustomerId.getContentPane());
        dialogCustomerId.getContentPane().setLayout(dialogCustomerIdLayout);
        dialogCustomerIdLayout.setHorizontalGroup(
            dialogCustomerIdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogCustomerIdLayout.createSequentialGroup()
                .addGroup(dialogCustomerIdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dialogCustomerIdLayout.createSequentialGroup()
                        .addGap(125, 125, 125)
                        .addComponent(dlgOkay))
                    .addGroup(dialogCustomerIdLayout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(lblCustId)
                        .addGap(41, 41, 41)
                        .addComponent(custIdText, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(57, Short.MAX_VALUE))
        );
        dialogCustomerIdLayout.setVerticalGroup(
            dialogCustomerIdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogCustomerIdLayout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addGroup(dialogCustomerIdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCustId)
                    .addComponent(custIdText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                .addComponent(dlgOkay)
                .addGap(33, 33, 33))
        );

        removeItems.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeItemsActionPerformed(evt);
            }
        });

        btnOkay.setText("Remove");
        btnOkay.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnOkayMouseClicked(evt);
            }
        });
        btnOkay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkayActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dialogRemoveItemLayout = new javax.swing.GroupLayout(dialogRemoveItem.getContentPane());
        dialogRemoveItem.getContentPane().setLayout(dialogRemoveItemLayout);
        dialogRemoveItemLayout.setHorizontalGroup(
            dialogRemoveItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogRemoveItemLayout.createSequentialGroup()
                .addGroup(dialogRemoveItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dialogRemoveItemLayout.createSequentialGroup()
                        .addGap(103, 103, 103)
                        .addComponent(removeItems, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(dialogRemoveItemLayout.createSequentialGroup()
                        .addGap(141, 141, 141)
                        .addComponent(btnOkay)))
                .addGap(122, 122, 122))
        );
        dialogRemoveItemLayout.setVerticalGroup(
            dialogRemoveItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogRemoveItemLayout.createSequentialGroup()
                .addContainerGap(78, Short.MAX_VALUE)
                .addComponent(removeItems, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55)
                .addComponent(btnOkay)
                .addGap(54, 54, 54))
        );

        dialogEditCustomer.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        dialogEditCustomer.setTitle("Create Customer");
        dialogEditCustomer.setAlwaysOnTop(true);
        dialogEditCustomer.setBackground(new java.awt.Color(226, 123, 71));
        dialogEditCustomer.setResizable(false);
        dialogEditCustomer.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                dialogEditCustomerWindowClosed(evt);
            }
        });
        dialogEditCustomer.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        dialogEditCustomer.getContentPane().add(jSeparator16, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 110, 120, 20));
        dialogEditCustomer.getContentPane().add(jSeparator17, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, 120, 20));
        dialogEditCustomer.getContentPane().add(jSeparator18, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 190, 50, 20));
        dialogEditCustomer.getContentPane().add(jSeparator19, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 70, 120, 20));
        dialogEditCustomer.getContentPane().add(jSeparator20, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 110, 120, 20));
        dialogEditCustomer.getContentPane().add(jSeparator21, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 150, 120, 20));
        dialogEditCustomer.getContentPane().add(jSeparator22, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 190, 70, 20));
        dialogEditCustomer.getContentPane().add(jSeparator23, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 70, 120, 20));

        editFirstName.setBackground(new java.awt.Color(247, 241, 227));
        editFirstName.setFont(new java.awt.Font("Maiandra GD", 0, 14)); // NOI18N
        editFirstName.setForeground(new java.awt.Color(71, 71, 71));
        editFirstName.setBorder(null);
        dialogEditCustomer.getContentPane().add(editFirstName, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 50, 120, 20));

        editLastName.setBackground(new java.awt.Color(247, 241, 227));
        editLastName.setFont(new java.awt.Font("Maiandra GD", 0, 14)); // NOI18N
        editLastName.setForeground(new java.awt.Color(71, 71, 71));
        editLastName.setBorder(null);
        dialogEditCustomer.getContentPane().add(editLastName, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 50, 120, 20));

        editEmail.setBackground(new java.awt.Color(247, 241, 227));
        editEmail.setFont(new java.awt.Font("Maiandra GD", 0, 14)); // NOI18N
        editEmail.setForeground(new java.awt.Color(71, 71, 71));
        editEmail.setBorder(null);
        dialogEditCustomer.getContentPane().add(editEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 90, 120, 20));

        editPhone.setBackground(new java.awt.Color(247, 241, 227));
        editPhone.setFont(new java.awt.Font("Maiandra GD", 0, 14)); // NOI18N
        editPhone.setForeground(new java.awt.Color(71, 71, 71));
        editPhone.setBorder(null);
        dialogEditCustomer.getContentPane().add(editPhone, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 90, 120, -1));

        editStreet.setBackground(new java.awt.Color(247, 241, 227));
        editStreet.setFont(new java.awt.Font("Maiandra GD", 0, 14)); // NOI18N
        editStreet.setForeground(new java.awt.Color(71, 71, 71));
        editStreet.setBorder(null);
        dialogEditCustomer.getContentPane().add(editStreet, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 130, 120, 20));

        editCity.setBackground(new java.awt.Color(247, 241, 227));
        editCity.setFont(new java.awt.Font("Maiandra GD", 0, 14)); // NOI18N
        editCity.setForeground(new java.awt.Color(71, 71, 71));
        editCity.setBorder(null);
        dialogEditCustomer.getContentPane().add(editCity, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 130, 120, -1));

        editState.setBackground(new java.awt.Color(247, 241, 227));
        editState.setFont(new java.awt.Font("Maiandra GD", 0, 14)); // NOI18N
        editState.setForeground(new java.awt.Color(71, 71, 71));
        editState.setBorder(null);
        dialogEditCustomer.getContentPane().add(editState, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 170, 50, 20));

        editZip.setBackground(new java.awt.Color(247, 241, 227));
        editZip.setFont(new java.awt.Font("Maiandra GD", 0, 14)); // NOI18N
        editZip.setForeground(new java.awt.Color(71, 71, 71));
        editZip.setBorder(null);
        dialogEditCustomer.getContentPane().add(editZip, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 170, 70, -1));

        jLabel27.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(71, 71, 71));
        jLabel27.setText("First Name:");
        dialogEditCustomer.getContentPane().add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        jLabel28.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(71, 71, 71));
        jLabel28.setText("Email:");
        dialogEditCustomer.getContentPane().add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, -1, -1));

        jLabel29.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(71, 71, 71));
        jLabel29.setText("Street:");
        dialogEditCustomer.getContentPane().add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, -1, -1));

        jLabel30.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(71, 71, 71));
        jLabel30.setText("State:");
        dialogEditCustomer.getContentPane().add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, -1, -1));

        jLabel31.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(71, 71, 71));
        jLabel31.setText("Last Name:");
        dialogEditCustomer.getContentPane().add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 50, -1, -1));

        jLabel32.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(71, 71, 71));
        jLabel32.setText("Phone:");
        dialogEditCustomer.getContentPane().add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 90, -1, -1));

        jLabel33.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(71, 71, 71));
        jLabel33.setText("City:");
        dialogEditCustomer.getContentPane().add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 130, -1, -1));

        jLabel34.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(71, 71, 71));
        jLabel34.setText("Zip:");
        dialogEditCustomer.getContentPane().add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 170, -1, -1));

        jLabel2.setBackground(new java.awt.Color(210, 109, 63));
        jLabel2.setFont(new java.awt.Font("Maiandra GD", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(71, 71, 71));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Edit Customer");
        dialogEditCustomer.getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 360, 30));

        editSubmit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/submitBtn.png"))); // NOI18N
        editSubmit.setBorder(null);
        editSubmit.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/submitClicked.png"))); // NOI18N
        editSubmit.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/submitClicked.png"))); // NOI18N
        editSubmit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editSubmitMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                editSubmitMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                editSubmitMouseExited(evt);
            }
        });
        editSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editSubmitActionPerformed(evt);
            }
        });
        dialogEditCustomer.getContentPane().add(editSubmit, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 228, 110, -1));

        setBackground(new java.awt.Color(247, 241, 227));
        setToolTipText("");
        setPreferredSize(new java.awt.Dimension(1366, 768));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        splitPaneMain.setBorder(null);
        splitPaneMain.setDividerSize(0);

        panelMenu.setBackground(new java.awt.Color(226, 123, 71));
        panelMenu.setPreferredSize(new java.awt.Dimension(260, 658));
        panelMenu.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnMainMenu.setBackground(new java.awt.Color(226, 123, 71));
        btnMainMenu.setFont(new java.awt.Font("Maiandra GD", 0, 21)); // NOI18N
        btnMainMenu.setForeground(new java.awt.Color(247, 241, 227));
        btnMainMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/icons8_long_arrow_left_52px.png"))); // NOI18N
        btnMainMenu.setText("  Back");
        btnMainMenu.setBorderPainted(false);
        btnMainMenu.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMainMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnMainMenuMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnMainMenuMouseExited(evt);
            }
        });
        panelMenu.add(btnMainMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 214, 250, 84));

        btnLogoutButton.setBackground(new java.awt.Color(226, 123, 71));
        btnLogoutButton.setFont(new java.awt.Font("Maiandra GD", 0, 21)); // NOI18N
        btnLogoutButton.setForeground(new java.awt.Color(247, 241, 227));
        btnLogoutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/icons8_logout_rounded_up_filled_50px.png"))); // NOI18N
        btnLogoutButton.setText("  Log Out");
        btnLogoutButton.setBorderPainted(false);
        btnLogoutButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnLogoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogoutButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogoutButtonMouseExited(evt);
            }
        });
        panelMenu.add(btnLogoutButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 304, 250, 84));

        btnCustomer.setBackground(new java.awt.Color(226, 123, 71));
        btnCustomer.setFont(new java.awt.Font("Maiandra GD", 0, 21)); // NOI18N
        btnCustomer.setForeground(new java.awt.Color(247, 241, 227));
        btnCustomer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/icons8_user_64px.png"))); // NOI18N
        btnCustomer.setText("Customer");
        btnCustomer.setBorderPainted(false);
        btnCustomer.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCustomer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCustomerMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnCustomerMouseExited(evt);
            }
        });
        btnCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCustomerActionPerformed(evt);
            }
        });
        panelMenu.add(btnCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 124, 250, 84));

        btnExitButton.setBackground(new java.awt.Color(226, 123, 71));
        btnExitButton.setFont(new java.awt.Font("Maiandra GD", 0, 21)); // NOI18N
        btnExitButton.setForeground(new java.awt.Color(247, 241, 227));
        btnExitButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/icons8_exit_sign_48px.png"))); // NOI18N
        btnExitButton.setText("  Exit");
        btnExitButton.setBorderPainted(false);
        btnExitButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnExitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnExitButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnExitButtonMouseExited(evt);
            }
        });
        panelMenu.add(btnExitButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 394, 250, 84));

        btnInventory.setBackground(new java.awt.Color(71, 71, 71));
        btnInventory.setFont(new java.awt.Font("Maiandra GD", 0, 21)); // NOI18N
        btnInventory.setForeground(new java.awt.Color(247, 241, 227));
        btnInventory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/icons8_move_by_trolley_64px.png"))); // NOI18N
        btnInventory.setText("Inventory");
        btnInventory.setBorderPainted(false);
        btnInventory.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnInventory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnInventoryMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnInventoryMouseExited(evt);
            }
        });
        btnInventory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventoryActionPerformed(evt);
            }
        });
        panelMenu.add(btnInventory, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 34, 250, 84));

        jLabel25.setBackground(new java.awt.Color(210, 109, 63));
        jLabel25.setOpaque(true);
        panelMenu.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, 476, 250, 150));

        splitPaneMain.setLeftComponent(panelMenu);

        panelCards.setPreferredSize(new java.awt.Dimension(650, 400));
        panelCards.setLayout(new java.awt.CardLayout());

        inventoryTab.setBackground(new java.awt.Color(71, 71, 71));
        inventoryTab.setPreferredSize(new java.awt.Dimension(198, 772));
        inventoryTab.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblInventorySearchIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/icons8_search_24px_3.png"))); // NOI18N
        inventoryTab.add(lblInventorySearchIcon, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 70, -1, 30));

        textInventorySearch.setBackground(new java.awt.Color(94, 93, 93));
        textInventorySearch.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        textInventorySearch.setForeground(new java.awt.Color(226, 123, 71));
        textInventorySearch.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        textInventorySearch.setText("Search...");
        textInventorySearch.setBorder(null);
        textInventorySearch.setCaretColor(new java.awt.Color(226, 123, 71));
        textInventorySearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textInventorySearchFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textInventorySearchFocusLost(evt);
            }
        });
        textInventorySearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textInventorySearchKeyReleased(evt);
            }
        });
        inventoryTab.add(textInventorySearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 70, 170, 30));

        itemTable.setForeground(new java.awt.Color(51, 255, 153));

        invTable.setBackground(new java.awt.Color(247, 241, 227));
        invTable.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        invTable.setForeground(new java.awt.Color(0, 0, 0));
        invTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Serial No.", "Item Name", "Manufacturer", "Price"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        invTable.setFocusable(false);
        invTable.setGridColor(new java.awt.Color(60, 99, 130));
        invTable.setIntercellSpacing(new java.awt.Dimension(0, 0));
        invTable.setSelectionBackground(new java.awt.Color(47, 149, 153));
        invTable.setSelectionForeground(new java.awt.Color(255, 255, 255));
        invTable.setShowGrid(false);
        invTable.setShowHorizontalLines(true);
        invTable.setShowVerticalLines(true);
        invTable.getTableHeader().setReorderingAllowed(false);
        itemTable.setViewportView(invTable);
        if (invTable.getColumnModel().getColumnCount() > 0) {
            invTable.getColumnModel().getColumn(0).setResizable(false);
            invTable.getColumnModel().getColumn(1).setResizable(false);
            invTable.getColumnModel().getColumn(2).setResizable(false);
            invTable.getColumnModel().getColumn(3).setResizable(false);
        }

        inventoryTab.add(itemTable, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 520, 390));

        lblInventoryTitle.setFont(new java.awt.Font("Maiandra GD", 1, 36)); // NOI18N
        lblInventoryTitle.setForeground(new java.awt.Color(247, 241, 227));
        lblInventoryTitle.setText("Inventory");
        inventoryTab.add(lblInventoryTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 270, 50));

        invSearchBy.setBackground(new java.awt.Color(94, 93, 93));
        invSearchBy.setFont(new java.awt.Font("Maiandra GD", 1, 12)); // NOI18N
        invSearchBy.setForeground(new java.awt.Color(226, 123, 71));
        invSearchBy.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Serial No.", "Name", "Manufacturer", "Price" }));
        invSearchBy.setToolTipText("");
        invSearchBy.setBorder(null);
        inventoryTab.add(invSearchBy, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 100, 170, -1));

        panelCards.add(inventoryTab, "inventoryPanel");

        customerTab.setBackground(new java.awt.Color(71, 71, 71));
        customerTab.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        custTable.setBackground(new java.awt.Color(247, 241, 227));
        custTable.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        custTable.setForeground(new java.awt.Color(0, 0, 0));
        custTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Customer ID", "First Name", "Last Name", "Phone", "Email"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        custTable.setFocusable(false);
        custTable.setGridColor(new java.awt.Color(60, 99, 130));
        custTable.setIntercellSpacing(new java.awt.Dimension(0, 0));
        custTable.setSelectionBackground(new java.awt.Color(47, 149, 153));
        custTable.setSelectionForeground(new java.awt.Color(255, 255, 255));
        custTable.getTableHeader().setReorderingAllowed(false);
        scrollPaneCustomers.setViewportView(custTable);
        if (custTable.getColumnModel().getColumnCount() > 0) {
            custTable.getColumnModel().getColumn(0).setResizable(false);
            custTable.getColumnModel().getColumn(1).setResizable(false);
            custTable.getColumnModel().getColumn(2).setResizable(false);
            custTable.getColumnModel().getColumn(3).setResizable(false);
            custTable.getColumnModel().getColumn(4).setResizable(false);
        }

        customerTab.add(scrollPaneCustomers, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 520, 390));

        customerCreate.setBackground(new java.awt.Color(71, 71, 71));
        customerCreate.setForeground(new java.awt.Color(71, 71, 71));
        customerCreate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/createAccBtn.png"))); // NOI18N
        customerCreate.setBorder(null);
        customerCreate.setBorderPainted(false);
        customerCreate.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/createAccClicked.png"))); // NOI18N
        customerCreate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                customerCreateMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                customerCreateMouseExited(evt);
            }
        });
        customerCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customerCreateActionPerformed(evt);
            }
        });
        customerTab.add(customerCreate, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 560, 130, 40));

        customerEdit.setBackground(new java.awt.Color(71, 71, 71));
        customerEdit.setForeground(new java.awt.Color(71, 71, 71));
        customerEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/editBtn.png"))); // NOI18N
        customerEdit.setAlignmentY(0.0F);
        customerEdit.setBorderPainted(false);
        customerEdit.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/editClicked.png"))); // NOI18N
        customerEdit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                customerEditMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                customerEditMouseExited(evt);
            }
        });
        customerEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customerEditActionPerformed(evt);
            }
        });
        customerTab.add(customerEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 560, 130, 40));

        lblInventorySearchIcon1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/icons8_search_24px_3.png"))); // NOI18N
        customerTab.add(lblInventorySearchIcon1, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 70, 30, 30));

        customerDelete.setBackground(new java.awt.Color(71, 71, 71));
        customerDelete.setForeground(new java.awt.Color(71, 71, 71));
        customerDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/dltBtn.png"))); // NOI18N
        customerDelete.setBorderPainted(false);
        customerDelete.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/dltClicked.png"))); // NOI18N
        customerDelete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                customerDeleteMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                customerDeleteMouseExited(evt);
            }
        });
        customerDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customerDeleteActionPerformed(evt);
            }
        });
        customerTab.add(customerDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 560, 130, 40));

        lblInventoryTitle1.setFont(new java.awt.Font("Maiandra GD", 1, 36)); // NOI18N
        lblInventoryTitle1.setForeground(new java.awt.Color(247, 241, 227));
        lblInventoryTitle1.setText("Customer Information");
        customerTab.add(lblInventoryTitle1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 380, 50));

        textCustomerSearch.setBackground(new java.awt.Color(94, 93, 93));
        textCustomerSearch.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        textCustomerSearch.setForeground(new java.awt.Color(226, 123, 71));
        textCustomerSearch.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        textCustomerSearch.setText("Search...");
        textCustomerSearch.setBorder(null);
        textCustomerSearch.setCaretColor(new java.awt.Color(226, 123, 71));
        textCustomerSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textCustomerSearchFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textCustomerSearchFocusLost(evt);
            }
        });
        textCustomerSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textCustomerSearchKeyReleased(evt);
            }
        });
        customerTab.add(textCustomerSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 70, 170, 30));

        custSearchBy.setBackground(new java.awt.Color(94, 93, 93));
        custSearchBy.setFont(new java.awt.Font("Maiandra GD", 1, 12)); // NOI18N
        custSearchBy.setForeground(new java.awt.Color(226, 123, 71));
        custSearchBy.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Customer ID", "First Name", "Last Name", "Phone", "Email" }));
        custSearchBy.setToolTipText("");
        customerTab.add(custSearchBy, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 100, 170, -1));

        panelCards.add(customerTab, "customerPanel");

        splitPaneMain.setRightComponent(panelCards);

        add(splitPaneMain, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 140, 810, 660));

        invoiceTable.setBackground(new java.awt.Color(247, 241, 227));
        invoiceTable.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        invoiceTable.setForeground(new java.awt.Color(0, 0, 0));
        invoiceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item Id", "Item", "Quantity", "Price"
            }
        ));
        invoiceTable.setGridColor(new java.awt.Color(60, 99, 130));
        invoiceTable.setSelectionBackground(new java.awt.Color(47, 149, 153));
        invoiceTable.setSelectionForeground(new java.awt.Color(255, 255, 255));
        invoiceTable.getTableHeader().setReorderingAllowed(false);
        scrollPaneCart.setViewportView(invoiceTable);
        if (invoiceTable.getColumnModel().getColumnCount() > 0) {
            invoiceTable.getColumnModel().getColumn(2).setResizable(false);
            invoiceTable.getColumnModel().getColumn(3).setResizable(false);
        }

        cartButtons.setBackground(new java.awt.Color(247, 241, 227));
        cartButtons.setForeground(new java.awt.Color(247, 241, 227));

        addItem.setBackground(new java.awt.Color(247, 241, 227));
        addItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/addBtn.png"))); // NOI18N
        addItem.setBorder(null);
        addItem.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/addClicked.png"))); // NOI18N
        addItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addItemMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addItemMouseExited(evt);
            }
        });
        addItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addItemActionPerformed(evt);
            }
        });

        removeItem.setBackground(new java.awt.Color(247, 241, 227));
        removeItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/removeBtn.png"))); // NOI18N
        removeItem.setBorder(null);
        removeItem.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/removeClicked.png"))); // NOI18N
        removeItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                removeItemMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                removeItemMouseExited(evt);
            }
        });
        removeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeItemActionPerformed(evt);
            }
        });

        payButton.setBackground(new java.awt.Color(247, 241, 227));
        payButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/payBtn.png"))); // NOI18N
        payButton.setBorder(null);
        payButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/payClicked.png"))); // NOI18N
        payButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                payButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                payButtonMouseExited(evt);
            }
        });
        payButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payButtonActionPerformed(evt);
            }
        });

        customerReward.setBackground(new java.awt.Color(247, 241, 227));
        customerReward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/crBtn.png"))); // NOI18N
        customerReward.setBorder(null);
        customerReward.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/crClicked.png"))); // NOI18N
        customerReward.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                customerRewardMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                customerRewardMouseExited(evt);
            }
        });
        customerReward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customerRewardActionPerformed(evt);
            }
        });

        couponDiscount.setBackground(new java.awt.Color(247, 241, 227));
        couponDiscount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/cpnBtn.png"))); // NOI18N
        couponDiscount.setBorder(null);
        couponDiscount.setPreferredSize(new java.awt.Dimension(106, 47));
        couponDiscount.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/cpnClicked.png"))); // NOI18N
        couponDiscount.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                couponDiscountMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                couponDiscountMouseExited(evt);
            }
        });
        couponDiscount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                couponDiscountActionPerformed(evt);
            }
        });

        cancelTransaction.setBackground(new java.awt.Color(247, 241, 227));
        cancelTransaction.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/ctBtn.png"))); // NOI18N
        cancelTransaction.setBorder(null);
        cancelTransaction.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/ctClicked.png"))); // NOI18N
        cancelTransaction.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cancelTransactionMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                cancelTransactionMouseExited(evt);
            }
        });
        cancelTransaction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelTransactionActionPerformed(evt);
            }
        });

        cartButtons.setLayer(addItem, javax.swing.JLayeredPane.DEFAULT_LAYER);
        cartButtons.setLayer(removeItem, javax.swing.JLayeredPane.DEFAULT_LAYER);
        cartButtons.setLayer(payButton, javax.swing.JLayeredPane.DEFAULT_LAYER);
        cartButtons.setLayer(customerReward, javax.swing.JLayeredPane.DEFAULT_LAYER);
        cartButtons.setLayer(couponDiscount, javax.swing.JLayeredPane.DEFAULT_LAYER);
        cartButtons.setLayer(cancelTransaction, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout cartButtonsLayout = new javax.swing.GroupLayout(cartButtons);
        cartButtons.setLayout(cartButtonsLayout);
        cartButtonsLayout.setHorizontalGroup(
            cartButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cartButtonsLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(cartButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cartButtonsLayout.createSequentialGroup()
                        .addComponent(addItem, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(removeItem, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(cartButtonsLayout.createSequentialGroup()
                        .addComponent(couponDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(customerReward, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(cancelTransaction, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addComponent(payButton, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        cartButtonsLayout.setVerticalGroup(
            cartButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cartButtonsLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(cartButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cartButtonsLayout.createSequentialGroup()
                        .addGroup(cartButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(addItem, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(removeItem, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(cartButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(couponDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(customerReward, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cancelTransaction, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(payButton))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        paymentOptions.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        payByCashbtn.setBackground(new java.awt.Color(247, 241, 227));
        payByCashbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/cashBtn_2.png"))); // NOI18N
        payByCashbtn.setBorder(null);
        payByCashbtn.setBorderPainted(false);
        payByCashbtn.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/cashClicked.png"))); // NOI18N
        payByCashbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                payByCashbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                payByCashbtnMouseExited(evt);
            }
        });
        payByCashbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payByCashbtnActionPerformed(evt);
            }
        });
        paymentOptions.add(payByCashbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 0, 190, 130));

        payByCardbtn.setBackground(new java.awt.Color(247, 241, 227));
        payByCardbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/cardBtn.png"))); // NOI18N
        payByCardbtn.setBorder(null);
        payByCardbtn.setBorderPainted(false);
        payByCardbtn.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/cardClicked.png"))); // NOI18N
        payByCardbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                payByCardbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                payByCardbtnMouseExited(evt);
            }
        });
        payByCardbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payByCardbtnActionPerformed(evt);
            }
        });
        paymentOptions.add(payByCardbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 0, 190, 130));

        paymentBack.setBackground(new java.awt.Color(247, 241, 227));
        paymentBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/bckbtn.png"))); // NOI18N
        paymentBack.setBorder(null);
        paymentBack.setBorderPainted(false);
        paymentBack.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/bckClicked.png"))); // NOI18N
        paymentBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                paymentBackMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                paymentBackMouseExited(evt);
            }
        });
        paymentBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paymentBackActionPerformed(evt);
            }
        });
        paymentOptions.add(paymentBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 160, 130));

        cashPaymentPnl.setBackground(new java.awt.Color(71, 71, 71));
        cashPaymentPnl.setForeground(new java.awt.Color(71, 71, 71));
        cashPaymentPnl.setOpaque(true);
        cashPaymentPnl.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        cashPaymentPnl.add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 50, 90, 10));
        cashPaymentPnl.add(jSeparator6, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 110, 90, 10));
        cashPaymentPnl.add(jSeparator7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, 90, 10));

        cashAmountText.setBackground(new java.awt.Color(71, 71, 71));
        cashAmountText.setFont(new java.awt.Font("Maiandra GD", 1, 12)); // NOI18N
        cashAmountText.setForeground(new java.awt.Color(255, 255, 255));
        cashAmountText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cashAmountText.setBorder(null);
        cashAmountText.setCaretColor(new java.awt.Color(255, 255, 255));
        cashAmountText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cashAmountTextActionPerformed(evt);
            }
        });
        cashPaymentPnl.add(cashAmountText, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 30, 90, -1));

        jLabel21.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(247, 241, 227));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("Amount Received");
        cashPaymentPnl.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, -1, -1));

        jLabel23.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(247, 241, 227));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("Change");
        cashPaymentPnl.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 70, -1, -1));

        cashChangeText.setBackground(new java.awt.Color(71, 71, 71));
        cashChangeText.setFont(new java.awt.Font("Maiandra GD", 1, 12)); // NOI18N
        cashChangeText.setForeground(new java.awt.Color(255, 255, 255));
        cashChangeText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cashChangeText.setBorder(null);
        cashChangeText.setCaretColor(new java.awt.Color(255, 255, 255));
        cashChangeText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cashChangeTextActionPerformed(evt);
            }
        });
        cashPaymentPnl.add(cashChangeText, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 90, -1));

        jLabel26.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(247, 241, 227));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("Total Cost");
        cashPaymentPnl.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, -1, -1));

        cashCostText.setBackground(new java.awt.Color(71, 71, 71));
        cashCostText.setFont(new java.awt.Font("Maiandra GD", 1, 12)); // NOI18N
        cashCostText.setForeground(new java.awt.Color(255, 255, 255));
        cashCostText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cashCostText.setBorder(null);
        cashCostText.setCaretColor(new java.awt.Color(255, 255, 255));
        cashCostText.setFocusable(false);
        cashCostText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cashCostTextActionPerformed(evt);
            }
        });
        cashPaymentPnl.add(cashCostText, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 80, -1));

        cashBackbtn.setBackground(new java.awt.Color(71, 71, 71));
        cashBackbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/bckcashBtn_1.png"))); // NOI18N
        cashBackbtn.setBorder(null);
        cashBackbtn.setBorderPainted(false);
        cashBackbtn.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/bckcashClicked.png"))); // NOI18N
        cashBackbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cashBackbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                cashBackbtnMouseExited(evt);
            }
        });
        cashBackbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cashBackbtnActionPerformed(evt);
            }
        });
        cashPaymentPnl.add(cashBackbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 0, 120, 120));

        cashConfirmbtn.setBackground(new java.awt.Color(71, 71, 71));
        cashConfirmbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/cnfrmBtn.png"))); // NOI18N
        cashConfirmbtn.setBorder(null);
        cashConfirmbtn.setBorderPainted(false);
        cashConfirmbtn.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/cnfrmClicked.png"))); // NOI18N
        cashConfirmbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cashConfirmbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                cashConfirmbtnMouseExited(evt);
            }
        });
        cashConfirmbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cashConfirmbtnActionPerformed(evt);
            }
        });
        cashPaymentPnl.add(cashConfirmbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 0, 120, 120));

        javax.swing.GroupLayout cartPnlLayout = new javax.swing.GroupLayout(cartPnl);
        cartPnl.setLayout(cartPnlLayout);
        cartPnlLayout.setHorizontalGroup(
            cartPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cartPnlLayout.createSequentialGroup()
                .addGroup(cartPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cartButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(paymentOptions, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(cartPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneCart, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE))
            .addGroup(cartPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(cashPaymentPnl))
        );
        cartPnlLayout.setVerticalGroup(
            cartPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cartPnlLayout.createSequentialGroup()
                .addGap(0, 340, Short.MAX_VALUE)
                .addGroup(cartPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cartButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(paymentOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(cartPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(cartPnlLayout.createSequentialGroup()
                    .addComponent(scrollPaneCart, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 129, Short.MAX_VALUE)))
            .addGroup(cartPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cartPnlLayout.createSequentialGroup()
                    .addContainerGap(344, Short.MAX_VALUE)
                    .addComponent(cashPaymentPnl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        add(cartPnl, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 300, 530, 470));

        cardPaymentPnl.setBackground(new java.awt.Color(71, 71, 71));
        cardPaymentPnl.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        cardPaymentPnl.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 150, 10));
        cardPaymentPnl.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 90, 150, 10));
        cardPaymentPnl.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 160, 50, 10));
        cardPaymentPnl.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 160, 30, 10));

        jLabel3.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(247, 241, 227));
        jLabel3.setText("Cardholder Name");
        cardPaymentPnl.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 50, -1, -1));

        cardholderText.setBackground(new java.awt.Color(71, 71, 71));
        cardholderText.setFont(new java.awt.Font("Maiandra GD", 0, 12)); // NOI18N
        cardholderText.setForeground(new java.awt.Color(247, 241, 227));
        cardholderText.setBorder(null);
        cardholderText.setCaretColor(new java.awt.Color(247, 241, 227));
        cardholderText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cardholderTextKeyReleased(evt);
            }
        });
        cardPaymentPnl.add(cardholderText, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 70, 150, 20));

        jLabel4.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(247, 241, 227));
        jLabel4.setText("Card Number");
        cardPaymentPnl.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 50, -1, -1));

        cardnumberText.setBackground(new java.awt.Color(71, 71, 71));
        cardnumberText.setFont(new java.awt.Font("Maiandra GD", 0, 12)); // NOI18N
        cardnumberText.setForeground(new java.awt.Color(247, 241, 227));
        cardnumberText.setBorder(null);
        cardnumberText.setCaretColor(new java.awt.Color(247, 241, 227));
        cardnumberText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cardnumberTextKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cardnumberTextKeyReleased(evt);
            }
        });
        cardPaymentPnl.add(cardnumberText, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 70, 150, -1));

        jLabel22.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(247, 241, 227));
        jLabel22.setText("Expiration Date (MM/YY)");
        cardPaymentPnl.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 120, -1, -1));

        expiryText.setBackground(new java.awt.Color(71, 71, 71));
        expiryText.setFont(new java.awt.Font("Maiandra GD", 0, 12)); // NOI18N
        expiryText.setForeground(new java.awt.Color(247, 241, 227));
        expiryText.setBorder(null);
        expiryText.setCaretColor(new java.awt.Color(247, 241, 227));
        expiryText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expiryTextActionPerformed(evt);
            }
        });
        expiryText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                expiryTextKeyReleased(evt);
            }
        });
        cardPaymentPnl.add(expiryText, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 140, 50, -1));

        jLabel24.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(247, 241, 227));
        jLabel24.setText("CVC");
        cardPaymentPnl.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 120, -1, -1));

        cvcText.setBackground(new java.awt.Color(71, 71, 71));
        cvcText.setFont(new java.awt.Font("Maiandra GD", 0, 12)); // NOI18N
        cvcText.setForeground(new java.awt.Color(247, 241, 227));
        cvcText.setBorder(null);
        cvcText.setCaretColor(new java.awt.Color(247, 241, 227));
        cardPaymentPnl.add(cvcText, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 140, 30, -1));

        cardConfirmbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/confirmBtn.png"))); // NOI18N
        cardConfirmbtn.setBorder(null);
        cardConfirmbtn.setBorderPainted(false);
        cardConfirmbtn.setContentAreaFilled(false);
        cardConfirmbtn.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/confirmClicked.png"))); // NOI18N
        cardConfirmbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cardConfirmbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                cardConfirmbtnMouseExited(evt);
            }
        });
        cardConfirmbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cardConfirmbtnActionPerformed(evt);
            }
        });
        cardPaymentPnl.add(cardConfirmbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 390, 270, 80));

        imgVisaLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/icons8_visa_96px.png"))); // NOI18N
        cardPaymentPnl.add(imgVisaLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 300, 100, 70));

        lblExpiry.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblExpiry.setForeground(new java.awt.Color(247, 241, 227));
        lblExpiry.setText("MM/YY");
        cardPaymentPnl.add(lblExpiry, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 330, -1, 30));

        lblMonthYear.setFont(new java.awt.Font("Maiandra GD", 0, 7)); // NOI18N
        lblMonthYear.setForeground(new java.awt.Color(247, 241, 227));
        lblMonthYear.setText("Month/Year");
        cardPaymentPnl.add(lblMonthYear, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 330, -1, 10));

        lblThru.setFont(new java.awt.Font("Maiandra GD", 0, 6)); // NOI18N
        lblThru.setForeground(new java.awt.Color(247, 241, 227));
        lblThru.setText("THRU");
        cardPaymentPnl.add(lblThru, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 350, -1, -1));

        lblCardholderName.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        lblCardholderName.setForeground(new java.awt.Color(247, 241, 227));
        lblCardholderName.setText("Cardholder Name");
        cardPaymentPnl.add(lblCardholderName, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 350, 150, 30));

        lblValid.setFont(new java.awt.Font("Maiandra GD", 0, 6)); // NOI18N
        lblValid.setForeground(new java.awt.Color(247, 241, 227));
        lblValid.setText("VALID");
        cardPaymentPnl.add(lblValid, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 340, -1, -1));

        lblCardNumber.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblCardNumber.setForeground(new java.awt.Color(247, 241, 227));
        lblCardNumber.setText("1234 1234 1234 1234");
        cardPaymentPnl.add(lblCardNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 300, 190, 30));

        lblSmallNum.setFont(new java.awt.Font("Maiandra GD", 0, 10)); // NOI18N
        lblSmallNum.setForeground(new java.awt.Color(247, 241, 227));
        lblSmallNum.setText("1234");
        cardPaymentPnl.add(lblSmallNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 330, -1, 10));

        imgCreditCard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/creditCardpng.png"))); // NOI18N
        imgCreditCard.setOpaque(true);
        cardPaymentPnl.add(imgCreditCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 190, 320, -1));

        cardBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/backcardBtn.png"))); // NOI18N
        cardBack.setBorder(null);
        cardBack.setBorderPainted(false);
        cardBack.setContentAreaFilled(false);
        cardBack.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/backcardClicked.png"))); // NOI18N
        cardBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cardBackMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                cardBackMouseExited(evt);
            }
        });
        cardBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cardBackActionPerformed(evt);
            }
        });
        cardPaymentPnl.add(cardBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 390, 260, 80));

        add(cardPaymentPnl, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 300, 530, 470));

        scanItemSerial.setBackground(new java.awt.Color(247, 241, 227));
        scanItemSerial.setForeground(new java.awt.Color(247, 241, 227));
        scanItemSerial.setBorder(null);
        scanItemSerial.setCaretColor(new java.awt.Color(247, 241, 227));
        scanItemSerial.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        scanItemSerial.setDisabledTextColor(new java.awt.Color(247, 241, 227));
        scanItemSerial.setSelectedTextColor(new java.awt.Color(247, 241, 227));
        scanItemSerial.setSelectionColor(new java.awt.Color(247, 241, 227));
        scanItemSerial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                scanItemSerialKeyTyped(evt);
            }
        });
        add(scanItemSerial, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 110, 80, -1));

        lblSubtotal.setForeground(new java.awt.Color(0, 255, 0));
        lblSubtotal.setText("Subtotal:");
        add(lblSubtotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 160, 250, 30));

        lblDiscount.setForeground(new java.awt.Color(0, 255, 0));
        lblDiscount.setText("Discount:");
        add(lblDiscount, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 190, 160, 30));

        lblTax.setForeground(new java.awt.Color(0, 255, 0));
        lblTax.setText("Tax:");
        add(lblTax, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 220, 150, 30));

        totalText.setBackground(new java.awt.Color(0, 0, 0));
        totalText.setForeground(new java.awt.Color(0, 255, 0));
        totalText.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        totalText.setText("$0.00");
        add(totalText, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 250, 200, 40));

        discountText.setBackground(new java.awt.Color(0, 0, 0));
        discountText.setForeground(new java.awt.Color(0, 255, 0));
        discountText.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        discountText.setText("$0.00");
        add(discountText, new org.netbeans.lib.awtextra.AbsoluteConstraints(1121, 190, 230, 30));

        subtotalText.setBackground(new java.awt.Color(0, 0, 0));
        subtotalText.setForeground(new java.awt.Color(0, 255, 0));
        subtotalText.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        subtotalText.setText("$0.00");
        add(subtotalText, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 160, 220, 30));

        taxText.setBackground(new java.awt.Color(0, 0, 0));
        taxText.setForeground(new java.awt.Color(0, 255, 0));
        taxText.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        taxText.setText("$0.00");
        add(taxText, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 220, 200, 30));

        lblTotal.setForeground(new java.awt.Color(0, 255, 0));
        lblTotal.setText("Total:");
        add(lblTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 250, 140, 40));

        lblCashDisplayBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/black.png"))); // NOI18N
        lblCashDisplayBackground.setText("jLabel21");
        add(lblCashDisplayBackground, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 150, 510, 150));

        lblCashDisplayBorder.setBackground(new java.awt.Color(71, 71, 71));
        lblCashDisplayBorder.setOpaque(true);
        add(lblCashDisplayBorder, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 140, 530, 160));

        lblUserType.setFont(new java.awt.Font("Maiandra GD", 1, 16)); // NOI18N
        lblUserType.setForeground(new java.awt.Color(247, 241, 227));
        lblUserType.setText("Admin");
        add(lblUserType, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 90, 90, 30));

        lblUserName.setFont(new java.awt.Font("Maiandra GD", 1, 18)); // NOI18N
        lblUserName.setForeground(new java.awt.Color(226, 123, 71));
        lblUserName.setText("John Dough Pizza");
        add(lblUserName, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 50, 170, 40));

        lblUserPicture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/icons8_user_male_circle_52px.png"))); // NOI18N
        add(lblUserPicture, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 60, 80));

        lblUserBackground.setBackground(new java.awt.Color(71, 71, 71));
        lblUserBackground.setForeground(new java.awt.Color(242, 107, 56));
        lblUserBackground.setToolTipText("");
        lblUserBackground.setOpaque(true);
        add(lblUserBackground, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 250, 140));

        lblWeekDay.setFont(new java.awt.Font("Maiandra GD", 1, 28)); // NOI18N
        lblWeekDay.setForeground(new java.awt.Color(226, 123, 71));
        lblWeekDay.setText("DAY");
        add(lblWeekDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(1110, 20, 80, 50));

        lblTime.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        lblTime.setForeground(new java.awt.Color(226, 123, 71));
        lblTime.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblTime.setText("Time");
        add(lblTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(1180, 40, 70, -1));

        lblDate.setFont(new java.awt.Font("Maiandra GD", 1, 14)); // NOI18N
        lblDate.setForeground(new java.awt.Color(226, 123, 71));
        lblDate.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblDate.setText("Date");
        add(lblDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(1180, 60, 70, 20));

        lblMonitorScreen.setBackground(new java.awt.Color(71, 71, 71));
        lblMonitorScreen.setOpaque(true);
        add(lblMonitorScreen, new org.netbeans.lib.awtextra.AbsoluteConstraints(1105, 20, 155, 70));

        lblMonitorImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/icons8_monitor_200px.png"))); // NOI18N
        add(lblMonitorImg, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, -10, 190, 170));

        lblStoreName.setBackground(new java.awt.Color(71, 71, 71));
        lblStoreName.setFont(new java.awt.Font("Imprint MT Shadow", 1, 48)); // NOI18N
        lblStoreName.setForeground(new java.awt.Color(71, 71, 71));
        lblStoreName.setText("Tony's General Store");
        add(lblStoreName, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 30, 670, 90));

        lblBanner.setBackground(new java.awt.Color(247, 241, 227));
        lblBanner.setOpaque(true);
        add(lblBanner, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 0, 1130, 140));
    }// </editor-fold>//GEN-END:initComponents
    
    private void showDate() {
        Date d = new Date();
        SimpleDateFormat s = new SimpleDateFormat("MM/dd/yyyy");
        lblDate.setText(s.format(d));
    }
    
    private void showTime() {
        Date d = new Date();
        SimpleDateFormat s = new SimpleDateFormat("hh:mm a");
        lblTime.setText(s.format(d));
    }
    
    private void showDay() {
        Calendar now = Calendar.getInstance();
        
        String[] strDays = new String[] { "SUN", "MON", "TUE", "WED", "THU",
                                            "FRI", "SAT" };
 
        lblWeekDay.setText(strDays[now.get(Calendar.DAY_OF_WEEK) - 1]);
    }
    
    private void btnCustomerActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnCustomerActionPerformed
        cardLayout.show(panelCards, "customerPanel");
        btnInventory.setBackground(MENU_BTN_BG_COLOR);
        btnCustomer.setBackground(MENU_BTN_SEL_COLOR);
    }//GEN-LAST:event_btnCustomerActionPerformed

    private void couponDiscountActionPerformed(ActionEvent evt) {//GEN-FIRST:event_couponDiscountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_couponDiscountActionPerformed


    private void customerCreateActionPerformed(ActionEvent evt) {//GEN-FIRST:event_customerCreateActionPerformed
        UI_Tools.fixWindow(dialogCreateCustomer);
        dialogCreateCustomer.setVisible(true);
        
        disableMainPanel();
    }//GEN-LAST:event_customerCreateActionPerformed

    private void customerDeleteActionPerformed(ActionEvent evt) {//GEN-FIRST:event_customerDeleteActionPerformed
        // TODO add your handling code here:
        int selectedRow = custTable.getSelectedRow();
        if (selectedRow < 0)
            return;
        
        DefaultTableModel model = (DefaultTableModel)custTable.getModel();
        int custid = (int)custTable.getModel().getValueAt(
                custTable.convertRowIndexToModel(selectedRow), 0);
        
        pos.customers.remove(custid);
        updateCustomerTableData();
    }//GEN-LAST:event_customerDeleteActionPerformed

    private void dialogCreateCustomerWindowClosed(WindowEvent evt) {//GEN-FIRST:event_dialogCreateCustomerWindowClosed
        createFirstName.setText("");
        createLastName.setText("");
        createEmail.setText("");
        createPhone.setText("");
        createStreet.setText("");
        createCity.setText("");
        createState.setText("");
        createZip.setText("");
        
        enableMainPanel();
    }//GEN-LAST:event_dialogCreateCustomerWindowClosed

    private void createSubmitActionPerformed(ActionEvent evt) {//GEN-FIRST:event_createSubmitActionPerformed
        
        String firstName = createFirstName.getText();
        String lastName = createLastName.getText();
        String phoneNumber = createPhone.getText();
        String email = createEmail.getText();
        String street = createStreet.getText();
        String city = createCity.getText();
        String state = createState.getText();
        int zip = 0;
        if (UI_Tools.isInteger(createZip.getText()))
            zip = Integer.valueOf(createZip.getText());
        
        if (pos.customers.create(firstName, lastName, phoneNumber
                                , email, street, city, state, zip) < 1) {
            System.out.println("FAILED TO CREATE CUSTOMER!");
            return;
        }
        updateCustomerTableData();
        dialogCreateCustomer.dispose();
    }//GEN-LAST:event_createSubmitActionPerformed

    private void customerEditActionPerformed(ActionEvent evt) {//GEN-FIRST:event_customerEditActionPerformed
        int selectedRow = custTable.getSelectedRow();
        if (selectedRow < 0)
            return;
        
        int customerID = (int)custTable.getModel().getValueAt(
                custTable.convertRowIndexToModel(selectedRow), 0);
        Customer cust = pos.customers.load(customerID);
        if (cust == null) {
            System.out.println(customerID);
            System.out.println("Customer not found in database!");
            return;
        }
        
        editFirstName.setText(cust.getFirstName());
        editLastName.setText(cust.getLastName());
        editPhone.setText(cust.getPhone());
        editEmail.setText(cust.getEmail());
        editStreet.setText(cust.getStreet());
        editCity.setText(cust.getCity());
        editState.setText(cust.getState());
        editZip.setText(cust.getZipcode() + "");
        
        UI_Tools.fixWindow(dialogEditCustomer);
        dialogEditCustomer.setVisible(true);
        
        disableMainPanel();
    }//GEN-LAST:event_customerEditActionPerformed

    private void btnMainMenuMouseEntered(MouseEvent evt) {//GEN-FIRST:event_btnMainMenuMouseEntered
        btnMainMenu.setBackground(MAIN_BG_COLOR);
        btnMainMenu.setFont(btnMainMenu.getFont().deriveFont((float)22));
    }//GEN-LAST:event_btnMainMenuMouseEntered

    private void btnMainMenuMouseExited(MouseEvent evt) {//GEN-FIRST:event_btnMainMenuMouseExited
        btnMainMenu.setBackground(MENU_BTN_BG_COLOR);
        btnMainMenu.setFont(btnMainMenu.getFont().deriveFont((float)21));
    }//GEN-LAST:event_btnMainMenuMouseExited

    private void btnCustomerMouseEntered(MouseEvent evt) {//GEN-FIRST:event_btnCustomerMouseEntered
        if (btnCustomer.getBackground().equals(MENU_BTN_BG_COLOR)) {
            btnCustomer.setBackground(MAIN_BG_COLOR);
            btnCustomer.setFont(btnMainMenu.getFont().deriveFont((float)22));
        }
    }//GEN-LAST:event_btnCustomerMouseEntered

    private void btnCustomerMouseExited(MouseEvent evt) {//GEN-FIRST:event_btnCustomerMouseExited
        if (btnCustomer.getBackground().equals(MAIN_BG_COLOR)) {
            btnCustomer.setBackground(MENU_BTN_BG_COLOR);
            btnCustomer.setFont(btnMainMenu.getFont().deriveFont((float)21));
        }
    }//GEN-LAST:event_btnCustomerMouseExited

    private void btnLogoutButtonMouseEntered(MouseEvent evt) {//GEN-FIRST:event_btnLogoutButtonMouseEntered
        btnLogoutButton.setBackground(MAIN_BG_COLOR);
        btnLogoutButton.setFont(btnMainMenu.getFont().deriveFont((float)22));
    }//GEN-LAST:event_btnLogoutButtonMouseEntered

    private void btnLogoutButtonMouseExited(MouseEvent evt) {//GEN-FIRST:event_btnLogoutButtonMouseExited
        btnLogoutButton.setBackground(MENU_BTN_BG_COLOR);
        btnLogoutButton.setFont(btnMainMenu.getFont().deriveFont((float)21));
    }//GEN-LAST:event_btnLogoutButtonMouseExited

    private void btnExitButtonMouseEntered(MouseEvent evt) {//GEN-FIRST:event_btnExitButtonMouseEntered
        btnExitButton.setBackground(MAIN_BG_COLOR);
        btnExitButton.setFont(btnMainMenu.getFont().deriveFont((float)22));
    }//GEN-LAST:event_btnExitButtonMouseEntered

    private void btnExitButtonMouseExited(MouseEvent evt) {//GEN-FIRST:event_btnExitButtonMouseExited
        btnExitButton.setBackground(MENU_BTN_BG_COLOR);
        btnExitButton.setFont(btnMainMenu.getFont().deriveFont((float)21));
    }//GEN-LAST:event_btnExitButtonMouseExited

    private void btnInventoryActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnInventoryActionPerformed
        cardLayout.show(panelCards, "inventoryPanel");
        btnInventory.setBackground(MENU_BTN_SEL_COLOR);
        btnCustomer.setBackground(MENU_BTN_BG_COLOR);
    }//GEN-LAST:event_btnInventoryActionPerformed

    private void btnInventoryMouseEntered(MouseEvent evt) {//GEN-FIRST:event_btnInventoryMouseEntered
        if (btnInventory.getBackground().equals(MENU_BTN_BG_COLOR)) {
            btnInventory.setBackground(MAIN_BG_COLOR);
            btnInventory.setFont(btnMainMenu.getFont().deriveFont((float)22));
        }
    }//GEN-LAST:event_btnInventoryMouseEntered

    private void btnInventoryMouseExited(MouseEvent evt) {//GEN-FIRST:event_btnInventoryMouseExited
        if (btnInventory.getBackground().equals(MAIN_BG_COLOR)) {
            btnInventory.setBackground(MENU_BTN_BG_COLOR);
            btnInventory.setFont(btnMainMenu.getFont().deriveFont((float)21));
        }
    }//GEN-LAST:event_btnInventoryMouseExited

    private void textInventorySearchFocusGained(FocusEvent evt) {//GEN-FIRST:event_textInventorySearchFocusGained
        if (textInventorySearch.getText().equals("Search..."))
            textInventorySearch.setText("");
        else
            textInventorySearch.selectAll();
    }//GEN-LAST:event_textInventorySearchFocusGained

    private void textInventorySearchFocusLost(FocusEvent evt) {//GEN-FIRST:event_textInventorySearchFocusLost
        if (textInventorySearch.getText().equals(""))
            textInventorySearch.setText("Search...");
    }//GEN-LAST:event_textInventorySearchFocusLost

    private void payButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_payButtonActionPerformed
        if (cart == null)
            return;
        cartButtons.setVisible(false);
        paymentOptions.setVisible(true);
    }//GEN-LAST:event_payButtonActionPerformed

    private void textCustomerSearchFocusGained(FocusEvent evt) {//GEN-FIRST:event_textCustomerSearchFocusGained
        if (textCustomerSearch.getText().equals("Search..."))
            textCustomerSearch.setText("");
        else
            textCustomerSearch.selectAll();
    }//GEN-LAST:event_textCustomerSearchFocusGained

    private void textCustomerSearchFocusLost(FocusEvent evt) {//GEN-FIRST:event_textCustomerSearchFocusLost
        if (textCustomerSearch.getText().equals(""))
            textCustomerSearch.setText("Search...");
    }//GEN-LAST:event_textCustomerSearchFocusLost

    private void textInventorySearchKeyReleased(KeyEvent evt) {//GEN-FIRST:event_textInventorySearchKeyReleased
        DefaultTableModel table = (DefaultTableModel)invTable.getModel();
        String search = textInventorySearch.getText();
        TableRowSorter<DefaultTableModel> tr = new TableRowSorter<>(table);
        invTable.setRowSorter(tr);
        
        switch (invSearchBy.getSelectedItem().toString().toLowerCase()) {
            case "serial no.":
                tr.setRowFilter(RowFilter.regexFilter("(?i)" + search, 0));
                break;
            case "name":
                tr.setRowFilter(RowFilter.regexFilter("(?i)" + search, 1));
                break;
            case "manufacturer":
                tr.setRowFilter(RowFilter.regexFilter("(?i)" + search, 2));
                break;
            case "price":
                tr.setRowFilter(RowFilter.regexFilter("(?i)" + search, 3));
                break;
            default:
                tr.setRowFilter(RowFilter.regexFilter("(?i)" + search));
                break;
        }
    }//GEN-LAST:event_textInventorySearchKeyReleased

    private void textCustomerSearchKeyReleased(KeyEvent evt) {//GEN-FIRST:event_textCustomerSearchKeyReleased
        String search = textCustomerSearch.getText();
        if (search.equals("")) {
            custTable.setVisible(false);
            return;
        }
        
        DefaultTableModel table = (DefaultTableModel)custTable.getModel();
        TableRowSorter<DefaultTableModel> tr = new TableRowSorter<>(table);
        custTable.setRowSorter(tr);
        
        switch (custSearchBy.getSelectedItem().toString().toLowerCase()) {
            case "customer id":
                tr.setRowFilter(RowFilter.regexFilter("(?i)" + search, 0));
                break;
            case "first name":
                tr.setRowFilter(RowFilter.regexFilter("(?i)" + search, 1));
                break;
            case "last name":
                tr.setRowFilter(RowFilter.regexFilter("(?i)" + search, 2));
                break;
            case "phone":
                tr.setRowFilter(RowFilter.regexFilter("(?i)" + search, 3));
                break;
            case "email":
                tr.setRowFilter(RowFilter.regexFilter("(?i)" + search, 4));
                break;
            default:
                tr.setRowFilter(RowFilter.regexFilter("(?i)" + search));
                break;
        }
        custTable.setVisible(true);
    }//GEN-LAST:event_textCustomerSearchKeyReleased

    private void addItemActionPerformed(ActionEvent evt) {//GEN-FIRST:event_addItemActionPerformed
        int selectedRow = invTable.getSelectedRow();
        if (selectedRow < 0)
            return;
        
        String serial = invTable.getModel().getValueAt(
                invTable.convertRowIndexToModel(selectedRow), 0).toString();
        
        if (cart == null)
            cart = pos.customers.createCart(0, pos.getEmployeeID());
        
        pos.customers.addCartItem(cart, serial, 1);
        updateInvoiceTableData();
    }//GEN-LAST:event_addItemActionPerformed

    
    private void scanItemSerialKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_scanItemSerialKeyTyped
        if (evt.getKeyChar() == java.awt.event.KeyEvent.VK_ENTER) {
            InventoryItem item = pos.inventory.load(scanItemSerial.getText());
            if (item != null) {
                if (cart == null)
                    cart = pos.customers.createCart(0, pos.getEmployeeID());
                if (!pos.customers.addCartItem(cart, scanItemSerial.getText(), 1))
                    System.out.println("Failed to add item!");
                updateInvoiceTableData();
            }
            else
                System.out.println("Item doesn't exist!");
            
            scanItemSerial.setText("");
        }
    }//GEN-LAST:event_scanItemSerialKeyTyped

    private void removeItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeItemActionPerformed
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow < 0)
            return;
        
        String serial = invoiceTable.getModel().getValueAt(
                invoiceTable.convertRowIndexToModel(selectedRow), 0).toString();
        pos.customers.removeCartItem(cart, serial, 1);
//        if(model.getValueAt(selectedRow, 2).equals(1)){
//            pos.customers.removeCartItem(cart, serial, 1);
//        }
//        else{
//            UI_Tools.fixWindow(dialogRemoveItem);
//            dialogRemoveItem.setVisible(true);
//            
//        }
       
        updateInvoiceTableData();
    }//GEN-LAST:event_removeItemActionPerformed

    private void payByCashbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_payByCashbtnActionPerformed
        cartButtons.setVisible(false);
        paymentOptions.setVisible(false);
        cashPaymentPnl.setVisible(true);
        cashCostText.setText("$" + cart.getTotal().setScale(2, RoundingMode.CEILING).toString());
    }//GEN-LAST:event_payByCashbtnActionPerformed

    private void payByCardbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_payByCardbtnActionPerformed
        cardPaymentPnl.setVisible(true);
        cartPnl.setVisible(false);
        //paymentOptions.setVisible(false);
    }//GEN-LAST:event_payByCardbtnActionPerformed

    private void customerRewardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customerRewardActionPerformed
//        dialogCustomerId.setVisible(true);
//        
//        disableMainPanel();
    }//GEN-LAST:event_customerRewardActionPerformed

    private void cardConfirmbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cardConfirmbtnActionPerformed
        String CardPayment = "Card";
        pos.customers.createTransaction(cart, CardPayment);
        
        cardPaymentPnl.setVisible(false);
        cartPnl.setVisible(true);
        paymentOptions.setVisible(false);
        cartButtons.setVisible(true);
        cardholderText.setText("");
        cardnumberText.setText("");
        expiryText.setText("");
        cvcText.setText("");
        lblCardNumber.setText("1234 1234 1234 1234");
        lblSmallNum.setText("1234");
        lblExpiry.setText("MM/YY");
        lblCardholderName.setText("Cardholder Name");
        cart = null;
        updateInvoiceTableData();
    }//GEN-LAST:event_cardConfirmbtnActionPerformed

    private void cardholderTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cardholderTextKeyReleased
        lblCardholderName.setText(cardholderText.getText());
    }//GEN-LAST:event_cardholderTextKeyReleased

    private void cardnumberTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cardnumberTextKeyReleased
        int a = 4;
        if (evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            lblCardNumber.setText(cardnumberText.getText());
            if (cardnumberText.getText().length() < 4) {
                a = cardnumberText.getText().length();
            }
    
            lblSmallNum.setText(cardnumberText.getText().substring(0, a));
        }
        else {
            if (cardnumberText.getText().startsWith("4")) {
                imgVisaLogo.setVisible(true);
            }
            
            else if (cardnumberText.getText().startsWith("5")) {
                imgVisaLogo.setVisible(false);
            }
            
            if (cardnumberText.getText().length() == 4) {
                cardnumberText.setText(cardnumberText.getText() + " ");
            }
        
            else if (cardnumberText.getText().length() == 9) {
                cardnumberText.setText(cardnumberText.getText() + " ");
            }
        
            else if (cardnumberText.getText().length() == 14) {
                cardnumberText.setText(cardnumberText.getText() + " ");
            }
        
            lblCardNumber.setText(cardnumberText.getText());
        
            if (cardnumberText.getText().length() < 4) {
                a = cardnumberText.getText().length();
            }
    
            lblSmallNum.setText(cardnumberText.getText().substring(0, a));
        }
    }//GEN-LAST:event_cardnumberTextKeyReleased

    private void expiryTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_expiryTextKeyReleased
        int a = 2;
        if (evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            lblExpiry.setText(expiryText.getText());
            if (expiryText.getText().length() < 2)
                a = expiryText.getText().length();
        }
        else {
            if (expiryText.getText().length() == 2)
                expiryText.setText(expiryText.getText() + "/");
            lblExpiry.setText(expiryText.getText());
        }
    }//GEN-LAST:event_expiryTextKeyReleased

    private void cardnumberTextKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cardnumberTextKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_cardnumberTextKeyPressed

    private void paymentBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paymentBackActionPerformed
        cartButtons.setVisible(true);
        paymentOptions.setVisible(false);
    }//GEN-LAST:event_paymentBackActionPerformed

    private void cardBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cardBackActionPerformed
        cardPaymentPnl.setVisible(false);
        cartPnl.setVisible(true);
        paymentOptions.setVisible(true);
        cartButtons.setVisible(false);
    }//GEN-LAST:event_cardBackActionPerformed

    private void cashBackbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cashBackbtnActionPerformed
        cashPaymentPnl.setVisible(false);
        cartPnl.setVisible(true);
        paymentOptions.setVisible(true);
        cartButtons.setVisible(false);
    }//GEN-LAST:event_cashBackbtnActionPerformed

    private void cashConfirmbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cashConfirmbtnActionPerformed
        
        String CashPayment = "Cash";
        pos.customers.createTransaction(cart, CashPayment);
        
        cashPaymentPnl.setVisible(false);
        cartPnl.setVisible(true);
        paymentOptions.setVisible(false);
        cartButtons.setVisible(true);
        cashCostText.setText("");
        cashAmountText.setText("");
        cashChangeText.setText("");
        cart = null;
        updateInvoiceTableData();
    }//GEN-LAST:event_cashConfirmbtnActionPerformed

    private void cashCostTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cashCostTextActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_cashCostTextActionPerformed

    private void cashChangeTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cashChangeTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cashChangeTextActionPerformed

    private void cashAmountTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cashAmountTextActionPerformed
        BigDecimal amtRcv = new BigDecimal(cashAmountText.getText());
        BigDecimal chg;
        MathContext mc = new MathContext(4);
        chg = amtRcv.subtract(cart.getTotal(), mc);
        chg = chg.setScale(2, RoundingMode.CEILING);
        cashChangeText.setText("$"+chg.toString());
         
    }//GEN-LAST:event_cashAmountTextActionPerformed

    private void cancelTransactionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelTransactionActionPerformed
        if (cart == null)
            return;
        
        pos.customers.closeCart(cart.getID());
        cart = null;
        cashCostText.setText("");
        cashAmountText.setText("");
        cashChangeText.setText("");
        cardholderText.setText("");
        cardnumberText.setText("");
        expiryText.setText("");
        cvcText.setText("");
        lblCardNumber.setText("1234 1234 1234 1234");
        lblSmallNum.setText("1234");
        lblExpiry.setText("MM/YY");
        lblCardholderName.setText("Cardholder Name");
        updateInvoiceTableData();
    }//GEN-LAST:event_cancelTransactionActionPerformed

    private void dlgOkayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dlgOkayActionPerformed
        pos.customers.load(Integer.getInteger(custIdText.getText()));
        dialogCustomerId.dispose();
   
    }//GEN-LAST:event_dlgOkayActionPerformed

    private void btnOkayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkayActionPerformed
        // TODO add your handling code here:
        

    }//GEN-LAST:event_btnOkayActionPerformed

    private void btnOkayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnOkayMouseClicked
        // TODO add your handling code here:
//        int selectedRow = invoiceTable.getSelectedRow();
//        if (selectedRow < 0)
//            return;
//        DefaultTableModel model = (DefaultTableModel)invoiceTable.getModel();
//        String serial = model.getValueAt(selectedRow, 0).toString();
//
//        pos.customers.removeCartItem(cart, serial, Integer.parseInt(removeItems.getText()));
//        
//       
//        updateInvoiceTableData();
//        dialogRemoveItem.dispose();
        dialogRemoveItem.setVisible(false);
    }//GEN-LAST:event_btnOkayMouseClicked

    private void removeItemsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeItemsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_removeItemsActionPerformed

    private void addItemMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addItemMouseEntered
        // TODO add your handling code here:
        addItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/addHover.png")));
    }//GEN-LAST:event_addItemMouseEntered

    private void addItemMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addItemMouseExited
        // TODO add your handling code here:
        addItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/addBtn.png")));
    }//GEN-LAST:event_addItemMouseExited

    private void removeItemMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_removeItemMouseEntered
        // TODO add your handling code here:
        removeItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/removeHover.png"))); // NOI18N

    }//GEN-LAST:event_removeItemMouseEntered

    private void removeItemMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_removeItemMouseExited
        // TODO add your handling code here:
        removeItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/removeBtn.png"))); // NOI18N

    }//GEN-LAST:event_removeItemMouseExited

    private void couponDiscountMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_couponDiscountMouseEntered
        // TODO add your handling code here:
        couponDiscount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/cpnHover.png"))); // NOI18N

    }//GEN-LAST:event_couponDiscountMouseEntered

    private void couponDiscountMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_couponDiscountMouseExited
        // TODO add your handling code here:
        couponDiscount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/cpnBtn.png"))); // NOI18N

    }//GEN-LAST:event_couponDiscountMouseExited

    private void customerRewardMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_customerRewardMouseEntered
        // TODO add your handling code here:
        customerReward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/crHover.png"))); // NOI18N

    }//GEN-LAST:event_customerRewardMouseEntered

    private void customerRewardMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_customerRewardMouseExited
        // TODO add your handling code here:
        customerReward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/crBtn.png"))); // NOI18N

    }//GEN-LAST:event_customerRewardMouseExited

    private void cancelTransactionMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelTransactionMouseEntered
        // TODO add your handling code here:
        cancelTransaction.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/ctHover.png"))); // NOI18N

    }//GEN-LAST:event_cancelTransactionMouseEntered

    private void cancelTransactionMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelTransactionMouseExited
        // TODO add your handling code here:
        cancelTransaction.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/ctBtn.png"))); // NOI18N

    }//GEN-LAST:event_cancelTransactionMouseExited

    private void payButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_payButtonMouseEntered
        // TODO add your handling code here:
        payButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/payHover.png"))); // NOI18N

    }//GEN-LAST:event_payButtonMouseEntered

    private void payButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_payButtonMouseExited
        // TODO add your handling code here:
        payButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/payBtn.png"))); // NOI18N

    }//GEN-LAST:event_payButtonMouseExited

    private void paymentBackMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_paymentBackMouseEntered
        // TODO add your handling code here:
        paymentBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/bckHover.png"))); // NOI18N

    }//GEN-LAST:event_paymentBackMouseEntered

    private void paymentBackMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_paymentBackMouseExited
        // TODO add your handling code here:
        paymentBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/bckbtn.png"))); // NOI18N

    }//GEN-LAST:event_paymentBackMouseExited

    private void payByCashbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_payByCashbtnMouseEntered
        // TODO add your handling code here:
        payByCashbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/cashHover.png"))); // NOI18N

    }//GEN-LAST:event_payByCashbtnMouseEntered

    private void payByCashbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_payByCashbtnMouseExited
        // TODO add your handling code here:
        payByCashbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/cashBtn_2.png"))); // NOI18N

    }//GEN-LAST:event_payByCashbtnMouseExited

    private void payByCardbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_payByCardbtnMouseEntered
        // TODO add your handling code here:
        payByCardbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/cardHover.png"))); // NOI18N

    }//GEN-LAST:event_payByCardbtnMouseEntered

    private void payByCardbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_payByCardbtnMouseExited
        // TODO add your handling code here:
        payByCardbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/cardBtn.png"))); // NOI18N

    }//GEN-LAST:event_payByCardbtnMouseExited

    private void cashBackbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cashBackbtnMouseEntered
        // TODO add your handling code here:
        cashBackbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/bckcashHover.png"))); // NOI18N

    }//GEN-LAST:event_cashBackbtnMouseEntered

    private void cashBackbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cashBackbtnMouseExited
        // TODO add your handling code here:
        cashBackbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/bckcashBtn_1.png"))); // NOI18N

    }//GEN-LAST:event_cashBackbtnMouseExited

    private void cashConfirmbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cashConfirmbtnMouseEntered
        // TODO add your handling code here:
        cashConfirmbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/cnfrmHover.png"))); // NOI18N

    }//GEN-LAST:event_cashConfirmbtnMouseEntered

    private void cashConfirmbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cashConfirmbtnMouseExited
        // TODO add your handling code here:
        cashConfirmbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/cnfrmBtn.png"))); // NOI18N

    }//GEN-LAST:event_cashConfirmbtnMouseExited

    private void cardConfirmbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cardConfirmbtnMouseEntered
        // TODO add your handling code here:
        cardConfirmbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/confirmHover.png"))); // NOI18N

    }//GEN-LAST:event_cardConfirmbtnMouseEntered

    private void cardConfirmbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cardConfirmbtnMouseExited
        // TODO add your handling code here:
        cardConfirmbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/confirmBtn.png"))); // NOI18N

    }//GEN-LAST:event_cardConfirmbtnMouseExited

    private void cardBackMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cardBackMouseEntered
        // TODO add your handling code here:
        cardBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/backcardHover.png"))); // NOI18N

    }//GEN-LAST:event_cardBackMouseEntered

    private void cardBackMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cardBackMouseExited
        // TODO add your handling code here:
        cardBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/backcardBtn.png"))); // NOI18N

    }//GEN-LAST:event_cardBackMouseExited

    private void customerCreateMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_customerCreateMouseEntered
        // TODO add your handling code here:
        customerCreate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/createAccHover.png"))); // NOI18N

    }//GEN-LAST:event_customerCreateMouseEntered

    private void customerCreateMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_customerCreateMouseExited
        // TODO add your handling code here:
        customerCreate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/createAccBtn.png"))); // NOI18N

    }//GEN-LAST:event_customerCreateMouseExited

    private void customerEditMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_customerEditMouseEntered
        // TODO add your handling code here:
        customerEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/editHover.png"))); // NOI18N

    }//GEN-LAST:event_customerEditMouseEntered

    private void customerEditMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_customerEditMouseExited
        // TODO add your handling code here:
        customerEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/editBtn.png"))); // NOI18N

    }//GEN-LAST:event_customerEditMouseExited

    private void customerDeleteMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_customerDeleteMouseEntered
        // TODO add your handling code here:
        customerDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/dltHover.png"))); // NOI18N

    }//GEN-LAST:event_customerDeleteMouseEntered

    private void customerDeleteMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_customerDeleteMouseExited
        // TODO add your handling code here:
        customerDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/dltBtn.png"))); // NOI18N

    }//GEN-LAST:event_customerDeleteMouseExited

    private void expiryTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expiryTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_expiryTextActionPerformed

    private void dialogEditCustomerWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_dialogEditCustomerWindowClosed
        // TODO add your handling code here:
        editFirstName.setText("");
        editLastName.setText("");
        editEmail.setText("");
        editPhone.setText("");
        editStreet.setText("");
        editCity.setText("");
        editState.setText("");
        editZip.setText("");
        
        enableMainPanel();
    }//GEN-LAST:event_dialogEditCustomerWindowClosed

    private void createSubmitMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_createSubmitMouseEntered
        // TODO add your handling code here:
         createSubmit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/submitHover.png"))); // NOI18N

    }//GEN-LAST:event_createSubmitMouseEntered

    private void createSubmitMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_createSubmitMouseExited
        // TODO add your handling code here:
        createSubmit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/submitBtn.png"))); // NOI18N

    }//GEN-LAST:event_createSubmitMouseExited

    private void createSubmitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_createSubmitMouseClicked
        // TODO add your handling code here:
        createSubmit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/images/submitClicked.png"))); // NOI18N

    }//GEN-LAST:event_createSubmitMouseClicked

    private void editSubmitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editSubmitMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_editSubmitMouseClicked

    private void editSubmitMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editSubmitMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_editSubmitMouseEntered

    private void editSubmitMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editSubmitMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_editSubmitMouseExited

    private void editSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editSubmitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_editSubmitActionPerformed
    
    private void disableMainPanel() {
        UI_Tools.updatePanelComponents(this, false);
    }
    
    private void enableMainPanel() {
        UI_Tools.updatePanelComponents(this, true);
    }
    
    private void updateCustomerTableData() {
        DefaultTableModel model = (DefaultTableModel) custTable.getModel();
        model.setRowCount(0);
        Customer[] customerList = pos.customers.loadAll();
        for (Customer customer : customerList)
            model.addRow(new Object[] {
                customer.getID(), customer.getFirstName(), customer.getLastName()
                , customer.getPhone(), customer.getEmail()
            });
    }
    
    private void updateInventoryTableData() {
        DefaultTableModel model = (DefaultTableModel) invTable.getModel();
        model.setRowCount(0);
        InventoryItem[] inventoryList = pos.inventory.loadAll();
        for (InventoryItem inventory : inventoryList)
            model.addRow(new Object[] {
                inventory.getSerialNumber(), inventory.getModelName()
                , inventory.getManufacturer(), inventory.getListPrice().setScale(2, RoundingMode.CEILING)
            });
    }
    
    private void updateInvoiceTableData() {
        DefaultTableModel model = (DefaultTableModel) invoiceTable.getModel();
        
        model.setRowCount(0);
        if (cart == null) {
            subtotalText.setText("$0.00");
            taxText.setText("$0.00");
            totalText.setText("$0.00");
            return;
        }
        
        ArrayList<CartItem> cartItems = cart.getItems();
        cartItems.forEach((item) -> { 
            model.addRow(new Object[] {
                item.getSerialNumber(), item.getModelName(), item.getQuantity()
                , item.getListPrice().setScale(2, RoundingMode.CEILING)
            });
        });
        
        
        subtotalText.setText("$" + cart.getSubtotal().setScale(2, RoundingMode.CEILING).toString());
        taxText.setText("$" + ((cart.getSubtotal().multiply(cart.getTaxRate()))).setScale(2, RoundingMode.CEILING).toString());
        totalText.setText("$" + cart.getTotal().setScale(2, RoundingMode.CEILING).toString());
        
    }
    
    public class RoundJTextField extends JTextField {
        private Shape shape;
        public RoundJTextField(int size) {
            super(size);
            super.setOpaque(false); // As suggested by @AVD in comment.
        }
        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(getBackground());
            g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            super.paintComponent(g);
        }
        @Override
        protected void paintBorder(Graphics g) {
            g.setColor(getForeground());
            g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
        }
        @Override
        public boolean contains(int x, int y) {
            if (shape == null || !shape.getBounds().equals(getBounds())) {
                 shape = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            }
            return shape.contains(x, y);
        }
    }
    
    public final class LengthRestrictedDocument extends PlainDocument {

    private final int limit;

    public LengthRestrictedDocument(int limit) {
      this.limit = limit;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a)
        throws BadLocationException {
        if (str == null)
          return;

        if ((getLength() + str.length()) <= limit) {
          super.insertString(offs, str, a);
        }
      }
    }
    
    public void close() {
        updateTimer.stop();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addItem;
    public javax.swing.JButton btnCustomer;
    public javax.swing.JButton btnExitButton;
    private javax.swing.JButton btnInventory;
    public javax.swing.JButton btnLogoutButton;
    public javax.swing.JButton btnMainMenu;
    private javax.swing.JButton btnOkay;
    private javax.swing.JButton cancelTransaction;
    private javax.swing.JButton cardBack;
    private javax.swing.JButton cardConfirmbtn;
    private javax.swing.JPanel cardPaymentPnl;
    private javax.swing.JTextField cardholderText;
    private javax.swing.JTextField cardnumberText;
    private javax.swing.JLayeredPane cartButtons;
    private javax.swing.JPanel cartPnl;
    private javax.swing.JTextField cashAmountText;
    private javax.swing.JButton cashBackbtn;
    private javax.swing.JTextField cashChangeText;
    private javax.swing.JButton cashConfirmbtn;
    private javax.swing.JTextField cashCostText;
    private javax.swing.JLayeredPane cashPaymentPnl;
    public javax.swing.JButton couponDiscount;
    private javax.swing.JTextField createCity;
    private javax.swing.JTextField createEmail;
    private javax.swing.JTextField createFirstName;
    private javax.swing.JTextField createLastName;
    private javax.swing.JTextField createPhone;
    private javax.swing.JTextField createState;
    private javax.swing.JTextField createStreet;
    private javax.swing.JButton createSubmit;
    private javax.swing.JTextField createZip;
    private javax.swing.JTextField custIdText;
    public javax.swing.JComboBox<String> custSearchBy;
    public javax.swing.JTable custTable;
    private javax.swing.JButton customerCreate;
    private javax.swing.JButton customerDelete;
    private javax.swing.JButton customerEdit;
    public javax.swing.JButton customerReward;
    private javax.swing.JPanel customerTab;
    private javax.swing.JTextField cvcText;
    private javax.swing.JDialog dialogCreateCustomer;
    private javax.swing.JDialog dialogCustomerId;
    private javax.swing.JDialog dialogEditCustomer;
    private javax.swing.JDialog dialogRemoveItem;
    private javax.swing.JLabel discountText;
    private javax.swing.JButton dlgOkay;
    private javax.swing.JTextField editCity;
    private javax.swing.JTextField editEmail;
    private javax.swing.JTextField editFirstName;
    private javax.swing.JTextField editLastName;
    private javax.swing.JTextField editPhone;
    private javax.swing.JTextField editState;
    private javax.swing.JTextField editStreet;
    private javax.swing.JButton editSubmit;
    private javax.swing.JTextField editZip;
    private javax.swing.JTextField expiryText;
    private javax.swing.JLabel imgCreditCard;
    private javax.swing.JLabel imgVisaLogo;
    public javax.swing.JComboBox<String> invSearchBy;
    public javax.swing.JTable invTable;
    private javax.swing.JPanel inventoryTab;
    public javax.swing.JTable invoiceTable;
    private javax.swing.JScrollPane itemTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator15;
    private javax.swing.JSeparator jSeparator16;
    private javax.swing.JSeparator jSeparator17;
    private javax.swing.JSeparator jSeparator18;
    private javax.swing.JSeparator jSeparator19;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator20;
    private javax.swing.JSeparator jSeparator21;
    private javax.swing.JSeparator jSeparator22;
    private javax.swing.JSeparator jSeparator23;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JLabel lblBanner;
    private javax.swing.JLabel lblCardNumber;
    private javax.swing.JLabel lblCardholderName;
    private javax.swing.JLabel lblCashDisplayBackground;
    private javax.swing.JLabel lblCashDisplayBorder;
    private javax.swing.JLabel lblCustId;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblDiscount;
    private javax.swing.JLabel lblExpiry;
    private javax.swing.JLabel lblInventorySearchIcon;
    private javax.swing.JLabel lblInventorySearchIcon1;
    private javax.swing.JLabel lblInventoryTitle;
    private javax.swing.JLabel lblInventoryTitle1;
    private javax.swing.JLabel lblMonitorImg;
    private javax.swing.JLabel lblMonitorScreen;
    private javax.swing.JLabel lblMonthYear;
    private javax.swing.JLabel lblSmallNum;
    private javax.swing.JLabel lblStoreName;
    private javax.swing.JLabel lblSubtotal;
    private javax.swing.JLabel lblTax;
    private javax.swing.JLabel lblThru;
    private javax.swing.JLabel lblTime;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblUserBackground;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JLabel lblUserPicture;
    private javax.swing.JLabel lblUserType;
    private javax.swing.JLabel lblValid;
    private javax.swing.JLabel lblWeekDay;
    private javax.swing.JPanel panelCards;
    private javax.swing.JPanel panelMenu;
    public javax.swing.JButton payButton;
    private javax.swing.JButton payByCardbtn;
    private javax.swing.JButton payByCashbtn;
    private javax.swing.JButton paymentBack;
    private javax.swing.JLayeredPane paymentOptions;
    public javax.swing.JButton removeItem;
    public javax.swing.JTextField removeItems;
    private javax.swing.JTextField scanItemSerial;
    private javax.swing.JScrollPane scrollPaneCart;
    private javax.swing.JScrollPane scrollPaneCustomers;
    private javax.swing.JSplitPane splitPaneMain;
    private javax.swing.JLabel subtotalText;
    private javax.swing.JLabel taxText;
    private javax.swing.JTextField textCustomerSearch;
    private javax.swing.JTextField textInventorySearch;
    private javax.swing.JLabel totalText;
    // End of variables declaration//GEN-END:variables
}
