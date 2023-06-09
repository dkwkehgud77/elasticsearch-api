package com.talentbank.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableElasticsearchRepositories
@SpringBootApplication
public class SearchApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchApiApplication.class, args);
    }

}
