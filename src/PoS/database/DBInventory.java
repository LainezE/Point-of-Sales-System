/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PoS.database;

import PoS.classes.InventoryItem;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The database interactions for the inventory and items.
 * @author Eli Lainez
 */
public class DBInventory {
    /**
     * Initialize an employees database object.
     */
    public DBInventory() {
        
    }
    
    /**
     * Creates an item in the inventory database table.
     * @param serialNumber The items serial number.
     * @param manufacturer The manufacturer of the item.
     * @param modelNumber The model number of the item.
     * @param modelName The model name of the item.
     * @param description The description of the item.
     * @param supplierLink The suppliers link of the item.
     * @param cost The cost to purchase the item.
     * @param listPrice The price the item is listed for sale at.
     * @param msrp The MSRP of the item.
     * @param stock The current stock of the item.
     * @param baseStock The base stock number of the item.
     * @param reOrderAmount The amount of stock that signifies being low.
     * @return True if the item was created, otherwise false.
     */
    public boolean create(String serialNumber, String manufacturer
            , int modelNumber, String modelName, String description
            , String supplierLink, BigDecimal cost, BigDecimal listPrice
            , BigDecimal msrp, int stock, int baseStock, int reOrderAmount) {
        boolean itemCreated = false;
        
        String query = "INSERT INTO Inventory(Serial_Number, Manufacturer"
                + ", Model_Number, Model_Name, Description, Supplier_Link, Cost"
                + ", List_Price, MSRP, Stock, Base_Stock, ReOrder_Amount)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query)) {
            prepStmt.setString    (1, serialNumber);
            prepStmt.setString    (2, manufacturer);
            prepStmt.setInt       (3, modelNumber);
            prepStmt.setString    (4, modelName);
            prepStmt.setString    (5, description);
            prepStmt.setString    (6, supplierLink);
            prepStmt.setBigDecimal(7, cost);
            prepStmt.setBigDecimal(8, listPrice);
            prepStmt.setBigDecimal(9, msrp);
            prepStmt.setInt       (10, stock);
            prepStmt.setInt       (11, baseStock);
            prepStmt.setInt       (12, reOrderAmount);
            
            itemCreated = (prepStmt.executeUpdate() > 0);
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return itemCreated;
    }
    
