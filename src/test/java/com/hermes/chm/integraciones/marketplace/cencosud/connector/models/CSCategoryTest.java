package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CSCategoryTest {
    // {
    // "id": "string",
    // "code": "string",
    // "name": "string"
    // }

    private String json = "{\r \"id\": \"string\",\r\"code\": \"string\",\r\"name\": \"string\"\r}";

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCSCategory() throws JsonProcessingException {

        CSCategory category = mapper.readValue(json, CSCategory.class);
        assert (category != null);
        assert (category.getId().equals("string"));
        assert (category.getCode().equals("string"));
        assert (category.getName().equals("string"));
    }
}
