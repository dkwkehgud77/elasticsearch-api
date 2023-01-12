package com.talentbank.search.dto.request;

import com.talentbank.search.dto.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuccessCaseRequest extends SearchRequest {

    @ApiModelProperty(position = 1, value = "검색어", example = "기업")
    private String keyword;

    @ApiModelProperty(position = 2, value = "결과 내 검색어", example = "인사")
    private String keyword_detail;

    @ApiModelProperty(position = 3, value = "성공사례 타입 코드", example = "CS_TY001")
    private String case_type_code;

    @ApiModelProperty(position = 5, value = "페이지 시작번호", example = "0")
    private int page_index;
    @ApiModelProperty(position = 6, value = "페이지 사이즈", example = "20")
    private int page_size;

    @ApiModelProperty(position = 7, value = "정렬 컬럼", example = "order_no")
    private String sort_column;
    @ApiModelProperty(position = 8, value = "정렬 타입", example = "desc")
    private String sort_direction;
}
