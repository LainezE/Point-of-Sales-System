/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import PoS.PoS;
import PoS.database.DBConn;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Runs the PoS program with a UI.
 * @author 
 */
public class PoSProgram {
    /**
     * Runs the program.
     * @param args Possible program arguments.
     */
    public static void main(String[] args) {
        // Change database connection to Dustin's home
        DBConn.setDBInfo("npgame.ddns.net:3306", "pos", "posuser", "S57bvzV1akg3!");
        PoSProgram run = new PoSProgram();
    }
    
    
    private final PoS pos;
    private final JFrame frame;
    
    /**
     * Creates a new PoS program with a UI.
     */
    public PoSProgram() {
        pos = new PoS();
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("PoS System");
        frame.setUndecorated(true);
        frame.setResizable(false);
        
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;    
                }
            }
        } catch(ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            
        }
        
        LoadScreen loadScreen = new LoadScreen();
        loadScreen.setVisible(true);
        try {
            for (int i = 0; i <= 100; i++) {
                Thread.sleep(15);
                LoadScreen.progressBar.setValue(i);
            }
        } catch(InterruptedException e) {
            
        }
        // Dispose of the loading screen since we're done with it
        loadScreen.dispose();
        
        // Start the program with the login screen
        displayPosLogin();
        
        // Start the programs display
        frame.setVisible(true);
    }
    
    /**
     * Attempt to log a user in with provided credentials.
     */
    private void onLoginSubmit(String username, char[] password) {
        pos.login(username, password);
        
        switch (pos.getAccessLevel()) {
            case PoS.SALES_LEVEL:
                displaySalesMenu();
                break;
            case PoS.INVENTORY_LEVEL:
                displayAdminInventory();
                break;
            case PoS.ADMIN_LEVEL:
                displayAdminMenu();
                break;
            default:
                System.out.println("Login failed!");
                break;
        }
    }
    
    /**
     * Log a user out so another user can use the system.
     */
    private void onLogoutSubmit() {
        pos.logout();
        displayPosLogin();
    }
    
    /**
     * Exit and destroy the frame.
     */
    private void onExitSubmit() {
        frame.dispose();
    }
    
    /**
     * Display the Login Panel.
     */
    private void displayPosLogin() {
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.loginButton.addActionListener((ActionEvent evt) -> {
            onLoginSubmit(loginScreen.getUsername(), loginScreen.getPassword());
        });
        loginScreen.exitButton.addActionListener((ActionEvent evt) -> {
            onExitSubmit();
        });
        
        UI_Tools.displayNewPage(frame, loginScreen);
    }
    
    /**
     * Display the Sale System Main Menu.
     */
    private void displaySalesMenu() {
        SaleScreen saleScreen = new SaleScreen(pos);
        saleScreen.btnMainMenu.addActionListener((ActionEvent evt) -> {
            saleScreen.close();
            displayAdminMenu();
        });
        saleScreen.btnLogoutButton.addActionListener((ActionEvent evt) -> {
            saleScreen.close();
            onLogoutSubmit();
        });
        saleScreen.btnExitButton.addActionListener((ActionEvent evt) -> {
            saleScreen.close();
            onExitSubmit();
        });
        
        UI_Tools.displayNewPage(frame, saleScreen);
    }
    
    /**
     * Display the Admin System Main Menu.
     */
    private void displayAdminMenu() {
        if (pos.getAccessLevel() != PoS.ADMIN_LEVEL)
            return;
        
        AdminMenu adminMenu = new AdminMenu();
        adminMenu.userBtn.addActionListener((ActionEvent evt) -> {
            displayAdminUsers();
        });
        adminMenu.employeeBtn.addActionListener((ActionEvent evt) -> {
            displayAdminEmployees();
        });
        adminMenu.inventoryBtn.addActionListener((ActionEvent evt) -> {
            displayAdminInventory();
        });
        /*
        adminMenu.reportsBtn.addActionListener((ActionEvent evt) -> {
            displayAdminReports();
        });
        */
        adminMenu.posBtn.addActionListener((ActionEvent evt) -> {
            displaySalesMenu();
        });
        adminMenu.exitBtn.addActionListener((ActionEvent evt) -> {
            onExitSubmit();
        });
        adminMenu.logoutBtn.addActionListener((ActionEvent evt) -> {
            onLogoutSubmit();
        });
        
        UI_Tools.displayNewPage(frame, adminMenu);
    }
    
    /**
     * Display the Admin System Users Page.
     */
    private void displayAdminUsers() {
        if (pos.getAccessLevel() != PoS.ADMIN_LEVEL)
            return;
        
        UserPage userPanel = new UserPage(pos);
        userPanel.btnBack.addActionListener((ActionEvent evt) -> {
            displayAdminMenu();
        });
        userPanel.btnExit.addActionListener((ActionEvent evt) -> {
            onExitSubmit();
        });
        
        UI_Tools.displayNewPage(frame, userPanel);
    }
    
    // Display the Admin System Inventory Page.
    private void displayAdminInventory() {
        if (pos.getAccessLevel() != PoS.ADMIN_LEVEL
                && pos.getAccessLevel() != PoS.INVENTORY_LEVEL)
            return;
        
        InventoryPage inventoryPanel = new InventoryPage(pos);
        inventoryPanel.backBtn.addActionListener((ActionEvent evt) -> {
            if (pos.getAccessLevel() == PoS.ADMIN_LEVEL)
                displayAdminMenu();
            else
                onLogoutSubmit();
        });
        inventoryPanel.exitBtn.addActionListener((ActionEvent evt) -> {
            onExitSubmit();
        });
        
        UI_Tools.displayNewPage(frame, inventoryPanel);
    }
    
    /**
     * Display the Admin System Employees Page.
     */
    private void displayAdminEmployees() {
        if (pos.getAccessLevel() != PoS.ADMIN_LEVEL)
            return;
        
        EmployeePage employeePanel = new EmployeePage(pos);
        employeePanel.btnBack.addActionListener((ActionEvent evt) -> {
            displayAdminMenu();
        });
        employeePanel.btnExit.addActionListener((ActionEvent evt) -> {
            onExitSubmit();
        });
        
        UI_Tools.displayNewPage(frame, employeePanel);
    }
    
    /**
     * Display the Admin System Reports Page.
     */
    private void displayAdminReports(){
        if (pos.getAccessLevel() != PoS.ADMIN_LEVEL)
            return;
        
        ReportsPage reportsPage = new ReportsPage(pos);
        reportsPage.backBtn.addActionListener((ActionEvent evt) -> {
            displayAdminMenu();
        });
        
        UI_Tools.displayNewPage(frame, reportsPage);
    }
}
