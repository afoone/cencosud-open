package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

// {
//     "id": "string",
//     "type": "string",
//     "position": 0,
//     "name": "string",
//     "path": "string",
//     "originalPath": "string",
//     "status": "string",
//     "createdAt": "2019-08-24T14:15:22Z",
//     "updatedAt": "2019-08-24T14:15:22Z"
//   }
public class CSMedia {
    private String id;
    private String type = "";
    private int position;
    private String name;
    private String path;
    private String originalPath;
    private String status;
    private String createdAt;
    private String updatedAt;

    // constructors
    public CSMedia() {
    }

    public CSMedia(int position, String name, String path) {
        this.position = position;
        this.name = name;
        this.path = path;
    }

    // getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
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

}
