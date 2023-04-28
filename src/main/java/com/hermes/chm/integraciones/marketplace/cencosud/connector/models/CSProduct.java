package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

import java.util.ArrayList;
import java.util.List;

/**

    {
      "id": "string",
      "sku": "string",
      "name": "string",
      "refProduct": "string",
      "family": {
        "id": "string",
        "code": "string",
        "name": "string"
      },
      "medias": [
        {
          "id": "string",
          "type": "string",
          "position": 0,
          "name": "string",
          "path": "string",
          "originalPath": "string",
          "status": "string",
          "createdAt": "2019-08-24T14:15:22Z",
          "updatedAt": "2019-08-24T14:15:22Z"
        }
      ],
      "sellerId": "string",
      "status": "string",
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "productPrices": [
        {
          "id": "string",
          "value": 0,
          "price": {
            "id": "string",
            "code": "string",
            "name": "string"
          },
          "store": {
            "id": "string",
            "code": "string",
            "name": "string"
          },
          "showFrom": "2019-08-24T14:15:22Z",
          "showTo": "2019-08-24T14:15:22Z",
          "status": "string"
        }
      ],
      "attributeDetails": [
        {
          "id": "string",
          "value": "string",
          "attributeOption": {
            "id": "string",
            "name": "string",
            "code": "string",
            "status": "string",
            "createdAt": "2019-08-24T14:15:22Z",
            "updatedAt": "2019-08-24T14:15:22Z",
            "position": 0
          },
          "attribute": {
            "id": "string",
            "code": "string",
            "name": null
          }
        }
      ],
      "productCategories": [
        {
          "category": {
            "id": "string",
            "name": "string",
            "code": "string"
          },
          "isPrimary": true
        }
      ],
      "variants": [
        {
          "id": "string",
          "name": "string",
          "refVariant": "string",
          "sku": "string",
          "status": "string",
          "attributeDetails": [
            {
              "value": "string",
              "attributeOption": {
                "id": "string",
                "name": "string",
                "code": "string",
                "status": "string",
                "createdAt": "2019-08-24T14:15:22Z",
                "updatedAt": "2019-08-24T14:15:22Z",
                "position": 0
              },
              "attribute": {
                "id": "string",
                "code": "string",
                "name": "string"
              }
            }
          ]
        }
      ]

}
 */
public class CSProduct {
    private String id;
    private String sku;
    private String name;
    private String refProduct;
    private CSFamily family;
    private List<CSMedia> medias;
    private String sellerId;
    private String status;
    private String createdAt;
    private String updatedAt;
    private List<CSProductPrice> productPrices;
    private List<CSAttributeDetail> attributeDetails;
    private List<CSProductCategory> productCategories;
    private List<CSVariant> variants;

    public CSProduct() {
    }

    public CSProduct(String sellerSKU) {
      CSAttributeDetail attribute = new CSAttributeDetail(sellerSKU, "sellerSKU");
      this.attributeDetails = new ArrayList<>();
      this.attributeDetails.add(attribute);
      
    }

    // getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRefProduct() {
        return refProduct;
    }

    public void setRefProduct(String refProduct) {
        this.refProduct = refProduct;
    }

    public CSFamily getFamily() {
        return family;
    }

    public void setFamily(CSFamily family) {
        this.family = family;
    }

    public List<CSMedia> getMedias() {
        return medias;
    }

    public void setMedias(List<CSMedia> medias) {
        this.medias = medias;
    }

    public void addMedia(CSMedia media) {
        if (this.medias == null) {
            this.medias = new ArrayList<>();
        }
        this.medias.add(media);
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
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

    public List<CSProductPrice> getProductPrices() {
        return productPrices;
    }

    public void setProductPrices(List<CSProductPrice> productPrices) {
        this.productPrices = productPrices;
    }

    public void addProductPrice(CSProductPrice productPrice) {
        if (this.productPrices == null) {
            this.productPrices = new ArrayList<>();
        }
        this.productPrices.add(productPrice);
    }

    public List<CSAttributeDetail> getAttributeDetails() {
        return attributeDetails;
    }

    public void setAttributeDetails(List<CSAttributeDetail> attributeDetails) {
        this.attributeDetails = attributeDetails;
    }

    public void addAttributeDetaul(CSAttributeDetail attributeDetail) {
        if (this.attributeDetails == null) {
            this.attributeDetails = new ArrayList<>();
        }
        this.attributeDetails.add(attributeDetail);
    }

    public List<CSProductCategory> getProductCategories() {
        return productCategories;
    }

    public void setProductCategories(List<CSProductCategory> productCategories) {
        this.productCategories = productCategories;
    }

    public void addProductCategory(CSProductCategory productCategory) {
        if (this.productCategories == null) {
            this.productCategories = new ArrayList<>();
        }
        this.productCategories.add(productCategory);
    }

    public List<CSVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<CSVariant> variants) {
        this.variants = variants;
    }

    public void addVariant(CSVariant variant) {
        if (this.variants == null) {
            this.variants = new ArrayList<>();
        }
        this.variants.add(variant);
    }
    
    
}
