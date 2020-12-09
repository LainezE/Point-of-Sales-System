/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PoS.classes;

/**
 * Represents an employee.
 * @author Dustin Gritman
 */
public class Employee {
    private int    id;
    private String fname;
    private String lname;
    private String title;
    private String phone;
    private String email;
    private String street;
    private String city;
    private String state;
    private int    zipcode;
    
    /**
     * Initialize an employee with all information for the employee.
     * @param employeeID The employees id.
     * @param firstName The employees first name.
     * @param lastName The employees last name.
     * @param title The new employees position title.
     * @param phoneNumber The employees phone number.
     * @param email The employees email address.
     * @param street The employees street.
     * @param city The employees city.
     * @param state The employees state. (Ex. NY)
     * @param zipcode The employees zip code.
     */
    public Employee(int employeeID, String firstName, String lastName
            , String title, String phoneNumber, String email, String street
            , String city, String state, int zipcode) {
        this.id      = employeeID;
        this.fname   = firstName;
        this.lname   = lastName;
        this.title   = title;
        this.phone   = phoneNumber;
        this.email   = email;
        this.street  = street;
        this.city    = city;
        this.state   = state;
        this.zipcode = zipcode;
    }
    
    /**
     * Gets the employees employee id.
     * @return The employees employee id.
     */
    public int getID() {
        return this.id;
    }
    
    /**
     * Gets the employees first name.
     * @return The employees first name.
     */
    public String getFirstName() {
        return this.fname;
    }
    
    /**
     * Gets the employees last name.
     * @return The employees last name.
     */
    public String getLastName() {
        return this.lname;
    }
    
    /**
     * Gets the employees title.
     * @return The employees title.
     */
    public String getTitle() {
        return this.title;
    }
    
    /**
     * Gets the employees current phone number.
     * @return The employees phone number.
     */
    public String getPhone() {
        return this.phone;
    }
    
    /**
     * Gets the employees current email address.
     * @return The employees email address.
     */
    public String getEmail() {
        return this.email;
    }
    
    /**
     * Gets the employees current street.
     * @return The employees street.
     */
    public String getStreet() {
        return this.street;
    }
    
    /**
     * Gets the employees current city.
     * @return The employees city.
     */
    public String getCity() {
        return this.city;
    }
    
    /**
     * Gets the employees current state.
     * @return The employees state.
     */
    public String getState() {
        return this.state;
    }
    
    /**
     * Gets the employees current zip code.
     * @return The employees zip code.
     */
    public int getZipcode() {
        return this.zipcode;
    }
}
