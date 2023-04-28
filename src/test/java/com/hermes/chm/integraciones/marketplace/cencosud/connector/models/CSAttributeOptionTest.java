package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;


class CSAttributeOptionTest {

    // {
    // "id": "string",
    // "name": "string",
    // "code": "string",
    // "status": "string",
    // "createdAt": "2019-08-24T14:15:22Z",
    // "updatedAt": "2019-08-24T14:15:22Z",
    // "position": 0
    // }

    private String json = "{\r \"id\": \"string\",\r\"name\": \"string\",\r\"code\": \"string\",\r\"status\": \"string\",\r\"createdAt\": \"2019-08-24T14:15:22Z\",\r\"updatedAt\": \"2019-08-24T14:15:22Z\",\r\"position\": 0\r}";

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCSAttributeOption() throws JsonProcessingException {

        CSAttributeOption attributeOption = mapper.readValue(json, CSAttributeOption.class);
        assert (attributeOption != null);
        assert (attributeOption.getId().equals("string"));
        assert (attributeOption.getName().equals("string"));
        assert (attributeOption.getCode().equals("string"));
        assert (attributeOption.getStatus().equals("string"));
        assert (attributeOption.getCreatedAt().equals("2019-08-24T14:15:22Z"));
        assert (attributeOption.getUpdatedAt().equals("2019-08-24T14:15:22Z"));
        assert (attributeOption.getPosition() == 0);
    }

}