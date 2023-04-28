package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CSFamilyTest {
    // {
    // "id": "string",
    // "code": "string",
    // "name": "string"
    // }

    private String json = "{\r \"id\": \"string\",\r\"code\": \"string\",\r\"name\": \"string\"\r}";

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCSFamily() throws JsonProcessingException {

        CSFamily family = mapper.readValue(json, CSFamily.class);
        assert (family != null);
        assert (family.getId().equals("string"));
        assert (family.getCode().equals("string"));
        assert (family.getName().equals("string"));
    }
}
