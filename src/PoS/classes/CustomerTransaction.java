/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PoS.classes;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Represents a customer transaction.
 * @author Eli Lainez
 */
public class CustomerTransaction {
    private int    id;
    private int    customerID;
    private int    employeeID;
    private BigDecimal subtotal;
    private BigDecimal total;
    private String paymentType;
    private ArrayList<CartItem> items;
    
    /**
     * 
     * @param transactionID
     * @param cart
     * @param paymentType 
     */
    public CustomerTransaction(int transactionID, CustomerCart cart, String paymentType) {
        this(transactionID, cart.getCustomerID(), cart.getEmployeeID()
                , cart.getSubtotal(), cart.getTotal(), paymentType, cart.getItems());
    }
    
    /**
     * 
     * @param transactionID
     * @param customerID
     * @param employeeID
     * @param subtotal
     * @param total
     * @param paymentType
     * @param items 
     */
    public CustomerTransaction(int transactionID, int customerID, int employeeID
            , BigDecimal subtotal, BigDecimal total, String paymentType, ArrayList<CartItem> items) {
        this.id          = transactionID;
        this.customerID  = customerID;
        this.employeeID  = employeeID;
        this.subtotal    = subtotal;
        this.total       = total;
        this.paymentType = paymentType;
        this.items       = items;
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
    public int getCustomerID() {
        return customerID;
    }
    
    /**
     * 
     * @return 
     */
    public int getEmployeeID() {
        return employeeID;
    }
    
    /**
     * 
     * @return 
     */
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    /**
     * 
     * @return 
     */
    public BigDecimal getTotal() {
        return total;
    }
    
    /**
     * 
     * @return 
     */
    public String getPaymentType() {
        return paymentType;
    }
    
    /**
     * 
     * @return 
     */
    public ArrayList<CartItem> getItems() {
        return (ArrayList<CartItem>) items.clone();
    }
}
