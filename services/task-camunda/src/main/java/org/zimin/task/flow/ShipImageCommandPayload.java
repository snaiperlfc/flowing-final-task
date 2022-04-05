package org.zimin.task.flow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipImageCommandPayload {

    private String refId;
    private Long imageId;
    private String logisticsProvider;
    private String recipientAddress;

}
