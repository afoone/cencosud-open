package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

// {
//     "id": "string",
//     "code": "string",
//     "name": "string"
//   }
public class CSFamily {
    private String id;
    private String code;
    private String name;

    public CSFamily() {
    }

    public CSFamily(String code) {
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
