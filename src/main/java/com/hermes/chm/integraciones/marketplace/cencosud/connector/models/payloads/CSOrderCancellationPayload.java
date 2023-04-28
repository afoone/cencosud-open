package com.hermes.chm.integraciones.marketplace.cencosud.connector.models.payloads;

import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSSKUQuantity;

// {
//     "status": "cancelled",
//     "skus": [
//       {
//         "sku": "MK00L68E8A-1",
//         "quantity": 3
//       },
//       {
//         "sku": "MKRP52HOC5-4",
//         "quantity": 1
//       }
//     ],
//     "cancellationReasonId": 0
//   }
public class CSOrderCancellationPayload {
    private String status;
    private CSSKUQuantity[] skus;
    private int cancellationReasonId;

    // getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CSSKUQuantity[] getSkus() {
        return skus;
    }

    public void setSkus(CSSKUQuantity[] skus) {
        this.skus = skus;
    }

    public int getCancellationReasonId() {
        return cancellationReasonId;
    }

    public void setCancellationReasonId(int cancellationReasonId) {
        this.cancellationReasonId = cancellationReasonId;
    }

}
