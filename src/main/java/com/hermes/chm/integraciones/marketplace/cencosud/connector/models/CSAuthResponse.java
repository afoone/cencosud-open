package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

// {
//     "expiresIn": "14400",
//     "jwtPayload": {
//     "id": "617a063e-31b0-47cc-8529-17242a25f79c",
//     "email": "dev@cencosud.cl",
//     "first_name": "Dév",
//     "last_name": "Eiffel",
//     "seller_id": "string",
//     "seller_name": "string",
//     "role": "admin",
//     "financial_access": true,
//     "facility_id": "string",
//     "seller_type": "string",
//     "sellerSapClient": "string",
//     "sellerSapProvider": "string",
//     "sellerIsPublished": "string",
//     "is_collector": "string",
//     "api_key": "string",
//     "permissions": [
//     {
//     "c": "OrdersV1Controller",
//     "a": "*"
//     }
//     ],
//     "policies": [
//     {
//     "id": "5922ce45-af17-11ec-a1c9-0e8f869ec6f7",
//     "name": "everyone ADMIN can GET at ANY is ALLOW",
//     "action": "GET",
//     "effect": "ALLOW",
//     "target": "ANY",
//     "resource": "SELLERS",
//     "abilities": []
//     }
//     ]
//     },
//     "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjYxN2EwNjNlLTMxYjAtNDdjYy04NTI5LTE3MjQyYTI1Zjc5YyIsImVtYWlsIjoiZGV2QGNlbmNvc3VkLmNsIiwiZmlyc3RfbmFtZSI6IkTDqXYiLCJsYXN0X25hbWUiOiJFaWZmZWwiLCJzZWxsZXJfaWQiOm51bGwsInNlbGxlcl9uYW1lIjpudWxsLCJyb2xlIjoiYWRtaW4iLCJmaW5hbmNpYWxfYWNjZXNzIjpmYWxzZSwiZmFjaWxpdHlfaWQiOm51bGwsInNlbGxlcl90eXBlIjpudWxsLCJzZWxsZXJTYXBDbGllbnQiOm51bGwsInNlbGxlclNhcFByb3ZpZGVyIjpudWxsLCJzZWxsZXJJc1B1Ymxpc2hlZCI6bnVsbCwiaXNfY29sbGVjdG9yIjpudWxsLCJhcGlfa2V5IjpudWxsLCJwZXJtaXNzaW9ucyI6W3siYyI6Ik9yZGVyc1YxQ29udHJvbGxlciIsImEiOiIqIn1dLCJwb2xpY2llcyI6W3siaWQiOiI1OTIyY2U0NS1hZjE3LTExZWMtYTFjOS0wZThmODY5ZWM2ZjciLCJuYW1lIjoiZXZlcnlvbmUgQURNSU4gY2FuIEdFVCBhdCBBTlkgaXMgQUxMT1ciLCJhY3Rpb24iOiJHRVQiLCJlZmZlY3QiOiJBTExPVyIsInRhcmdldCI6IkFOWSIsInJlc291cmNlIjoiU0VMTEVSUyIsImFiaWxpdGllcyI6W119LHsiaWQiOiI1YWI0YmU2NS1hZjE3LTExZWMtYTFjOS0wZThmODY5ZWM2ZjciLCJuYW1lIjoiZXZlcnlvbmUgQURNSU4gY2FuIFBPU1QgYXQgQU5ZIGlzIEFMTE9XIiwiYWN0aW9uIjoiUE9TVCIsImVmZmVjdCI6IkFMTE9XIiwidGFyZ2V0IjoiQU5ZIiwicmVzb3VyY2UiOiJTRUxMRVJTIiwiYWJpbGl0aWVzIjpbXX0seyJpZCI6IjVjNDY2NjVhLWFmMTctMTFlYy1hMWM5LTBlOGY4NjllYzZmNyIsIm5hbWUiOiJldmVyeW9uZSBBRE1JTiBjYW4gREVMRVRFIGF0IEFOWSBpcyBBTExPVyIsImFjdGlvbiI6IkRFTEVURSIsImVmZmVjdCI6IkFMTE9XIiwidGFyZ2V0IjoiQU5ZIiwicmVzb3VyY2UiOiJTRUxMRVJTIiwiYWJpbGl0aWVzIjpbXX0seyJpZCI6IjVkZTEzZWEyLWFmMTctMTFlYy1hMWM5LTBlOGY4NjllYzZmNyIsIm5hbWUiOiJldmVyeW9uZSBBRE1JTiBjYW4gUFVUIGF0IEFOWSBpcyBBTExPVyIsImFjdGlvbiI6IlBVVCIsImVmZmVjdCI6IkFMTE9XIiwidGFyZ2V0IjoiQU5ZIiwicmVzb3VyY2UiOiJTRUxMRVJTIiwiYWJpbGl0aWVzIjpbXX1dLCJpYXQiOjE2NjMyNzM2NzEsImV4cCI6MTY2MzI4ODA3MSwiaXNzIjoiRWlmZmVsLVN0ZyJ9.qrVIMRKdQ-OUMgIH9ab5w0j73MLReX59isoLWhJu1so"
//     }
public class CSAuthResponse {
    private String expiresIn;
    private CSJWTPayload jwtPayload;
    private String accessToken;

    // getters and setters
    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public CSJWTPayload getJwtPayload() {
        return jwtPayload;
    }

    public void setJwtPayload(CSJWTPayload jwtPayload) {
        this.jwtPayload = jwtPayload;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
