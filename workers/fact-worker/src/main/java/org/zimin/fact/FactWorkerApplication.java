package org.zimin.fact;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
public class FactWorkerApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(FactWorkerApplication.class, args);
    }

}
