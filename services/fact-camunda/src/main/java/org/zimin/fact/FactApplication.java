package org.zimin.fact;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableProcessApplication
public class FactApplication {

    public static void main(String[] args) {
        SpringApplication.run(FactApplication.class, args);
    }

    @Bean
    public RestTemplate createRestTemplate() {
        return new RestTemplate();
    }

//    @Bean
//    public Consumer<Message<RetrieveFactCommandPayload>> retrieveFactCommand() {
//        return msg -> {
//            if (msg.getType().equals("RetrieveFactCommand"))
//                System.out.println(msg);
//        };
//    }
}
