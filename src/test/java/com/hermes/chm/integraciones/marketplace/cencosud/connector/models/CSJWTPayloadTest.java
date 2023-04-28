package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CSJWTPayloadTest {
    // {
    // "id": "617a063e-31b0-47cc-8529-17242a25f79c",
    // "email": "dev@cencosud.cl",
    // "first_name": "Dév",
    // "last_name": "Eiffel",
    // "seller_id": "string",
    // "seller_name": "string",
    // "role": "admin",
    // "financial_access": true,
    // "facility_id": "string",
    // "seller_type": "string",
    // "sellerSapClient": "string",
    // "sellerSapProvider": "string",
    // "sellerIsPublished": "string",
    // "is_collector": "string",
    // "api_key": "string",
    // "permissions": [
    // {
    // "c": "OrdersV1Controller",
    // "a": "*"
    // }
    // ],
    // "policies": [
    // {
    // "id": "5922ce45-af17-11ec-a1c9-0e8f869ec6f7",
    // "name": "everyone ADMIN can GET at ANY is ALLOW",
    // "action": "GET",
    // "effect": "ALLOW",
    // "target": "ANY",
    // "resource": "SELLERS",
    // "abilities": [
    // null
    // ]
    // }
    // ]
    // }

    private String json = "     {\r\n      \"id\": \"617a063e-31b0-47cc-8529-17242a25f79c\",\r\n      \"email\": \"dev@cencosud.cl\",\r\n      \"first_name\": \"D\u00E9v\",\r\n      \"last_name\": \"Eiffel\",\r\n      \"seller_id\": \"string\",\r\n      \"seller_name\": \"string\",\r\n      \"role\": \"admin\",\r\n      \"financial_access\": true,\r\n      \"facility_id\": \"string\",\r\n      \"seller_type\": \"string\",\r\n      \"sellerSapClient\": \"string\",\r\n      \"sellerSapProvider\": \"string\",\r\n      \"sellerIsPublished\": \"string\",\r\n      \"is_collector\": \"string\",\r\n      \"api_key\": \"string\",\r\n      \"permissions\": [\r\n        {\r\n          \"c\": \"OrdersV1Controller\",\r\n          \"a\": \"*\"\r\n        }\r\n      ],\r\n      \"policies\": [\r\n        {\r\n          \"id\": \"5922ce45-af17-11ec-a1c9-0e8f869ec6f7\",\r\n          \"name\": \"everyone ADMIN can GET at ANY is ALLOW\",\r\n          \"action\": \"GET\",\r\n          \"effect\": \"ALLOW\",\r\n          \"target\": \"ANY\",\r\n          \"resource\": \"SELLERS\",\r\n          \"abilities\": [\r\n            null\r\n          ]\r\n        }\r\n      ]\r\n    }";

    private ObjectMapper mapper = new ObjectMapper();

    // @Test
    public void testCSJWTPayload() throws JsonProcessingException {

        CSJWTPayload payload = mapper.readValue(json, CSJWTPayload.class);
        assert (payload != null);
        assert (payload.getId().equals("617a063e-31b0-47cc-8529-17242a25f79c"));
    }

}
