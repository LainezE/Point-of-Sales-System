/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PoS.classes;

/**
 * Represents a customer.
 * @author Dustin Gritman
 */
public class Customer {
    private int    id;
    private String fname;
    private String lname;
    private String phone;
    private String email;
    private String street;
    private String city;
    private String state;
    private int    zipcode;
    private String joinDate;
    
    /**
     * Initialize a customer object with the values provided.
     * @param customerID The customers id.
     * @param firstName The customers first name.
     * @param lastName The customers last name.
     * @param phoneNumber The customers phone number.
     * @param email The customers email address.
     * @param street The customers street address.
     * @param city The customers city.
     * @param state The customers state. (Ex. NY)
     * @param zipcode The customers zip code.
     * @param joinDate The customers join date.
     */
    public Customer(int customerID, String firstName, String lastName
            , String phoneNumber, String email, String street, String city
            , String state, int zipcode, String joinDate) {
        this.id       = customerID;
        this.fname    = firstName;
        this.lname    = lastName;
        this.phone    = phoneNumber;
        this.email    = email;
        this.street   = street;
        this.city     = city;
        this.state    = state;
        this.zipcode  = zipcode;
        this.joinDate = joinDate;
    }
    
    /**
     * 
     * @return 
     */
    public int getID() {
        return id;
    }
    
    /**
     * Get the customers first name.
     * @return The customers first name.
     */
    public String getFirstName() {
        return fname;
    }
    
    /**
     * Get the customers last name.
     * @return The customers last name.
     */
    public String getLastName() {
        return lname;
    }
    
    /**
     * Get the customers phone number.
     * @return The customer phone number.
     */
    public String getPhone() {
        return phone;
    }
    
    /**
     * Get the customers email address.
     * @return The customers email address.
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Get the customers current street.
     * @return The customers current street.
     */
    public String getStreet() {
        return street;
    }
    
    /**
     * Get the customers current city.
     * @return The customers current city.
     */
    public String getCity() {
        return city;
    }
    
    /**
     * Get the customers current state.
     * @return The customers current state.
     */
    public String getState() {
        return state;
    }
    
    /**
     * Get the customers current zip code.
     * @return The customers current zip code.
     */
    public int getZipcode() {
        return zipcode;
    }

    /**
     * Get the date that the customer joined.
     * @return The date that the customer joined.
     */
    public String getJoinDate() {
        return joinDate;
    }
}
