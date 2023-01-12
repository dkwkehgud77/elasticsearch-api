package com.talentbank.search.service;

import com.talentbank.search.dto.request.ExpertFrontRequest;
import com.talentbank.search.dto.request.OnlineConsultRequest;
import com.talentbank.search.dto.request.ProjectProductRequest;
import com.talentbank.search.dto.request.ProjectRequest;
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
public class ExpertFrontService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RestHighLevelClient esClient;

    public Object search(ExpertFrontRequest expertFrontRequest) {

        try{
            logger.info("ExpertFrontService :: " + expertFrontRequest.toString());
            // 검색 Request
            Optional<ExpertFrontRequest> req = Optional.ofNullable(expertFrontRequest);
            String keyword = req.map(ExpertFrontRequest::getKeyword).orElse("").trim();
            String keyword_detail = req.map(ExpertFrontRequest::getKeyword_detail).orElse("").trim();
            String keyword_filter = req.map(ExpertFrontRequest::getKeyword_filter).orElse("").trim();
            String job_field_code = req.map(ExpertFrontRequest::getJob_field_code).orElse("").trim();
            String tag = req.map(ExpertFrontRequest::getTag).orElse("").trim();

            String hour_price_code = req.map(ExpertFrontRequest::getHour_price_code).orElse("").trim();
            if(hour_price_code.contains("HP_PYM_TY001")){
                hour_price_code = "";
            }
            String day_price_code = req.map(ExpertFrontRequest::getDay_price_code).orElse("").trim();
            if(hour_price_code.contains("HP_PYM_TY001")){
                hour_price_code = "";
            }

            int page_index = req.map(ExpertFrontRequest::getPage_index).orElse(0);
            int page_size = req.map(ExpertFrontRequest::getPage_size).orElse(10);
            String sort_column = req.map(ExpertFrontRequest::getSort_column).orElse("").trim();
            String sort_direction = req.map(ExpertFrontRequest::getSort_direction).orElse("").trim();

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
                        keywordQuery.should(QueryBuilders.multiMatchQuery(keyword,"str_academy_list.korean","str_academy_list.ngram"))
                                .should(QueryBuilders.multiMatchQuery(keyword,"str_major_list.korean","str_major_list.ngram"))
                                .should(QueryBuilders.multiMatchQuery(keyword,"str_agency_list.korean","str_agency_list.ngram"))
                                .should(QueryBuilders.multiMatchQuery(keyword,"str_certification_list.korean","str_certification_list.ngram"));
                        if(StringUtils.hasText(keyword_detail)){
                            keywordDetailQuery.should(QueryBuilders.multiMatchQuery(keyword_detail,"str_academy_list.korean","str_academy_list.ngram").operator(Operator.AND))
                                    .should(QueryBuilders.multiMatchQuery(keyword_detail,"str_major_list.korean","str_major_list.ngram").operator(Operator.AND))
                                    .should(QueryBuilders.multiMatchQuery(keyword_detail,"str_agency_list.korean","str_agency_list.ngram").operator(Operator.AND))
                                    .should(QueryBuilders.multiMatchQuery(keyword_detail,"str_certification_list.korean","str_certification_list.ngram").operator(Operator.AND));
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
                            .should(QueryBuilders.multiMatchQuery(keyword,"str_academy_list.korean","str_academy_list.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"str_major_list.korean","str_major_list.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"str_agency_list.korean","str_agency_list.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"str_certification_list.korean","str_certification_list.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"office_name_list.korean","office_name_list.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"department_name_list.korean","department_name_list.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"career_detail_list.korean","career_detail_list.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"main_work_list.korean","main_work_list.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"project_name_list.korean","project_name_list.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"str_keyword_list.korean","str_keyword_list.ngram"));

                    if(StringUtils.hasText(keyword_detail)){
                        keywordDetailQuery.should(QueryBuilders.multiMatchQuery(keyword_detail,"profile_title.korean","profile_title.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"introduction.korean","introduction.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"short_project_content.korean","short_project_content.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"str_academy_list.korean","str_academy_list.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"str_major_list.korean","str_major_list.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"str_agency_list.korean","str_agency_list.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"str_certification_list.korean","str_certification_list.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"office_name_list.korean","office_name_list.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"department_name_list.korean","department_name_list.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"career_detail_list.korean","career_detail_list.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"main_work_list.korean","main_work_list.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"project_name_list.korean","project_name_list.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"str_keyword_list.korean","str_keyword_list.ngram").operator(Operator.AND));
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
                filterQuery.must(QueryBuilders.matchQuery("str_keyword_list",tag));

            if(StringUtils.hasText(hour_price_code))
                filterQuery.must(QueryBuilders.termQuery("hour_price_code",hour_price_code));

            if(StringUtils.hasText(day_price_code))
                filterQuery.must(QueryBuilders.termQuery("day_price_code",day_price_code));

            if(StringUtils.hasText(tag))
                filterQuery.must(QueryBuilders.matchQuery("str_keyword_list",tag));

            filterQuery.must(QueryBuilders.termQuery("service_type_code","SVC_TY003"))
                    .must(QueryBuilders.termQuery("user_status_code","US_ST001"))
                    .must(QueryBuilders.termQuery("confirm_type_code","CF009"))
                    .must(QueryBuilders.termQuery("offer_receive_yn","Y"))
                    .mustNot(QueryBuilders.termQuery("test_id","Y"))
                    .mustNot(QueryBuilders.termQuery("grade_code","US_GR008"));

            // 최종 검색 쿼리
            QueryBuilder esQuery = QueryBuilders.boolQuery()
                    .filter(filterQuery)
                    .must(MustQuery);

            sourceBuilder.query(esQuery);
            System.out.println(esQuery.toString());

            // 검색 필드
            String[] includeFileds = new String[]{
                    "user_seq",
                    "user_id",
                    "project_wish_price_hours",
                    "project_wish_price_day",
                    "image_file_seq",
                    "user_field_list",
                    "profile_title",
                    "introduction",
                    "short_project_content",
                    "str_academy_list",
                    "str_major_list",
                    "str_agency_list",
                    "str_certification_list",
                    "office_name_list",
                    "department_name_list",
                    "career_detail_list",
                    "main_work_list",
                    "project_name_list",
                    "str_keyword_list",
                    "expert_rating",
                    "rating_cnt",
                    "grade_order",
                    "active_expert_yn",
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
            if("rating_cnt".equals(sort_column)){
                sortColumn2 = new FieldSortBuilder("expert_rating").order(SortOrder.DESC);
            }
            sourceBuilder.sort(sortColumn1);
            sourceBuilder.sort(sortColumn2);

            // 타임아웃
            sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

            // Elasticsearch 인덱스 검색
            String esIndex = "search-engine-talentbank-expert-front";
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

            response.setPath("/api/expert/front/fullsearch");
            LocalDateTime timestamp = LocalDateTime.now();
            response.setTimestamp(timestamp);

            return response;
        }
    }
}
