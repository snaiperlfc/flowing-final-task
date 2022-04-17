package org.zimin.task.messages;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zimin.task.domain.Task;
import org.zimin.task.flow.TaskFlowContext;
import org.zimin.task.flow.payload.ImageShippedEventPayload;
import org.zimin.task.flow.payload.RetrieveFactCommandPayload;
import org.zimin.task.flow.payload.RetrieveImageCommandPayload;
import org.zimin.task.persistence.TaskRepository;

import java.util.Collections;

@Component
@EnableBinding(Sink.class)
public class MessageListener {

    private final TaskRepository repository;

    private final ZeebeClient zeebe;

    private final ObjectMapper objectMapper;

    public MessageListener(TaskRepository repository, ObjectMapper objectMapper, ZeebeClient zeebe) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.zeebe = zeebe;
    }

    /**
     * Handles incoming TaskPlacedEvents.
     */
    @StreamListener(target = Sink.INPUT, condition = "(headers['type']?:'')=='TaskPlacedEvent'")
    @Transactional
    public void taskPlacedReceived(Message<Task> message) throws InterruptedException {
        Task task = message.getData();

        System.out.println("New task placed, start flow. " + task);

        // persist domain entity
        Task newTask = repository.save(task);
//        Thread.sleep(1000);

        // prepare data for workflow
        TaskFlowContext context = new TaskFlowContext();
        context.setTaskId(newTask.getId());
        context.setTraceId(message.getTraceid());
        context.setEmail(task.getEmail());

        // and kick of a new flow instance
        System.out.println("New task placed, start flow with " + context);
        zeebe.newCreateInstanceCommand() //
                .bpmnProcessId("task-camunda-8") //
                .latestVersion() //
                .variables(context.asMap()) //
                .send().join();
    }

    @StreamListener(target = Sink.INPUT, condition = "(headers['type']?:'')=='FactReceivedEvent'")
    @Transactional
    public void factReceived(String messageJson) throws Exception {
        Message<RetrieveFactCommandPayload> message = objectMapper.readValue(messageJson, new TypeReference<>() {
        });

        RetrieveFactCommandPayload retrieveFactCommand = message.getData();

        System.out.println("Retrieve fact for " + retrieveFactCommand.getRefId());

        zeebe.newPublishMessageCommand() //
                .messageName(message.getType())
                .correlationKey(message.getCorrelationid())
                .variables(Collections.singletonMap("fact", retrieveFactCommand.getFact()))
                .send().join();

        System.out.println("Correlated " + message);
    }

    @StreamListener(target = Sink.INPUT, condition = "(headers['type']?:'')=='ImageReceivedEvent'")
    @Transactional
    public void imageReceived(String messageJson) throws Exception {
        Message<RetrieveImageCommandPayload> message = objectMapper.readValue(messageJson, new TypeReference<>() {
        });

        Long imageId = message.getData().getImageId();

        zeebe.newPublishMessageCommand() //
                .messageName(message.getType()) //
                .correlationKey(message.getCorrelationid()) //
                .variables(Collections.singletonMap("imageId", imageId)) //
                .send().join();

        System.out.println("Correlated " + message);
    }


    @StreamListener(target = Sink.INPUT, condition = "(headers['type']?:'')=='ImageShippedEvent'")
    @Transactional
    public void goodsShippedReceived(String messageJson) throws Exception {
        Message<ImageShippedEventPayload> message = objectMapper.readValue(messageJson, new TypeReference<>() {
        });

        String shipmentId = message.getData().getShipmentId();

        zeebe.newPublishMessageCommand() //
                .messageName(message.getType()) //
                .correlationKey(message.getCorrelationid()) //
                .variables(Collections.singletonMap("shipmentId", shipmentId)) //
                .send().join();

        System.out.println("Correlated " + message);
    }

}
