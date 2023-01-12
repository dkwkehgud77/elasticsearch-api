package com.talentbank.search.service;

import com.talentbank.search.dto.request.ProjectProductRequest;
import com.talentbank.search.dto.request.ProjectRequest;
import com.talentbank.search.dto.request.SuccessCaseRequest;
import com.talentbank.search.dto.response.ErrorResponse;
import com.talentbank.search.dto.response.SrchResponse;
import com.talentbank.search.service.kafka.KafkaProducer;
import com.talentbank.search.service.kafka.XapiProducer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.AnalyzeResponse;
import org.elasticsearch.common.Nullable;
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

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RestHighLevelClient esClient;

    private final KafkaProducer kafkaProducer;

    private final XapiProducer xapiProducer;

    private static final String url = "/api/project/front/fullsearch";

    public Object search( ProjectRequest projectRequest) {

        try {
            logger.info("ProjectRequest :: " + projectRequest.toString());
            // 검색 log
            String search_log = projectRequest.toString();
            projectRequest.setUrl(url);
            kafkaProducer.sendMessage(search_log);
            xapiProducer.setSearchRequest(projectRequest);

            // 검색 Request
            Optional<ProjectRequest> req = Optional.ofNullable(projectRequest);
            String keyword = req.map(ProjectRequest::getKeyword).orElse("").trim();
            String keyword_detail = req.map(ProjectRequest::getKeyword_detail).orElse("").trim();
            String keyword_filter = req.map(ProjectRequest::getKeyword_filter).orElse("").trim();
            String tag = req.map(ProjectRequest::getTag).orElse("").trim();
            int page_index = req.map(ProjectRequest::getPage_index).orElse(0);
            int page_size = req.map(ProjectRequest::getPage_size).orElse(10);

            // 검색 Response
            SrchResponse response = new SrchResponse();
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            // keyword 검색 쿼리
            BoolQueryBuilder keywordQuery = QueryBuilders.boolQuery();
            BoolQueryBuilder keywordDetailQuery = QueryBuilders.boolQuery();
            if(StringUtils.hasText(keyword_filter)){
                String[] filters = keyword_filter.split(",");
                for (String filter : filters){
                    if("project".equals(filter)){
                        keywordQuery.should(QueryBuilders.multiMatchQuery(keyword,"project_name.korean","project_name.ngram"))
                                .should(QueryBuilders.multiMatchQuery(keyword,"comment.korean","comment.ngram"))
                                .should(QueryBuilders.multiMatchQuery(keyword,"project_request_background.korean","project_request_background.ngram"))
                                .should(QueryBuilders.multiMatchQuery(keyword,"expert_ability_experience.korean","expert_ability_experience.ngram"));
                        if(StringUtils.hasText(keyword_detail)){
                            keywordDetailQuery.should(QueryBuilders.multiMatchQuery(keyword_detail,"project_name.korean","project_name.ngram").operator(Operator.AND))
                                    .should(QueryBuilders.multiMatchQuery(keyword_detail,"comment.korean","comment.ngram").operator(Operator.AND))
                                    .should(QueryBuilders.multiMatchQuery(keyword_detail,"project_request_background.korean","project_request_background.ngram").operator(Operator.AND))
                                    .should(QueryBuilders.multiMatchQuery(keyword_detail,"expert_ability_experience.korean","expert_ability_experience.ngram").operator(Operator.AND));
                        }
                    }

                    if("company".equals(filter)){
                        keywordQuery.should(QueryBuilders.multiMatchQuery(keyword,"company_name.korean","company_name.ngram"))
                                .should(QueryBuilders.multiMatchQuery(keyword,"introduction.korean","introduction.ngram"));
                        if(StringUtils.hasText(keyword_detail)){
                            keywordDetailQuery.should(QueryBuilders.multiMatchQuery(keyword_detail,"company_name.korean","company_name.ngram").operator(Operator.AND))
                                    .should(QueryBuilders.multiMatchQuery(keyword_detail,"introduction.korean","introduction.ngram").operator(Operator.AND));
                        }
                    }

                    if("tag".equals(filter)){
                        keywordQuery.should(QueryBuilders.multiMatchQuery(keyword,"keyword_list.korean","keyword_list.ngram"));
                        if(StringUtils.hasText(keyword_detail)){
                            keywordDetailQuery.should(QueryBuilders.multiMatchQuery(keyword_detail,"keyword_list.korean","keyword_list.ngram").operator(Operator.AND));
                        }
                    }

                }
            } else {
                if(!StringUtils.hasText(keyword)){
                    if (StringUtils.hasText(tag)) {
                        keywordQuery.should(QueryBuilders.multiMatchQuery(tag ,"keyword_list.korean","keyword_list.ngram").operator(Operator.AND));
                    }
                } else {
                    keywordQuery.should(QueryBuilders.multiMatchQuery(keyword,"project_name.korean","project_name.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"comment.korean","comment.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"project_request_background.korean","project_request_background.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"expert_ability_experience.korean","expert_ability_experience.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"company_name.korean","company_name.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"introduction.korean","introduction.ngram"))
                            .should(QueryBuilders.multiMatchQuery(keyword,"keyword_list.korean","keyword_list.ngram"));

                    if(StringUtils.hasText(keyword_detail)){
                        keywordDetailQuery.should(QueryBuilders.multiMatchQuery(keyword_detail,"project_name.korean","project_name.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"comment.korean","comment.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"project_request_background.korean","project_request_background.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"expert_ability_experience.korean","expert_ability_experience.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"company_name.korean","company_name.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"introduction.korean","introduction.ngram").operator(Operator.AND))
                                .should(QueryBuilders.multiMatchQuery(keyword_detail,"keyword_list.korean","keyword_list.ngram").operator(Operator.AND));
                    }
                }
            }

            QueryBuilder MustQuery = QueryBuilders.boolQuery()
                    .must(keywordQuery)
                    .must(keywordDetailQuery);

            // filter 검색 쿼리
            BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();

            if(StringUtils.hasText(tag)){
                filterQuery.filter(QueryBuilders.matchQuery("keyword_list",tag));
            }

            filterQuery.must(QueryBuilders.termQuery("use_type_code","US_TY001"))
                    .must(QueryBuilders.termQuery("display_type_code","DSP_TY001"))
                    .must(QueryBuilders.matchQuery("project_status_code","PRJ_ST004,PRJ_ST005,PRJ_ST006,PRJ_ST007,PRJ_ST008,PRJ_ST009,PRJ_ST010,PRJ_ST011,PRJ_ST022,PRJ_ST023"));

            // 최종 검색 쿼리
            QueryBuilder esQuery = QueryBuilders.boolQuery()
                    .filter(filterQuery)
                    .must(MustQuery);

            //System.out.println(esQuery.toString());
            sourceBuilder.query(esQuery);

            // 검색 필드
            String[] includeFileds = new String[]{
                    "project_seq",
                    "project_name",
                    "comment",
                    "project_request_background",
                    "expert_ability_experience",
                    "hope_service_payment_name",
                    "budget",
                    "invitation_start_date",
                    "invitation_end_date",
                    "project_start_schedule_date",
                    "work_type_code",
                    "work_type_name",
                    "work_area_city_name",
                    "work_area_location_name",
                    "project_status_code",
                    "recruit_type_code",
                    "job_field_str",
                    "keyword_list",
                    "recruit_cnt",
                    "is_new",
                    "company_name",
                    "introduction",
                    "project_expectation_period_name",
                    "work_shape",
                    "_score"
            };
            String[] excludeFileds = new String[]{};
            sourceBuilder.fetchSource(includeFileds, excludeFileds);
            sourceBuilder.trackScores(true);

            // 페이징
            sourceBuilder.from(page_index);
            sourceBuilder.size(page_size);

            // 정렬
            SortBuilder sortColumn1 = new FieldSortBuilder("project_status_order").order(SortOrder.ASC);
            SortBuilder sortColumn2 = new FieldSortBuilder("last_edit_date").order(SortOrder.DESC);
            sourceBuilder.sort(sortColumn1);
            sourceBuilder.sort(sortColumn2);

            // 타임아웃
            sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

            // Elasticsearch 인덱스 검색
            String esIndex = "search-engine-talentbank-project";
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
            logger.info("analyze :: " + analyze);

            // 검색 Response
            response.setTotal_count(totalCnt);
            response.setAnalyze(analyze);
            response.setDatas(dataList);

            //logger.info("response :: " + response);
            return response;
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();

            ErrorResponse response = new ErrorResponse();
            response.setStatus(500);
            response.setError("Internal Server Error");

            String message = e.getMessage().toString();
            response.setMessage(message);

            response.setPath(url);
            LocalDateTime timestamp = LocalDateTime.now();
            response.setTimestamp(timestamp);

            return response;
        }

    }
}
