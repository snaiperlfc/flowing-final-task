package org.zimin.fact.messages;

import java.io.IOException;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@EnableBinding(Sink.class)
public class MessageListener {

    private final RuntimeService camunda;

    private final ObjectMapper objectMapper;

    public MessageListener(RuntimeService camunda, ObjectMapper objectMapper) {
        this.camunda = camunda;
        this.objectMapper = objectMapper;
    }

    @StreamListener(target = Sink.INPUT, condition = "(headers['type']?:'')=='RetrieveFactCommand'")
    @Transactional
    public void retrieveFactCommandReceived(String messageJson) throws IOException {
        Message<RetrieveFactCommandPayload> message = objectMapper.readValue(messageJson, new TypeReference<>() {
        });
        RetrieveFactCommandPayload retrieveFactCommand = message.getData();

        System.out.println("Retrieve fact for " + retrieveFactCommand.getRefId());

        camunda.createMessageCorrelation(message.getType()) //
                .processInstanceBusinessKey(message.getTraceid())
//                .setVariable("fact", retrieveFactCommand.getFact()) //
                .setVariable("refId", retrieveFactCommand.getRefId()) //
                .setVariable("correlationId", message.getCorrelationid()) //
                .correlateWithResult();
    }


}
