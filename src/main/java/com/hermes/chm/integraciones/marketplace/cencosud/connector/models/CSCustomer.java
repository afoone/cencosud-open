package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;
// {
//     "id": "string",
//     "name": "string",
//     "email": "string",
//     "documentType": "string",
//     "documentNumber": "string"
//     }
public class CSCustomer {
    private String id;
    private String name;
    private String email;
    private String documentType;
    private String documentNumber;
    // getters and setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getDocumentType() {
        return documentType;
    }
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
    public String getDocumentNumber() {
        return documentNumber;
    }
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
    

}
