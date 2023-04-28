package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

//  {
//       "id": "617a063e-31b0-47cc-8529-17242a25f79c",
//       "email": "dev@cencosud.cl",
//       "first_name": "Dév",
//       "last_name": "Eiffel",
//       "seller_id": "string",
//       "seller_name": "string",
//       "role": "admin",
//       "financial_access": true,
//       "facility_id": "string",
//       "seller_type": "string",
//       "sellerSapClient": "string",
//       "sellerSapProvider": "string",
//       "sellerIsPublished": "string",
//       "is_collector": "string",
//       "api_key": "string",
//       "permissions": [
//         {
//           "c": "OrdersV1Controller",
//           "a": "*"
//         }
//       ],
//       "policies": [
//         {
//           "id": "5922ce45-af17-11ec-a1c9-0e8f869ec6f7",
//           "name": "everyone ADMIN can GET at ANY is ALLOW",
//           "action": "GET",
//           "effect": "ALLOW",
//           "target": "ANY",
//           "resource": "SELLERS",
//           "abilities": [
//             null
//           ]
//         }
//       ]
//     }
public class CSJWTPayload {
    private String id;
    private String email;
    private String first_name;
    private String last_name;
    private String seller_id;
    private String seller_name;
    private String role;
    private boolean financial_access;
    private String facility_id;
    private String seller_type;
    private String sellerSapClient;
    private String sellerSapProvider;
    private String sellerIsPublished;
    private String is_collector;
    private String api_key;
    private CSPermission[] permissions;
    // private CSPolicy[] policies;

    // getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isFinancial_access() {
        return financial_access;
    }

    public void setFinancial_access(boolean financial_access) {
        this.financial_access = financial_access;
    }

    public String getFacility_id() {
        return facility_id;
    }

    public void setFacility_id(String facility_id) {
        this.facility_id = facility_id;
    }

    public String getSeller_type() {
        return seller_type;
    }

    public void setSeller_type(String seller_type) {
        this.seller_type = seller_type;
    }

    public String getSellerSapClient() {
        return sellerSapClient;
    }

    public void setSellerSapClient(String sellerSapClient) {
        this.sellerSapClient = sellerSapClient;
    }

    public String getSellerSapProvider() {
        return sellerSapProvider;
    }

    public void setSellerSapProvider(String sellerSapProvider) {
        this.sellerSapProvider = sellerSapProvider;
    }

    public String getSellerIsPublished() {
        return sellerIsPublished;
    }

    public void setSellerIsPublished(String sellerIsPublished) {
        this.sellerIsPublished = sellerIsPublished;
    }

    public String getIs_collector() {
        return is_collector;
    }

    public void setIs_collector(String is_collector) {
        this.is_collector = is_collector;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public CSPermission[] getPermissions() {
        return permissions;
    }

    public void setPermissions(CSPermission[] permissions) {
        this.permissions = permissions;
    }

    // public CSPolicy[] getPolicies() {
    //     return policies;
    // }

    // public void setPolicies(CSPolicy[] policies) {
    //     this.policies = policies;
    // }

}
