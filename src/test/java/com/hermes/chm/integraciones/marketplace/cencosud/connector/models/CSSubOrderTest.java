package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CSSubOrderTest {
    // {
    // "id": 0,
    // "orderId": "string",
    // "subOrderNumber": "string",
    // "statusId": 0,
    // "carrier": "string",
    // "trackingNumber": "string",
    // "labelUrl": "string",
    // "labelId": "string",
    // "deliveryExternalId": "string",
    // "dispatchDate": "2019-08-24",
    // "arrivalDate": "2019-08-24",
    // "arrivalDateEnd": "2019-08-24",
    // "effectiveArrivalDate": "string",
    // "effectiveDispatchDate": "string",
    // "effectiveManifestDate": "string",
    // "lastNotificationId": "string",
    // "fulfillment": "string",
    // "centryId": "string",
    // "cost": "string",
    // "updatedAt": "string",
    // "oldShipmentId": "string",
    // "carrierSystemId": 0,
    // "tracking": [],
    // "deliveryOption": {},
    // "status": {},
    // "shippingAddress": {},
    // "items": []
    // }

    private String json = "    {\r\n    \"id\": 0,\r\n    \"orderId\": \"string\",\r\n    \"subOrderNumber\": \"string\",\r\n    \"statusId\": 0,\r\n    \"carrier\": \"string\",\r\n    \"trackingNumber\": \"string\",\r\n    \"labelUrl\": \"string\",\r\n    \"labelId\": \"string\",\r\n    \"deliveryExternalId\": \"string\",\r\n    \"dispatchDate\": \"2019-08-24\",\r\n    \"arrivalDate\": \"2019-08-24\",\r\n    \"arrivalDateEnd\": \"2019-08-24\",\r\n    \"effectiveArrivalDate\": \"string\",\r\n    \"effectiveDispatchDate\": \"string\",\r\n    \"effectiveManifestDate\": \"string\",\r\n    \"lastNotificationId\": \"string\",\r\n    \"fulfillment\": \"string\",\r\n    \"centryId\": \"string\",\r\n    \"cost\": \"string\",\r\n    \"updatedAt\": \"string\",\r\n    \"oldShipmentId\": \"string\",\r\n    \"carrierSystemId\": 0,\r\n    \"tracking\": [],\r\n    \"deliveryOption\": {},\r\n    \"status\": {},\r\n    \"shippingAddress\": {},\r\n    \"items\": []\r\n    }";

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test() throws Exception {
        CSSubOrder subOrder = mapper.readValue(json, CSSubOrder.class);
        assert(subOrder != null);
        
    }

}
