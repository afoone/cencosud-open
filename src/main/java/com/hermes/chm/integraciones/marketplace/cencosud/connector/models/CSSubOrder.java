package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

// {
//     "id": 0,
//     "orderId": "string",
//     "subOrderNumber": "string",
//     "statusId": 0,
//     "carrier": "string",
//     "trackingNumber": "string",
//     "labelUrl": "string",
//     "labelId": "string",
//     "deliveryExternalId": "string",
//     "dispatchDate": "2019-08-24",
//     "arrivalDate": "2019-08-24",
//     "arrivalDateEnd": "2019-08-24",
//     "effectiveArrivalDate": "string",
//     "effectiveDispatchDate": "string",
//     "effectiveManifestDate": "string",
//     "lastNotificationId": "string",
//     "fulfillment": "string",
//     "centryId": "string",
//     "cost": "string",
//     "updatedAt": "string",
//     "oldShipmentId": "string",
//     "carrierSystemId": 0,
//     "tracking": [],
//     "deliveryOption": {},
//     "status": {},
//     "shippingAddress": {},
//     "items": []
//     }
public class CSSubOrder {
    private int id;
    private String orderId;
    private String subOrderNumber;
    private int statusId;
    private String carrier;
    private String trackingNumber;
    private String labelUrl;
    private String labelId;
    private String deliveryExternalId;
    private String dispatchDate;
    private String arrivalDate;
    private String arrivalDateEnd;
    private String effectiveArrivalDate;
    private String effectiveDispatchDate;
    private String effectiveManifestDate;
    private String lastNotificationId;
    private String fulfillment;
    private String centryId;
    private String cost;
    private String updatedAt;
    private String oldShipmentId;
    private int carrierSystemId;
    private String[] tracking;
    private CSDeliveryOption deliveryOption;
    private CSStatus status;
    private CSAddress shippingAddress;
    private CSItem[] items;

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSubOrderNumber() {
        return subOrderNumber;
    }

    public void setSubOrderNumber(String subOrderNumber) {
        this.subOrderNumber = subOrderNumber;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getLabelUrl() {
        return labelUrl;
    }

    public void setLabelUrl(String labelUrl) {
        this.labelUrl = labelUrl;
    }

    public String getLabelId() {
        return labelId;
    }

    public void setLabelId(String labelId) {
        this.labelId = labelId;
    }

    public String getDeliveryExternalId() {
        return deliveryExternalId;
    }

    public void setDeliveryExternalId(String deliveryExternalId) {
        this.deliveryExternalId = deliveryExternalId;
    }

    public String getDispatchDate() {
        return dispatchDate;
    }

    public void setDispatchDate(String dispatchDate) {
        this.dispatchDate = dispatchDate;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getArrivalDateEnd() {
        return arrivalDateEnd;
    }

    public void setArrivalDateEnd(String arrivalDateEnd) {
        this.arrivalDateEnd = arrivalDateEnd;
    }

    public String getEffectiveArrivalDate() {
        return effectiveArrivalDate;
    }

    public void setEffectiveArrivalDate(String effectiveArrivalDate) {
        this.effectiveArrivalDate = effectiveArrivalDate;
    }

    public String getEffectiveDispatchDate() {
        return effectiveDispatchDate;
    }

    public void setEffectiveDispatchDate(String effectiveDispatchDate) {
        this.effectiveDispatchDate = effectiveDispatchDate;
    }

    public String getEffectiveManifestDate() {
        return effectiveManifestDate;
    }

    public void setEffectiveManifestDate(String effectiveManifestDate) {
        this.effectiveManifestDate = effectiveManifestDate;
    }

    public String getLastNotificationId() {
        return lastNotificationId;
    }

    public void setLastNotificationId(String lastNotificationId) {
        this.lastNotificationId = lastNotificationId;
    }

    public String getFulfillment() {
        return fulfillment;
    }

    public void setFulfillment(String fulfillment) {
        this.fulfillment = fulfillment;
    }

    public String getCentryId() {
        return centryId;
    }

    public void setCentryId(String centryId) {
        this.centryId = centryId;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getOldShipmentId() {
        return oldShipmentId;
    }

    public void setOldShipmentId(String oldShipmentId) {
        this.oldShipmentId = oldShipmentId;
    }

    public int getCarrierSystemId() {
        return carrierSystemId;
    }

    public void setCarrierSystemId(int carrierSystemId) {
        this.carrierSystemId = carrierSystemId;
    }

    public String[] getTracking() {
        return tracking;
    }

    public void setTracking(String[] tracking) {
        this.tracking = tracking;
    }

    public CSDeliveryOption getDeliveryOption() {
        return deliveryOption;
    }

    public void setDeliveryOption(CSDeliveryOption deliveryOption) {
        this.deliveryOption = deliveryOption;
    }

    public CSStatus getStatus() {
        return status;
    }

    public void setStatus(CSStatus status) {
        this.status = status;
    }

    public CSAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(CSAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public CSItem[] getItems() {
        return items;
    }

    public void setItems(CSItem[] items) {
        this.items = items;
    }

}
