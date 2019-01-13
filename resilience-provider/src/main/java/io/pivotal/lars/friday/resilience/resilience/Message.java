package io.pivotal.lars.friday.resilience.resilience;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Message
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private String text;
    
}