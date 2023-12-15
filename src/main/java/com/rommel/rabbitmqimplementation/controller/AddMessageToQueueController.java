package com.rommel.rabbitmqimplementation.controller;

import com.rommel.rabbitmqimplementation.model.RabbitMessage;
import com.rommel.rabbitmqimplementation.stream.OutputHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AddMessageToQueueController {
    @Autowired
    private OutputHandler outputHandler;

    @GetMapping("/test")
    public String testEndpoint() {
        return "Im working fine!";
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<RabbitMessage> sendMessage(@RequestBody final RabbitMessage rabbitMessage, final HttpServletRequest request) {
        outputHandler.publishMessage(rabbitMessage);
        return new ResponseEntity<>(rabbitMessage, HttpStatus.OK);
    }

}
