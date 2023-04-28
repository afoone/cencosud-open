package com.hermes.chm.integraciones.marketplace.cencosud.connector.models.responses;
// {
//     //       "quantity": 0,
//     //       "limit": 0,
//     //       "offset": 0
//     //     },
public class CSProductsPagging {
    private int quantity;
    private int limit;
    private int offset;
    // getters and setters
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public int getLimit() {
        return limit;
    }
    public void setLimit(int limit) {
        this.limit = limit;
    }
    public int getOffset() {
        return offset;
    }
    public void setOffset(int offset) {
        this.offset = offset;
    }
    
}
