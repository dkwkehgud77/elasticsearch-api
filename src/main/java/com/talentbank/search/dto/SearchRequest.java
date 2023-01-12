package com.talentbank.search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    private String name = "testId";
    private String mbox = "testEmail@hunet.co.kr";

    private String session = "testSession@0817cruv871ebdlorvdhcdm1";

    private String home_page = "https://search.talentbank.co.kr";

    private String keyword;

    private String keyword_detail;

    private String url;

}
