package com.hermes.chm.integraciones.marketplace.cencosud.connector;

// Decorators
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSAuthResponse;
import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSFamily;
import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSProduct;
import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.responses.CSFamilyResponse;
import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.responses.CSProductsResponse;
import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.responses.CSResponse;

import org.springframework.beans.factory.annotation.Value;

@Component
@PropertySource("classpath:marketplaceCencosud.properties")
public class CencosudProvider {

    private String token = null;

    @Value("${cencosud.api.url}")
    private String BASE_URL = "https://api-developers.ecomm-stg.cencosud.com/";

    @Value("${cencosud.api.version}")
    private String API_VERSION = "v1";

    @Value("${cencosud.api.key}")
    private String API_KEY = "----";

    @Value("${cencosud.connection.timeout}")
    private int CONNECTION_TIMEOUT;

    void setToken(String token) {
        this.token = token;
    }

    /**
     * Gets token
     * 
     * @return String, token
     */
    String getToken() {
        return this.token;
    }

    /**
     * Gets base url for api
     * 
     * @return String, base url
     */
    String getBaseURL() {
        return this.BASE_URL + "/" + this.API_VERSION + "/";
    }

    /**
     * Creates basic headers for api
     * 
     * @return
     */
    HttpHeaders createBasicHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * Gets Authorization header with Bearer authentication , cencosud api key
     * 
     * @return HttpHeaders
     */
    HttpHeaders createApiKeyHeaders() {
        HttpHeaders headers = createBasicHeaders();
        headers.add("Authorization", "Bearer " + API_KEY);
        return headers;
    }

    /**
     * Gets Authorization header with Bearer authentication , cencosud token
     * valid for the api
     * 
     * @return HttpHeaders
     */
    HttpHeaders createTokenHeaders() {
        HttpHeaders headers = createBasicHeaders();
        headers.add("Authorization", "Bearer " + token);
        return headers;
    }

    /**
     * Utility method to show data
     */
    public void showData() {
        System.out.println("Logging in...");
        System.out.println("Base URL: " + BASE_URL);
        System.out.println("API Version: " + API_VERSION);
        System.out.println("API Key: " + API_KEY);
        System.out.println("Connection Timeout: " + CONNECTION_TIMEOUT);
    }

    /**
     * Do login and save token
     * 
     * @return void
     */
    public void login() {
        System.out.println("Logging in...");
        String uri = getBaseURL() + "auth/apiKey";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createApiKeyHeaders();
        HttpEntity<Object> entity = new HttpEntity<Object>(headers);
        try {
            ResponseEntity<CSAuthResponse> response = restTemplate.exchange(uri, HttpMethod.POST, entity,
                    CSAuthResponse.class);

            setToken(response.getBody() != null ? response.getBody().getAccessToken() : null);

        } catch (HttpClientErrorException e) {
            System.out.println(e);
        }
        System.out.println("Token: " + getToken());
    }

    /**
     * Gets the products from cencosud api
     * 
     * @param limit
     * @param offset
     * @return
     */
    public CSProductsResponse getProducts(int limit, int offset) {
        return getProducts(limit, offset, null);
    }

    /**
     * Gets the products from cencosud api, with search
     * 
     */
    public CSProductsResponse getProducts(int limit, int offset, String search) {
        String uri = getBaseURL() + "product";
        uri += "?limit=" + limit;
        uri += "&offset=" + offset;
        if (search != null) {
            uri += "&search=" + search;
        }
        uri += "&pagination=true";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createTokenHeaders();
        HttpEntity<Object> entity = new HttpEntity<Object>(headers);
        try {
            ResponseEntity<CSProductsResponse> response = restTemplate.exchange(uri, HttpMethod.GET, entity,
                    CSProductsResponse.class);
            System.out.println(response.getBody());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Gets single product from cencosud api
     */
    public CSProduct getProduct(String sku) {
        String uri = getBaseURL() + "product/" + sku;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createTokenHeaders();
        HttpEntity<Object> entity = new HttpEntity<Object>(headers);
        try {
            ResponseEntity<CSProduct> response = restTemplate.exchange(uri, HttpMethod.GET, entity, CSProduct.class);
            System.out.println(response.getBody());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Creates a product in cencosud api
     */
    public CSProduct createProduct(CSProduct product) {
        String uri = getBaseURL() + "product";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createTokenHeaders();
        HttpEntity<Object> entity = new HttpEntity<Object>(product, headers);
        try {
            ResponseEntity<CSProduct> response = restTemplate.exchange(uri, HttpMethod.POST, entity, CSProduct.class);
            System.out.println(response.getBody());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Updates a product in cencosud api
     */
    public CSProduct updateProduct(CSProduct product) {
        String uri = getBaseURL() + "product/" + product.getSku();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createTokenHeaders();
        HttpEntity<Object> entity = new HttpEntity<Object>(product, headers);
        try {
            ResponseEntity<CSProduct> response = restTemplate.exchange(uri, HttpMethod.PUT, entity, CSProduct.class);
            System.out.println(response.getBody());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Deletes a product in cencosud api
     */
    public void deleteProduct(String sku) {
        String uri = getBaseURL() + "product/" + sku;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createTokenHeaders();
        HttpEntity<Object> entity = new HttpEntity<Object>(headers);
        try {
            ResponseEntity<CSProduct> response = restTemplate.exchange(uri, HttpMethod.DELETE, entity, CSProduct.class);
            System.out.println(response.getBody());
        } catch (HttpClientErrorException e) {
            System.out.println(e);
        }
    }

    /**
     * Gets the families from cencosud api
     * 
     * @param limit
     * @param offset
     * @return
     */
    public CSFamilyResponse getFamilies(int limit, int offset) {
        String uri = getBaseURL() + "family";
        uri += "?limit=" + limit;
        uri += "&offset=" + offset;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createTokenHeaders();
        HttpEntity<Object> entity = new HttpEntity<Object>(headers);
        try {
            ResponseEntity<CSFamilyResponse> response = restTemplate.exchange(uri, HttpMethod.GET, entity,
                    CSFamilyResponse.class);
            System.out.println(response.getBody());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            System.out.println(e);
            return null;
        }
    }

}
