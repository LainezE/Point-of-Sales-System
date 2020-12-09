/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PoS.database;

import PoS.classes.Customer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The database interactions for customers.
 * @author Dustin Gritman
 */
public class DBCustomers {
    /**
     * Initialize a DBCustomers object to be used to interact with customer data.
     */
    public DBCustomers() {
        
    }
    
    /**
     * Create a new customer in the Customer table of the database.
     * @param firstName The first name of the new customer.
     * @param lastName The last name of the new customer.
     * @param phoneNumber The phone number of the new customer.
     * @param email The email address of the the new customer.
     * @param street The street address of the new customer.
     * @param city The city of the new customer.
     * @param state The state of the new customer.
     * @param zip The zip code of the new customer.
     * @return The new customers generated id number or -1 if failed.
     */
    public int create(String firstName, String lastName, String phoneNumber
            , String email, String street, String city, String state, int zip) {
        int newCustomerID = -1;
        
        String query = "INSERT INTO Customers(First_Name, Last_Name, Phone_Number"
                + ", Email, Street, City, State, Zip)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query
                        , PreparedStatement.RETURN_GENERATED_KEYS);) {
            prepStmt.setString(1, firstName);
            prepStmt.setString(2, lastName);
            prepStmt.setString(3, phoneNumber);
            prepStmt.setString(4, email);
            prepStmt.setString(5, street);
            prepStmt.setString(6, city);
            prepStmt.setString(7, state);
            prepStmt.setInt   (8, zip);
            
            prepStmt.execute();
            ResultSet rs = prepStmt.getGeneratedKeys();
            if (rs.next())
                newCustomerID = rs.getInt(1);
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return newCustomerID;
    }
    
    /**
     * Remove a customer from the database by customerID.
     * @param customerID The ID of the customer to remove.
     * @return True if the customer was removed, otherwise false.
     */
    public boolean remove(int customerID) {
        boolean customerRemoved = false;
        
        String query = "DELETE FROM Customers WHERE Customer_ID=?";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query);) {
            prepStmt.setInt(1, customerID);
            
            customerRemoved = (prepStmt.executeUpdate() > 0);
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return customerRemoved;
    }
    
    /**
     * Load specified Customer from the database.
     * @param customerID The customers id to load.
     * @return A customer object if found, otherwise null.
     */
    public Customer load(int customerID){
        Customer customer = null;
        
        String query = "SELECT Customer_ID, First_Name, Last_Name, Phone_Number"
                + ", Email, Street, City, State, Zip, Join_Date FROM Customers"
                + " WHERE Customer_ID=?";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query)) {
            prepStmt.setInt(1, customerID);
            
            ResultSet rs = prepStmt.executeQuery();
            // Go to the last record to get the count of the rows retrieved
            if (rs.next()) {
                customer = new Customer(rs.getInt("Customer_ID")
                        , rs.getString("First_Name"), rs.getString("Last_Name")
                        , rs.getString("Phone_Number"), rs.getString("Email")
                        , rs.getString("Street"), rs.getString("City")
                        , rs.getString("State"), rs.getInt("Zip")
                        , rs.getString("Join_Date")
                );
            }
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return customer;
    }
    
    /**
     * Load all the customers from the database.
     * @return An array of Customer objects containing the customers.
     */
    public Customer[] loadAll() {
        Customer[] customers = null;
        
        String query = "SELECT Customer_ID, First_Name, Last_Name, Phone_Number"
                + ", Email, Street, City, State, Zip, Join_Date FROM Customers";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query)) {
            ResultSet rs = prepStmt.executeQuery();
            
            // Go to the last record to get the count of the rows retrieved
            if (rs.last()) {
                customers = new Customer[rs.getRow()];
                rs.beforeFirst();
            }
           
            int index = 0;
            while (rs.next())
                customers[index++] = new Customer(rs.getInt("Customer_ID")
                        , rs.getString("First_Name"), rs.getString("Last_Name")
                        , rs.getString("Phone_Number"), rs.getString("Email")
                        , rs.getString("Street"), rs.getString("City")
                        , rs.getString("State"), rs.getInt("Zip")
                        , rs.getString("Join_Date")
                );
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return customers;
    }
    
    /**
     * Search for customers by the fields offered. (Note: to ignore a field 
     * enter it as null or as an empty String)
     * @param firstName The first name of the customer(s) to find.
     * @param lastName The last name of the customer(s) to find.
     * @param phoneNumber The phone number of the customer(s) to find.
     * @param email The email of the customer(s) to find.
     * @return The customers found with the fields given, otherwise null.
     */
    public Customer[] search(String firstName, String lastName
            , String phoneNumber, String email) {
        Customer[] customers = null;
        
        ArrayList<String[]> searchInfo = new ArrayList<>();
        if (firstName != null && !firstName.equals(""))
            searchInfo.add(new String[]{"First_Name", firstName});
        if (lastName != null && !lastName.equals(""))
            searchInfo.add(new String[]{"Last_Name", lastName});
        if (phoneNumber != null && !phoneNumber.equals(""))
            searchInfo.add(new String[]{"Phone_Number", phoneNumber});
        if (email != null && !email.equals(""))
            searchInfo.add(new String[]{"Email", email});
        
        if (searchInfo.size() < 1)
            return null;
        
        String query = "SELECT Customer_ID, First_Name, Last_Name, Phone_Number"
                + ", Email, Street, City, State, Zip, Join_Date FROM Customers"
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
                customers = new Customer[rs.getRow()];
                rs.beforeFirst();
            }
           
            int index = 0;
            while (rs.next())
                customers[index++] = new Customer(rs.getInt("Customer_ID")
                        , rs.getString("First_Name"), rs.getString("Last_Name")
                        , rs.getString("Phone_Number"), rs.getString("Email")
                        , rs.getString("Street"), rs.getString("City")
                        , rs.getString("State"), rs.getInt("Zip")
                        , rs.getString("Join_Date")
                );
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return customers;
    }
}
