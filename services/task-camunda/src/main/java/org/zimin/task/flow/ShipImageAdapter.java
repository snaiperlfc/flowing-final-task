package org.zimin.task.flow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.zimin.task.domain.Task;
import org.zimin.task.messages.Message;
import org.zimin.task.messages.MessageSender;
import org.zimin.task.persistence.TaskRepository;

@Component
public class ShipImageAdapter implements JavaDelegate {

    private final MessageSender messageSender;

    private final TaskRepository taskRepository;

    public ShipImageAdapter(MessageSender messageSender, TaskRepository taskRepository) {
        this.messageSender = messageSender;
        this.taskRepository = taskRepository;
    }

    @Override
    public void execute(DelegateExecution context) {
        Task task = taskRepository.findById( (String) context.getVariable("taskId")).get();
        Long imageId = (Long) context.getVariable("imageId");
        String traceId = context.getProcessBusinessKey();

        messageSender.send(new Message<>( //
                "ShipImageCommand", //
                traceId, //
                new ShipImageCommandPayload(task.getId(), imageId, "email", task.getEmail()) //
        ));
    }

}
