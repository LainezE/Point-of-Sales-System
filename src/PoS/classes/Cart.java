/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PoS.classes;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Represents a PoS cart.
 * @author Dustin Gritman
 */
public class Cart {
    private int        id;
    private int        employeeID;
    private ArrayList<CartItem> items;
    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal tax;
    private BigDecimal total;
    
    /**
     * Initializes a cart with the cart's information and items.
     * @param cartID The carts unique id.
     * @param employeeID The employees id that created the cart.
     * @param cartItems  The items currently in the cart.
     */
    public Cart(int cartID, int employeeID, ArrayList<CartItem> cartItems) {
        this.id = cartID;
        this.employeeID = employeeID;
        if (cartItems == null)
            cartItems = new ArrayList<>();
        items = cartItems;
        subtotal = BigDecimal.valueOf(0);
        items.forEach((item) -> {
            subtotal.add(item.getListPrice());
        });
        taxRate = BigDecimal.valueOf(0.08);
        updateTotal();
    }
    
    /**
     * Set the tax rate of the items in the cart and update the total with this rate.
     */
    private void updateTotal() {
        tax = subtotal.multiply(taxRate);
        total = subtotal.add(tax);
    }
    
    /**
     * Set the tax rate of the items in the cart and update the total with this rate.
     * @param newTaxRate The new tax rate to use.
     */
    public void setTaxRate(BigDecimal newTaxRate) {
        taxRate = newTaxRate;
        updateTotal();
    }
    
    /**
     * Add an item to the cart.
     * @param inventoryItem The item to add to the cart.
     * @param quantity The quantity of the item to add to the cart.
     */
    public void addItem(InventoryItem inventoryItem, int quantity) {
        if (quantity < 0)
            return;
        
        for (CartItem item : items)
            if (inventoryItem.getSerialNumber().equals(item.getSerialNumber())) {
                updateItem(item.getSerialNumber(), quantity);
                return;
            }
        CartItem newItem = new CartItem(inventoryItem, quantity);
        items.add(newItem);
        subtotal = subtotal.add(newItem.getListPrice().multiply(BigDecimal.valueOf(quantity)));
        updateTotal();
    }
    
    /**
     * Add/Remove the quantity of the item.
     * @param serialNumber The serialnumber of the item to update the quantity of.
     * @param quantity The quantity to adjust.
     */
    public void updateItem(String serialNumber, int quantity) {
        for (CartItem item : items)
            if (serialNumber.equals(item.getSerialNumber())) {
                if (item.getQuantity() + quantity > 0) {
                    subtotal = subtotal.add((BigDecimal.valueOf(quantity)).multiply(item.getListPrice()));
                    item.updateQuantity(quantity);
                }
                else {
                    subtotal = subtotal.add((BigDecimal.valueOf(-item.getQuantity())).multiply(item.getListPrice()));
                    items.remove(item);
                }
                updateTotal();
                return;
            }
    }
    
    /**
     * Set the items quantity in the cart.
     * @param serialNumber The item's serialnumber to update quantity of.
     * @param quantity The item's quantity in cart.
     */
    public void setItemQuantity(String serialNumber, int quantity) {
        if (quantity < 1)
            return;
        
        for (CartItem item : items)
            if (serialNumber.equals(item.getSerialNumber())) {
                int quantDiff = quantity - item.getQuantity();
                subtotal = subtotal.add((BigDecimal.valueOf(quantDiff)).multiply(item.getListPrice()));
                item.updateQuantity(quantDiff);
                updateTotal();
                return;
            }
    }
    
    /**
     * Get the carts unique id.
     * @return The carts id.
     */
    public int getID() {
        return id;
    }
    
    /**
     * Get the employees id who is working on the cart.
     * @return The employees id.
     */
    public int getEmployeeID() {
        return employeeID;
    }
    
    /**
     * Get an array of the items currently in the cart.
     * @return An arraylist of cart items that are in the cart.
     */
    public ArrayList<CartItem> getItems() {
        return (ArrayList<CartItem>) items.clone();
    }
    
    /**
     * Get the current subtotal of the cart.
     * @return The subtotal of the cart.
     */
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    /**
     * Get the carts current total based on the tax rate.
     * @return The total of the cart.
     */
    public BigDecimal getTotal() {
        return total;
    }
    
    /**
     * Get the current tax rate of the cart.
     * @return The current tax rate.
     */
    public BigDecimal getTax() {
        return tax;
    }
    
    /**
     * Get the current tax rate of the cart.
     * @return The current tax rate.
     */
    public BigDecimal getTaxRate() {
        return taxRate;
    }
}
