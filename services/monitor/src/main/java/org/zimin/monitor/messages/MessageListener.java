package org.zimin.monitor.messages;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zimin.monitor.domain.PastEvent;
import org.zimin.monitor.persistence.LogRepository;

import java.nio.charset.StandardCharsets;

@Component
@EnableBinding(Sink.class)
public class MessageListener {

    private final SimpMessagingTemplate simpMessageTemplate;

    private final ObjectMapper objectMapper;

    public MessageListener(SimpMessagingTemplate simpMessageTemplate, ObjectMapper objectMapper) {
        this.simpMessageTemplate = simpMessageTemplate;
        this.objectMapper = objectMapper;
    }

    @StreamListener(target = Sink.INPUT)
    @Transactional
    public void messageReceived(byte[] messageJsonBytes) throws Exception {
        String messageJson = new String(messageJsonBytes, StandardCharsets.UTF_8);
        Message<JsonNode> message = objectMapper.readValue( //
                messageJson, //
                new TypeReference<>() {
                });

        String type = "Event";
        if (message.getType().endsWith("Command")) {
            type = "Command";
        }

        PastEvent event = new PastEvent( //
                type, //
                message.getType(), //
                message.getTraceid(), //
                message.getSource(), //
                message.getData().toString());
        event.setSourceJson(messageJson);

        // save
        LogRepository.instance.addEvent(event);

        // and probably send to connected websocket
        simpMessageTemplate.convertAndSend("/topic/events", event);
    }

}
