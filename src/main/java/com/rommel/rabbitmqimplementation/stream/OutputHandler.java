package com.rommel.rabbitmqimplementation.stream;

import com.rommel.rabbitmqimplementation.model.RabbitMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class OutputHandler {
    private StreamBridge streamBridge;

    @Autowired
    public OutputHandler(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publishMessage(final RabbitMessage rabbitMessage) {
        System.out.println("Sending message : " + rabbitMessage.toString());
        streamBridge.send("publishMessage-out-0", rabbitMessage);
        //streamBridge.send("test-out", rabbitMessage);
    }
}
