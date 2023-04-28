package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CSAttributeDetailTest {

    // {
    // "id": "string",
    // "value": "string",
    // "attributeOption": {
    // "id": "string",
    // "name": "string",
    // "code": "string",
    // "status": "string",
    // "createdAt": "2019-08-24T14:15:22Z",
    // "updatedAt": "2019-08-24T14:15:22Z",
    // "position": 0
    // },
    // "attribute": {
    // "id": "string",
    // "code": "string",
    // "name": null
    // }
    // }

    private String json = "{\n" +
            "    \"id\": \"string\",\n" +
            "    \"value\": \"string\",\n" +
            "    \"attributeOption\": {\n" +
            "      \"id\": \"string\",\n" +
            "      \"name\": \"string\",\n" +
            "      \"code\": \"string\",\n" +
            "      \"status\": \"string\",\n" +
            "      \"createdAt\": \"2019-08-24T14:15:22Z\",\n" +
            "      \"updatedAt\": \"2019-08-24T14:15:22Z\",\n" +
            "      \"position\": 0\n" +
            "    },\n" +
            "    \"attribute\": {\n" +
            "      \"id\": \"string\",\n" +
            "      \"code\": \"string\",\n" +
            "      \"name\": null\n" +
            "    }\n" +
            "  }";

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    void test() throws JsonProcessingException {
        CSAttributeDetail csAttributeDetail = mapper.readValue(json, CSAttributeDetail.class);
        assertNotNull(csAttributeDetail);
        assertNotNull(csAttributeDetail.getAttribute());
        assertNotNull(csAttributeDetail.getAttributeOption());
        assertNotNull(csAttributeDetail.getId());
        assertNotNull(csAttributeDetail.getValue());
    }

}