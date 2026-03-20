package com.innowise.paymentservice.client;

import com.innowise.paymentservice.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;



@Component
public class RandomClient {
    private final RestTemplate restTemplate;

    public RandomClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "randomnumberapi", fallbackMethod = "randomFallBack")
    public int random() {
        Integer[] forObject = restTemplate.getForObject("https://www.randomnumberapi.com/api/v1.0/random", Integer[].class);
        if (forObject != null && forObject.length > 0) {
            return forObject[0];
        }
        return 0;
    }

    public int randomFallBack(Throwable t)  {
        throw new ServiceUnavailableException("Random service if unavailable",t);
    }
}
