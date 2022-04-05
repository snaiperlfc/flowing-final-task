package org.zimin.fact.flow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import org.zimin.fact.messages.Message;
import org.zimin.fact.messages.MessageSender;
import org.zimin.fact.model.Fact;

@Component
public class FactReceivedAdapter implements JavaDelegate {

    private final MessageSender messageSender;

    public FactReceivedAdapter(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public void execute(DelegateExecution context) {
        String refId = (String) context.getVariable("refId");
        Fact fact = (Fact) context.getVariable("fact");
        String correlationId = (String) context.getVariable("correlationId");
        String traceId = context.getProcessBusinessKey();

        messageSender.send( //
                new Message<>( //
                        "FactReceivedEvent", //
                        traceId, //
                        new FactReceivedEventPayload() //
                                .setRefId(refId)
                                .setFact(fact.getFact()))
                        .setCorrelationid(correlationId));
    }

}
