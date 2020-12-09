/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PoS.database;

import PoS.classes.CartItem;
import PoS.classes.VendorCart;
import PoS.classes.VendorTransaction;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 
 * @author Eli Lainez
 */
public class DBVendorTransactions extends DBVendorCarts {
    /**
     * Create a new DBVendorTransactions object.
     * @param inventory The DBInventory object to use for calling inventory.
     */
    public DBVendorTransactions(DBInventory inventory) {
        super(inventory);
    }
    
    /**
     * Create a vendor transaction.
     * @param cart The cart that the transaction was made using.
     * @param paymentType The payment type used for the transaction.
     * @return A new vendor transaction object if completed, otherwise null.
     */
    public VendorTransaction createTransaction(VendorCart cart, String paymentType) {
        VendorTransaction transaction = null;
        
        String query = "INSERT INTO Transaction_Vendor(Vendor_ID, Employee_ID"
                + ", Subtotal , Total, Payment_Type) Values(?, ?, ?, ?, ?)";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query
                        , PreparedStatement.RETURN_GENERATED_KEYS);) {
            prepStmt.setInt       (1, cart.getVendorID());
            prepStmt.setInt       (2, cart.getEmployeeID());
            prepStmt.setBigDecimal(3, cart.getSubtotal());
            prepStmt.setBigDecimal(4, cart.getTotal());
            prepStmt.setString    (5, paymentType);
            
            prepStmt.execute();
            ResultSet rs = prepStmt.getGeneratedKeys();
            if (rs.next()) {
                int transactionID = rs.getInt(1);
                for (CartItem item : cart.getItems())
                    addTransactionItem(transactionID, item);
                transaction = new VendorTransaction(transactionID, cart.getVendorID()
                        , cart.getEmployeeID(), cart.getSubtotal(), cart.getTotal()
                        , paymentType, cart.getItems());
                super.closeCart(cart.getID());
            }
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return transaction;
    }
    
    /**
     * Add an item to a transaction.
     * @param transactionID The id of the transaction to add the item to.
     * @param item The item to add to the transaction.
     * @return True if the item was added, otherwise false.
     */
    public boolean addTransactionItem(int transactionID, CartItem item) {
        boolean itemAdded = false;
        
        String query = "INSERT INTO Transaction_Vendor_Items(Transaction_ID"
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
     * Load a vendor transaction object by the transaction id.
     * @param transactionID The id of the vendor transaction to load.
     * @return The vendor transaction object found, otherwise null.
     */
    public VendorTransaction loadTransaction(int transactionID) {
        VendorTransaction transaction = null;
        
        String query = "SELECT Customer_ID, Employee_ID, Subtotal, Total"
                + ", Payment_Type FROM Transaction_Vendor WHERE Transaction_ID=?";
        try (Connection con = DBConn.createConnection();) {
            PreparedStatement prepStmt = con.prepareStatement(query);
            prepStmt.setInt(1, transactionID);
            
            // Verify a cart with this id is open
            ResultSet rs = prepStmt.executeQuery();
            if (rs.next()) {
                int vendorID = rs.getInt("Customer_ID");
                int employeeID = rs.getInt("Employee_ID");
                BigDecimal subtotal = rs.getBigDecimal("Subtotal");
                BigDecimal total = rs.getBigDecimal("Total");
                String paymentType = rs.getString("Payment_Type");
                rs.close();
                prepStmt.close();
                
                query = "SELECT Quantity, Inventory.Serial_Number, Manufacturer"
                        + ", Model_Number, Model_Name, Description, Supplier_Link"
                        + ", Cost, List_Price, MSRP, Stock, Base_Stock, ReOrder_Amount"
                        + " FROM Transaction_Vendor_Items JOIN Inventory"
                        + " ON Transaction_Vendor_Items.Serial_Number = Inventory.Serial_Number"
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
                
                transaction = new VendorTransaction(transactionID, vendorID
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
     * @param transactionID The id of the customer to search transactions for. (0 to ignore)
     * @param vendorID The id of the employee to search transactions for. (0 to ignore)
     * @param startDate The date that the transaction must have been made on/after.
     * @param endDate The date that the transaction must have been made on/before.
     * @return An array of customer transactions matching the search, otherwise null.
     */
    public VendorTransaction[] searchTransactions(int transactionID, int vendorID
            , Date startDate, Date endDate) {
        VendorTransaction[] transactions = null;
        
        ArrayList<String> qWhere = new ArrayList<>();
        ArrayList<Object> setStmts = new ArrayList<>();
        if (transactionID > -1) {
            qWhere.add("Transaction_ID=NULLIF(0, ?)");
            setStmts.add(transactionID);
        }
        if (vendorID > 0) {
            qWhere.add("Vendor_ID=?");
            setStmts.add(vendorID);
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
        
        String query = "SELECT Transaction_ID FROM Transaction_Vendor"
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
                transactions = new VendorTransaction[rs.getRow()];
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
