/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PoS.database;

import PoS.classes.Vendor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 
 * @author Eli Lainez
 */
public class DBVendors {
    /**
     * Initialize a DBUsers object to be used to interact with vendor data.
     */
    public DBVendors() {
        
    }
    
    /**
     * Create a new Vendor in the Vendors table of the database.
     * @param vendorName The vendor name of the new vendor.
     * @param phoneNumber The phone number of the new vendor.
     * @param email The email address of the the new vendor.
     * @param street The street address of the new vendor.
     * @param city The city of the new vendor.
     * @param state The state of the new vendor.
     * @param zip The zip code of the new vendor.
     * @return The new vendors generated ID or -1 if failed.
     */
    public int create(String vendorName, String phoneNumber,
             String email, String street, String city, String state, String zip) {
        int newVendorID = -1;
        
        String query = "INSERT INTO Vendors(Vendor_Name, Phone_Number, Street"
                    + ", City, State, Zip, Email)"
                    + " Values (?, ?, ?, ?, ?, ?, ?)";
        try(Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query
                        , PreparedStatement.RETURN_GENERATED_KEYS);) {
            prepStmt.setString(1, vendorName);
            prepStmt.setString(2, phoneNumber);
            prepStmt.setString(3, email);
            prepStmt.setString(4, street);
            prepStmt.setString(5, city);
            prepStmt.setString(6, state);
            prepStmt.setString(7, zip); 
            
            prepStmt.execute(query);
            ResultSet rs = prepStmt.getGeneratedKeys();
            if (rs.next())
                newVendorID = rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e);
        }
        
        return newVendorID;
    }
    
    /**
     * Attempt to remove a vendor from the database.
     * @param vendorID The id of the vendor to remove.
     * @return True if the vendor was removed, otherwise false.
     */
    public boolean remove(int vendorID) {
        boolean vendorRemoved = false;
        
        String query = "DELETE FROM Vendors WHERE Vendor_ID=?";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query);) {
            prepStmt.setInt(1, vendorID);

            vendorRemoved = (prepStmt.executeUpdate() > 0);
        } catch (SQLException e) {
            System.out.println(e);
        }
        
        return vendorRemoved;
    }
    
    /**
     * Load a vendor by id.
     * @param vendorID The id of the vendor to load.
     * @return The vendor object found, otherwise null.
     */
    public Vendor load(int vendorID){
        Vendor vendor = null;
        
        String query = "Select Vendor_ID, Vendor_Name, Phone_Number, Email, "
                + "Street, City, State, Zip From Vendors WHERE Vendor_ID=?";
        try {
            Connection con = DBConn.createConnection();
            PreparedStatement prepStmt = con.prepareStatement(query);
            prepStmt.setInt(1, vendorID);
            
            ResultSet rs = prepStmt.executeQuery();
            if (rs.next()) {
                vendor = new Vendor(rs.getInt("Vendor_ID"),
                    rs.getString("Vendor_Name"),
                    rs.getString("Phone_Number"),
                    rs.getString("Email"),
                    rs.getString("Street"),
                    rs.getString("City"),
                    rs.getString("State"),
                    rs.getInt("Zip")
                );
            }
            
            prepStmt.close();
            con.close();
        }
        catch(SQLException e){
                    System.out.println(e);
        }
        return vendor; 
    }
    
    /**
     * Load all the Vendors from the database.
     * @return An array of Vendor objects containing the employees.
     */
    public Vendor[] loadAll() {
        Vendor[] vendors = null;
        
        String query = "Select Vendor_ID, Vendor_Name, Phone_Number, Email, "
                + "Street, City, State, Zip From Vendors";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query)) {
            ResultSet rs = prepStmt.executeQuery();
            
            // Go to the last record to get the count of the rows retrieved
            if (rs.last()) {
                vendors = new Vendor[rs.getRow()];
                rs.beforeFirst();
            }
           
            int index = 0;
            while (rs.next())
                vendors[index++] = new Vendor(rs.getInt("Vendor_ID"),
                    rs.getString("Vendor_Name"),
                    rs.getString("Phone_Number"),
                    rs.getString("Email"),
                    rs.getString("Street"),
                    rs.getString("City"),
                    rs.getString("State"),
                    rs.getInt("Zip")
                );
            
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return vendors;
    }
    
    /**
     * Search for an array of vendors. (use "" as the value for a parameter 
     * to ignore that field)
     * @param vendorID The vendor id to search for.
     * @param vendorName The vendor name to search for.
     * @param phoneNumber The vendor phone number to search for.
     * @param email The vendor email to search for.
     * @param street The vendor street to search for.
     * @param city The vendor city to search for.
     * @param state The vendor state to search for.
     * @param zip The vendor zip code to search for.
     * @return An array of vendors found that are similiar to the search criteria.
     */
    public Vendor[] search(String vendorID,String vendorName, String phoneNumber, 
        String email, String street, String city, String state, int zip){
        Vendor[] vendors = null; 
        
        ArrayList<String[]> searchInfo = new ArrayList<>(); 
        
        if (vendorName != null && !vendorName.equals(""))
            searchInfo.add(new String[]{"First_Name", vendorName});
        if (phoneNumber != null && !phoneNumber.equals(""))
            searchInfo.add(new String[]{"Phone_Number", phoneNumber});
        if (email!= null && !email.equals(""))
            searchInfo.add(new String[]{"Email", email});
        if (street != null && !street.equals(""))
            searchInfo.add(new String[]{"Street", street});
        if (city != null && !city.equals(""))
            searchInfo.add(new String[]{"City", city});
        if (state != null && !state.equals(""))
            searchInfo.add(new String[]{"State", state});
        
        if (searchInfo.size() < 1)
            return null;
        
        String query = "Select Vendor_ID, Vendor_Name, Phone_Number, Email, "
                + "Street, City, State, Zip From Vendors WHERE " 
                + searchInfo.get(0)[0] + " LIKE ?";
        for (int i = 1; i < searchInfo.size(); i++)
            query += " AND " + searchInfo.get(i)[0] + " LIKE ?";
        
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query)) {
            for (int i = 0; i < searchInfo.size(); i++)
                prepStmt.setString(i, searchInfo.get(i)[1]);
            
            ResultSet rs = prepStmt.executeQuery();
            // Go to the last record to get the count of the rows retrieved
            if (rs.last()) {
                vendors = new Vendor[rs.getRow()];
                rs.beforeFirst();
            }
           
            int index = 0;
            while (rs.next())
                vendors[index++] = new Vendor(rs.getInt("Vendor_ID"),
                    rs.getString("Vendor_Name"),
                    rs.getString("Phone_Number"),
                    rs.getString("Email"),
                    rs.getString("Street"),
                    rs.getString("City"),
                    rs.getString("State"),
                    rs.getInt("Zip")
                );
            
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return vendors;
    }
}
