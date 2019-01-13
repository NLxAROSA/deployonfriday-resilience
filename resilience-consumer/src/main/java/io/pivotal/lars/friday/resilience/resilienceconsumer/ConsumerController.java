package io.pivotal.lars.friday.resilience.resilienceconsumer;

import java.net.URISyntaxException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * ConsumerController
 */
@RestController
public class ConsumerController {

    private final RestTemplate restTemplate;

    public ConsumerController(RestTemplate restTemplate)    {
        this.restTemplate = restTemplate;
    }

    @GetMapping()
    public String doSomething() throws RestClientException, URISyntaxException {
        return "The message was " + restTemplate.getForObject("https://resilience-provider/", String.class);
    }
    
}