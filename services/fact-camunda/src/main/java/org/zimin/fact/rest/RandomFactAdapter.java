package org.zimin.fact.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.zimin.fact.model.Fact;
import org.zimin.fact.service.FactService;

@Component
@Slf4j
public class RandomFactAdapter implements JavaDelegate {

    final
    FactService factService;

    public RandomFactAdapter(FactService factService) {
        this.factService = factService;
    }

    public void execute(DelegateExecution ctx) throws JsonProcessingException {
        ResponseEntity<String> response = factService.getRandomFact();
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("response {}", response.getBody());
            throw new BpmnError("FACT_ERROR", response.getStatusCode().toString());
        }
        ObjectMapper mapper = new ObjectMapper();
        Fact fact = mapper.readValue(response.getBody(), Fact.class);
        log.info(fact.toString());
        ctx.setVariable("fact", fact);
    }

}
