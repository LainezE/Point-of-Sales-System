/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PoS.database;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * The database connection class.
 * @author Dustin Gritman
 */
public class DBConn {
    private static String address = "cs.newpaltz.edu:3306";
    private static String name = "se_19f_g03_db";
    private static String username = "se_19f_g03";
    private static String password = "s57bvw";
    
    /**
     * Initialize a DBInterface with specified connection info.
     * @param dbAddress The database address and port Ex. localhost:3306
     * @param dbName The database name to connect to.
     * @param dbUsername The username to connect with.
     * @param dbPassword The password to connect with.
     */
    public static void setDBInfo(String dbAddress, String dbName, String dbUsername, String dbPassword) {
        address  = dbAddress;
        name     = dbName;
        username = dbUsername;
        password = dbPassword;
    }
    
    /**
     * Setup a connection to the database, this connection must be closed.
     * @return An sql connection object that must be closed.
     */
    static Connection createConnection() {
        Connection con = null;
        
        try {
            // Setup connection to the database
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            con = DriverManager.getConnection("jdbc:mysql://" + address + "/" 
                    + name + "?serverTimezone=EST"
                    , username, password
            );
        } catch (ClassNotFoundException e) {
            System.out.println("Missing sql driver!");
        } catch (SQLException e) {
            System.out.println("Error connecting to server/database!");
        }
        
        return con;
    }
}
