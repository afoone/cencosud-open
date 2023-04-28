package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

import java.util.List;

// {
//     "id": "string",
//     "name": "string",
//     "sku": "string",
//     "status": "string",
//     "createdAt": "2019-08-24T14:15:22Z",
//     "updatedAt": "2019-08-24T14:15:22Z",
//     "attributeDetails": [
//       {
//         "id": "string",
//         "value": "string",
//         "attributeOption": {
//           "id": "string",
//           "name": "string",
//           "code": "string",
//           "status": "string",
//           "createdAt": "2019-08-24T14:15:22Z",
//           "updatedAt": "2019-08-24T14:15:22Z",
//           "position": 0
//         },
//         "attribute": {
//           "id": "string",
//           "code": "string",
//           "name": null
//         }
//       }
//     ]
//   }
public class CSVariant {
    private String id;
    private String name;
    private String refVariant;
    private String sku;
    private String status;
    private List<CSAttributeDetail> attributeDetails;
    private String createdAt;
    private String updatedAt;

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

    public String getRefVariant() {
        return refVariant;
    }

    public void setRefVariant(String refVariant) {
        this.refVariant = refVariant;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CSAttributeDetail> getAttributeDetails() {
        return attributeDetails;
    }

    public void setAttributeDetails(List<CSAttributeDetail> attributeDetails) {
        this.attributeDetails = attributeDetails;
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
