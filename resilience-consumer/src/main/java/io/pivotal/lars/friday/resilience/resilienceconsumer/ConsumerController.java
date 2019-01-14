package io.pivotal.lars.friday.resilience.resilienceconsumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
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

    private final String providerUri;

    public ConsumerController(RestTemplate restTemplate, @Value("${provider.uri}") String providerUri)    {
        this.restTemplate = restTemplate;
        this.providerUri = providerUri;
    }

    @GetMapping()
    public String okay()    {
        return "The message was " + restTemplate.getForObject(providerUri, String.class);
    }

    @GetMapping("/slow")
    public Mono<String> slow()    {
        Bulkhead bulkhead = Bulkhead.ofDefaults("backendName");
        return Mono.fromCallable(()-> {
            return "The message was " + restTemplate.getForObject(providerUri + "/slow", String.class);
        }).transform(BulkheadOperator.of(bulkhead));
    }
    
}