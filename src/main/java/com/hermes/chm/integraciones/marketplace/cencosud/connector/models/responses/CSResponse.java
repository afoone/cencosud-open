package com.hermes.chm.integraciones.marketplace.cencosud.connector.models.responses;

import java.util.List;

public class CSResponse <T> {
    private CSProductsPagging pagging;
    private List<T> data;

    // getters and setters
    public CSProductsPagging getPagging() {
        return pagging;
    }

    public void setPagging(CSProductsPagging pagging) {
        this.pagging = pagging;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = (List<T>) data;
    }

    
}
