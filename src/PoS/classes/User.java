/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PoS.classes;

/**
 * Represents a user account.
 * @author Dustin Gritman
 */
public class User {
    private int    id;
    private int    employeeID;
    private String username;
    private short  accessLevel;
    
    /**
     * Initialize a user object with the user info.
     * @param userID The users userID.
     * @param employeeID The users employeeID.
     * @param username The users username.
     * @param accessLevel The users access level.
     */
    public User(int userID, int employeeID, String username, short accessLevel) {
        this.id          = userID;
        this.employeeID  = employeeID;
        this.username    = username;
        this.accessLevel = accessLevel;
    }
    
    /**
     * Gets the userID of the user.
     * @return The users user id.
     */
    public int getID() {
        return id;
    }
    
    /**
     * Gets the employeeID of the user.
     * @return The users employee id.
     */
    public int getEmployeeID() {
        return employeeID;
    }
    
    /**
     * Gets the username of the user.
     * @return The users username.
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Get the access level of the user.
     * @return The users access level.
     */
    public short getAccessLevel() {
        return accessLevel;
    }
}
