package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

// {
//     "id": "string",
//     "value": 0,
//     "price": {
//       "id": "string",
//       "code": "string",
//       "name": "string"
//     },
//     "store": {
//       "id": "string",
//       "code": "string",
//       "name": "string"
//     },
//     "showFrom": "2019-08-24T14:15:22Z",
//     "showTo": "2019-08-24T14:15:22Z",
//     "status": "string"
//   }
public class CSProductPrice {
    private String id;
    private double value;
    private CSPrice price;
    private CSCode store;
    private String showFrom;
    private String showTo;
    private String status;

    // getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public CSPrice getPrice() {
        return price;
    }

    public void setPrice(CSPrice price) {
        this.price = price;
    }

    public CSCode getStore() {
        return store;
    }

    public void setStore(CSCode store) {
        this.store = store;
    }

    public String getShowFrom() {
        return showFrom;
    }

    public void setShowFrom(String showFrom) {
        this.showFrom = showFrom;
    }

    public String getShowTo() {
        return showTo;
    }

    public void setShowTo(String showTo) {
        this.showTo = showTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
