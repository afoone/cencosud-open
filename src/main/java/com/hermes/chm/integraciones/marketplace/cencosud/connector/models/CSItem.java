package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

// {
//     "id": "string",
//     "sku": "string",
//     "name": "string",
//     "sellerId": "string",
//     "jdaSku": "string",
//     "basePrice": "string",
//     "grossPrice": "string",
//     "taxRate": "string",
//     "size": "string",
//     "sellerSku": "string",
//     "position": 0,
//     "subOrderNumber": "string",
//     "cancellationReasonId": "string",
//     "statusId": 0,
//     "imagePath": "string",
//     "itemSize": "string",
//     "orderId": "string",
//     "userId": "string",
//     "category": "string",
//     "categoryId": "string",
//     "status": {
//     "id": 0,
//     "name": "string",
//     "description": "string"
//     },
//     "cancellationReason": "string"
//     }
public class CSItem {
    private String id;
    private String sku;
    private String name;
    private String sellerId;
    private String jdaSku;
    private String basePrice;
    private String grossPrice;
    private String taxRate;
    private String size;
    private String sellerSku;
    private int position;
    private String subOrderNumber;
    private String cancellationReasonId;
    private int statusId;
    private String imagePath;
    private String itemSize;
    private String orderId;
    private String userId;
    private String category;
    private String categoryId;
    private CSStatus status;
    private String cancellationReason;

    // getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getJdaSku() {
        return jdaSku;
    }

    public void setJdaSku(String jdaSku) {
        this.jdaSku = jdaSku;
    }

    public String getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(String basePrice) {
        this.basePrice = basePrice;
    }

    public String getGrossPrice() {
        return grossPrice;
    }

    public void setGrossPrice(String grossPrice) {
        this.grossPrice = grossPrice;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSellerSku() {
        return sellerSku;
    }

    public void setSellerSku(String sellerSku) {
        this.sellerSku = sellerSku;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getSubOrderNumber() {
        return subOrderNumber;
    }

    public void setSubOrderNumber(String subOrderNumber) {
        this.subOrderNumber = subOrderNumber;
    }

    public String getCancellationReasonId() {
        return cancellationReasonId;
    }

    public void setCancellationReasonId(String cancellationReasonId) {
        this.cancellationReasonId = cancellationReasonId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getItemSize() {
        return itemSize;
    }

    public void setItemSize(String itemSize) {
        this.itemSize = itemSize;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public CSStatus getStatus() {
        return status;
    }

    public void setStatus(CSStatus status) {
        this.status = status;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

}
