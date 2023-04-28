package com.hermes.chm.integraciones.marketplace.cencosud.connector.models.payloads;

public class Pagination {
    private int limit;
    private int offset;

    public Pagination(int limit, int offset) {
        this.limit = limit;
        this.offset = offset;
    }

    public Pagination(int offset) {
        this.limit = 50;
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

}
