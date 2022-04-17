package org.zimin.image.service;

import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface ImageService {

    ResponseEntity<String> getRandomImage();

    void mergeImageFact(String imageUrl, String fact, String email, ByteArrayOutputStream outputStream) throws IOException;
}
