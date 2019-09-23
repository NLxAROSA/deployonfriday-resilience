package io.pivotal.lars.friday.resilience.resilienceconsumer;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;

/**
 * ConsumerController
 */
@RestController
public class ConsumerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerController.class);
    private final RestTemplate restTemplate;
    private final String providerUri;
    private final Bulkhead bulkhead;
    private final CircuitBreaker circuitBreaker;

    public ConsumerController(RestTemplate restTemplate, @Value("${provider.uri}") String providerUri,
            @Value("${maxConcurrent}") int maxConcurrent) {
        this.restTemplate = restTemplate;
        this.providerUri = providerUri;
        this.bulkhead = createBulkHead(maxConcurrent);
        this.circuitBreaker = createCircuitBreaker();
    }

    private Bulkhead createBulkHead(int maxConcurrent) {
        BulkheadConfig bulkheadConfig = BulkheadConfig.custom().maxConcurrentCalls(maxConcurrent).build();
        Bulkhead bulkhead = Bulkhead.of("resilience-provider", bulkheadConfig);
        bulkhead.getEventPublisher().onCallPermitted(event -> LOGGER.info("Call permitted by bulkhead"))
                .onCallRejected(event -> LOGGER.info("Call rejected by bulkhead"));
        return bulkhead;
    }

    private CircuitBreaker createCircuitBreaker() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom().failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(20000)).build();
        CircuitBreaker circuitBreaker = CircuitBreaker.of("resilience-provider", circuitBreakerConfig);
        circuitBreaker.getEventPublisher().onSuccess(event -> LOGGER.info("Call success via circuit breaker"))
                .onCallNotPermitted(event -> LOGGER.info("Call denied by circuit breaker"))
                .onError(event -> LOGGER.info("Call failed via circuit breaker"));
        return circuitBreaker;
    }

    @GetMapping()
    public String okay() {
        return "The message was " + restTemplate.getForObject(providerUri, String.class);
    }

    @GetMapping("/bulkhead")
    public String bulkhead() {
        CheckedFunction0<String> someServiceCall = Bulkhead.decorateCheckedSupplier(bulkhead,
                () -> "The message was " + restTemplate.getForObject(providerUri + "/slow", String.class));
        Try<String> result = Try.of(someServiceCall).recover((throwable) -> "This is a bulkhead fallback");
        return result.get();
    }

    @GetMapping("/circuitbreaker")
    public String circuitBreakerFail(@RequestParam boolean shouldFail) {
        if (shouldFail) {
            return callServiceViaCircuitBreaker("/error");
        } else {
            return callServiceViaCircuitBreaker("/");
        }
    }

    private String callServiceViaCircuitBreaker(String uri) {
        CheckedFunction0<String> someServiceCall = CircuitBreaker.decorateCheckedSupplier(circuitBreaker,
                () -> "The message was " + restTemplate.getForObject(providerUri + uri, String.class));
        Try<String> result = Try.of(someServiceCall).recover((throwable) -> "This is a circuit breaker fallback");
        return result.get();
    }
}
