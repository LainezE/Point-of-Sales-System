/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PoS.classes;

import java.util.ArrayList;

/**
 *
 * @author Dustin Gritman
 */
public class CustomerCart extends Cart {
    private int customerID;
    
    /**
     * Initializes a cart with the cart's information and items.
     * @param cartID The carts unique id.
     * @param customerID The customers id that the cart belongs to.
     * @param employeeID The employees id that created the cart.
     * @param cartItems  The items currently in the cart.
     */
    public CustomerCart(int cartID, int customerID, int employeeID, ArrayList<CartItem> cartItems) {
        super(cartID, employeeID, cartItems);
        this.customerID = customerID;
    }
    
    /**
     * Get the customer id that the cart belongs to.
     * @return The customers id.
     */
    public int getCustomerID() {
        return customerID;
    }
}
