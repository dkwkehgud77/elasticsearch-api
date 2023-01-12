package com.talentbank.search.dto.request;


import com.talentbank.search.dto.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpertAdminRequest extends SearchRequest {

    @ApiModelProperty(value = "검색 키워드", example = "기획")
    private String keyword;

    @ApiModelProperty(value = "결과 내 검색", example = " ")
    private String keyword_detail;

    @ApiModelProperty(value = "키워드 필터 expert, education, career, tag, memo", example = "memo")
    private String keyword_filter;

    @ApiModelProperty(value = "가입코드", example = " ")
    private String join_path_code;

    @ApiModelProperty(value = "인터뷰방법", example = " ")
    private String interview_code;

    @ApiModelProperty(value = "인증구분코드", example = " ")
    private String auth_status_code;

    @ApiModelProperty(value = "인증세부구분코드", example = " ")
    private String auth_status_detail_code;

    @ApiModelProperty(value = "제안받기여부 YN", example = " ")
    private String offer_receive_yn;

    @ApiModelProperty(value = "신분인증구분코드", example = " ")
    private String id_status_code;

    @ApiModelProperty(value = "학력증명서 학교인증 구분코드", example = " ")
    private String edu_status_code;

    @ApiModelProperty(value = "경력인증 구분코드", example = " ")
    private String exp_status_code;

    @ApiModelProperty(value = "은행상태코드", example = " ")
    private String bank_status_code;

    @ApiModelProperty(value = "포트폴리오코드", example = " ")
    private String portfolio_code;

    @ApiModelProperty(value = "전문분야", example = " ")
    private String job_field_code;

    @ApiModelProperty(value = "1시간당 금액 코드", example = " ")
    private String hour_price_code;

    @ApiModelProperty(value = "1일당 금액 코드", example = " ")
    private String day_price_code;

    @ApiModelProperty(value = "추천", example = " ")
    private String recommend_type;

    @ApiModelProperty(value = "회원 상태코드", example = " ")
    private String user_status_code;

    @ApiModelProperty(value = "회원등급코드", example = " ")
    private String user_grade_code;

    @ApiModelProperty(value = "모바일확인코드", example = " ")
    private String mobile_confirm_code;

    @ApiModelProperty(value = "최신 프로젝트 구분 코드", example = " ")
    private String recent_project_alarm_type_code;

    @ApiModelProperty(value = "뉴스알림 구분코드", example = " ")
    private String news_alarm_type_code;

    @ApiModelProperty(value = "서비스이용알림 구분코드", example = " ")
    private String service_use_alarm_type_code;

    @ApiModelProperty(value = "App Push 수신", example = " ")
    private String push_receive_code;

    @ApiModelProperty(value = "성별", example = " ")
    private String gender_code;

    @ApiModelProperty(value = "주소 city", example = " ")
    private String sch_juso_city;

    @ApiModelProperty(value = "주소 local", example = " ")
    private String sch_juso_local;

    @ApiModelProperty(value = "생년월일 시작일", example = " ")
    private String birth_start_date;

    @ApiModelProperty(value = "생년월일 종료일", example = " ")
    private String birth_end_date;

    @ApiModelProperty(value = "태그", example = " ")
    private String tag;

    @ApiModelProperty(value = "기간검색 T, A, L", example = "T")
    private String date_code;

    @ApiModelProperty(value = "시작일", example = "2015-03-01")
    private String start_date;

    @ApiModelProperty(value = "종료일", example = "2021-04-01")
    private String end_date;

    @ApiModelProperty(value = "테스트id여부", example = "N")
    private String test_id_yn;

    @ApiModelProperty(value = "페이지 번호", example = "0")
    private int page_index;

    @ApiModelProperty(value = "페이지 당 출력 개수", example = "20")
    private int page_size;

    @ApiModelProperty(value = "정렬 컬럼명", example = "_score")
    private String sort_column;

    @ApiModelProperty(value = "정렬 구분 desc, asc", example = "desc")
    private String sort_direction;
}
