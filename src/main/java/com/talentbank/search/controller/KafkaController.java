package com.talentbank.search.controller;

import com.talentbank.search.service.kafka.KafkaProducer;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Api(tags = {"Kafka xAPI Controller"})
@RestController
@RequestMapping("/kafka")
@RequiredArgsConstructor
public class KafkaController {

    private final KafkaProducer kafkaProducer;

    @PostMapping("/produce")
    public String sendMessage(@RequestParam("message") String message) throws ExecutionException, InterruptedException {
        System.out.println("[KafkaController] :: " + Thread.currentThread().getName());
        CompletableFuture<String> asyncFuture = kafkaProducer.sendMessage(message);

        return asyncFuture.get().toString();
    }


}
