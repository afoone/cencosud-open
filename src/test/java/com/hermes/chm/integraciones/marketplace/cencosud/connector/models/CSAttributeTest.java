package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CSAttributeTest {

    // {
    // "id": "string",
    // "code": "string",
    // "name": null
    // }

    private String json = "{\r \"id\": \"string\",\r\"code\": \"string\",\r\"name\": null\r}";

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCSAttribute() throws JsonProcessingException {

        CSAttribute attribute = mapper.readValue(json, CSAttribute.class);
        assert (attribute != null);
        assert (attribute.getId().equals("string"));
        assert (attribute.getCode().equals("string"));
        assert (attribute.getName() == null);
    }

}