package com.hermes.chm.integraciones.marketplace.cencosud.connector.factory;

import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSCategory;
import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSPrice;
import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSProductPrice;
import com.hermes.core.dto.PrecioProductoZonaPrecioDto;

public class CSProductPriceFactory {
    public static CSProductPrice getProductPrice() {
        return new CSProductPrice();
    }

    // from PrecioProductoZonaPrecioDto
    public static CSProductPrice getProductPrice(PrecioProductoZonaPrecioDto precioProductoZonaPrecioDto) {
        CSProductPrice csProductPrice = new CSProductPrice();
        csProductPrice.setValue(precioProductoZonaPrecioDto.getPrecio().doubleValue());
        CSPrice csPrice = new CSPrice();
        csPrice.setCode(Long.toString(precioProductoZonaPrecioDto.getIdZonaPrecio()));

   

        return csProductPrice;
    }
}
