package com.talentbank.search.dto.xAPI;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Object {
    private String id;
    private Definition definition;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Definition {
        Map<String,String> description;

        Map<String,String> extensions;
    }
}
