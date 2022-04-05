package org.zimin.task.flow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import org.zimin.task.domain.Task;
import org.zimin.task.messages.Message;
import org.zimin.task.messages.MessageSender;
import org.zimin.task.persistence.TaskRepository;

@Component
public class RetrieveFactAdapter implements JavaDelegate {

    private final MessageSender messageSender;

    private final TaskRepository taskRepository;

    public RetrieveFactAdapter(MessageSender messageSender, TaskRepository taskRepository) {
        this.messageSender = messageSender;
        this.taskRepository = taskRepository;
    }

    @Override
    public void execute(DelegateExecution context) {
        Task task = taskRepository.findById((String) context.getVariable("taskId")).get();
        String traceId = context.getProcessBusinessKey();

        messageSender.send( //
                new Message<>( //
                        "RetrieveFactCommand", //
                        traceId, //
                        new RetrieveFactCommandPayload(task.getId(), null) //
                ));
    }

}
