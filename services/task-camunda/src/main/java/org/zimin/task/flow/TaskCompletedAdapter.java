package org.zimin.task.flow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import org.zimin.task.messages.Message;
import org.zimin.task.messages.MessageSender;

@Component
public class TaskCompletedAdapter implements JavaDelegate {

  private final MessageSender messageSender;

  public TaskCompletedAdapter(MessageSender messageSender) {
    this.messageSender = messageSender;
  }

  @Override
  public void execute(DelegateExecution context) {
    String orderId = (String)context.getVariable("taskId");
    String traceId = context.getProcessBusinessKey();

    messageSender.send( //
            new Message<>( //
                    "TaskCompletedEvent", //
                    traceId, //
                    new TaskCompletedEventPayload(orderId) //
            ));
  }



}
