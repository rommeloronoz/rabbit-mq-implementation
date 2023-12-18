package com.rommel.rabbitmqimplementation.stream;

import com.rommel.rabbitmqimplementation.model.RabbitMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.function.context.config.ContextFunctionCatalogAutoConfiguration;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;

import java.util.Arrays;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class OutputHandlerTest {

    //@InjectMocks
    //@Autowired
    //private OutputHandler outputHandler;
    //@Autowired
    @Autowired
    private StreamBridge streamBridge;
    @Autowired
    private OutputDestination outputDestination;

    @Test
    void bridgeActivationWhenFunctionDefinitionIsPresent() {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(TestChannelBinderConfiguration
                .getCompleteConfiguration(SimpleConfiguration.class))
                .web(WebApplicationType.NONE).run(
                        "--spring.cloud.function.definition=echo;uppercase",
                        "--spring.jmx.enabled=false")) {

            StreamBridge bridge = context.getBean(StreamBridge.class);
            bridge.send("echo-in-0", "hello foo");

            OutputDestination outputDestination = context.getBean(OutputDestination.class);
            assertThat(new String(outputDestination.receive(100, "echo-out-0").getPayload())).isEqualTo("hello foo");
        }
    }

    @Test
    public void sampleTest() {
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
            //assertThat(new String(result.getPayload())).isEqualTo(rabbitMessage.toString());
            assertThat(new String(result.getPayload()).contains(rabbitMessage.getMessage()));
            System.out.println("Sended: " + new String(result.getPayload()) + "   Expected: " + rabbitMessage.toString());
        }
    }
    @Test
    public void checkPayloadOfSendedMessage4_usingTestOutAndTestDestination() {
        final RabbitMessage rabbitMessage = new RabbitMessage("MyExchange", "This is a test message");
        OutputHandler outputHandler = new OutputHandler(streamBridge);
        outputHandler.publishMessage(rabbitMessage);
        //Message<byte[]> result = outputDestination.receive(100, "receiveMessage-in-0");
        Message<byte[]> result = outputDestination.receive(100, "rabbit-mq-implementation.exchange");
        System.out.println("PAYLOAD ---> " + new String(result.getPayload()));
        //assertThat(result).isNotNull();
        //assertThat(new String(result.getPayload())).isEqualTo("Hello");
    }
    @Test
    public void checkPayloadOfSendedMessage3_usingTestOutAndTestDestination() {
        final RabbitMessage rabbitMessage = new RabbitMessage("MyExchange", "This is a test message");
        OutputHandler outputHandler = new OutputHandler(streamBridge);
        outputHandler.publishMessage(rabbitMessage);
        //Message<byte[]> result = outputDestination.receive(100, "receiveMessage-in-0");
        Message<byte[]> result = outputDestination.receive(100, "test-destination");
        System.out.println("PAYLOAD ---> " + new String(result.getPayload()));
        //assertThat(result).isNotNull();
        //assertThat(new String(result.getPayload())).isEqualTo("Hello");
    }
    @Test
    public void checkPayloadOfSendedMessage2() {
        final RabbitMessage rabbitMessage = new RabbitMessage("MyExchange", "This is a test message");
        OutputHandler outputHandler = new OutputHandler(streamBridge);
        outputHandler.publishMessage(rabbitMessage);
        //Message<byte[]> result = outputDestination.receive(100, "receiveMessage-in-0");
        Message<byte[]> result = outputDestination.receive(100, "receiveMessage-in-0");
        System.out.println("PAYLOAD ---> " + new String(result.getPayload()));
        //assertThat(result).isNotNull();
        //assertThat(new String(result.getPayload())).isEqualTo("Hello");
    }
    @Test
    public void checkPayloadOfSendedMessage() {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(TestChannelBinderConfiguration
                .getCompleteConfiguration(SimpleConfiguration.class))
                .web(WebApplicationType.NONE).run(
                        "--spring.cloud.function.definition=publishMessage;uppercase",
                        "--spring.jmx.enabled=false")) {

            StreamBridge bridge = context.getBean(StreamBridge.class);
            OutputHandler outputHandler = new OutputHandler(bridge);
            //bridge.send("echo-in-0", "hello foo");
            final RabbitMessage rabbitMessage = new RabbitMessage("MyExchange", "This is a test message");
            outputHandler.publishMessage(rabbitMessage);

            OutputDestination outputDestination = context.getBean(OutputDestination.class);
            //System.out.println("INTERCEPTADO : " + Arrays.toString(outputDestination.receive(100, "publishMessage-out-0").getPayload()));
            System.out.println(" INTERCEPTADO " + new String(outputDestination.receive(100, "publishMessage-out-0").getPayload()));
            //assertThat(new String(outputDestination.receive(100, "echo-out-0").getPayload().toString())).isEqualTo(rabbitMessage.toString());
        }



        //final RabbitMessage rabbitMessage = new RabbitMessage("MyExchange", "This is a test message");
        //outputHandler.publishMessage(rabbitMessage);
        //Mockito.verify(streamBridge.send(Mockito.eq("publishMessage-out-0"), Mockito.eq(rabbitMessage)));
    }

    @Test
    void testPublishMessage() {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(TestChannelBinderConfiguration
                .getCompleteConfiguration(SimpleConfiguration.class))
                .web(WebApplicationType.NONE).run(
                        "--spring.cloud.function.definition=publishMessage;uppercase",
                        "--spring.jmx.enabled=false")) {
            // Arrange
            //OutputHandler outputHandler = new OutputHandler(streamBridge);
            final RabbitMessage rabbitMessage = new RabbitMessage("MyExchange", "This is a test message");

            // Act
            //outputHandler.publishMessage(rabbitMessage);
            streamBridge.send("publishMessage-out-0", "hola");

            // Assert
            // Verify that the message is sent to the correct channel
            assertThat(new String(context.getBean(OutputDestination.class)
                    .receive(100, "publishMessage-out-0").getPayload()))
                    .isEqualTo("hola");
        }
    }
    @EnableAutoConfiguration
    public static class SimpleConfiguration {

        @Bean
        public Function<String, String> echo() {
            return v -> v;
        }

        @Bean
        public Function<String, String> uppercase() {
            return String::toUpperCase;
        }
    }

    @EnableAutoConfiguration
    public static class EmptyConfiguration {
    }

/*    @Bean
    @GlobalChannelInterceptor(patterns = "*")
    public ChannelInterceptor customInterceptor() {
        return new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
            }
        };
    }*/


}