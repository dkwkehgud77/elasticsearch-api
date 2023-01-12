package com.talentbank.search.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentbank.search.dto.xAPI.XapiMessage;
import com.talentbank.search.dto.xAPI.Actor;
import com.talentbank.search.dto.xAPI.Object;
import com.talentbank.search.dto.xAPI.Verb;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "experience-api-grow";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Async("messagingTaskExecutor")
    public CompletableFuture<String> sendMessage(String keyword) {
        System.out.println("[messagingTaskExecutor] :: " + Thread.currentThread().getName());

        XapiMessage xapiMessage = new XapiMessage();
        Actor actor = new Actor();
        actor.setName("2021028");
        actor.setMbox("dohyeong0715@hunet.co.kr");
        Actor.Agentaccount agentaccount = new Actor.Agentaccount();
        agentaccount.setSession("김도형");
        agentaccount.setHome_age("https://search-tb.hunet.co.kr");
        actor.setAgentaccount(agentaccount);

        Verb verb = new Verb();
        verb.setKo_kr("검색");
        verb.setEn_us("searched");

        Object object = new Object();
        object.setId("키워드");
        Object.Definition definition = new Object.Definition();
        Map description = new HashMap<>();
        description.put("keyword", "검색 키워드");
        description.put("keyword_detail", "결과 내 재검색");
        description.put("url", "검색 URL");
        Map extensions = new HashMap<>();
        extensions.put("keyword", "테스트");
        extensions.put("keyword_detail", "상세 테스트");
        extensions.put("url", "/api/expert/admin/fullsearch");
        definition.setDescription(description);
        definition.setExtensions(extensions);
        object.setDefinition(definition);

        xapiMessage.setTopic("experience-api-grow");
        xapiMessage.setActor(actor);
        xapiMessage.setVerb(verb);
        xapiMessage.setObject(object);

        ObjectMapper mapper = new ObjectMapper();
        String message = null;
        try {
            message = mapper.writeValueAsString(xapiMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println("============================================================");
        System.out.println(xapiMessage);
        System.out.println(message);
        System.out.println("============================================================");
//        kafkaTemplate.send(TOPIC, message);


        RestTemplate restTemplate = new RestTemplate();
        String JSONInput = ("{\n" +
                "    \"topic\": \"talentbank-search-log\",\n" +
                "    \"field\": [{\n" +
                "       \"search-keyword\" : \"dddddddddddsssss\"\n" +
                "    }]\n" +
                "} ");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity params = new HttpEntity(message, httpHeaders);
        String result = restTemplate.postForObject("https://kafka.hunet.co.kr/xAPI/grow", params, String.class);
        System.out.println(result);

//        System.out.println(result.getStatusCode());
//        System.out.println(result.getBody());

        return CompletableFuture.completedFuture("success");

    }

}
