package org.zimin.task.flow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.plugin.variable.value.JsonValue;
import org.springframework.stereotype.Component;
import org.zimin.task.domain.Fact;
import org.zimin.task.domain.Task;
import org.zimin.task.messages.Message;
import org.zimin.task.messages.MessageSender;
import org.zimin.task.persistence.TaskRepository;

@Component
public class RetrieveImageAdapter implements JavaDelegate {

    private final MessageSender messageSender;

    private final TaskRepository taskRepository;

    public RetrieveImageAdapter(MessageSender messageSender, TaskRepository taskRepository) {
        this.messageSender = messageSender;
        this.taskRepository = taskRepository;
    }

    @Override
    public void execute(DelegateExecution context) {
        Task task = taskRepository.findById((String) context.getVariable("taskId")).get();
        String fact = (String) context.getVariable("fact");

        String traceId = context.getProcessBusinessKey();

        // publish
        messageSender.send(new Message<>( //
                "RetrieveImageCommand", //
                traceId, //
                new RetrieveImageCommandPayload(task.getId(), task.getEmail(), fact) //
        ));
    }

}
