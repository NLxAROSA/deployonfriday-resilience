package io.pivotal.lars.friday.resilience.resilience;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * MessageController
 */
@RestController
public class MessageController {

    private static final int WAIT_TIME_MS = 1000;

    @GetMapping("/")
    public String okay() {
        return "I'm okay.";
    }

    @GetMapping("/slow")
    public String slow() throws InterruptedException {
        Thread.sleep(WAIT_TIME_MS);
        return "I'm okay, just slow";
    }

    @GetMapping("/error")
    public String error() {
        throw new InternalServerErrorException("I'm definitely not okay!");
    }

}