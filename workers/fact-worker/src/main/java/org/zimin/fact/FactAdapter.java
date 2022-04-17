package org.zimin.fact;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.zimin.fact.messages.FactReceivedEventPayload;
import org.zimin.fact.messages.Message;
import org.zimin.fact.messages.MessageSender;
import org.zimin.fact.model.Fact;
import org.zimin.fact.service.FactService;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class FactAdapter {

    final
    FactService factService;

    private final MessageSender messageSender;

    public FactAdapter(FactService factService, MessageSender messageSender) {
        this.factService = factService;
        this.messageSender = messageSender;
    }

    @ZeebeWorker(type = "retrieve-fact")
    public void retrieveFact(JobClient client, ActivatedJob job) {
        Map<String, Object> variables;
        String correlationId = UUID.randomUUID().toString();
        try {
            variables = job.getVariablesAsMap();
            String traceId = (String) variables.get("traceId");
            String taskId = (String) variables.get("taskId");


            System.out.println("Retrieve fact for " + taskId);

            Thread.sleep(500);
            ResponseEntity<String> response = factService.getRandomFact();
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("response {}", response.getBody());
                throw new ZeebeBpmnError("FACT_ERROR", response.getStatusCode().toString());
            }
            ObjectMapper mapper = new ObjectMapper();
            Fact fact = mapper.readValue(response.getBody(), Fact.class);
            log.info(fact.toString());
            variables.put("fact", fact);

            messageSender.send( //
                    new Message<>( //
                            "FactReceivedEvent", //
                            traceId, //
                            new FactReceivedEventPayload() //
                                    .setRefId(taskId)
                                    .setFact(fact.getFact()))
                            .setCorrelationid(correlationId));

        } catch (Exception e) {
            throw new RuntimeException("Could not parse payload: " + e.getMessage(), e);
        }

        client.newCompleteCommand(job.getKey()).variables(Collections.singletonMap("CorrelationId_RetrieveFact", correlationId)).send()
                .exceptionally(throwable -> {
                    throw new RuntimeException("Could not complete job " + job, throwable);
                });
    }

}
