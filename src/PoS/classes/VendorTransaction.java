/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PoS.classes;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 *
 * @author Eli Lainez
 */
public class VendorTransaction {
    private int        id;
    private int        vendorID;
    private int        employeeID;
    private BigDecimal subtotal;
    private BigDecimal total;
    private String     paymentType;
    private ArrayList<CartItem> items;
    
    /**
     * 
     * @param transactionID
     * @param vendorID
     * @param employeeID
     * @param subtotal
     * @param total
     * @param paymentType 
     */
    public VendorTransaction(int transactionID, int vendorID, int employeeID
            , BigDecimal subtotal, BigDecimal total, String paymentType
            , ArrayList<CartItem> items) {
        this.id          = transactionID;
        this.vendorID    = vendorID;
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
    public int getVendorID() {
        return vendorID;
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
