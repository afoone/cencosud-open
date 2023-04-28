package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CSOrderTest {
    // {
    // "id": "string",
    // "origin": "string",
    // "originOrderNumber": "string",
    // "originInvoiceType": "string",
    // "originOrderDate": "string",
    // "createdAt": "string",
    // "customer": {},
    // "businessInvoice": "string",
    // "billingAddress": {},
    // "subOrders": []
    // }

    private String json = "{\r \"id\": \"string\",\r\"origin\": \"string\",\r\"originOrderNumber\": \"string\",\r\"originInvoiceType\": \"string\",\r\"originOrderDate\": \"string\",\r\"createdAt\": \"string\",\r\"customer\": {},\r\"businessInvoice\": \"string\",\r\"billingAddress\": {},\r\"subOrders\": []\r}";

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCSOrder() throws JsonProcessingException {

        CSOrder order = mapper.readValue(json, CSOrder.class);
        assert (order != null);
        assert (order.getId().equals("string"));
        assert (order.getOrigin().equals("string"));
        assert (order.getOriginOrderNumber().equals("string"));
        assert (order.getOriginInvoiceType().equals("string"));
        assert (order.getOriginOrderDate().equals("string"));
        assert (order.getCreatedAt().equals("string"));
        assert (order.getCustomer() != null);
        assert (order.getBusinessInvoice().equals("string"));
        assert (order.getBillingAddress() != null);
        assert (order.getSubOrders() != null);
    }
}
