/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PoS;

import java.sql.Date;
import PoS.classes.Employee;
import PoS.classes.User;
import PoS.classes.InventoryItem;
import PoS.classes.CustomerCart;
import PoS.classes.CustomerTransaction;

/**
 * Used for testing the PoS class.
 * @author Dustin Gritman
 */
public class PoSTester {
    public static PoS pos = new PoS();
    
    public static void main(String[] args) {
        /** PoS user login test **/
        char[] password = {'a','d','m','i','n'};
        if (pos.login("admin", password))
            System.out.printf("Login successful!  UserID: %s\n", pos.getUserID());
        
        //pos.employees.create("Anthony", "Denizard", "Manager", "1234567890", "denizara@newpaltz.edu", "1 Hawk Drive", "New Paltz", "NY", 12561);
        
        //loadEmployeesTest();
        //loadUsersTest();
        //loadInventoryTest();
        
        //createCustomerCartTest(0, pos.getEmployeeID());
        //addCustomerCartItemTest(4, 1315059, 4);
        //addCustomerCartItemTest(4, 7192503, 2);
        //addCustomerCartItemTest(4, 5882479, 1);
        //removeCustomerCartItemTest(6, 5882479, 1);
        //loadCustomerCartTest(4);
        //closeCustomerCartTest(6);
        //createCustomerTransactionTest(4, "Cash");
        //loadCustomerTransactionsTest(3, Date.valueOf("2019-10-25"), Date.valueOf("2019-10-27"));
        //loadInventoryItemTest(1315059);
        //updateInventoryItemStockTest(1315059, 462);
        //loadInventoryItemTest(1315059);
        
        /** PoS user logout test **/
        pos.logout();
    }
    
    public static void createCustomerCartTest(int customerID, int employeeID) {
        System.out.println("------Create Customer Cart Test------");
        CustomerCart cart = pos.customers.createCart(customerID, employeeID);
        if (cart == null)
            System.out.println("Failed!");
        else
            System.out.printf("CreatedCartID: %d\n", cart.getID());
        System.out.println("--------------------------------------");
    }
    
    public static void createCustomerTransactionTest(int cartID, String paymentType) {
        System.out.println("------Create Customer Transaction Test------");
        CustomerCart cart = pos.customers.loadCart(cartID);
        CustomerTransaction transaction = pos.customers.createTransaction(cart, paymentType);
        if (transaction == null)
            System.out.println("Failed!");
        else
            System.out.printf("CreatedTransactionID: %d\n", transaction.getID());
        System.out.println("---------------------------------------------");
    }
    
    public static void closeCustomerCartTest(int cartID) {
        System.out.println("------Close Customer Cart Test------");
        if (pos.customers.closeCart(cartID))
            System.out.printf("Closed Cart!");
        else
            System.out.println("Failed!");
        System.out.println("--------------------------------------");
    }
    
    public static void addCustomerCartItemTest(int cartID, String serialNumber, int quantity) {
        System.out.println("-----Add Customer Cart Item Test-----");
        if (pos.customers.addCartItem(cartID, serialNumber, quantity))
            System.out.println("Item Added!");
        else
            System.out.println("Failed!");
        System.out.println("--------------------------------------");
    }
    
    public static void removeCustomerCartItemTest(int cartID, String serialNumber, int quantity) {
        System.out.println("----Remove Customer Cart Item Test----");
        if (pos.customers.removeCartItem(cartID, serialNumber, quantity))
            System.out.println("Item Removed!");
        else
            System.out.println("Failed!");
        System.out.println("--------------------------------------");
    }
    
    public static void loadCustomerCartTest(int cartID) {
        System.out.println("-------Load Customer Cart Test-------");
        CustomerCart cart = pos.customers.loadCart(cartID);
        if (cart == null)
            System.out.println("Failed!");
        else
            System.out.printf("CartID: %d\n", cart.getID());
        System.out.println("--------------------------------------");
    }
    
    public static void loadCustomerTransactionTest(int transactionID) {
        System.out.println("-------Load Customer Transaction Test-------");
        CustomerTransaction transaction = pos.customers.loadTransaction(transactionID);
        if (transaction == null)
            System.out.println("Failed!");
        else
            System.out.printf("TransactionID: %d\n", transaction.getID());
        System.out.println("--------------------------------------");
    }
    
    public static void loadCustomerTransactionsTest(int customerID, Date startDate, Date endDate) {
        System.out.println("-------Load Customer Transaction Test-------");
        CustomerTransaction[] transactions = pos.customers.searchTransactions((short) 0, customerID, startDate, endDate);
        if (transactions == null)
            System.out.println("Failed!");
        else {
            System.out.printf("-- TransactionsCount: %d\n", transactions.length);
            for (CustomerTransaction transaction : transactions) {
                System.out.printf("TrasnsactionID: %d\n", transaction.getID());
                transaction.getItems().forEach((item) -> {
                    System.out.printf("---- ItemSerial: %s\n---- ItemQuantity: %d\n"
                            , item.getSerialNumber(), item.getQuantity());
                });
            }
        }
        System.out.println("--------------------------------------");
    }
    
    public static void loadEmployeesTest() {
        System.out.println("Load Employees Test");
        Employee[] employees = pos.employees.loadAll();
        for (Employee employee : employees)
            System.out.printf("EmployeeID: %d | Name: %s %s | Phone: %s\n"
                    , employee.getID(), employee.getFirstName(), employee.getLastName()
                    , employee.getPhone()
            );
    }
    
    public static void loadUsersTest() {
        User[] users = pos.users.loadAll();
        for (User user : users)
            System.out.printf("UserID: %d | EmployeeID: %d | Username: %s | AccessLevel: %d\n"
                    , user.getID(), user.getEmployeeID(), user.getUsername(), user.getAccessLevel()
            );
    }
    
    public static void loadInventoryTest() {
        InventoryItem[] inventory = pos.inventory.loadAll();
        for (InventoryItem item : inventory)
            System.out.printf("Serial: %s | ModelName: %s | MSRP: $%s | Cost: $%s\n"
                    , item.getSerialNumber(), item.getModelName()
                    , item.getMSRP().toString(), item.getCost().toString()
            );
    }
    
    public static void loadInventoryItemTest(String serialNumber) {
        InventoryItem item = pos.inventory.load(serialNumber);
        if (item == null)
            System.out.println("Failed to load item!");
        else
            System.out.printf("Serial: %s | Name: %s | Stock: %d\n"
                    , item.getSerialNumber(), item.getModelName(), item.getStock());
        System.out.println("--------------------------------");
    }
    
    public static void updateInventoryItemStockTest(String serialNumber, int quantity) {
        System.out.println("Update Inventory Item Stock Test");
        if (pos.inventory.updateStock(serialNumber, quantity))
            System.out.println("Stock updated!");
        else
            System.out.println("Failed to update stock!");
        System.out.println("--------------------------------");
    }
}
