package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CSItemTest {
    // {
    // "id": "string",
    // "sku": "string",
    // "name": "string",
    // "sellerId": "string",
    // "jdaSku": "string",
    // "basePrice": "string",
    // "grossPrice": "string",
    // "taxRate": "string",
    // "size": "string",
    // "sellerSku": "string",
    // "position": 0,
    // "subOrderNumber": "string",
    // "cancellationReasonId": "string",
    // "statusId": 0,
    // "imagePath": "string",
    // "itemSize": "string",
    // "orderId": "string",
    // "userId": "string",
    // "category": "string",
    // "categoryId": "string",
    // "status": {
    // "id": 0,
    // "name": "string",
    // "description": "string"
    // },
    // "cancellationReason": "string"
    // }

    private String json = "{\r \"id\": \"string\",\r\"sku\": \"string\",\r\"name\": \"string\",\r\"sellerId\": \"string\",\r\"jdaSku\": \"string\",\r\"basePrice\": \"string\",\r\"grossPrice\": \"string\",\r\"taxRate\": \"string\",\r\"size\": \"string\",\r\"sellerSku\": \"string\",\r\"position\": 0,\r\"subOrderNumber\": \"string\",\r\"cancellationReasonId\": \"string\",\r\"statusId\": 0,\r\"imagePath\": \"string\",\r\"itemSize\": \"string\",\r\"orderId\": \"string\",\r\"userId\": \"string\",\r\"category\": \"string\",\r\"categoryId\": \"string\",\r\"status\": {\r \"id\": 0,\r\"name\": \"string\",\r\"description\": \"string\"\r},\r\"cancellationReason\": \"string\"\r}";

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCSItem() throws JsonProcessingException {

        CSItem item = mapper.readValue(json, CSItem.class);
        assert (item != null);
        assert (item.getId().equals("string"));
        assert (item.getSku().equals("string"));
        assert (item.getName().equals("string"));
        assert (item.getSellerId().equals("string"));
        assert (item.getJdaSku().equals("string"));
        assert (item.getBasePrice().equals("string"));
        assert (item.getGrossPrice().equals("string"));
        assert (item.getTaxRate().equals("string"));
        assert (item.getSize().equals("string"));
        assert (item.getSellerSku().equals("string"));
        assert (item.getPosition() == 0);
        assert (item.getSubOrderNumber().equals("string"));
        assert (item.getCancellationReasonId().equals("string"));
        assert (item.getStatusId() == 0);
        assert (item.getImagePath().equals("string"));
        assert (item.getItemSize().equals("string"));
        assert (item.getOrderId().equals("string"));
        assert (item.getUserId().equals("string"));
        assert (item.getCategory().equals("string"));
        assert (item.getCategoryId().equals("string"));
        assert (item.getStatus() != null);
        assert (item.getStatus().getId() == 0);
        assert (item.getStatus().getName().equals("string"));
        assert (item.getStatus().getDescription().equals("string"));
        assert (item.getCancellationReason().equals("string"));
    }
}
