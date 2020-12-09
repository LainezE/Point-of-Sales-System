/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PoS.database;

import PoS.classes.BCrypt;
import PoS.classes.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The database interactions for users.
 * @author Dustin Gritman
 */
public class DBUsers {
    /**
     * Initialize a DBUsers object to be used to interact with user account data.
     */
    public DBUsers() {
        
    }
    
    /**
     * Create a user in the User_Accounts database.
     * @param employeeID The employee id of the user.
     * @param username The username for the user.
     * @param password The password for the user.
     * @param accessLevel The users access level.
     * @return True if the user was created, otherwise false.
     */
    public int create(int employeeID, String username, char[] password
            , short accessLevel) {
        int newUserID = -1;
        
        String query = "INSERT INTO User_Accounts(Employee_ID, Username"
                + ", Password, Access_Level) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query
                        , PreparedStatement.RETURN_GENERATED_KEYS);) {
            prepStmt.setInt   (1, employeeID);
            prepStmt.setString(2, username);
            prepStmt.setString(3, BCrypt.hashpw(String.valueOf(password), BCrypt.gensalt()));
            prepStmt.setShort (4, accessLevel);
            
            prepStmt.execute();
            ResultSet rs = prepStmt.getGeneratedKeys();
            if (rs.next())
                newUserID = rs.getInt(1);
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return newUserID;
    }
    
    /**
     * Change a users password.
     * @param userID The user id to change the password of.
     * @param newPassword The users new password.
     * @return True if the password was changed, otherwise false.
     */
    public boolean updatePassword(int userID, char[] newPassword) {
        boolean passChanged = false;
        
        String query = "UPDATE User_Accounts SET Password=? WHERE User_ID=?";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query);) {
            prepStmt.setString(1, BCrypt.hashpw(String.valueOf(newPassword), BCrypt.gensalt()));
            prepStmt.setInt   (2, userID);
            
            passChanged = (prepStmt.executeUpdate() > 0);
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return passChanged;
    }
    
    /**
     * Change a users username.
     * @param userID The user id to change the username of.
     * @param newUsername The users new username.
     * @return True if the username was changed, otherwise false.
     */
    public boolean updateUsername(int userID, String newUsername) {
        boolean updated = false;
        
        String query = "UPDATE User_Accounts SET Username=? WHERE User_ID=?";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query);) {
            prepStmt.setString(1, newUsername);
            prepStmt.setInt   (2, userID);
            
            updated = (prepStmt.executeUpdate() > 0);
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return updated;
    }
    
    /**
     * Change a users access level.
     * @param userID The user id to change the access level of.
     * @param newAccessLevel The users new access level.
     * @return True if the access level was changed, otherwise false.
     */
    public boolean updateAccessLevel(int userID, short newAccessLevel) {
        boolean updated = false;
        
        String query = "UPDATE User_Accounts SET Access_Level=? WHERE User_ID=?";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query);) {
            prepStmt.setShort(1, newAccessLevel);
            prepStmt.setInt  (2, userID);
            
            updated = (prepStmt.executeUpdate() > 0);
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return updated;
    }
    
    /**
     * Remove a user from the User_Accounts database.
     * @param userID The user id of the user to remove.
     * @return True if user was removed and false otherwise.
     */
    public boolean remove(int userID) {
        boolean userRemoved = false;
        
        String query = "DELETE FROM User_Accounts WHERE User_ID=?";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query);) {
            prepStmt.setInt(1, userID);
            
            userRemoved = (prepStmt.executeUpdate() > 0);
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return userRemoved;
    }
    
    /**
     * Load a user based on username and password.
     * @param username The username of the user.
     * @param password The password of the user.
     * @return The loaded user object if the user info has a match.
     */
    public User load(String username, char[] password) {
        User user = null;
        
        String query = "SELECT User_ID, Password, Employee_ID, Access_Level"
                + " FROM User_Accounts WHERE Username=?";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query)) {
            prepStmt.setString(1, username);
            
            ResultSet rs = prepStmt.executeQuery();
            if (rs.next())
                if (BCrypt.checkpw(String.valueOf(password), rs.getString("Password")))
                    user = new User(rs.getInt("User_ID"), rs.getInt("Employee_ID")
                            , username, rs.getShort("Access_Level")
                    );
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return user;
    }
    
    /**
     * Load an array of user objects for all the users.
     * @return An array of all the users.
     */
    public User[] loadAll() {
        User[] users = null;
        
        String query = "SELECT User_ID, Employee_ID, Username, Access_Level"
                + " FROM User_Accounts";
        try (Connection con = DBConn.createConnection();
                PreparedStatement prepStmt = con.prepareStatement(query)) {
            ResultSet rs = prepStmt.executeQuery();
            
            // Go to the last record to get the count of the rows retrieved
            if (rs.last()) {
                users = new User[rs.getRow()];
                rs.beforeFirst();
            }
            
            int index = 0;
            while (rs.next())
                users[index++] = new User(rs.getInt("User_ID"), rs.getInt("Employee_ID")
                        , rs.getString("Username"), rs.getShort("Access_Level")
                );
            
        } catch(SQLException e) {
            System.out.println(e);
        }
        
        return users;
    }
}
