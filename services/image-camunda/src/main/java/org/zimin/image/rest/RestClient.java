package org.zimin.image.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.zimin.image.exceptions.RestTemplateResponseErrorHandler;

@Component
@Slf4j
public class RestClient {

    final
    RestTemplateResponseErrorHandler restTemplateResponseErrorHandler;

    public RestClient(RestTemplateResponseErrorHandler restTemplateResponseErrorHandler) {
        this.restTemplateResponseErrorHandler = restTemplateResponseErrorHandler;
    }

    public ResponseEntity<String> get(String url) {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "*/*");
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        rest.setErrorHandler(restTemplateResponseErrorHandler);
        return rest.exchange(url, HttpMethod.GET, requestEntity, String.class);
    }

}
