package com.talentbank.search.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SrchResponse {

    private long total_count;
    private String analyze;
    private List datas;

}
