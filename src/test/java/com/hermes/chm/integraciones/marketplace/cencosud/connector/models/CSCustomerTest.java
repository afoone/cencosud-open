package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CSCustomerTest {
    // {
    // "id": "string",
    // "name": "string",
    // "email": "string",
    // "documentType": "string",
    // "documentNumber": "string"
    // }

    private String json = "{\r \"id\": \"string\",\r\"name\": \"string\",\r\"email\": \"string\",\r\"documentType\": \"string\",\r\"documentNumber\": \"string\"\r}";

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCSCustomer() throws JsonProcessingException {

        CSCustomer customer = mapper.readValue(json, CSCustomer.class);
        assert (customer != null);
        assert (customer.getId().equals("string"));
        assert (customer.getName().equals("string"));
        assert (customer.getEmail().equals("string"));
        assert (customer.getDocumentType().equals("string"));
        assert (customer.getDocumentNumber().equals("string"));
    }
}
