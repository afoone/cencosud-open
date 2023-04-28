package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CSStatusTest {
    // {
    // "id": 0,
    // "name": "string",
    // "description": "string"
    // }

    private String json = "{\r \"id\": 0,\r\"name\": \"string\",\r\"description\": \"string\"\r}";

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCSStatus() throws JsonProcessingException {

        CSStatus status = mapper.readValue(json, CSStatus.class);
        assert (status != null);
        assert (status.getId() == 0);
        assert (status.getName().equals("string"));
        assert (status.getDescription().equals("string"));
    }
}
