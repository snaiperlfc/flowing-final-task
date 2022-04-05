package org.zimin.shipping.messages;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zimin.shipping.service.ShippingService;

@Component
@EnableBinding(Sink.class)
public class MessageListener {

    private final MessageSender messageSender;

    private final ShippingService shippingService;

    private final ObjectMapper objectMapper;

    public MessageListener(MessageSender messageSender, ShippingService shippingService, ObjectMapper objectMapper) {
        this.messageSender = messageSender;
        this.shippingService = shippingService;
        this.objectMapper = objectMapper;
    }

    @StreamListener(target = Sink.INPUT, condition = "(headers['type']?:'')=='ShipImageCommand'")
    @Transactional
    public void shipGoodsCommandReceived(String messageJson) throws Exception {
        Message<ShipImageCommandPayload> message =
                objectMapper.readValue(messageJson, new TypeReference<>() {
                });

        String shipmentId = shippingService.createShipment( //
                message.getData().getImageId(), //
                message.getData().getRecipientAddress(), //
                message.getData().getLogisticsProvider());

        messageSender.send( //
                new Message<>( //
                        "ImageShippedEvent", //
                        message.getTraceid(), //
                        new ImageShippedEventPayload() //
                                .setRefId(message.getData().getRefId())
                                .setShipmentId(shipmentId))
                        .setCorrelationid(message.getCorrelationid()));
    }


}
