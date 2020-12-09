/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PoS.classes;

import java.math.BigDecimal;

/**
 * Represents an item in a PoS inventory.
 * @author Dustin Gritman
 */
public class InventoryItem {
    private String serialNum;
    private String manufacturer;
    private int    modelNumber;
    private String modelName;
    private String description;
    private String supplierLink;
    private BigDecimal cost;
    private BigDecimal listPrice;
    private BigDecimal msrp;
    private int    stock;
    private int    baseStock;
    private int    reorderAmt;
    
    /**
     * Initialize an inventory item object with the values given.
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
    public InventoryItem(String serialNumber, String  manufacturer, int modelNumber
            , String modelName, String description, String supplierLink
            , BigDecimal cost, BigDecimal listPrice, BigDecimal msrp
            , int currentStock, int baseStock, int reorderAmt) {
        this.serialNum    = serialNumber;
        this.manufacturer = manufacturer;
        this.modelNumber  = modelNumber;
        this.modelName    = modelName;
        this.description  = description;
        this.supplierLink = supplierLink;
        this.cost         = cost;
        this.listPrice    = listPrice;
        this.msrp         = msrp;
        this.stock        = currentStock;
        this.baseStock    = baseStock;
        this.reorderAmt   = reorderAmt;
    }
    
    
    /**
     * Get the items serial number.
     * @return The items serial number.
     */
    public String getSerialNumber() {
        return serialNum;
    }
    
    /**
     * Get the items manufacturer.
     * @return The items manufacturer.
     */
    public String getManufacturer() {
        return manufacturer;
    }
    
    /**
     * Get the items model number.
     * @return The items model number.
     */
    public int getModelNumber() {
        return modelNumber;
    }
    
    /**
     * Get the items model name.
     * @return The items model name.
     */
    public String getModelName() {
        return modelName;
    }
    
    /**
     * Get the items description.
     * @return The items description.
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Get the items supplier link for item data.
     * @return The items supplier link.
     */
    public String getSupplierLink() {
        return supplierLink;
    }
    
    /**
     * Get the cost of the item.
     * @return The items cost.
     */
    public BigDecimal getCost() {
        return cost;
    }
    
    /**
     * Get the items listed sale price.
     * @return The items list price.
     */
    public BigDecimal getListPrice() {
        return listPrice;
    }
    
    /**
     * Get the items manufacturer suggested retail price.
     * @return The items MSRP.
     */
    public BigDecimal getMSRP() {
        return msrp;
    }
    
    /**
     * Get the number of items currently in stock.
     * @return The number of items in stock.
     */
    public int getStock() {
        return stock;
    }
    
    /**
     * Get the items base stock amount.
     * @return The items base stock amount.
     */
    public int getBaseStock() {
        return baseStock;
    }
    
    /**
     * Get the amount of items to reoder when low.
     * @return The items reorder amount.
     */
    public int getReorderAmount() {
        return reorderAmt;
    }
}
