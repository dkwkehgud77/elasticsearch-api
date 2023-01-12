package com.talentbank.search.dto.request;

import com.talentbank.search.dto.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest extends SearchRequest {

    @ApiModelProperty(value = "검색어", example = "기획")
    private String keyword;

    @ApiModelProperty(value = "결과 내 검색", example = " ")
    private String keyword_detail;

    @ApiModelProperty(value = "키워드 필터", example = "project")
    private String keyword_filter;

    @ApiModelProperty(value = "태그", example = " ")
    private String tag;

    @ApiModelProperty(value = "페이지 시작번호", example = "0")
    private int page_index;

    @ApiModelProperty(value = "페이지 사이즈", example = "10")
    private int page_size;

}
