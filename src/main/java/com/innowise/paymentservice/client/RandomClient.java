package com.innowise.paymentservice.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RandomClient {
    private final RestTemplate restTemplate;

    public RandomClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public int random() {
        Integer[] forObject = restTemplate.getForObject("https://www.randomnumberapi.com/api/v1.0/random", Integer[].class);
        return forObject[0];
    }

}
