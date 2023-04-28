package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CSPriceTest {
    // {
    // "id": "string",
    // "code": "string",
    // "name": "string"
    // }

    private String json = "{\r \"id\": \"string\",\r\"code\": \"string\",\r\"name\": \"string\"\r}";

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCSPrice() throws JsonProcessingException {

        CSPrice price = mapper.readValue(json, CSPrice.class);
        assert (price != null);
        assert (price.getId().equals("string"));
        assert (price.getCode().equals("string"));
        assert (price.getName().equals("string"));
    }
}
