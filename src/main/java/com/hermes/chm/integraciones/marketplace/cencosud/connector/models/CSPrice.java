package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

//     {
//       "id": "string",
//       "code": "string",
//       "name": "string"
//     }
public class CSPrice {
    private String id;
    private String code;
    private String name;

    // constructors
    public CSPrice() {
    }

    public CSPrice(String code) {
        this.code = code;
    }

    // getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
