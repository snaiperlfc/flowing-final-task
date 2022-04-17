package org.zimin.image.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.zimin.image.model.Image;
import org.zimin.image.persistence.ImageRepository;

@Slf4j
@RestController
@RequestMapping("/api")
public class WebController {

    final
    ImageRepository repository;

    public WebController(ImageRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/image/{id}")
    @ResponseBody
    public byte[] getImage(@PathVariable Long id) {
        Image image = repository.findById(id).get();
        return image.getData();
    }
}
