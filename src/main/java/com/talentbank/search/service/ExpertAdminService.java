package com.talentbank.search.service;

import com.talentbank.search.dto.request.ExpertAdminRequest;
import com.talentbank.search.dto.request.ExpertFrontRequest;
import com.talentbank.search.dto.response.ErrorResponse;
import com.talentbank.search.dto.response.SrchResponse;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.AnalyzeResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpertAdminService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RestHighLevelClient esClient;

    public Object search(ExpertAdminRequest expertAdminRequest) {
        try {
            // 검색 Request
            logger.info("ExpertAdminRequest :: " + expertAdminRequest.toString());
            String keyword = Optional.ofNullable(expertAdminRequest.getKeyword()).orElse("").trim();
            String keyword_detail = Optional.ofNullable(expertAdminRequest.getKeyword_detail()).orElse("").trim();
            String keyword_filter = Optional.ofNullable(expertAdminRequest.getKeyword_filter()).orElse("").trim();
            String join_path_code = Optional.ofNullable(expertAdminRequest.getJoin_path_code()).orElse("").trim();
            String interview_code = Optional.ofNullable(expertAdminRequest.getInterview_code()).orElse("").trim();
            String auth_status_code = Optional.ofNullable(expertAdminRequest.getAuth_status_detail_code()).orElse("").trim();
            String auth_status_detail_code = Optional.ofNullable(expertAdminRequest.getAuth_status_code()).orElse("").trim();
            String offer_receive_yn = Optional.ofNullable(expertAdminRequest.getOffer_receive_yn()).orElse("").trim();
            String id_status_code = Optional.ofNullable(expertAdminRequest.getId_status_code()).orElse("").trim();
            String edu_status_code = Optional.ofNullable(expertAdminRequest.getEdu_status_code()).orElse("").trim();
            String exp_status_code = Optional.ofNullable(expertAdminRequest.getExp_status_code()).orElse("").trim();
            String bank_status_code = Optional.ofNullable(expertAdminRequest.getBank_status_code()).orElse("").trim();
            String portfolio_code = Optional.ofNullable(expertAdminRequest.getPortfolio_code()).orElse("").trim();
            String job_field_code = Optional.ofNullable(expertAdminRequest.getJob_field_code()).orElse("").trim();
            String hour_price_code = Optional.ofNullable(expertAdminRequest.getHour_price_code()).orElse("").trim();
            if(hour_price_code.contains("HP_PYM_TY001")){
                hour_price_code = "";
            }
            String day_price_code = Optional.ofNullable(expertAdminRequest.getHour_price_code()).orElse("").trim();
            if(hour_price_code.contains("HP_PYM_TY001")){
                hour_price_code = "";
            }
            String recommend_type = Optional.ofNullable(expertAdminRequest.getRecommend_type()).orElse("").trim();
            String user_status_code = Optional.ofNullable(expertAdminRequest.getUser_status_code()).orElse("").trim();
            String user_grade_code = Optional.ofNullable(expertAdminRequest.getUser_grade_code()).orElse("").trim();
            String mobile_confirm_code = Optional.ofNullable(expertAdminRequest.getMobile_confirm_code()).orElse("").trim();
            String recent_project_alarm_type_code = Optional.ofNullable(expertAdminRequest.getRecent_project_alarm_type_code()).orElse("").trim();
            String news_alarm_type_code = Optional.ofNullable(expertAdminRequest.getNews_alarm_type_code()).orElse("").trim();
            String service_use_alarm_type_code = Optional.ofNullable(expertAdminRequest.getService_use_alarm_type_code()).orElse("").trim();
            String push_receive_code = Optional.ofNullable(expertAdminRequest.getPush_receive_code()).orElse("").trim();
            String gender_code = Optional.ofNullable(expertAdminRequest.getGender_code()).orElse("").trim();
            String sch_juso_city = Optional.ofNullable(expertAdminRequest.getSch_juso_city()).orElse("").trim();
            String sch_juso_local = Optional.ofNullable(expertAdminRequest.getSch_juso_local()).orElse("").trim();
            String birth_start_date = Optional.ofNullable(expertAdminRequest.getBirth_start_date()).orElse("").trim();
            String birth_end_date = Optional.ofNullable(expertAdminRequest.getBirth_end_date()).orElse("").trim();
            String tag = Optional.ofNullable(expertAdminRequest.getTag()).orElse("").trim();
            String date_code = Optional.ofNullable(expertAdminRequest.getDate_code()).orElse("").trim();
            String start_date = Optional.ofNullable(expertAdminRequest.getStart_date()).orElse("").trim();
            String end_date = Optional.ofNullable(expertAdminRequest.getEnd_date()).orElse("").trim();
            String test_id_yn = Optional.ofNullable(expertAdminRequest.getTest_id_yn()).orElse("").trim();

            int page_index = Optional.ofNullable(expertAdminRequest.getPage_index()).orElse(10);
            int page_size = Optional.ofNullable(expertAdminRequest.getPage_size()).orElse(10);
            String sort_column = Optional.ofNullable(expertAdminRequest.getSort_column()).orElse("").trim();
            String sort_direction = Optional.ofNullable(expertAdminRequest.getSort_direction()).orElse("").trim();

            // 검색 Response
            SrchResponse response = new SrchResponse();
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            // keyword 검색 쿼리
            BoolQueryBuilder keywordQuery = QueryBuilders.boolQuery();
            BoolQueryBuilder keywordDetailQuery = QueryBuilders.boolQuery();

            if(StringUtils.hasText(keyword_filter)){
                String[] filters = keyword_filter.split(",");
                for (String filter : filters){
                    if("expert".equals(filter)){
                        keywordQuery.should(QueryBuilders.multiMatchQuery(keyword,"profile_title.korean","profile_title.ngram"))
                                .should(QueryBuilders.multiMatchQuery(keyword,"introduction.korean","introduction.ngram"))
                                .should(QueryBuilders.multiMatchQuery(keyword,"short_project_content.korean","short_project_content.ngram"));
                        if(StringUtils.hasText(keyword_detail)){
                            keywordDetailQuery.should(QueryBuilders.multiMatchQuery(keyword_detail,"profile_title.korean","profile_title.ngram").operator(Operator.AND))
                                    .should(QueryBuilders.multiMatchQuery(keyword_detail,"introduction.korean","introduction.ngram").operator(Operator.AND))
                                    .should(QueryBuilders.multiMatchQuery(keyword_detail,"short_project_content.korean","short_project_content.ngram").operator(Operator.AND));
                        }
                    }

                    if("education".equals(filter)){
                        keywordQuery.should(QueryBuilders.multiMatchQuery(keyword,"academy_list.korean","academy_list.ngram"))
                                .should(QueryBuilders.multiMatchQuery(keyword,"major_list.korean","major_list.ngram"))
                                .should(QueryBuilders.multiMatchQuery(keyword,"agency_list.korean","agency_list.ngram"))
                                .should(QueryBuilders.multiMatchQuery(keyword,"certification_list.korean","certification_list.ngram"));
                        if(StringUtils.hasText(keyword_detail)){
                            keywordDetailQuery.should(QueryBuilders.multiMatchQuery(keyword_detail,"academy_list.korean","academy_list.ngram").operator(Operator.AND))
                                    .should(QueryBuilders.multiMatchQuery(keyword_detail,"major_list.korean","major_list.ngram").operator(Operator.AND))
                                    .should(QueryBuilders.multiMatchQuery(keyword_detail,"agency_list.korean","agency_list.ngram").operator(Operator.AND))
                                    .should(QueryBuilders.multiMatchQuery(keyword_detail,"certification_list.korean","certification_list.ngram").operator(Operator.AND));
                        }
                    }

                    if("career".equals(filter)){
                        keywordQuery.should(QueryBuilders.multiMatchQuery(keyword,"office_name_list.korean","office_name_list.ngram"))
                                .should(QueryBuilders.multiMatchQuery(keyword,"department_name_list.korean","department_name_list.ngram"))
                                .should(QueryBuilders.multiMatchQuery(keyword,"career_detail_list.korean","career_detail_list.ngram"))
                                .should(QueryBuilders.multiMatchQuery(keyword,"main_work_list.korean","main_work_list.ngram"))
                                .should(QueryBuilders.multiMatchQuery(keyword,"project_name_list.korean","project_name_list.ngram"));
                        if(StringUtils.hasText(keyword_detail)){
                            keywordDetailQuery.should(QueryBuilders.multiMatchQuery(keyword_detail,"office_name_list.korean","office_name_list.ngram").operator(Operator.AND))
                                    .should(QueryBuilders.multiMatchQuery(keyword_detail,"department_name_list.korean","department_name_list.ngram").operator(Operator.AND))
                                    .should(QueryBuilders.multiMatchQuery(keyword_detail,"career_detail_list.korean","career_detail_list.ngram").operator(Operator.AND))
                                    .should(QueryBuilders.multiMatchQuery(keyword_detail,"main_work_list.korean","main_work_list.ngram").operator(Operator.AND))
                                    .should(QueryBuilders.multiMatchQuery(keyword_detail,"project_name_list.korean","project_name_list.ngram").operator(Operator.AND));
                        }
                    }

                    if("memo".equals(filter)){
                        keywordQuery.should(QueryBuilders.multiMatchQuery(keyword,"memo.korean","memo.ngram"));
                        if(StringUtils.hasText(keyword_detail)){
                            keywordDetailQuery.should(QueryBuilders.multiMatchQuery(keyword_detail,"memo.korean","memo.ngram").operator(Operator.AND));
                        }
                    }

                    if("tag".equals(filter)){
                        keywordQuery.should(QueryBuilders.multiMatchQuery(keyword,"str_keyword_list.korean","str_keyword_list.ngram"));
                        if(StringUtils.hasText(keyword_detail)){
                            keywordDetailQuery.should(QueryBuilders.multiMatchQuery(keyword_detail,"str_keyword_list.korean","str_keyword_list.ngram").operator(Operator.AND));
                        }
                    }

                }
            } else {
                if(!StringUtils.hasText(keyword)){
                    if (StringUtils.hasText(tag)) {
                        keywordQuery.should(QueryBuilders.multiMatchQuery(tag,"keyword_list.korean","keyword_list.ngram").operator(Operator.AND));
                    }
                } else {
                    keywordQuery.should(QueryBuilders.multiMatchQuery(keyword,"profile_title.korean","profile_title.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"introduction.korean","introduction.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"short_project_content.korean","short_project_content.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"academy_list.korean","academy_list.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"major_list.korean","major_list.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"agency_list.korean","agency_list.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"certification_list.korean","certification_list.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"office_name_list.korean","office_name_list.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"department_name_list.korean","department_name_list.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"career_detail_list.korean","career_detail_list.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"main_work_list.korean","main_work_list.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"project_name_list.korean","project_name_list.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"keyword_list.korean","keyword_list.ngram"));

                    if(StringUtils.hasText(keyword_detail)){
                        keywordDetailQuery.should(QueryBuilders.multiMatchQuery(keyword_detail,"profile_title.korean","profile_title.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"introduction.korean","introduction.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"short_project_content.korean","short_project_content.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"major_list.korean","major_list.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"major_list.korean","major_list.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"agency_list.korean","agency_list.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"certification_list.korean","certification_list.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"office_name_list.korean","office_name_list.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"department_name_list.korean","department_name_list.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"career_detail_list.korean","career_detail_list.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"main_work_list.korean","main_work_list.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"project_name_list.korean","project_name_list.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"keyword_list.korean","keyword_list.ngram").operator(Operator.AND));
                    }
                }
            }

            QueryBuilder MustQuery = QueryBuilders.boolQuery()
                    .must(keywordQuery)
                    .must(keywordDetailQuery);

            // filter 검색 쿼리
            BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();

            if(StringUtils.hasText(job_field_code)){
                if(job_field_code.indexOf(",") == 8 || job_field_code.length() == 8){
                    filterQuery.must(QueryBuilders.matchQuery("user_field_list1", job_field_code));
                }
                if(job_field_code.indexOf(",") == 10 || job_field_code.length() == 10){
                    filterQuery.must(QueryBuilders.matchQuery("user_field_list", job_field_code));
                }
            }

            if(StringUtils.hasText(tag))
                filterQuery.must(QueryBuilders.matchQuery("keyword_list",tag));

            if(StringUtils.hasText(hour_price_code))
                filterQuery.must(QueryBuilders.termQuery("hour_price_code",hour_price_code));

            if(StringUtils.hasText(day_price_code))
                filterQuery.must(QueryBuilders.termQuery("day_price_code",day_price_code));

            if(StringUtils.hasText(join_path_code))
                filterQuery.must(QueryBuilders.matchQuery("join_path_code",join_path_code));

            if(StringUtils.hasText(interview_code))
                filterQuery.must(QueryBuilders.matchQuery("interview_code",interview_code));

            if(StringUtils.hasText(auth_status_code))
                filterQuery.must(QueryBuilders.matchQuery("auth_status_code",auth_status_code));

            if(StringUtils.hasText(auth_status_detail_code))
                filterQuery.must(QueryBuilders.matchQuery("auth_status_detail_code",auth_status_detail_code));

            if(StringUtils.hasText(offer_receive_yn))
                filterQuery.must(QueryBuilders.matchQuery("offer_receive_yn", offer_receive_yn));

            if(StringUtils.hasText(id_status_code))
                filterQuery.must(QueryBuilders.matchQuery("id_status_code", id_status_code));

            if(StringUtils.hasText(edu_status_code))
                filterQuery.must(QueryBuilders.matchQuery("edu_status_code",edu_status_code));

            if(StringUtils.hasText(exp_status_code))
                filterQuery.must(QueryBuilders.matchQuery("exp_status_code",exp_status_code));

            if(StringUtils.hasText(bank_status_code))
                filterQuery.must(QueryBuilders.matchQuery("bank_status_code",bank_status_code));

            if(StringUtils.hasText(portfolio_code))
                filterQuery.must(QueryBuilders.matchQuery("portfolio_code",portfolio_code));

            if(StringUtils.hasText(user_status_code))
                filterQuery.must(QueryBuilders.matchQuery("user_status_code",user_status_code));

            if(StringUtils.hasText(user_grade_code))
                filterQuery.must(QueryBuilders.matchQuery("user_grade_code",user_grade_code));

            if(StringUtils.hasText(mobile_confirm_code))
                filterQuery.must(QueryBuilders.matchQuery("mobile_confirm_code",mobile_confirm_code));

            if(StringUtils.hasText(recent_project_alarm_type_code))
                filterQuery.must(QueryBuilders.matchQuery("recent_project_alarm_type_code",recent_project_alarm_type_code));

            if(StringUtils.hasText(news_alarm_type_code))
                filterQuery.must(QueryBuilders.matchQuery("news_alarm_type_code",news_alarm_type_code));

            if(StringUtils.hasText(service_use_alarm_type_code))
                filterQuery.must(QueryBuilders.matchQuery("service_use_alarm_type_code",service_use_alarm_type_code));

            if(StringUtils.hasText(push_receive_code))
                filterQuery.must(QueryBuilders.matchQuery("push_receive_code",push_receive_code));

            if(StringUtils.hasText(sch_juso_city))
                filterQuery.must(QueryBuilders.matchQuery("sch_juso_city",sch_juso_city));

            if(StringUtils.hasText(sch_juso_local))
                filterQuery.must(QueryBuilders.matchQuery("sch_juso_local",sch_juso_local));

            if(StringUtils.hasText(birth_start_date))
                filterQuery.must(QueryBuilders.rangeQuery("birth_ymd").gte(birth_start_date).lte(birth_end_date));

            if(StringUtils.hasText(date_code)){
                if("T".equals(date_code)){
                    filterQuery.must(QueryBuilders.rangeQuery("regist_date").gte(start_date).lte(end_date));
                } else if ("A".equals(date_code)) {
                    filterQuery.must(QueryBuilders.rangeQuery("admin_confirm_date").gte(start_date).lte(end_date));
                } else if ("L".equals(date_code)) {
                    filterQuery.must(QueryBuilders.rangeQuery("last_edit_date").gte(start_date).lte(end_date));
                }
            }

            if(StringUtils.hasText(id_status_code))
                filterQuery.must(QueryBuilders.matchQuery("id_status_code", id_status_code));

            if(StringUtils.hasText(edu_status_code))
                filterQuery.must(QueryBuilders.matchQuery("edu_status_code", edu_status_code));

            if(StringUtils.hasText(exp_status_code))
                filterQuery.must(QueryBuilders.matchQuery("exp_status_code", exp_status_code));

            if("recommended".equals(recommend_type))
                filterQuery.mustNot(QueryBuilders.termQuery("recommended_user_seq", "N"));

            if("recommending".equals(recommend_type))
                filterQuery.mustNot(QueryBuilders.termQuery("recommending_user_seq", "N"));

            if(StringUtils.hasText(test_id_yn))
                filterQuery.must(QueryBuilders.termQuery("test_id_yn", test_id_yn));

            // 최종 검색 쿼리
            QueryBuilder esQuery = QueryBuilders.boolQuery()
                    .filter(filterQuery)
                    .must(MustQuery);

            sourceBuilder.query(esQuery);
            System.out.println(esQuery.toString());

            // 검색 필드
            String[] includeFileds = new String[]{
                    "user_seq",
                    "join_path_code",
                    "regist_date",
                    "auth_status_detail_name",
                    "admin_confirm_date",
                    "admin_dormancy_confirm_date",
                    "offer_receive_yn",
                    "pofol_type_w",
                    "pofol_type_d",
                    "pofol_type_c",
                    "user_field_list",
                    "keyword_list",
                    "service_type_code",
                    "user_id",
                    "user_name",
                    "email",
                    "project_wish_price_hours",
                    "project_wish_price_day",
                    "mobile_no",
                    "user_status_code",
                    "user_grade_code",
                    "user_grade_name",
                    "point",
                    "recent_project_alarm_type_code",
                    "news_alarm_type_code",
                    "service_use_alarm_type_code",
                    "id_status_name",   // 20210503 추가
                    "edu_status_name",
                    "exp_status_name",
                    "join_path_name",
                    "auth_dormancy_status_name",
                    "auth_status_name",
                    "auth_status_code",
                    "auth_status_detail_code",
                    "user_dormancy_grade_name",
                    "user_status_name",
                    "exclude_test_id_yn",
                    "_score"
            };
            String[] excludeFileds = new String[]{};
            sourceBuilder.fetchSource(includeFileds, excludeFileds);
            sourceBuilder.trackScores(true);

            // 페이징
            sourceBuilder.from(page_index);
            sourceBuilder.size(page_size);

            // 정렬
            SortOrder sortOrder = SortOrder.DESC;
            if ("asc".equals(sort_direction)) {
                sortOrder = SortOrder.ASC;
            }
            SortBuilder sortColumn1 = new FieldSortBuilder(sort_column).order(sortOrder);
            SortBuilder sortColumn2 = new FieldSortBuilder("_score").order(SortOrder.DESC);
            sourceBuilder.sort(sortColumn1);
            sourceBuilder.sort(sortColumn2);

            // 타임아웃
            sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

            // Elasticsearch 인덱스 검색
            String esIndex = "search-engine-talentbank-expert-admin";
            SearchRequest searchRequest = new SearchRequest(esIndex);
            searchRequest.source(sourceBuilder);
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);

            long totalCnt = searchResponse.getHits().getTotalHits().value;
            SearchHit[] hits = searchResponse.getHits().getHits();

            List dataList = new ArrayList<>();
            for(SearchHit hit : hits){
                Map hitMap = hit.getSourceAsMap();
                hitMap.put("_score", hit.getScore());
                dataList.add(hitMap);
            }

            // keyword 형태소 분석
            AnalyzeRequest analyzeRequest = AnalyzeRequest.withIndexAnalyzer(esIndex,"korean_analyzer", keyword);
            AnalyzeResponse analyzeResponse = esClient.indices().analyze(analyzeRequest, RequestOptions.DEFAULT);
            List<AnalyzeResponse.AnalyzeToken> tokens = analyzeResponse.getTokens();
            String analyze = tokens.stream().map(v->v.getTerm()).collect(Collectors.joining(","));

            // 검색 Response
            response.setTotal_count(totalCnt);
            response.setAnalyze(analyze);
            response.setDatas(dataList);

            logger.info("response :: " + response);
            return response;
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();

            ErrorResponse response = new ErrorResponse();
            response.setStatus(500);
            response.setError("Internal Server Error");

            String message = e.getMessage().toString();
            response.setMessage(message);

            response.setPath("/api/expert/admin/fullsearch");
            LocalDateTime timestamp = LocalDateTime.now();
            response.setTimestamp(timestamp);

            return response;
        }
    }
}
