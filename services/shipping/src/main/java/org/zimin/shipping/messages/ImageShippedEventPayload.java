package org.zimin.shipping.messages;

import lombok.Data;

@Data
public class ImageShippedEventPayload {

    private String refId;
    private String shipmentId;

    public ImageShippedEventPayload setRefId(String refId) {
        this.refId = refId;
        return this;
    }

    public ImageShippedEventPayload setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
        return this;
    }
}
