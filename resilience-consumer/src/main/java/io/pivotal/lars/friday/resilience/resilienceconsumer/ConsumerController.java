package io.pivotal.lars.friday.resilience.resilienceconsumer;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * ConsumerController
 */
@RestController
@Slf4j
public class ConsumerController {

    private final RestTemplate restTemplate;
    private final String providerUri;
    private final BulkheadConfig config;
    private final Bulkhead bulkhead;

    public ConsumerController(RestTemplate restTemplate, @Value("${provider.uri}") String providerUri,
            @Value("${maxConcurrent}") int maxConcurrent) {
        this.restTemplate = restTemplate;
        this.providerUri = providerUri;
        config = BulkheadConfig.custom().maxConcurrentCalls(maxConcurrent).build();
        bulkhead = Bulkhead.of("resilience-provider", config);
        bulkhead.getEventPublisher().onCallPermitted(event -> log.info("Call permitted"))
                .onCallRejected(event -> log.info("Call rejected"));
    }

    @GetMapping()
    public String okay() {
        return "The message was " + restTemplate.getForObject(providerUri, String.class);
    }

    @GetMapping("/slow")
    public String slow() {
        CheckedFunction0<String> bulkheadedBackend = Bulkhead.decorateCheckedSupplier(bulkhead,
                () -> "The message was " + restTemplate.getForObject(providerUri + "/slow", String.class));
        Try<String> result = Try.of(bulkheadedBackend).recover((throwable) -> "This is a fallback");
        return result.get();
    }
}
