package org.zimin.image.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.zimin.image.exceptions.RestTemplateResponseErrorHandler;
import org.zimin.image.rest.RestClient;

import java.io.*;
import java.util.UUID;

@RunWith(SpringRunner.class)
class ImageServiceImplTest {

    @Test
    void mergeImageFact() throws IOException {
        RestTemplateResponseErrorHandler restTemplateResponseErrorHandler = new RestTemplateResponseErrorHandler();
        RestClient restClient = new RestClient(restTemplateResponseErrorHandler);
        ImageService imageService = new ImageServiceImpl(restClient);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageService.mergeImageFact("https://picsum.photos/1024", "One thing to note on this dependency is that it includes the scope of test <scope>test</scope>. That means that when the application is bundled and packaged for deployment, any dependencies that are declared with the test scope are ignored. The test scope dependencies are only available when running in development and Maven test modes.", "zimin.niitp@gmail.com",
                outputStream);

        File f = new File("D:/final-task-images" + File.separator + UUID.randomUUID() + "_merged.png");

        // Note preferred way of declaring an array variable
        byte[] data = outputStream.toByteArray();
        try (OutputStream stream = new FileOutputStream(f)) {
            stream.write(data);
        }
    }

    @Test
    void paintTextWithOutline() {
    }
}