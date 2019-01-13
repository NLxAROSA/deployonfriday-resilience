package io.pivotal.lars.friday.resilience.resilienceconsumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * ConsumerConfiguration
 */
@Configuration
public class ConsumerConfiguration {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
}