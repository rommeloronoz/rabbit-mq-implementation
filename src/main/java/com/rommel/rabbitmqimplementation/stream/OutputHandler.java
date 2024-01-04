package com.rommel.rabbitmqimplementation.stream;

import com.rommel.rabbitmqimplementation.model.RabbitMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class OutputHandler {
    private StreamBridge streamBridge;

    @Autowired
    public OutputHandler(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publishMessage(final RabbitMessage rabbitMessage) {
        Message<RabbitMessage> messageToSend = MessageBuilder.withPayload(rabbitMessage)
                .setHeader("x-routing-key", "spring.to.dashboard")
                .setHeader("x-persist-mode", "payload")
                .build();
        System.out.println("Sending message, with payload --> " + messageToSend.getPayload() + " and headers --> " + messageToSend.getHeaders());
        streamBridge.send("publishMessage-out-0", messageToSend);
    }
}