    /**
     * Remove an inventory item from the inventory database table.
     * @param serialNumber The serial number of the item to remove.
     * @return True if the item was removed, otherwise false.
     */
    public boolean remove(String serialNumber) {
        boolean itemRemoved = false;
        
        String query = "DELETE FROM Inventory WHERE Serial_Number=?";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query);) {
            prepStmt.setString(1, serialNumber);
            
            itemRemoved = (prepStmt.executeUpdate() > 0);
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return itemRemoved;
    }
    
    /**
     * Load a specific inventory item from the database.
     * @param serialNumber The serial number of the item to load.
     * @return The loaded inventory item.
     */
    public InventoryItem load(String serialNumber) {
        InventoryItem item = null;
        
        String query = "SELECT Serial_Number, Manufacturer, Model_Number"
                + ", Model_Name, Description, Supplier_Link, Cost, List_Price"
                + ", MSRP, Stock, Base_Stock, ReOrder_Amount"
                + " FROM Inventory WHERE Serial_Number=?";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query)) {
            prepStmt.setString(1, serialNumber);
            
            ResultSet rs = prepStmt.executeQuery();
            if (rs.next()) {
                item = new InventoryItem(rs.getString("Serial_Number")
                        , rs.getString("Manufacturer"), rs.getInt("Model_Number")
                        , rs.getString("Model_Name"), rs.getString("Description")
                        , rs.getString("Supplier_Link"), rs.getBigDecimal("Cost")
                        , rs.getBigDecimal("List_Price"), rs.getBigDecimal("MSRP")
                        , rs.getInt("Stock"), rs.getInt("Base_Stock")
                        , rs.getInt("ReOrder_Amount")
                );
            }
        } catch(SQLException e) {
            System.out.println(e);
        } 
        
        return item;
    }
    
    /**
     * Get the entire inventory list from the database.
     * @return An array of InventoryItem objects containing the inventory.
     */
    public InventoryItem[] loadAll() {
        InventoryItem[] inventory = null;
        
        String query = "SELECT Serial_Number, Manufacturer, Model_Number"
                + ", Model_Name, Description, Supplier_Link, Cost, List_Price"
                + ", MSRP, Stock, Base_Stock, ReOrder_Amount FROM Inventory";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query)) {
            ResultSet rs = prepStmt.executeQuery();
            
            // Go to the last record to get the count of the rows retrieved
            if (rs.last()) {
                inventory = new InventoryItem[rs.getRow()];
                rs.beforeFirst();
            }
           
            int index = 0;
            while (rs.next())
                inventory[index++] = new InventoryItem(rs.getString("Serial_Number")
                        , rs.getString("Manufacturer"), rs.getInt("Model_Number")
                        , rs.getString("Model_Name"), rs.getString("Description")
                        , rs.getString("Supplier_Link"), rs.getBigDecimal("Cost")
                        , rs.getBigDecimal("List_Price"), rs.getBigDecimal("MSRP")
                        , rs.getInt("Stock"), rs.getInt("Base_Stock")
                        , rs.getInt("ReOrder_Amount")
                );
            
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return inventory;
    }
    
    /**
     * Updates an inventory items stock amount by the given quantity.
     * @param serialNumber The serial number of the item to update.
     * @param quantity The amount of the stock added or removed.
     * @return True if the update was successful, otherwise false.
     */
    public boolean updateStock(String serialNumber, int quantity) {
        boolean itemUpdated = false;
        
        String query = "UPDATE Inventory SET Stock=(Stock + ?) WHERE Serial_Number=?";
        if (quantity < 0)
            query += " AND Stock >= ?";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query);) {
            prepStmt.setInt   (1, quantity);
            prepStmt.setString(2, serialNumber);
            if (quantity < 0)
                prepStmt.setInt(3, -quantity);
            
            itemUpdated = (prepStmt.executeUpdate() > 0);
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return itemUpdated;
    }
    
    /**
     * Search for an inventory item.
     * @param serialNumber The serial number to search for. (null or "" to ignore)
     * @param manufacturer The manufacturer to search for. (null or "" to ignore)
     * @param modelName The model name to search for. (null or "" to ignore)
     * @param supplierLink The supplier link to search for. (null or "" to ignore)
     * @return The inventory items found from the search criteria, otherwise null.
     */
    public InventoryItem[] search(String serialNumber, String manufacturer
            , String modelName, String supplierLink) {
        InventoryItem[] items = null;
        
        ArrayList<String[]> searchInfo = new ArrayList<>();
        if (serialNumber != null && !serialNumber.equals(""))
            searchInfo.add(new String[]{"Serial_Number", serialNumber});
        if (manufacturer != null && !manufacturer.equals(""))
            searchInfo.add(new String[]{"Manufacturer", "%" + manufacturer + "%"});
        if (modelName != null && !modelName.equals(""))
            searchInfo.add(new String[]{"Model_Name", "%" + modelName + "%"});
        if (supplierLink != null && !supplierLink.equals(""))
            searchInfo.add(new String[]{"Supplier_Link", "%" + supplierLink + "%"});
        
        if (searchInfo.size() < 1)
            return null;
        
        String query = "SELECT Serial_Number, Manufacturer, Model_Number"
                + ", Model_Name, Description, Supplier_Link, Cost, List_Price"
                + ", MSRP, Stock, Base_Stock, ReOrder_Amount FROM Inventory"
                + " WHERE " + searchInfo.get(0)[0] + " LIKE ?";
        for (int i = 1; i < searchInfo.size(); i++)
            query += " AND " + searchInfo.get(i)[0] + " LIKE ?";
        
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query)) {
            for (int i = 0; i < searchInfo.size(); i++)
                prepStmt.setString(i, searchInfo.get(i)[1]);
            
            ResultSet rs = prepStmt.executeQuery();
            // Go to the last record to get the count of the rows retrieved
            if (rs.last()) {
                items = new InventoryItem[rs.getRow()];
                rs.beforeFirst();
            }
            
            int index = 0;
            while (rs.next())
                items[index++] = new InventoryItem(rs.getString("Serial_Number")
                        , rs.getString("Manufacturer"), rs.getInt("Model_Number")
                        , rs.getString("Model_Name"), rs.getString("Description")
                        , rs.getString("Supplier_Link"), rs.getBigDecimal("Cost")
                        , rs.getBigDecimal("List_Price"), rs.getBigDecimal("MSRP")
                        , rs.getInt("Stock"), rs.getInt("Base_Stock")
                        , rs.getInt("ReOrder_Amount")
                );
            
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return items;
    }
}
