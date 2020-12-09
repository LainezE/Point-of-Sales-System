/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI.test;

import PoS.PoS;
import PoS.database.DBConn;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

/**
 * Runs the PoS program with a UI.
 * @author Dustin Gritman
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
    
    public PoSProgram() {
        pos = new PoS();
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("PoS System");
        frame.setUndecorated(true);
        
        // Start the program with the login screen
        displayLogin();
        frame.setVisible(true);
    }
    
    private void displayLogin() {
        LoginPanel loginPanel = new LoginPanel();
        loginPanel.btnSubmit.addActionListener((ActionEvent evt) -> {
            onLoginSubmit(loginPanel.getUsername(), loginPanel.getPassword());
        });
        loginPanel.btnExit.addActionListener((ActionEvent evt) -> {
            onExitSubmit();
        });
        
        frame.getContentPane().removeAll();
        frame.add(loginPanel);
        fixFrame();
    }
    
    private void onLoginSubmit(String username, char[] password) {
        if (!pos.login(username, password))
            return;
        
        displayMenuPanel();
    }
    
    private void onLogoutSubmit() {
        pos.logout();
        displayLogin();
    }
    
    private void displayMenuPanel() {
        MenuPanel menuPanel = new MenuPanel();
        menuPanel.btnUsers.addActionListener((ActionEvent evt) -> {
            displayUserPanel();
        });
        menuPanel.btnEmployees.addActionListener((ActionEvent evt) -> {
            displayEmployeePanel();
        });
        menuPanel.btnLogout.addActionListener((ActionEvent evt) -> {
            onLogoutSubmit();
        });
        menuPanel.btnExit.addActionListener((ActionEvent evt) -> {
            onExitSubmit();
        });
        
        frame.getContentPane().removeAll();
        frame.add(menuPanel);
        fixFrame();
    }
    
    private void displayUserPanel() {
        UserPanel userPanel = new UserPanel(pos);
        userPanel.btnBack.addActionListener((ActionEvent evt) -> {
            displayMenuPanel();
        });
        userPanel.btnExit.addActionListener((ActionEvent evt) -> {
            onExitSubmit();
        });
        
        frame.getContentPane().removeAll();
        frame.add(userPanel);
        fixFrame();
    }
    
    private void displayEmployeePanel() {
        EmployeePanel employeePanel = new EmployeePanel(pos);
        employeePanel.btnBack.addActionListener((ActionEvent evt) -> {
            displayMenuPanel();
        });
        employeePanel.btnExit.addActionListener((ActionEvent evt) -> {
            onExitSubmit();
        });
        
        frame.getContentPane().removeAll();
        frame.add(employeePanel);
        fixFrame();
    }
    
    private void fixFrame() {
        frame.pack();

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }
    
    private void onExitSubmit() {
        frame.dispose();
    }
}
