package com.hermes.chm.integraciones.marketplace.cencosud.connector.factory;

import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSCategory;

public class CSCategoryFactory {
    public static CSCategory getCategory() {
        return new CSCategory();
    }

    public static CSCategory getCategory(String name) {
        CSCategory csCategory = new CSCategory();
        csCategory.setName(name);
        return csCategory;
    }

    public static CSCategory getCategory(String name, String code) {
        CSCategory csCategory = new CSCategory();
        csCategory.setName(name);
        csCategory.setCode(code);
        return csCategory;
    }
    
}
