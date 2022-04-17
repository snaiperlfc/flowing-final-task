package org.zimin.shipping;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zimin.shipping.messages.ImageShippedEventPayload;
import org.zimin.shipping.messages.Message;
import org.zimin.shipping.messages.MessageSender;
import org.zimin.shipping.service.ShippingService;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class ShipImageAdapter {

    private final MessageSender messageSender;

    private final ShippingService shippingService;


    public ShipImageAdapter(MessageSender messageSender, ShippingService shippingService) {
        this.messageSender = messageSender;
        this.shippingService = shippingService;
    }

    @ZeebeWorker(type = "ship-image")
    public void retrieveFact(JobClient client, ActivatedJob job) {
        Map<String, Object> variables;
        String correlationId = UUID.randomUUID().toString();
        try {
            variables = job.getVariablesAsMap();
            String traceId = (String) variables.get("traceId");
            String taskId = (String) variables.get("taskId");
            Integer imageId = (Integer) variables.get("imageId");
            String email = (String) variables.get("email");

            System.out.println("Fetch image for " + taskId);

            String shipmentId = shippingService.createShipment(imageId.longValue(), email, "email");

            messageSender.send( //
                    new Message<>( //
                            "ImageShippedEvent", //
                            traceId, //
                            new ImageShippedEventPayload() //
                                    .setRefId(taskId)
                                    .setShipmentId(shipmentId))
                            .setCorrelationid(correlationId));

        } catch (Exception e) {
            throw new RuntimeException("Could not parse payload: " + e.getMessage(), e);
        }

        client.newCompleteCommand(job.getKey()).variables(Collections.singletonMap("CorrelationId_ShipImage", correlationId)).send()
                .exceptionally(throwable -> {
                    throw new RuntimeException("Could not complete job " + job, throwable);
                });
    }
}
