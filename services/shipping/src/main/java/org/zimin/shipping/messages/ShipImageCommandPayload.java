package org.zimin.shipping.messages;

import lombok.Data;

@Data
public class ShipImageCommandPayload {

    private String refId;
    private Long imageId;
    private String logisticsProvider;
    private String recipientAddress;

}
