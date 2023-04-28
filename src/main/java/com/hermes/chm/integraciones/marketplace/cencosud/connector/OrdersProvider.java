package com.hermes.chm.integraciones.marketplace.cencosud.connector;

import org.springframework.stereotype.Component;

@Component
public class OrdersProvider {

    
    private CencosudProvider cencosudProvider;

    OrdersProvider(CencosudProvider cencosudProvider) {
        this.cencosudProvider = cencosudProvider;
    }
}
