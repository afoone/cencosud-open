package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CSPolicyTest {
    // "id": "5922ce45-af17-11ec-a1c9-0e8f869ec6f7",
    // "name": "everyone ADMIN can GET at ANY is ALLOW",
    // "action": "GET",
    // "effect": "ALLOW",
    // "target": "ANY",
    // "resource": "SELLERS",
    // "abilities": []
    // }

    private String json = "{\r \"id\": \"5922ce45-af17-11ec-a1c9-0e8f869ec6f7\",\r\"name\": \"everyone ADMIN can GET at ANY is ALLOW\",\r\"action\": \"GET\",\r\"effect\": \"ALLOW\",\r\"target\": \"ANY\",\r\"resource\": \"SELLERS\",\r\"abilities\": []\r}";

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCSPolicy() throws JsonProcessingException {

        CSPolicy policy = mapper.readValue(json, CSPolicy.class);
        assert (policy != null);
        assert (policy.getId().equals("5922ce45-af17-11ec-a1c9-0e8f869ec6f7"));
        assert (policy.getName().equals("everyone ADMIN can GET at ANY is ALLOW"));
        assert (policy.getAction().equals("GET"));
        assert (policy.getEffect().equals("ALLOW"));
        assert (policy.getTarget().equals("ANY"));
        assert (policy.getResource().equals("SELLERS"));
        assert (policy.getAbilities().length == 0);
    }
}
