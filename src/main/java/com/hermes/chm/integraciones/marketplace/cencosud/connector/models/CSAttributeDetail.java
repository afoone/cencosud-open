package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;
// {
//     "id": "string",
//     "value": "string",
//     "attributeOption": {
//       "id": "string",
//       "name": "string",
//       "code": "string",
//       "status": "string",
//       "createdAt": "2019-08-24T14:15:22Z",
//       "updatedAt": "2019-08-24T14:15:22Z",
//       "position": 0
//     },
//     "attribute": {
//       "id": "string",
//       "code": "string",
//       "name": null
//     }
//   }
public class CSAttributeDetail {
    private String id;
    private String value;
    private CSAttributeOption attributeOption;
    private CSAttribute attribute;

    // constructors
    public CSAttributeDetail() {
    }

    public CSAttributeDetail(String value, String code) {
        this.value = value;
        this.attribute = new CSAttribute(code);
    }

    // getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public CSAttributeOption getAttributeOption() {
        return attributeOption;
    }

    public void setAttributeOption(CSAttributeOption attributeOption) {
        this.attributeOption = attributeOption;
    }

    public CSAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(CSAttribute attribute) {
        this.attribute = attribute;
    }
    
}
