/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PoS.database;

import PoS.classes.CartItem;
import PoS.classes.VendorCart;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 
 * @author Eli Lainez
 */
public class DBVendorCarts extends DBVendors {
    final DBInventory inventory;
    
    /**
     * Create a new DBVendorCarts object.
     * @param dbInventory The DBInventory object to use for calling inventory.
     */
    public DBVendorCarts(DBInventory dbInventory) {
        inventory = dbInventory;
    }
    
    /**
     * Creates a new Cart for the Vendor
     * @param vendorID The vendor id that the cart belongs to.
     * @param employeeID The employee id that created the cart.
     * @return The new cart id if created, otherwise -1.
     */
    public int createCart(int vendorID, int employeeID) {
        int newCartID = -1;
        
        String query = "INSERT INTO Vendor_Cart(Vendor_ID, Employee_ID) VALUES (?, ?)";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query
                        , PreparedStatement.RETURN_GENERATED_KEYS);) {
            prepStmt.setInt(1, vendorID);
            
            prepStmt.execute();
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return newCartID;
    }
    
    /**
     * Close a vendor cart in the database.
     * @param cartID The id of the cart to close.
     * @return True if the cart was removed, otherwise false.
     */
    public boolean closeCart(int cartID) {
        boolean vendorCartRemoved = false;
        
        String query = "DELETE FROM Vendor_Cart WHERE Cart_ID=?";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query);) {
            prepStmt.setInt(1, cartID);
            
            vendorCartRemoved = (prepStmt.executeUpdate() > 0);
        } catch(SQLException e){
            System.out.println(e);
        }
        
        return vendorCartRemoved;    
    }
    
    /**
     * Loads a Vendor cart.
     * @param cartID The id of the cart to load.
     * @return The customer cart object that was loaded, otherwise null if failed.
     */
    public VendorCart loadCart(int cartID) {
        VendorCart cart = null;
        
        String query = "SELECT Vendor_ID, Employee_ID FROM Cart_Vendor WHERE Cart_ID=?";
        try (Connection con = DBConn.createConnection();) {
            PreparedStatement prepStmt = con.prepareStatement(query);
            prepStmt.setInt(1, cartID);
            
            // Verify a cart with this id is open
            ResultSet rs = prepStmt.executeQuery();
            if (rs.next()) {
                int customerID = rs.getInt("Vendor_ID");
                int employeeID = rs.getInt("Employee_ID");
                rs.close();
                prepStmt.close();
                
                query = "SELECT Quantity, Inventory.Serial_Number, Manufacturer"
                        + ", Model_Number, Model_Name, Description, Supplier_Link"
                        + ", Cost, List_Price, MSRP, Stock, Base_Stock, ReOrder_Amount"
                        + " FROM Cart_Customer_Items JOIN Inventory"
                        + " ON Cart_Customer_Items.Serial_Number = Inventory.Serial_Number"
                        + " WHERE Cart_ID=?";
                prepStmt = con.prepareStatement(query);
                prepStmt.setInt(1, cartID);
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
                
                cart = new VendorCart(cartID, customerID, employeeID, cartItems);
            }
            
            rs.close();
            prepStmt.close();
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return cart;
    }
    
    /**
     * Remove an item from the cart.
     * @param cart The cart to remove the item from.
     * @param serialNumber The serialnumber of the item to remove from the cart.
     * @param quantity The quantity of the item to be removed from the cart.
     * @return True if the item was removed, otherwise false.
     */
    public boolean removeCartItem(VendorCart cart, String serialNumber, int quantity) {
        if (removeCartItem(cart.getID(), serialNumber, quantity) ) {
            cart.updateItem(serialNumber, quantity);
            return true;
        }
        return false;
    }
    
    /**
     * Removes an item/updates the quantity in the cart of an item.
     * @param cartID The cart id to remove the item from.
     * @param serialNumber The serial number of the item to remove from the cart.
     * @param quantity The amount of the item to remove from the cart.
     * @return A customer cart object with the updated cart, otherwise null if 
     * the cart doesn't exist.
     */
    public boolean removeCartItem(int cartID, String serialNumber, int quantity) {
        if (quantity < 0)
            return false;
        
        boolean updatedCartItem = false;
        
        String query = "SELECT Quantity FROM Cart_Vendor_Items"
                + " WHERE Cart_ID=? AND Serial_Number=?";
        try (Connection con = DBConn.createConnection();) {
            PreparedStatement prepStmt = con.prepareStatement(query);
            prepStmt.setInt   (1, cartID);
            prepStmt.setString(2, serialNumber);
            
            ResultSet rs = prepStmt.executeQuery();
            if (rs.next()) {
                if (rs.getInt("Quantity") - quantity < 1) {
                    prepStmt.close();
                    query = "DELETE FROM Cart_Customer_Items "
                            + "WHERE Cart_ID=? AND Serial_Number=?";
                    prepStmt = con.prepareStatement(query);
                    prepStmt.setInt   (1, cartID);
                    prepStmt.setString(2, serialNumber);
                    
                    updatedCartItem = (prepStmt.executeUpdate() > 0);
                }
                else {
                    prepStmt.close();
                    query = "UPDATE Cart_Vendor_Items SET Quantity=(Quantity - ?)"
                            + " WHERE Cart_ID=? AND Serial_Number=?";
                    prepStmt = con.prepareStatement(query);
                    prepStmt.setInt   (1, quantity);
                    prepStmt.setInt   (2, cartID);
                    prepStmt.setString(3, serialNumber);
                    
                    updatedCartItem = (prepStmt.executeUpdate() > 0);
                }
            }
            
            prepStmt.close();
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return updatedCartItem;
    }
    /**
     * Search for customer carts by either customer id and/or employee id.
     * @param customerID The id of the customer to search carts for. (0 to ignore)
     * @param employeeID The id of the employee to search carts for. (0 to ignore)
     * @return An array of customer carts matching the search, otherwise null.
     */
    public VendorCart[] searchCarts(int customerID, int employeeID) {
        VendorCart[] carts = null;
        
        String query = "SELECT Cart_ID FROM Cart_Vendor WHERE";
        if (customerID > 0) {
            query += " Customer_ID=NULLIF(0, ?)";
            if (employeeID > 0)
                query += " AND Employe_ID=?";
        }
        else if (employeeID > 0)
            query += " Employee_ID=?";
        else
            return null;
        
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query);) {
            short prepSetIndex = 1;
            if (customerID > 0)
                prepStmt.setInt(prepSetIndex++, customerID);
            if (employeeID > 0)
                prepStmt.setInt(prepSetIndex++, employeeID);
            
            ResultSet rs = prepStmt.executeQuery();
            if (rs.last()) {
                carts = new VendorCart[rs.getRow()];
                rs.beforeFirst();
            }
            
            int index = 0;
            while (rs.next())
                carts[index++] = loadCart(rs.getInt("Cart_ID"));
            
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return carts;
    }
}
