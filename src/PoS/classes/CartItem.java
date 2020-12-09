/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PoS.classes;

import java.math.BigDecimal;

/**
 * Represents an item in a cart.
 * @author Dustin Gritman
 */
public class CartItem extends InventoryItem {
    private int quantity;
    
    /**
     * Initializes a cart item with the values.
     * @param quantity The amount of the item currently in the cart.
     * @param serialNumber The items serial number.
     * @param manufacturer The items manufacturer.
     * @param modelNumber The items model number.
     * @param modelName The items model name.
     * @param description The items description.
     * @param supplierLink The suppliers link for information.
     * @param cost The cost of the product.
     * @param listPrice The listing price for the item.
     * @param msrp The Manufacturer Suggested Retail Price of the item.
     * @param currentStock The current amount of the item in stock.
     * @param baseStock The amount of items in stock that is considered low.
     * @param reorderAmt The amount of the item to reorder.
     */
    public CartItem(int quantity, String serialNumber, String manufacturer
            , int modelNumber, String modelName, String description
            , String supplierLink, BigDecimal cost, BigDecimal listPrice
            , BigDecimal msrp, int currentStock, int baseStock, int reorderAmt) {
        super(serialNumber, manufacturer, modelNumber, modelName, description
                , supplierLink, cost, listPrice, msrp, currentStock
                , baseStock, reorderAmt);
        this.quantity = quantity;
    }
    
    /**
     * Initializes a cart item with the values.
     * @param item The inventory item.
     * @param quantity The amount of the item currently in the cart.
     */
    public CartItem(InventoryItem item, int quantity) {
        this(quantity, item.getSerialNumber(), item.getManufacturer()
                , item.getModelNumber(), item.getModelName(), item.getDescription()
                , item.getSupplierLink(), item.getCost(), item.getListPrice()
                , item.getMSRP(), item.getStock(), item.getBaseStock()
                , item.getReorderAmount());
    }
    
    void updateQuantity(int amount) {
        quantity += amount;
    }
    
    /**
     * Get the quantity of the item in the cart.
     * @return The quantity of the item.
     */
    public int getQuantity() {
        return quantity;
    }
}
