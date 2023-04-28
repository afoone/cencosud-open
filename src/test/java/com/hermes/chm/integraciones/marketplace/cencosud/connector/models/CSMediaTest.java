package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CSMediaTest {
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

    private String json = "    {\r\n    \"id\": \"string\",\r\n    \"type\": \"string\",\r\n    \"position\": 0,\r\n    \"name\": \"string\",\r\n    \"path\": \"string\",\r\n    \"originalPath\": \"string\",\r\n    \"status\": \"string\",\r\n    \"createdAt\": \"2019-08-24T14:15:22Z\",\r\n    \"updatedAt\": \"2019-08-24T14:15:22Z\"\r\n  }";

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCSMedia() throws JsonProcessingException {

        CSMedia media = mapper.readValue(json, CSMedia.class);
        assert (media != null);
        assert (media.getId().equals("string"));
        assert (media.getType().equals("string"));
        assert (media.getPosition() == 0);
        assert (media.getName().equals("string"));
        assert (media.getPath().equals("string"));
        assert (media.getOriginalPath().equals("string"));
        assert (media.getStatus().equals("string"));
        assert (media.getCreatedAt().equals("2019-08-24T14:15:22Z"));
        assert (media.getUpdatedAt().equals("2019-08-24T14:15:22Z"));
    }

}
