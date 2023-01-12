package com.talentbank.search.dto.request;

import com.talentbank.search.dto.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectProductRequest extends SearchRequest {

    @ApiModelProperty(position = 1, value = "검색어", example = "기업")
    private String keyword;

    @ApiModelProperty(position = 2, value = "결과 내 검색어", example = "인사")
    private String keyword_detail;

    @ApiModelProperty(position = 3, value = "전문 분야", example = "JOB_FD0402")
    private String job_field_code;

    @ApiModelProperty(position = 4, value = "상품 타입", example = "PRD_TB")
    private String product_type;

    @ApiModelProperty(position = 5, value = "페이지 시작번호", example = "0")
    private int page_index;
    @ApiModelProperty(position = 6, value = "페이지 사이즈", example = "10")
    private int page_size;

    @ApiModelProperty(position = 7, value = "정렬 컬럼", example = "view_count")
    private String sort_column;
    @ApiModelProperty(position = 8, value = "정렬 타입", example = "desc")
    private String sort_direction;
}
