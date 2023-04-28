package com.hermes.chm.integraciones.marketplace.cencosud.connector.models.payloads;

import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSItem;

/**
 * {
 * "sellerId": "string",
 * "categoryCode": "string",
 * "items": [
 * {
 * "sku": "MK2OLGESOZ-1",
 * "status": 2
 * },
 * {
 * "sku": "MKRP52HOC5-4",
 * "status": 1
 * }
 * ],
 * "orderStatus": "string",
 * "limit": 0
 * }
 */

public class CSOrderCreatePayload {
    private String sellerId;
    private String categoryCode;
    private CSItem[] items;
    private String orderStatus;
    private int limit;

    // getters and setters
    public String getSellerId() {
        return sellerId;
    }
    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
    public String getCategoryCode() {
        return categoryCode;
    }
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }
    public CSItem[] getItems() {
        return items;
    }
    public void setItems(CSItem[] items) {
        this.items = items;
    }
    public String getOrderStatus() {
        return orderStatus;
    }
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    public int getLimit() {
        return limit;
    }
    public void setLimit(int limit) {
        this.limit = limit;
    }
    

}
