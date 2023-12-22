package com.rommel.rabbitmqimplementation.stream;

import com.rommel.rabbitmqimplementation.model.RabbitMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class InputHandler {

    @Bean
    public Consumer<Message<RabbitMessage>> receiveMessage() {
        return message -> {
            listen(message.getPayload(), message.getHeaders());
        };
    }

    public void listen(final RabbitMessage payload, MessageHeaders headers) {
        System.out.println("Received : " + payload.toString() + " |------| With Headers --> " + headers);
        // Process request incoming....
    }
}
