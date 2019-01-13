package io.pivotal.lars.friday.resilience.resilience;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * MessageController
 */
@RestController
public class MessageController {

    @GetMapping("/")
    public Message okay()   {
        return new Message("I'm fine.");
    }

    @GetMapping("/slow")
    public Message slow() throws InterruptedException {
        Thread.sleep(250);
        return new Message("I'm fine, just slow");
    }

    @GetMapping("/extremelyslow")
    public Message extremelySlow() throws InterruptedException {
        Thread.sleep(180000);
        return new Message("I'm not fine, but will respond, most likely after your timeout has expired");
    }

    @GetMapping("/error")
    public Message error()  {
        throw new InternalServerErrorException("I'm definitely not fine!");
    }
    
}