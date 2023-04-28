package com.hermes.chm.integraciones.marketplace.cencosud.connector.models.responses;

import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSOrder;

public class CSOrdersResponse {
    private CSOrder[] data;
    private int count;

    // getters and setters
    public CSOrder[] getData() {
        return data;
    }

    public void setData(CSOrder[] data) {
        this.data = data;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
