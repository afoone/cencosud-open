package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
public class CSProductTest {
    // {
    // "id": "string",
    // "sku": "string",
    // "name": "string",
    // "refProduct": "string",
    // "family": {
    // "id": "string",
    // "code": "string",
    // "name": "string"
    // },
    // "medias": [
    // {
    // "id": "string",
    // "type": "string",
    // "position": 0,
    // "name": "string",
    // "path": "string",
    // "originalPath": "string",
    // "status": "string",
    // "createdAt": "2019-08-24T14:15:22Z",
    // "updatedAt": "2019-08-24T14:15:22Z"
    // }
    // ],
    // "sellerId": "string",
    // "status": "string",
    // "createdAt": "2019-08-24T14:15:22Z",
    // "updatedAt": "2019-08-24T14:15:22Z",
    // "productPrices": [
    // {
    // "id": "string",
    // "value": 0,
    // "price": {
    // "id": "string",
    // "code": "string",
    // "name": "string"
    // },
    // "store": {
    // "id": "string",
    // "code": "string",
    // "name": "string"
    // },
    // "showFrom": "2019-08-24T14:15:22Z",
    // "showTo": "2019-08-24T14:15:22Z",
    // "status": "string"
    // }
    // ],
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
    // ],
    // "productCategories": [
    // {
    // "category": {
    // "id": "string",
    // "name": "string",
    // "code": "string"
    // },
    // "isPrimary": true
    // }
    // ],
    // "variants": [
    // {
    // "id": "string",
    // "name": "string",
    // "refVariant": "string",
    // "sku": "string",
    // "status": "string",
    // "attributeDetails": [
    // {
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
    // "name": "string"
    // }
    // }
    // ]
    // }
    // ]

    // }

    private String json = "    {\r\n        \"id\": \"string\",\r\n        \"sku\": \"string\",\r\n        \"name\": \"string\",\r\n        \"refProduct\": \"string\",\r\n        \"family\": {\r\n          \"id\": \"string\",\r\n          \"code\": \"string\",\r\n          \"name\": \"string\"\r\n        },\r\n        \"medias\": [\r\n          {\r\n            \"id\": \"string\",\r\n            \"type\": \"string\",\r\n            \"position\": 0,\r\n            \"name\": \"string\",\r\n            \"path\": \"string\",\r\n            \"originalPath\": \"string\",\r\n            \"status\": \"string\",\r\n            \"createdAt\": \"2019-08-24T14:15:22Z\",\r\n            \"updatedAt\": \"2019-08-24T14:15:22Z\"\r\n          }\r\n        ],\r\n        \"sellerId\": \"string\",\r\n        \"status\": \"string\",\r\n        \"createdAt\": \"2019-08-24T14:15:22Z\",\r\n        \"updatedAt\": \"2019-08-24T14:15:22Z\",\r\n        \"productPrices\": [\r\n          {\r\n            \"id\": \"string\",\r\n            \"value\": 0,\r\n            \"price\": {\r\n              \"id\": \"string\",\r\n              \"code\": \"string\",\r\n              \"name\": \"string\"\r\n            },\r\n            \"store\": {\r\n              \"id\": \"string\",\r\n              \"code\": \"string\",\r\n              \"name\": \"string\"\r\n            },\r\n            \"showFrom\": \"2019-08-24T14:15:22Z\",\r\n            \"showTo\": \"2019-08-24T14:15:22Z\",\r\n            \"status\": \"string\"\r\n          }\r\n        ],\r\n        \"attributeDetails\": [\r\n          {\r\n            \"id\": \"string\",\r\n            \"value\": \"string\",\r\n            \"attributeOption\": {\r\n              \"id\": \"string\",\r\n              \"name\": \"string\",\r\n              \"code\": \"string\",\r\n              \"status\": \"string\",\r\n              \"createdAt\": \"2019-08-24T14:15:22Z\",\r\n              \"updatedAt\": \"2019-08-24T14:15:22Z\",\r\n              \"position\": 0\r\n            },\r\n            \"attribute\": {\r\n              \"id\": \"string\",\r\n              \"code\": \"string\",\r\n              \"name\": null\r\n            }\r\n          }\r\n        ],\r\n        \"productCategories\": [\r\n          {\r\n            \"category\": {\r\n              \"id\": \"string\",\r\n              \"name\": \"string\",\r\n              \"code\": \"string\"\r\n            },\r\n            \"isPrimary\": true\r\n          }\r\n        ],\r\n        \"variants\": [\r\n          {\r\n            \"id\": \"string\",\r\n            \"name\": \"string\",\r\n            \"refVariant\": \"string\",\r\n            \"sku\": \"string\",\r\n            \"status\": \"string\",\r\n            \"attributeDetails\": [\r\n              {\r\n                \"value\": \"string\",\r\n                \"attributeOption\": {\r\n                  \"id\": \"string\",\r\n                  \"name\": \"string\",\r\n                  \"code\": \"string\",\r\n                  \"status\": \"string\",\r\n                  \"createdAt\": \"2019-08-24T14:15:22Z\",\r\n                  \"updatedAt\": \"2019-08-24T14:15:22Z\",\r\n                  \"position\": 0\r\n                },\r\n                \"attribute\": {\r\n                  \"id\": \"string\",\r\n                  \"code\": \"string\",\r\n                  \"name\": \"string\"\r\n                }\r\n              }\r\n            ]\r\n          }\r\n        ]\r\n  \r\n  }";

    private ObjectMapper mapper = new ObjectMapper();


    public void test() throws JsonProcessingException {
        CSProduct product = mapper.readValue(json, CSProduct.class);
        assert (product != null);
    }

}
