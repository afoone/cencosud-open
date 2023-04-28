package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CSProductCategoryTest {
    // {
    // "category": {
    // "id": "string",
    // "name": "string",
    // "code": "string"
    // },
    // "isPrimary": true
    // }
    private String json = "{\r \"category\": {\r \"id\": \"string\",\r\"name\": \"string\",\r\"code\": \"string\"\r},\r\"isPrimary\": true\r}";

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCSProductCategory() throws JsonProcessingException {

        CSProductCategory productCategory = mapper.readValue(json, CSProductCategory.class);
        assert (productCategory != null);
        assert (productCategory.getCategory() != null);
        assert (productCategory.getCategory().getId().equals("string"));
        assert (productCategory.getCategory().getName().equals("string"));
        assert (productCategory.getCategory().getCode().equals("string"));
        assert (productCategory.getIsPrimary());
    }
}
