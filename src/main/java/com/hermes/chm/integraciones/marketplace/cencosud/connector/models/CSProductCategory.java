package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

// {
//     "category": {
//       "id": "string",
//       "name": "string",
//       "code": "string"
//     },
//     "isPrimary": true
//   }
public class CSProductCategory {
    private CSCategory category;
    private boolean isPrimary;

    // getters and setters
    public CSCategory getCategory() {
        return category;
    }

    public void setCategory(CSCategory category) {
        this.category = category;
    }

    public boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(boolean primary) {
        isPrimary = primary;
    }

}
