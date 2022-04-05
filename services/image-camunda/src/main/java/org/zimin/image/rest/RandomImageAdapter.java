package org.zimin.image.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.zimin.image.model.CatApiResponse;
import org.zimin.image.service.ImageService;

import java.util.List;

@Component
@Slf4j
public class RandomImageAdapter implements JavaDelegate {

    final
    ImageService imageService;

    public RandomImageAdapter(ImageService imageService) {
        this.imageService = imageService;
    }

    public void execute(DelegateExecution ctx) throws JsonProcessingException {
        ResponseEntity<String> response = imageService.getRandomImage();
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new BpmnError("IMAGE_ERROR");
        }
        ObjectMapper mapper = new ObjectMapper();
        List<CatApiResponse> catApiResponse = mapper.readValue(response.getBody(), new TypeReference<>() {
        });
        ctx.setVariable("randomImage", catApiResponse.get(0).getUrl());
    }

}
