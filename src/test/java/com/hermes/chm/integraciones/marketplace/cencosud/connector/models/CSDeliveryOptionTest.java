package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CSDeliveryOptionTest {
    // {
    // "id": 0,
    // "name": "string",
    // "description": "string"
    // }

    private String json = "{\r \"id\": 0,\r\"name\": \"string\",\r\"description\": \"string\"\r}";

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCSDeliveryOption() throws JsonProcessingException {

        CSDeliveryOption deliveryOption = mapper.readValue(json, CSDeliveryOption.class);
        assert (deliveryOption != null);
        assert (deliveryOption.getId() == 0);
        assert (deliveryOption.getName().equals("string"));
        assert (deliveryOption.getDescription().equals("string"));
    }
}
