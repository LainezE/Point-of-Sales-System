/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PoS.database;

import PoS.classes.Employee;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The database interactions for employees.
 * @author Eli Lainez
 */
public class DBEmployees {
    /**
     * Initialize an employees database object.
     */
    public DBEmployees() {
        
    }
    
    /**
     * Create a new employee in the Employees table of the database.
     * @param firstName The first name of the new employee.
     * @param lastName The last name of the new employee.
     * @param title The position title of the new employee.
     * @param phoneNumber The phone number of the new employee.
     * @param email The email address of the the new employee.
     * @param street The street address of the new employee.
     * @param city The city of the new employee.
     * @param state The state of the new employee.
     * @param zip The zip code of the new employee.
     * @return The new employees generated id number or -1 if failed.
     */
    public int create(String firstName, String lastName, String title
            , String phoneNumber, String email, String street, String city
            , String state, int zip) {
        int newEmployeeID = -1;
        
        String query = "INSERT INTO Employees(First_Name, Last_Name, Title"
                + ", Phone_Number, Email, Street, City, State, Zip, Start_Date)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query
                        , PreparedStatement.RETURN_GENERATED_KEYS);) {
            prepStmt.setString(1, firstName);
            prepStmt.setString(2, lastName);
            prepStmt.setString(3, title);
            prepStmt.setString(4, phoneNumber);
            prepStmt.setString(5, email);
            prepStmt.setString(6, street);
            prepStmt.setString(7, city);
            prepStmt.setString(8, state);
            prepStmt.setInt   (9, zip);
            
            prepStmt.execute();
            ResultSet rs = prepStmt.getGeneratedKeys();
            if (rs.next())
                newEmployeeID = rs.getInt(1);
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return newEmployeeID;
    }
    
    /**
     * Remove an employee from the Employees database.
     * @param employeeID The employee id of the employee to remove.
     * @return True if employee was removed and false otherwise.
     */
    public boolean remove(int employeeID) {
        boolean employeeRemoved = false;
        
        String query = "DELETE FROM Employees WHERE Employee_ID=?";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query);) {
            prepStmt.setInt(1, employeeID);
            
            employeeRemoved = (prepStmt.executeUpdate() > 0);
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return employeeRemoved;
    }
    
    /**
     * Loads a specific employee from the database.
     * @param employeeID The id of the employee to load.
     * @return The employee that was found, otherwise null.
     */
    public Employee load(int employeeID) {
        Employee employee = null;
        
        String query = "SELECT Employee_ID, First_Name, Last_Name, Title"
                + ", Phone_Number, Email, Street, City, State, Zip"
                + " FROM Employees WHERE Employee_ID=?";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query)) {
            prepStmt.setInt(1, employeeID);
            
            ResultSet rs = prepStmt.executeQuery();
            // Go to the last record to get the count of the rows retrieved
            if (rs.next()) {
                employee = new Employee(rs.getInt("Employee_ID")
                        , rs.getString("First_Name"), rs.getString("Last_Name")
                        , rs.getString("Title"), rs.getString("Phone_Number")
                        , rs.getString("Email"), rs.getString("Street")
                        , rs.getString("City"), rs.getString("State")
                        , rs.getInt("Zip")
                );
            }
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return employee;
    }
    
    /**
     * Load all the employees from the database.
     * @return An array of Employee objects containing the employees.
     */
    public Employee[] loadAll() {
        Employee[] employees = null;
        
        String query = "SELECT Employee_ID, First_Name, Last_Name, Title"
                + ", Phone_Number, Email, Street, City, State, Zip"
                + " FROM Employees";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query)) {
            ResultSet rs = prepStmt.executeQuery();
            
            // Go to the last record to get the count of the rows retrieved
            if (rs.last()) {
                employees = new Employee[rs.getRow()];
                rs.beforeFirst();
            }
           
            int index = 0;
            while (rs.next())
                employees[index++] = new Employee(rs.getInt("Employee_ID")
                        , rs.getString("First_Name"), rs.getString("Last_Name")
                        , rs.getString("Title"), rs.getString("Phone_Number")
                        , rs.getString("Email"), rs.getString("Street")
                        , rs.getString("City"), rs.getString("State")
                        , rs.getInt("Zip")
                );
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return employees;
    }
    
    /**
     * Search for employee by the fields offered. (Note: to ignore a field 
     * enter it as null or as an empty String)
     * @param firstName The first name of the employee(s) to find.
     * @param lastName The last name of the employee(s) to find.
     * @param title The title of the employee(s) to find.
     * @param phoneNumber The phone number of the employee(s) to find.
     * @param email The email of the employee(s) to find.
     * @return The employees found with the fields given, otherwise null.
     */
    public Employee[] search(String firstName, String lastName
            , String title, String phoneNumber, String email) {
        Employee[] employees = null;
        
        ArrayList<String[]> searchInfo = new ArrayList<>();
        if (firstName != null && !firstName.equals(""))
            searchInfo.add(new String[]{"First_Name", firstName});
        if (lastName != null && !lastName.equals(""))
            searchInfo.add(new String[]{"Last_Name", lastName});
        if (title != null && !title.equals(""))
            searchInfo.add(new String[]{"Title", title});
        if (phoneNumber != null && !phoneNumber.equals(""))
            searchInfo.add(new String[]{"Phone_Number", phoneNumber});
        if (email != null && !email.equals(""))
            searchInfo.add(new String[]{"Email", email});
        
        if (searchInfo.size() < 1)
            return null;
        
        String query = "SELECT Employee_ID, First_Name, Last_Name, Title"
                + ", Phone_Number, Email, Street, City, State, Zip"
                + " FROM Employees WHERE " + searchInfo.get(0)[0] + " LIKE ?";
        for (int i = 1; i < searchInfo.size(); i++)
            query += " AND " + searchInfo.get(i)[0] + " LIKE ?";
        
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query)) {
            for (int i = 0; i < searchInfo.size(); i++)
                prepStmt.setString(i, searchInfo.get(i)[1]);
            
            ResultSet rs = prepStmt.executeQuery();
            // Go to the last record to get the count of the rows retrieved
            if (rs.last()) {
                employees = new Employee[rs.getRow()];
                rs.beforeFirst();
            }
           
            int index = 0;
            while (rs.next())
                employees[index++] = new Employee(rs.getInt("Employee_ID")
                        , rs.getString("First_Name"), rs.getString("Last_Name")
                        , rs.getString("Title"), rs.getString("Phone_Number")
                        , rs.getString("Email"), rs.getString("Street")
                        , rs.getString("City"), rs.getString("State")
                        , rs.getInt("Zip")
                );
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return employees;
    }
    
    /**
     * update employee in the Employees table of the database.
     * @param employeeID The id of the employee to update.
     * @param firstName The first name of the new employee.
     * @param lastName The last name of the new employee.
     * @param title The position title of the new employee.
     * @param phoneNumber The phone number of the new employee.
     * @param email The email address of the the new employee.
     * @param street The street address of the new employee.
     * @param city The city of the new employee.
     * @param state The state of the new employee.
     * @param zip The zip code of the new employee.
     * @return The new employees generated id number or -1 if failed.
     */
    public boolean update(int employeeID, String firstName, String lastName, 
            String title, String phoneNumber, String email, String street,
            String city, String state, int zip){
        boolean updated = false; 
        
        String query = "UPDATE Employees SET First_Name=?, Last_Name=?"
                + ", Title=?, Phone_Number=?, Email=?, Street=?"
                + ", City=?, State=?, Zip=?"
                + " WHERE Employee_ID = ?";
        try {
            Connection con = DBConn.createConnection();
            PreparedStatement prepStmt = con.prepareStatement(query);
            prepStmt.setString(1, firstName);
            prepStmt.setString(2, lastName);
            prepStmt.setString(3, title);
            prepStmt.setString(4, phoneNumber);
            prepStmt.setString(5, email);
            prepStmt.setString(6, street);
            prepStmt.setString(7, city);
            prepStmt.setString(8, state);
            prepStmt.setInt   (9, zip);
            prepStmt.setInt   (10, employeeID);
            
            updated = (prepStmt.executeUpdate() > 0);
            
            prepStmt.close();
            con.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        
        return updated; 
    }
}
