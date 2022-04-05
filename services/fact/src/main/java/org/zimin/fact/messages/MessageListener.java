package org.zimin.fact.messages;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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

    private final MessageSender messageSender;

    private final ObjectMapper objectMapper;

    public MessageListener(MessageSender messageSender, ObjectMapper objectMapper) {
        this.messageSender = messageSender;
        this.objectMapper = objectMapper;
    }

    @StreamListener(target = Sink.INPUT, condition = "(headers['type']?:'')=='RetrieveFactCommand'")
    @Transactional
    public void retrieveFactCommandReceived(String messageJson) throws IOException {
        Message<RetrieveFactCommandPayload> message =
                objectMapper.readValue(messageJson, new TypeReference<>() {
                });
        RetrieveFactCommandPayload retrievePaymentCommand = message.getData();

        System.out.println("Retrieve fact for " + retrievePaymentCommand.getRefId());

        messageSender.send( //
                new Message<>( //
                        "FactReceivedEvent", //
                        message.getTraceid(), //
                        new FactReceivedEventPayload() //
                                .setRefId(retrievePaymentCommand.getRefId())
                                .setFact("fact_" + UUID.randomUUID()))
                        .setCorrelationid(message.getCorrelationid()));

    }


}
