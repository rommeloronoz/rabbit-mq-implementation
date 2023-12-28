package com.rommel.rabbitmqimplementation.stream;

import com.rommel.rabbitmqimplementation.model.RabbitMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class OutputHandlerTest {

    @Autowired
    private StreamBridge streamBridge;
    @Autowired
    private OutputDestination outputDestination;


    @Test
    public void checkThatSendedDataItsBeingReceivedInTheOutputChannel() {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(
                TestChannelBinderConfiguration.getCompleteConfiguration(
                        EmptyConfiguration.class))
                .web(WebApplicationType.NONE)
                .run("--spring.jmx.enabled=false")) {
            StreamBridge streamBridge = context.getBean(StreamBridge.class);
            OutputHandler outputHandler = new OutputHandler(streamBridge);
            final RabbitMessage rabbitMessage = new RabbitMessage("MyExchange", "This is a test message");
            outputHandler.publishMessage(rabbitMessage);
            OutputDestination output = context.getBean(OutputDestination.class);
            Message<byte[]> result = output.receive(100, "rabbit-mq-implementation.exchange");
            assertThat(result).isNotNull();
            assertThat(new String(result.getPayload()).contains(rabbitMessage.getMessage()));
            System.out.println("Sended: " + new String(result.getPayload()) + "   Expected: " + rabbitMessage.toString());
        }
    }

    @Test
    public void checkThatMultipleSendedMessagesAreBeingReceivedInTheOutputChannel() {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(
                TestChannelBinderConfiguration.getCompleteConfiguration(
                        EmptyConfiguration.class))
                .web(WebApplicationType.NONE)
                .run("--spring.jmx.enabled=false")) {
            StreamBridge streamBridge = context.getBean(StreamBridge.class);
            OutputHandler outputHandler = new OutputHandler(streamBridge);
            final RabbitMessage rabbitMessage1 = new RabbitMessage("MyExchange", "This is the FIRST test message");
            final RabbitMessage rabbitMessage2 = new RabbitMessage("MyExchange", "This is the SECOND test message");
            outputHandler.publishMessage(rabbitMessage1);
            outputHandler.publishMessage(rabbitMessage2);
            OutputDestination output = context.getBean(OutputDestination.class);

            Message<byte[]> result = output.receive(0, "rabbit-mq-implementation.exchange");
            assertThat(result).isNotNull();
            assertThat(new String(result.getPayload()).contains(rabbitMessage1.getMessage()));
            System.out.println("Sended: " + new String(result.getPayload()) + "   Expected: " + rabbitMessage1.toString());

            Message<byte[]> result2 = output.receive(0, "rabbit-mq-implementation.exchange");
            assertThat(result2).isNotNull();
            assertThat(new String(result2.getPayload()).contains(rabbitMessage2.getMessage()));
            System.out.println("Sended: " + new String(result2.getPayload()) + "   Expected: " + rabbitMessage2.toString());

            Message<byte[]> result3 = output.receive(0, "rabbit-mq-implementation.exchange");
            assertThat(result3).isNull();
        }
    }

    @EnableAutoConfiguration
    public static class EmptyConfiguration {
    }
}