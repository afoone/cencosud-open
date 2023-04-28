package com.afoone.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.hermes.chm.integraciones.marketplace.cencosud.connector.CencosudProvider;
import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSAttributeDetail;
import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSCategory;
import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSCode;
import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSFamily;
import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSMedia;
import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSPrice;
import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSProduct;
import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSProductCategory;
import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSProductPrice;
import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.responses.CSFamilyResponse;
import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.responses.CSProductsResponse;

@RestController
public class MainController {

    private CencosudProvider cencosudProvider = new CencosudProvider();

    @RequestMapping("/authorize")
    @ResponseBody
    public String authorize() {
        cencosudProvider.login();
        return "OK";

    }

    @RequestMapping("/products")
    @ResponseBody
    public CSProductsResponse products() {
        cencosudProvider.login();
        return cencosudProvider.getProducts(10, 0);
    }

    @RequestMapping("/products/{id}")
    @ResponseBody
    public List<CSProduct> product(@PathVariable String id) {
        System.out.println("ID: " + id);
        cencosudProvider.login();
        return cencosudProvider.getProducts(10, 0, id).getData();
    }

    @RequestMapping("/products/create/{sku}")
    @ResponseBody
    public CSProduct createProduct(@PathVariable String sku) {
        cencosudProvider.login();
        CSProduct product = new CSProduct(sku);
        product.setSku(sku);
        product.setName("Test product");
        product.setStatus(null);
        product.setFamily(new CSFamily("relojeria"));
        CSAttributeDetail attribute = new CSAttributeDetail();
        attribute.setId("1");
        attribute.setValue("Test value");
        product.setProductPrices(new ArrayList<>());
        product.setVariants(new ArrayList<>());

        CSProductCategory category = new CSProductCategory();
        CSCategory cat = new CSCategory("accesoriosHombreRelojes");
        category.setCategory(cat);
        category.setIsPrimary(true);
        product.addProductCategory(category);

        CSProductPrice csProductPrice = new CSProductPrice();
        csProductPrice.setValue(5000);
        csProductPrice.setStore(new CSCode("default"));
        csProductPrice.setPrice(new CSPrice("normal"));
        product.addProductPrice(csProductPrice);

        product.addMedia(new CSMedia(1, "image-1",
                "https://i.etsystatic.com/34619050/r/il/1b9c8c/4034692250/il_1588xN.4034692250_rper.jpg"));

        product.addAttributeDetaul(attribute);
        return cencosudProvider.createProduct(product);
    }

    @RequestMapping("/family")
    @ResponseBody
    public CSFamilyResponse family() {
        cencosudProvider.login();
        return cencosudProvider.getFamilies(200, 1);
    }

}
