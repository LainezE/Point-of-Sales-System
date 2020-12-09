/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PoS.classes;

/**
 *
 * @author Eli Lainez
 */
public class Vendor {
    private int    id;
    private String name;
    private String phone;
    private String email;
    private String street;
    private String city;
    private String state;
    private int    zipcode;
    
    /**
     * 
     * @param vendorID
     * @param vendorName
     * @param phoneNumber
     * @param email
     * @param street
     * @param city
     * @param state
     * @param zipcode 
     */
    public Vendor(int vendorID, String vendorName, String phoneNumber
            , String email, String street, String city, String state, int zipcode) {
        this.id      = vendorID;
        this.name    = vendorName;
        this.phone   = phone;
        this.street  = street;
        this.city    = city;
        this.state   = state;
        this.zipcode = zipcode;
        this.email   = email;
    }
    
    /**
     * 
     * @return 
     */
    public int getID() {
        return id;
    }
    
    /**
     * 
     * @return 
     */
    public String getName() {
        return name;
    }
    
    /**
     * 
     * @return 
     */
    public String getPhone() {
        return phone;
    }
    
    /**
     * 
     * @return 
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * 
     * @return 
     */
    public String getStreet() {
        return street;
    }
    
    /**
     * 
     * @return 
     */
    public String getCity() {
        return city;
    }
    
    /**
     * 
     * @return 
     */
    public String getState() {
        return state;
    }
    
    /**
     * 
     * @return 
     */
    public int getZipcode() {
        return zipcode;
    }
}
