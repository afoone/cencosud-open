package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.*;

public class CSVariantTest {
    // {
    // "id": "string",
    // "name": "string",
    // "sku": "string",
    // "status": "string",
    // "createdAt": "2019-08-24T14:15:22Z",
    // "updatedAt": "2019-08-24T14:15:22Z",
    // "attributeDetails": [
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
    // ]
    // }

    private String json = "{\r\n  \"id\": \"string\",\r\n  \"name\": \"string\",\r\n  \"sku\": \"string\",\r\n  \"status\": \"string\",\r\n  \"createdAt\": \"2019-08-24T14:15:22Z\",\r\n  \"updatedAt\": \"2019-08-24T14:15:22Z\",\r\n  \"attributeDetails\": [\r\n    {\r\n      \"id\": \"string\",\r\n      \"value\": \"string\",\r\n      \"attributeOption\": {\r\n        \"id\": \"string\",\r\n        \"name\": \"string\",\r\n        \"code\": \"string\",\r\n        \"status\": \"string\",\r\n        \"createdAt\": \"2019-08-24T14:15:22Z\",\r\n        \"updatedAt\": \"2019-08-24T14:15:22Z\",\r\n        \"position\": 0\r\n      },\r\n      \"attribute\": {\r\n        \"id\": \"string\",\r\n        \"code\": \"string\",\r\n        \"name\": null\r\n      }\r\n    }\r\n  ]\r\n}";
    private ObjectMapper mapper = new ObjectMapper();

    @Test
    void test() throws JsonProcessingException {
        CSVariant csVariant = mapper.readValue(json, CSVariant.class);
        System.out.println("--------->");
        System.out.println(csVariant);
        assertNotNull(csVariant);
        assertNotNull(csVariant.getAttributeDetails());
        assertNotNull(csVariant.getId());
        assertNotNull(csVariant.getName());
        //assertNotNull(csVariant.getRefVariant());
        assertNotNull(csVariant.getSku());
        assertNotNull(csVariant.getStatus());
    }

}
