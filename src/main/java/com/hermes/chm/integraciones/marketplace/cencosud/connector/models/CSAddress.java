package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

// {
//     "id": "string",
//     "firstName": "string",
//     "lastName": "string",
//     "address1": "string",
//     "address2": "string",
//     "address3": "string",
//     "city": "string",
//     "stateCode": "string",
//     "countryCode": "string",
//     "phone": "string",
//     "communaCode": "string"
//     }
public class CSAddress {
    private String id;
    private String firstName;
    private String lastName;
    private String address1;
    private String address2;
    private String address3;
    private String city;
    private String stateCode;
    private String countryCode;
    private String phone;
    private String communaCode;

    // getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCommunaCode() {
        return communaCode;
    }

    public void setCommunaCode(String communaCode) {
        this.communaCode = communaCode;
    }

}
