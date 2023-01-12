package com.talentbank.search.dto.xAPI;

import com.talentbank.search.dto.xAPI.Actor;
import com.talentbank.search.dto.xAPI.Verb;
import com.talentbank.search.dto.xAPI.Object;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class XapiMessage {

    private String topic;

    private Actor actor;
    private Verb verb;
    private Object object;

}


