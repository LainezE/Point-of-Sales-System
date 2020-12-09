/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PoS.classes;

import java.util.ArrayList;

/**
 *
 * @author Eli Lainez
 */
public class VendorCart extends Cart {
    private int vendorID;
    
    /**
     * Initializes a cart with the cart's information and items.
     * @param cartID The carts unique id.
     * @param vendorID The vendors id that the cart belongs to.
     * @param employeeID The employees id that created the cart.
     * @param cartItems  The items currently in the cart.
     */
    public VendorCart(int cartID, int vendorID, int employeeID
            , ArrayList<CartItem> cartItems) {
        super(cartID, employeeID, cartItems);
        this.vendorID = vendorID;
    }
    
    /**
     * Gets the carts vendor id.
     * @return The vendor id of the cart.
     */
    public int getVendorID() {
        return vendorID;
    }
}
