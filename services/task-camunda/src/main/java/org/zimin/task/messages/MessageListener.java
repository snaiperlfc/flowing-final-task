package org.zimin.task.messages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.spin.plugin.variable.SpinValues;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zimin.task.domain.Fact;
import org.zimin.task.domain.Task;
import org.zimin.task.flow.RetrieveFactCommandPayload;
import org.zimin.task.flow.RetrieveImageCommandPayload;
import org.zimin.task.persistence.TaskRepository;

@Component
@EnableBinding(Sink.class)
public class MessageListener {

    private final TaskRepository repository;

    private final RuntimeService camunda;

    private final ObjectMapper objectMapper;

    public MessageListener(TaskRepository repository, RuntimeService camunda, ObjectMapper objectMapper) {
        this.repository = repository;
        this.camunda = camunda;
        this.objectMapper = objectMapper;
    }

    /**
     * Handles incoming TaskPlacedEvents.
     */
    @StreamListener(target = Sink.INPUT, condition = "(headers['type']?:'')=='TaskPlacedEvent'")
    @Transactional
    public void taskPlacedReceived(Message<Task> message) {
        Task task = message.getData();

        System.out.println("New task placed, start flow. " + task);

        // persist domain entity
        repository.save(task);

        // and kick of a new flow instance
        camunda.createMessageCorrelation(message.getType())
                .processInstanceBusinessKey(message.getTraceid())
                .setVariable("taskId", task.getId())
                .correlateWithResult();
    }

    @StreamListener(target = Sink.INPUT, condition = "(headers['type']?:'').endsWith('Event')")
    @Transactional
    public void messageReceived(String messageJson) throws Exception {
        Message<JsonNode> message = objectMapper.readValue( //
                messageJson, //
                new TypeReference<>() {
                });

        long correlatingInstances = camunda.createExecutionQuery() //
                .messageEventSubscriptionName(message.getType()) //
                .processInstanceBusinessKey(message.getTraceid()) //
                .count();

        if (correlatingInstances == 1) {
            System.out.println("Correlating " + message + " to waiting flow instance");

            if (message.getType().equals("FactReceivedEvent")) {
                RetrieveFactCommandPayload fact =
                        objectMapper.readValue(message.getData().toString(), RetrieveFactCommandPayload.class);

                camunda.createMessageCorrelation(message.getType())
                        .processInstanceBusinessKey(message.getTraceid())
                        .setVariable("fact", fact.getFact())
                        .correlateWithResult();

            } else if (message.getType().equals("ImageReceivedEvent")) {
                RetrieveImageCommandPayload image =
                        objectMapper.readValue(message.getData().toString(), RetrieveImageCommandPayload.class);

                camunda.createMessageCorrelation(message.getType())
                        .processInstanceBusinessKey(message.getTraceid())
                        .setVariable("imageId", image.getImageId())
                        .correlateWithResult();

            } else {
                camunda.createMessageCorrelation(message.getType())
                        .processInstanceBusinessKey(message.getTraceid())
                        .setVariable(//
                                "PAYLOAD_" + message.getType(), //
                                SpinValues.jsonValue(message.getData().toString()).create())//
                        .correlateWithResult();
            }
        }

    }

}
