package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

// {
//     "id": "string",
//     "origin": "string",
//     "originOrderNumber": "string",
//     "originInvoiceType": "string",
//     "originOrderDate": "string",
//     "createdAt": "string",
//     "customer": {},
//     "businessInvoice": "string",
//     "billingAddress": {},
//     "subOrders": []
//   }
public class CSOrder {
    private String id;
    private String origin;
    private String originOrderNumber;
    private String originInvoiceType;
    private String originOrderDate;
    private String createdAt;
    private CSCustomer customer;
    private String businessInvoice;
    private CSAddress billingAddress;
    private CSSubOrder[] subOrders;
    // getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getOriginOrderNumber() {
        return originOrderNumber;
    }

    public void setOriginOrderNumber(String originOrderNumber) {
        this.originOrderNumber = originOrderNumber;
    }

    public String getOriginInvoiceType() {
        return originInvoiceType;
    }

    public void setOriginInvoiceType(String originInvoiceType) {
        this.originInvoiceType = originInvoiceType;
    }

    public String getOriginOrderDate() {
        return originOrderDate;
    }

    public void setOriginOrderDate(String originOrderDate) {
        this.originOrderDate = originOrderDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public CSCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(CSCustomer customer) {
        this.customer = customer;
    }

    public String getBusinessInvoice() {
        return businessInvoice;
    }

    public void setBusinessInvoice(String businessInvoice) {
        this.businessInvoice = businessInvoice;
    }

    public CSAddress getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(CSAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    public CSSubOrder[] getSubOrders() {
        return subOrders;
    }

    public void setSubOrders(CSSubOrder[] subOrders) {
        this.subOrders = subOrders;
    }

}
