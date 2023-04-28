package com.hermes.chm.integraciones.marketplace.cencosud.connector.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.hermes.chm.api.bl.CategoryService;
import com.hermes.chm.api.model.chm.CategoriaCanalBD;
import com.hermes.chm.api.model.hermes.CmCanalBD;
import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSProduct;
import com.hermes.core.commons.PublicConstants;
import com.hermes.core.dto.PrecioProductoZonaPrecioDto;
import com.hermes.core.dto.ProductoCollectionDto;

@Component
public class CSProductFactory {

    @Autowired
	CategoryService categoryService;

    // empty method 
    public static CSProduct getProduct() {
        return new CSProduct();
    }

    // from Product
    public CSProduct getProduct(ProductoCollectionDto producto, CmCanalBD cmCanalSite) {
        CSProduct csProduct = new CSProduct();
        csProduct.setSku(producto.getCodigoAlfa());
        csProduct.setName(producto.getNombre());
        csProduct.setRefProduct(producto.getIdArticuloModalia());
        csProduct.setSellerId(producto.getIdArticuloOriginal());
        for (PrecioProductoZonaPrecioDto precio : producto.getPrecios()) {
            csProduct.addProductPrice(CSProductPriceFactory.getProductPrice(precio));
        }
        
        CategoriaCanalBD categoria = categoryService.obtenerCategoriaMapeada(producto.getIdCategorias(),
        PublicConstants.CANAL_MERCADO_LIBRE_ID,
        // cmCanalSite.getIdSiteRotulo());
        

        return csProduct;
    }

    
}
