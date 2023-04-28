package com.hermes.chm.integraciones.marketplace.cencosud.connector.models.responses;

import java.util.List;

import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSProduct;

// {
//     "pagging": {
//       "quantity": 0,
//       "limit": 0,
//       "offset": 0
//     },
//     "data": [
//       {
//         "id": "string",
//         "sku": "string",
//         "name": "string",
//         "refProduct": "string",
//         "family": {
//           "id": "string",
//           "code": "string",
//           "name": "string"
//         },
//         "medias": [
//           {
//             "id": "string",
//             "type": "string",
//             "position": 0,
//             "name": "string",
//             "path": "string",
//             "originalPath": "string",
//             "status": "string",
//             "createdAt": "2019-08-24T14:15:22Z",
//             "updatedAt": "2019-08-24T14:15:22Z"
//           }
//         ],
//         "sellerId": "string",
//         "status": "string",
//         "createdAt": "2019-08-24T14:15:22Z",
//         "updatedAt": "2019-08-24T14:15:22Z",
//         "productPrices": [
//           {
//             "id": "string",
//             "value": 0,
//             "price": {
//               "id": "string",
//               "code": "string",
//               "name": "string"
//             },
//             "store": {
//               "id": "string",
//               "code": "string",
//               "name": "string"
//             },
//             "showFrom": "2019-08-24T14:15:22Z",
//             "showTo": "2019-08-24T14:15:22Z",
//             "status": "string"
//           }
//         ],
//         "attributeDetails": [
//           {
//             "id": "string",
//             "value": "string",
//             "attributeOption": {
//               "id": "string",
//               "name": "string",
//               "code": "string",
//               "status": "string",
//               "createdAt": "2019-08-24T14:15:22Z",
//               "updatedAt": "2019-08-24T14:15:22Z",
//               "position": 0
//             },
//             "attribute": {
//               "id": "string",
//               "code": "string",
//               "name": null
//             }
//           }
//         ],
//         "productCategories": [
//           {
//             "category": {
//               "id": "string",
//               "name": "string",
//               "code": "string"
//             },
//             "isPrimary": true
//           }
//         ],
//         "variants": [
//           {
//             "id": "string",
//             "name": "string",
//             "refVariant": "string",
//             "sku": "string",
//             "status": "string",
//             "attributeDetails": [
//               {
//                 "value": "string",
//                 "attributeOption": {
//                   "id": "string",
//                   "name": "string",
//                   "code": "string",
//                   "status": "string",
//                   "createdAt": "2019-08-24T14:15:22Z",
//                   "updatedAt": "2019-08-24T14:15:22Z",
//                   "position": 0
//                 },
//                 "attribute": {
//                   "id": "string",
//                   "code": "string",
//                   "name": "string"
//                 }
//               }
//             ]
//           }
//         ]
//       }
//     ]
//   }
public class CSProductsResponse {
    private CSProductsPagging pagging;
    private List<CSProduct> data;

    // getters and setters
    public CSProductsPagging getPagging() {
        return pagging;
    }

    public void setPagging(CSProductsPagging pagging) {
        this.pagging = pagging;
    }

    public List<CSProduct> getData() {
        return data;
    }

    public void setData(List<CSProduct> data) {
        this.data = data;
    }

}
