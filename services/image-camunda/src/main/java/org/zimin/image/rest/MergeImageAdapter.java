package org.zimin.image.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.zimin.image.model.Fact;
import org.zimin.image.model.Image;
import org.zimin.image.persistence.ImageRepository;
import org.zimin.image.service.ImageService;

import java.io.*;
import java.util.UUID;

@Component
@Slf4j
public class MergeImageAdapter implements JavaDelegate {

    @Value("${image.service.url}")
    private String imageUrl;

    final
    ImageService imageService;

    final
    ImageRepository repository;

    public MergeImageAdapter(ImageService imageService, ImageRepository repository) {
        this.imageService = imageService;
        this.repository = repository;
    }


    public void execute(DelegateExecution ctx) throws IOException {
        String fact = (String) ctx.getVariable("fact");
        String email = ctx.getVariable("email").toString();
        String randomImage = ctx.getVariable("randomImage").toString();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        imageService.mergeImageFact(randomImage, fact, email, out);

        Image newImage = repository.save(new Image(out.toByteArray()));
        ctx.setVariable("imageId", newImage.getId());
    }

}
