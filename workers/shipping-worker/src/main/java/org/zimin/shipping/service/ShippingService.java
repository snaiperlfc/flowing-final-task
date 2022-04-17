package org.zimin.shipping.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Component;
import org.zimin.shipping.rest.RestClient;

import java.util.UUID;


@Component
public class ShippingService {

    @Value("${image.service.url}")
    private String imageUrl;

    final
    EmailService emailService;

    final
    RestClient restClient;

    public ShippingService(EmailService emailService, RestClient restClient) {
        this.emailService = emailService;
        this.restClient = restClient;
    }

    /**
     * @param imageId           - image ID
     * @param recipientAddress  address the shipment is sent to
     * @param logisticsProvider delivering the shipment
     * @return shipment id created
     */
    public String createShipment(Long imageId, String recipientAddress, String logisticsProvider) throws Exception {
        System.out.println("Shipping to " + recipientAddress);

        byte[] response = restClient.getImage(imageUrl + "/" + imageId);
        InputStreamSource inputStreamSource = new ByteArrayResource(response);

        String shipmentId = UUID.randomUUID().toString();

        emailService.sendMessageWithAttachment(recipientAddress, "Финальное задание курса 2307",
                "Случайная картинка с фактом о кошках", shipmentId + ".png", inputStreamSource);

        return shipmentId;
    }

}
