package com.talentbank.search.dto.xAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Actor {
    private String name;
    private String mbox;

    private Agentaccount agentaccount;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Agentaccount {

        @JsonProperty("name")
        private String session;
        private String home_age;
    }

}
