package com.hermes.chm.integraciones.marketplace.cencosud.connector.models.responses;

import java.util.List;

import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSFamily;

public class CSFamilyResponse {
    private CSProductsPagging pagging;
    private List<CSFamily> data;

    // getters and setters
    public CSProductsPagging getPagging() {
        return pagging;
    }

    public void setPagging(CSProductsPagging pagging) {
        this.pagging = pagging;
    }

    public List<CSFamily> getData() {
        return data;
    }

    public void setData(List<CSFamily> data) {
        this.data = data;
    }

}
