package org.zimin.email.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zimin.email.domain.Task;
import org.zimin.email.messages.Message;
import org.zimin.email.messages.MessageSender;

import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
public class EmailRestController {

    private final MessageSender messageSender;

    public EmailRestController(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @RequestMapping(path = "/api/cart/task", method = PUT)
    public String placeTask(@RequestParam(value = "email") String email) {

        Task task = new Task();
        task.setEmail(email);

        Message<Task> message = new Message<>("TaskPlacedEvent", task);
        messageSender.send(message);

        return "{\"traceId\": \"" + message.getTraceid() + "\"}";
    }

    @RequestMapping(path = "/api/cart/tasks", method = PUT)
    public String placeTasks(@RequestParam(value = "count") Integer count) {

        for (int i = 0; i < count; i++) {
            Task task = new Task();
            task.setEmail("generated_email_" + UUID.randomUUID());

            Message<Task> message = new Message<>("TaskPlacedEvent", task);
            messageSender.send(message);
        }
        return "{\"status\": \"task started: " + count + "\"}";
    }

}