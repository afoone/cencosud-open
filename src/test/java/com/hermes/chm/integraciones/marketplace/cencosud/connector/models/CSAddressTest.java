package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CSAddressTest {

    // {
    // "id": "string",
    // "firstName": "string",
    // "lastName": "string",
    // "address1": "string",
    // "address2": "string",
    // "address3": "string",
    // "city": "string",
    // "stateCode": "string",
    // "countryCode": "string",
    // "phone": "string",
    // "communaCode": "string"
    // }

    private String json = "{\r \"id\": \"string\",\r\"firstName\": \"string\",\r\"lastName\": \"string\",\r\"address1\": \"string\",\r\"address2\": \"string\",\r\"address3\": \"string\",\r\"city\": \"string\",\r\"stateCode\": \"string\",\r\"countryCode\": \"string\",\r\"phone\": \"string\",\r\"communaCode\": \"string\"\r}";

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCSAddress() throws JsonProcessingException {

        CSAddress address = mapper.readValue(json, CSAddress.class);
        assert (address != null);
        assert (address.getId().equals("string"));
        assert (address.getFirstName().equals("string"));
        assert (address.getLastName().equals("string"));
        assert (address.getAddress1().equals("string"));
        assert (address.getAddress2().equals("string"));
        assert (address.getAddress3().equals("string"));
        assert (address.getCity().equals("string"));
        assert (address.getStateCode().equals("string"));
        assert (address.getCountryCode().equals("string"));
        assert (address.getPhone().equals("string"));
        assert (address.getCommunaCode().equals("string"));
    }
}