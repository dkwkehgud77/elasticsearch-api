package com.talentbank.search.dto.request;


import com.talentbank.search.dto.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpertAdminPartRequest extends SearchRequest {

    @ApiModelProperty(value = "검색 키워드", example = "전략기획의 치열한 승부사")
    private String keyword;

    @ApiModelProperty(value = "결과 내 검색", example = "마케팅")
    private String keyword_detail;

    @ApiModelProperty(value = "키워드 필터 expert, education, career, tag", example = "expert,education")
    private String keyword_filter;

    @ApiModelProperty(value = "전문 분야", example = " ")
    private String job_field_code;

    @ApiModelProperty(value = "회원등급코드", example = " ")
    private String user_grade_code;

    @ApiModelProperty(value = "페이지 번호", example = "0")
    private int page_index;

    @ApiModelProperty(value = "페이지 당 출력 개수", example = "20")
    private int page_size;

    @ApiModelProperty(value = "정렬 컬럼명", example = "_score")
    private String sort_column;

    @ApiModelProperty(value = "정렬 구분 desc, asc", example = "desc")
    private String sort_direction;

}
