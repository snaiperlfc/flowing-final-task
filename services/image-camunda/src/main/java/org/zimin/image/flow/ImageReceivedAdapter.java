package org.zimin.image.flow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.zimin.image.messages.Message;
import org.zimin.image.messages.MessageSender;

@Component
public class ImageReceivedAdapter implements JavaDelegate {

    private final MessageSender messageSender;

    public ImageReceivedAdapter(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public void execute(DelegateExecution context) {
        String refId = (String) context.getVariable("refId");
        Long imageId = (Long) context.getVariable("imageId");
        String correlationId = (String) context.getVariable("correlationId");
        String traceId = context.getProcessBusinessKey();

        messageSender.send( //
                new Message<>( //
                        "ImageReceivedEvent", //
                        traceId, //
                        new ImageReceivedEventPayload() //
                                .setRefId(refId)
                                .setImageId(imageId))
                        .setCorrelationid(correlationId));
    }

}
