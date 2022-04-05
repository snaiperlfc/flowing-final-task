package org.zimin.fact.messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(Source.class)
public class MessageSender {

    private final MessageChannel output;

    private final ObjectMapper objectMapper;

    public MessageSender(MessageChannel output, ObjectMapper objectMapper) {
        this.output = output;
        this.objectMapper = objectMapper;
    }

    public void send(Message<?> m) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(m);
            output.send(
                    MessageBuilder.withPayload(jsonMessage).setHeader("type", m.getType()).build());
        } catch (Exception e) {
            throw new RuntimeException("Could not transform and send message due to: " + e.getMessage(), e);
        }
    }
}
