package io.pivotal.lars.friday.resilience.resilienceconsumer;

import java.net.URISyntaxException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.reactor.bulkhead.operator.BulkheadOperator;
import reactor.core.publisher.Mono;

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
    public String okay()    {
        return "The message was " + restTemplate.getForObject("https://resilience-provider/", String.class);
    }

    @GetMapping("/slow")
    public Mono<String> slow()    {
        Bulkhead bulkhead = Bulkhead.ofDefaults("backendName");
        return Mono.fromCallable(()-> {
            return "The message was " + restTemplate.getForObject("https://resilience-provider/slow", String.class);
        }).transform(BulkheadOperator.of(bulkhead));
    }
    
}