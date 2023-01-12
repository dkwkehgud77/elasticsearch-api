package com.talentbank.search.dto.xAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Verb {

    @JsonProperty("en-us")
    private String en_us;

    @JsonProperty("ko-kr")
    private String ko_kr;

}
