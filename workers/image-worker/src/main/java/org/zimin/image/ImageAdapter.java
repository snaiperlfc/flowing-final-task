package org.zimin.image;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.zimin.image.messages.ImageReceivedEventPayload;
import org.zimin.image.messages.Message;
import org.zimin.image.messages.MessageSender;
import org.zimin.image.model.CatApiResponse;
import org.zimin.image.model.Image;
import org.zimin.image.persistence.ImageRepository;
import org.zimin.image.service.ImageService;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class ImageAdapter {

    @Value("${image.service.url}")
    private String imageUrl;

    final
    ImageService imageService;

    final
    ImageRepository repository;

    private final MessageSender messageSender;

    public ImageAdapter(ImageService imageService, ImageRepository repository, MessageSender messageSender) {
        this.imageService = imageService;
        this.repository = repository;
        this.messageSender = messageSender;
    }

    @ZeebeWorker(type = "fetch-image")
    public void retrieveFact(JobClient client, ActivatedJob job) {
        Map<String, Object> variables;
        String correlationId = UUID.randomUUID().toString();
        try {
            variables = job.getVariablesAsMap();
            String traceId = (String) variables.get("traceId");
            String taskId = (String) variables.get("taskId");
            String fact = (String) variables.get("fact");
            String email = (String) variables.get("email");

            System.out.println("Fetch image for " + taskId);

            ResponseEntity<String> response = imageService.getRandomImage();
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ZeebeBpmnError("IMAGE_ERROR", correlationId);
            }
            ObjectMapper mapper = new ObjectMapper();
            List<CatApiResponse> catApiResponse = mapper.readValue(response.getBody(), new TypeReference<>() {
            });
            variables.put("randomImage", catApiResponse.get(0).getUrl());

            String randomImage = (String) variables.get("randomImage");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            imageService.mergeImageFact(randomImage, fact, email, out);

            Image newImage = repository.save(new Image(out.toByteArray()));
            variables.put("imageId", newImage.getId());

            messageSender.send( //
                    new Message<>( //
                            "ImageReceivedEvent", //
                            traceId, //
                            new ImageReceivedEventPayload() //
                                    .setRefId(taskId)
                                    .setImageId(newImage.getId()))
                            .setCorrelationid(correlationId));

        } catch (Exception e) {
            throw new RuntimeException("Could not parse payload: " + e.getMessage(), e);
        }

        client.newCompleteCommand(job.getKey()).variables(Collections.singletonMap("CorrelationId_FetchImage", correlationId)).send()
                .exceptionally(throwable -> {
                    throw new RuntimeException("Could not complete job " + job, throwable);
                });
    }
}
