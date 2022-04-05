package org.zimin.image.messages;

import java.io.IOException;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.zimin.image.service.ImageService;

@Component
@EnableBinding(Sink.class)
public class MessageListener {

    private final RuntimeService camunda;

    private final ObjectMapper objectMapper;

    public MessageListener(RuntimeService camunda, ObjectMapper objectMapper) {
        this.camunda = camunda;
        this.objectMapper = objectMapper;
    }

    @StreamListener(target = Sink.INPUT, condition = "(headers['type']?:'')=='RetrieveImageCommand'")
    @Transactional
    public void retrieveImageCommandReceived(String messageJson) throws IOException {
        Message<RetrieveImageCommandPayload> message = objectMapper.readValue(messageJson, new TypeReference<>() {
        });
        RetrieveImageCommandPayload retrieveImageCommand = message.getData();

        System.out.println("Retrieve fact for " + retrieveImageCommand.getRefId());

        camunda.createMessageCorrelation(message.getType()) //
                .processInstanceBusinessKey(message.getTraceid())
                .setVariable("email", retrieveImageCommand.getEmail()) //
                .setVariable("fact", retrieveImageCommand.getFact()) //
                .setVariable("image", retrieveImageCommand.getImage()) //
                .setVariable("refId", retrieveImageCommand.getRefId()) //
                .setVariable("correlationId", message.getCorrelationid()) //
                .correlateWithResult();
    }


}
