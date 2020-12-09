/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PoS.database;

import PoS.classes.CartItem;
import PoS.classes.CustomerCart;
import PoS.classes.InventoryItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The database interactions for customer carts.
 * @author Dustin Gritman
 */
public class DBCustomerCarts extends DBCustomers {
    final DBInventory inventory;
    
    /**
     * Initialize a DBCustomerCarts object to be used to interact with all customer data.
     * @param dbInventory A database inventory object to use for inventory access.
     */
    public DBCustomerCarts(DBInventory dbInventory) {
        inventory = dbInventory;
    }
    
    /**
     * Creates a customer cart in the database.
     * @param customerID The customers id that the cart will belong to.
     * @param Employee_ID The employees id that created the cart.
     * @return The created customer cart object, otherwise null if it failed.
     */
    public CustomerCart createCart(int customerID, int Employee_ID) {
        CustomerCart cart = null;
        
        String query = "INSERT INTO Cart_Customer(Customer_ID, Employee_ID)"
                + " VALUES (NULLIF(0, ?), ?)";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query
                        , PreparedStatement.RETURN_GENERATED_KEYS);) {
            prepStmt.setInt(1, customerID);
            prepStmt.setInt(2, Employee_ID);
            
            prepStmt.execute();
            ResultSet rs = prepStmt.getGeneratedKeys();
            if (rs.next())
                cart = new CustomerCart(rs.getInt(1), customerID, Employee_ID
                        , new ArrayList<>());
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return cart;
    }
    
    /**
     * Change the customer id of a cart.
     * @param cartID The cart id to change the customer of.
     * @param newCustomerID The new customer id that the cart belongs to.
     * @return True if the customer id was updated, otherwise false.
     */
    public boolean updateCartCustID(int cartID, int newCustomerID) {
        boolean customerUpdated = false;
        
        String query = "UPDATE Cart_Customer SET Customer_ID=NULLIF(0, ?) WHERE Cart_ID=?";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query);) {
            prepStmt.setInt(1, newCustomerID);
            prepStmt.setInt(2, cartID);
            
            customerUpdated = (prepStmt.executeUpdate() > 0);
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return customerUpdated;
    }
    
    /**
     * Deletes a cart from the database.
     * @param cartID The cart id of the cart to close.
     * @return The removed carts info, otherwise null if failed.
     */
    public boolean closeCart(int cartID) {
        boolean cartRemoved = false;
        
        String query = "DELETE FROM Cart_Customer WHERE Cart_ID=?";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query);) {
            prepStmt.setInt(1, cartID);
            
            cartRemoved = (prepStmt.executeUpdate() > 0);
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return cartRemoved;
    }
    
    /**
     * Adds an item to the cart.
     * @param cart The cart to add the item to.
     * @param item The item to add to the cart.
     * @param quantity The quantity of the item to add to the cart.
     * @return True if the item was added/updated, otherwise false.
     */
    public boolean addCartItem(CustomerCart cart, InventoryItem item, int quantity) {
        if (addCartItem(cart.getID(), item.getSerialNumber(), quantity)) {
            cart.addItem(item, quantity);
            return true;
        }
        return false;
    }
    
    /**
     * Adds an item to the cart.
     * @param cart The cart to add the item to.
     * @param serialNumber The serialnumber of the item to add to the cart.
     * @param quantity The quantity of the item to add to the cart.
     * @return True if the item was added/updated, otherwise false.
     */
    public boolean addCartItem(CustomerCart cart, String serialNumber, int quantity) {
        if (addCartItem(cart.getID(), serialNumber, quantity)) {
            cart.addItem(inventory.load(serialNumber), quantity);
            return true;
        }
        return false;
    }
    
    /**
     * Adds an item/updates the quantity in the cart of an item.
     * @param cartID The cart id to add the item to.
     * @param serialNumber The serial number of the item to add to the cart.
     * @param quantity The amount of the item to add to the cart.
     * @return A customer cart object with the updated cart, otherwise null if 
     * the cart doesn't exist.
     */
    public boolean addCartItem(int cartID, String serialNumber, int quantity) {
        if (quantity < 0)
            return false;
        
        boolean itemAdded = false;
        
        String query = "INSERT INTO Cart_Customer_Items(Cart_ID, Serial_Number, Quantity)"
                + " VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE quantity=(quantity + ?)";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query);) {
            prepStmt.setInt   (1, cartID);
            prepStmt.setString(2, serialNumber);
            prepStmt.setInt   (3, quantity);
            prepStmt.setInt   (4, quantity);
            
            itemAdded = (prepStmt.executeUpdate() > 0);
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return itemAdded;
    }
    
    /**
     * Removes an item/updates the quantity in the cart of an item.
     * @param cart The cart to remove the item from.
     * @param serialNumber The serial number of the item to remove from the cart.
     * @param quantity The amount of the item to remove from the cart.
     * @return A customer cart object with the updated cart, otherwise null if 
     * the cart doesn't exist.
     */
    public boolean removeCartItem(CustomerCart cart, String serialNumber, int quantity) {
        if (removeCartItem(cart.getID(), serialNumber, quantity)) {
            cart.updateItem(serialNumber, -quantity);
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
        
        String query = "SELECT Quantity FROM Cart_Customer_Items"
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
                    query = "UPDATE Cart_Customer_Items SET Quantity=(Quantity - ?)"
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
     * Set the quantity of an item in the cart.
     * @param cart The cart to update.
     * @param serialNumber The serialnumber of the item to change the quantity of.
     * @param quantity The new quantity of the item.
     * @return True if the cart was updated, otherwise false.
     */
    public boolean setCartItemQuantity(CustomerCart cart, String serialNumber, int quantity) {
        if (quantity < 1)
            return removeCartItem(cart, serialNumber, -1);
        
        if (setCartItemQuantity(cart.getID(), serialNumber, quantity)) {
            cart.setItemQuantity(serialNumber, quantity);
            return true;
        }
        
        return false;
    }
    
    /**
     * Set the quantity of an item in the cart.
     * @param cartID The id of the cart to update.
     * @param serialNumber The serialnumber of the item to change the quantity of.
     * @param quantity The new quantity of the item.
     * @return True if the cart was updated, otherwise false.
     */
    public boolean setCartItemQuantity(int cartID, String serialNumber, int quantity) {
        if (quantity < 1)
            return removeCartItem(cartID, serialNumber, Integer.MAX_VALUE);
        
        boolean itemUpdated = false;
        String query = "UPDATE Cart_Customer_Items SET Quantity=?"
                + " WHERE Cart_ID=? AND Serial_Number=?";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query);) {
            prepStmt.setInt   (1, quantity);
            prepStmt.setInt   (2, cartID);
            prepStmt.setString(3, serialNumber);
            
            itemUpdated = (prepStmt.executeUpdate() > 0);
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return itemUpdated;
    }
    
    /**
     * Loads a customer cart.
     * @param cartID The id of the cart to load.
     * @return The customer cart object that was loaded, otherwise null if failed.
     */
    public CustomerCart loadCart(int cartID) {
        CustomerCart cart = null;
        
        String query = "SELECT Customer_ID, Employee_ID FROM Cart_Customer WHERE Cart_ID=?";
        try (Connection con = DBConn.createConnection();) {
            PreparedStatement prepStmt = con.prepareStatement(query);
            prepStmt.setInt(1, cartID);
            
            // Verify a cart with this id is open
            ResultSet rs = prepStmt.executeQuery();
            if (rs.next()) {
                int customerID = rs.getInt("Customer_ID");
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
                
                cart = new CustomerCart(cartID, customerID, employeeID, cartItems);
            }
            
            rs.close();
            prepStmt.close();
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return cart;
    }
    
    /**
     * Search for customer carts by either customer id and/or employee id.
     * @param customerID The id of the customer to search carts for. (0 to ignore)
     * @param employeeID The id of the employee to search carts for. (0 to ignore)
     * @return An array of customer carts matching the search, otherwise null.
     */
    public CustomerCart[] searchCarts(int customerID, int employeeID) {
        CustomerCart[] carts = null;
        
        String query = "SELECT Cart_ID FROM Cart_Customer WHERE";
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
                carts = new CustomerCart[rs.getRow()];
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
