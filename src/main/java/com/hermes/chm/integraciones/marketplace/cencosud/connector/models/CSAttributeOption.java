package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;
/**
 * {
//       "id": "string",
//       "name": "string",
//       "code": "string",
//       "status": "string",
//       "createdAt": "2019-08-24T14:15:22Z",
//       "updatedAt": "2019-08-24T14:15:22Z",
//       "position": 0
//     },
 */
public class CSAttributeOption {
    private String id;
    private String name;
    private String code;
    private String status;
    private String createdAt;
    private String updatedAt;
    private int position;
    
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
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public String getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }
    
}
