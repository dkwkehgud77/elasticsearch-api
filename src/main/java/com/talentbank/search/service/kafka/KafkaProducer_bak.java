//package com.talentbank.search.service.kafka;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.talentbank.search.dto.xAPI.Actor;
//import com.talentbank.search.dto.xAPI.Object;
//import com.talentbank.search.dto.xAPI.Verb;
//import com.talentbank.search.dto.xAPI.XapiMessage;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//
//@Service
//@RequiredArgsConstructor
//public class KafkaProducer_bak {
//
//    private final KafkaTemplate<String, String> kafkaTemplate;
//
//    private static final String TOPIC = "experience-api-grow";
//
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @Async("messagingTaskExecutor")
//    public CompletableFuture<String> sendMessage(String keyword) {
//        System.out.println("[messagingTaskExecutor] :: " + Thread.currentThread().getName());
//
//        XapiMessage xapiMessage = new XapiMessage();
//        Actor actor = new Actor();
//        actor.setName("2021028");
//        actor.setMbox("dohyeong0715@hunet.co.kr");
//        Actor.Agentaccount agentaccount = new Actor.Agentaccount();
//        agentaccount.setName("김도형");
//        agentaccount.setHome_age("https://search-tb.hunet.co.kr");
//        actor.setAgentaccount(agentaccount);
//
//        Verb verb = new Verb();
//        verb.setId("https://xapi.hunet.co.kr/verbs/searched");
//        Verb.Display display = new Verb.Display();
//        display.setKo_kr("searched");
//        display.setEn_us("검색");
//        verb.setDisplay(display);
//
//        Object object = new Object();
//        object.setId("키워드");
//        Object.Definition definition = new Object.Definition();
//        Map description = new HashMap<>();
//        description.put("keyword", "검색 키워드");
//        description.put("keyword_detail", "결과 내 재검색");
//        description.put("url", "검색 URL");
//        Map extensions = new HashMap<>();
//        extensions.put("keyword", "테스트");
//        extensions.put("keyword_detail", "상세 테스트");
//        extensions.put("url", "https://search.talentbank.co.kr/api/expert/admin/fullsearch");
//        definition.setDescription(description);
//        definition.setExtensions(extensions);
//        object.setDefinition(definition);
//
//
//        xapiMessage.setActor(actor);
//        xapiMessage.setVerb(verb);
//        xapiMessage.setObject(object);
//
//        ObjectMapper mapper = new ObjectMapper();
//        String message = null;
//        try {
//            message = mapper.writeValueAsString(xapiMessage);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("============================================================");
//        System.out.println(searchMessage);
//        System.out.println(xapiMessage);
//        System.out.println("============================================================");
////        kafkaTemplate.send(TOPIC, message);
//
////        URI uri = UriComponentsBuilder
////                .fromUriString("https://kafka.hunet.co.kr")
////                .path("/xAPI/b2b")
////                .encode()
////                .build()
////                .toUri();
////        System.out.println(uri.toString());
//
////        RestTemplate restTemplate = new RestTemplate();
////        String JSONInput = ("{\n" +
////                "    \"topic\": \"talentbank-search-log\",\n" +
////                "    \"field\": [{\n" +
////                "       \"search-keyword\" : \"dddddddddddsssss\"\n" +
////                "    }]\n" +
////                "} ");
////        HttpHeaders httpHeaders = new HttpHeaders();
////        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
////        HttpEntity param = new HttpEntity(JSONInput, httpHeaders);
////        String result = restTemplate.postForObject("https://kafka.hunet.co.kr/api/kafka", param, String.class);
////        System.out.println(result);
//
////        System.out.println(result.getStatusCode());
////        System.out.println(result.getBody());
//
//        return CompletableFuture.completedFuture("success");
//
//    }
//
//}
