/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PoS;

import PoS.classes.User;
import PoS.database.DBEmployees;
import PoS.database.DBUsers;
import PoS.database.DBInventory;
import PoS.database.DBCustomerTransactions;
import PoS.database.DBVendorTransactions;

/**
 * Represents a Point of Sale system and a user that's logged in.
 * @author Dustin Gritman
 */
public class PoS {
    public final static short SALES_LEVEL     = 1;
    public final static short INVENTORY_LEVEL = 2;
    public final static short ADMIN_LEVEL     = 5;
    
    public final DBEmployees            employees;
    public final DBUsers                users;
    public final DBInventory            inventory;
    public final DBCustomerTransactions customers;
    public final DBVendorTransactions   vendors;
    private User user;
    
    /**
     * Initialize a Point of Sale system object.
     */
    public PoS() {
        employees = new DBEmployees();
        users     = new DBUsers();
        inventory = new DBInventory();
        customers = new DBCustomerTransactions(inventory);
        vendors   = new DBVendorTransactions(inventory);
        
        user = null;
    }
    
    /**
     * Log the user in and store the required information.
     * @param username The username of the user.
     * @param password The password of the user.
     * @return true when user successfully logged in, otherwise false.
     */
    public boolean login(String username, char[] password) {
        if (user == null)
            user = users.load(username, password);
        
        return user != null;
    }
    
    /**
     * Log the current user out.
     */
    public void logout() {
        user = null;
    }
    
    /**
     * Gets the userID of the current user.
     * @return The logged in users user id.
     */
    public int getUserID() {
        if (user == null)
            return -1;
        
        return user.getID();
    }
    
    /**
     * Gets the employeeID of the current user.
     * @return The logged in users employee id.
     */
    public int getEmployeeID() {
        if (user == null)
            return -1;
        
        return user.getEmployeeID();
    }
    
    /**
     * Get the access level of the current user.
     * @return The logged in users access level.
     */
    public short getAccessLevel() {
        if (user == null)
            return -1;
        
        return user.getAccessLevel();
    }
}
