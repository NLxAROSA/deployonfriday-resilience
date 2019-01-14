package io.pivotal.lars.friday.resilience.resilience;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

/**
 * MessageController
 */
@RestController
public class MessageController {

    @GetMapping("/")
    public Mono<String> okay()   {
        return Mono.just("I'm fine.");
    }

    @GetMapping("/slow")
    public Mono<String> slow() throws InterruptedException {
        Thread.sleep(250);
        return Mono.just("I'm fine, just slow");
    }

    @GetMapping("/extremelyslow")
    public Mono<String> extremelySlow() throws InterruptedException {
        Thread.sleep(180000);
        return Mono.just("I'm not fine, but will respond, most likely after your timeout has expired");
    }

    @GetMapping("/error")
    public Mono<String> error()  {
        throw new InternalServerErrorException("I'm definitely not fine!");
    }
    
}