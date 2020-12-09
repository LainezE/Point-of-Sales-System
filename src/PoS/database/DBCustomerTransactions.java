/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PoS.database;

import PoS.classes.CartItem;
import PoS.classes.CustomerCart;
import PoS.classes.CustomerTransaction;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The database interactions for customer transactions.
 * @author Dustin Gritman
 */
public class DBCustomerTransactions extends DBCustomerCarts {
    /**
     * Initialize a DBCustomerTransactions object to be used to interact with all
     * customer data.
     * @param dbInventory A database inventory object to use for inventory access.
     */
    public DBCustomerTransactions(DBInventory dbInventory) {
        super(dbInventory);
    }
    
    /**
     * Creates a customer transaction in the database.
     * @param cart The customers cart to create the transaction from.
     * @param paymentType The payment type used.
     * @return A customer transaction object that was made, otherwise null if failed.
     */
    public CustomerTransaction createTransaction(CustomerCart cart, String paymentType) {
        ArrayList<CartItem> cartItems = cart.getItems();
        // Remove the items from the inventory
        for (int i=0; i < cartItems.size(); i++) {
            // If any failed to be removed, revert previous changes and return null
            if (!inventory.updateStock(cartItems.get(i).getSerialNumber(), -cartItems.get(i).getQuantity())) {
                for (int j=0; j < i; j++)
                    inventory.updateStock(cartItems.get(j).getSerialNumber(), cartItems.get(j).getQuantity());
                return null;
            }
        }
        
        CustomerTransaction transaction = null;
        
        String query = "INSERT INTO Transaction_Customer(Customer_ID, Employee_ID"
                + ", Subtotal, Total, Payment_Type) VALUES (NULLIF(0, ?), ?, ?, ?, ?)";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query
                        , PreparedStatement.RETURN_GENERATED_KEYS);) {
            prepStmt.setInt       (1, cart.getCustomerID());
            prepStmt.setInt       (2, cart.getEmployeeID());
            prepStmt.setBigDecimal(3, cart.getSubtotal());
            prepStmt.setBigDecimal(4, cart.getTotal());
            prepStmt.setString    (5, paymentType);
            
            prepStmt.execute();
            ResultSet rs = prepStmt.getGeneratedKeys();
            if (rs.next()) {
                int transactionID = rs.getInt(1);
                cart.getItems().forEach((item) -> {
                    addTransactionItem(transactionID, item);
                });
                transaction = new CustomerTransaction(transactionID, cart, paymentType);
                super.closeCart(cart.getID());
            }
        } catch(SQLException e) {
            System.out.println(e);
        }
        // If transaction failed, return stock to inventory
        if (transaction == null)
            cart.getItems().forEach((item) -> {
                inventory.updateStock(item.getSerialNumber(), item.getQuantity());
            });
        
        return transaction;
    }
    
    /**
     * Adds an item to the transaction in the database.
     * @param transactionID The id of the transaction to add the item to.
     * @param item The item to be added.
     * @return True if the item was added, otherwise false if it failed.
     */
    protected boolean addTransactionItem(int transactionID, CartItem item) {
        boolean itemAdded = false;
        
        String query = "INSERT INTO Transaction_Customer_Items(Transaction_ID"
                + ", Serial_Number, Quantity) VALUES (?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE quantity=(quantity + ?)";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query);) {
            prepStmt.setInt   (1, transactionID);
            prepStmt.setString(2, item.getSerialNumber());
            prepStmt.setInt   (3, item.getQuantity());
            prepStmt.setInt   (4, item.getQuantity());
            
            itemAdded = (prepStmt.executeUpdate() > 0);
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return itemAdded;
    }
    
    /**
     * Load a customer transaction.
     * @param transactionID The id of the transaction to load.
     * @return A customer transaction object if found, otherwise null.
     */
    public CustomerTransaction loadTransaction(int transactionID) {
        CustomerTransaction transaction = null;
        
        String query = "SELECT Customer_ID, Employee_ID, Subtotal, Total"
                + ", Payment_Type FROM Transaction_Customer WHERE Transaction_ID=?";
        try (Connection con = DBConn.createConnection();) {
            PreparedStatement prepStmt = con.prepareStatement(query);
            prepStmt.setInt(1, transactionID);
            
            // Verify a cart with this id is open
            ResultSet rs = prepStmt.executeQuery();
            if (rs.next()) {
                int customerID = rs.getInt("Customer_ID");
                int employeeID = rs.getInt("Employee_ID");
                BigDecimal subtotal = rs.getBigDecimal("Subtotal");
                BigDecimal total = rs.getBigDecimal("Total");
                String paymentType = rs.getString("Payment_Type");
                rs.close();
                prepStmt.close();
                
                query = "SELECT Quantity, Inventory.Serial_Number, Manufacturer"
                        + ", Model_Number, Model_Name, Description, Supplier_Link"
                        + ", Cost, List_Price, MSRP, Stock, Base_Stock, ReOrder_Amount"
                        + " FROM Transaction_Customer_Items JOIN Inventory"
                        + " ON Transaction_Customer_Items.Serial_Number = Inventory.Serial_Number"
                        + " WHERE Transaction_ID=?";
                prepStmt = con.prepareStatement(query);
                prepStmt.setInt(1, transactionID);
                rs = prepStmt.executeQuery();
                
                ArrayList<CartItem> cartItems = new ArrayList<>();
                while (rs.next())
                    cartItems.add(new CartItem(rs.getInt("Quantity")
                            , rs.getString("Serial_Number"), rs.getString("Manufacturer")
                            , rs.getInt("Model_Number"), rs.getString("Model_Name")
                            , rs.getString("Description"), rs.getString("Supplier_Link")
                            , rs.getBigDecimal("Cost"), rs.getBigDecimal("List_Price")
                            , rs.getBigDecimal("MSRP"), rs.getInt("Stock")
                            , rs.getInt("Base_Stock"), rs.getInt("ReOrder_Amount"))
                    );
                
                transaction = new CustomerTransaction(transactionID, customerID
                        , employeeID, subtotal, total, paymentType, cartItems);
            }
            
            rs.close();
            prepStmt.close();
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return transaction;
    }
    
    /**
     * Search for customer transactions by either customer id and/or employee id
     * and/or within a time frame.
     * @param customerID The id of the customer to search transactions for. (0 to ignore)
     * @param employeeID The id of the employee to search transactions for. (0 to ignore)
     * @param startDate The date that the transaction must have been made on/after.
     * @param endDate The date that the transaction must have been made on/before.
     * @return An array of customer transactions matching the search, otherwise null.
     */
    public CustomerTransaction[] searchTransactions(int customerID, int employeeID
            , Date startDate, Date endDate) {
        CustomerTransaction[] transactions = null;
        
        ArrayList<String> qWhere = new ArrayList<>();
        ArrayList<Object> setStmts = new ArrayList<>();
        if (customerID > -1) {
            qWhere.add("Customer_ID=NULLIF(0, ?)");
            setStmts.add(customerID);
        }
        if (employeeID > 0) {
            qWhere.add("Employee_ID=?");
            setStmts.add(employeeID);
        }
        if (startDate != null) {
            qWhere.add("Transaction_DateTime >= ?");
            setStmts.add(startDate);
        }
        if (endDate != null) {
            qWhere.add("Transaction_DateTime <= ?");
            setStmts.add(endDate);
        }
        
        if (qWhere.size() < 1)
            return null;
        
        String query = "SELECT Transaction_ID FROM Transaction_Customer"
                + " WHERE " + qWhere.get(0);
        for (int qwIndex=0; qwIndex < qWhere.size(); qwIndex++)
            query += " AND " + qWhere.get(qwIndex);

        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query);) {
            short setIndex = 1;
            for (Object setStmt : setStmts)
                prepStmt.setObject(setIndex++, setStmt);
            
            ResultSet rs = prepStmt.executeQuery();
            if (rs.last()) {
                transactions = new CustomerTransaction[rs.getRow()];
                rs.beforeFirst();
            }
            
            int index = 0;
            while (rs.next())
                transactions[index++] = loadTransaction(rs.getInt("Transaction_ID"));
            
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return transactions;
    }
}
