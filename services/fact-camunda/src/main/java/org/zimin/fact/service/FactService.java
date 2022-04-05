package org.zimin.fact.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.zimin.fact.rest.RestClient;

@Service
@Slf4j
public class FactService {
    @Value("${fact.service.url}")
    String factUrl;

    final
    RestClient restClient;

    public FactService(RestClient restClient) {
        this.restClient = restClient;
    }

    public ResponseEntity<String> getRandomFact() {
        return restClient.get(factUrl);
    }
}
