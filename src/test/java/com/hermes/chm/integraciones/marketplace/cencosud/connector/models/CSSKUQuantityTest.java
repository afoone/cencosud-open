package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CSSKUQuantityTest {
    // {
    // "sku": "MKRP52HOC5-4",
    // "quantity": 1
    // }
    private String json = "{\r \"sku\": \"MKRP52HOC5-4\",\r\"quantity\": 1\r}";

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCSSKUQuantity() throws JsonProcessingException {

        CSSKUQuantity skuQuantity = mapper.readValue(json, CSSKUQuantity.class);
        assert (skuQuantity != null);
        assert (skuQuantity.getSku().equals("MKRP52HOC5-4"));
        assert (skuQuantity.getQuantity() == 1);
    }
}
