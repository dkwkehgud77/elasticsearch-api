package com.talentbank.search.service;

import com.talentbank.search.dto.request.OnlineConsultRequest;
import com.talentbank.search.dto.request.ProjectProductRequest;
import com.talentbank.search.dto.request.SuccessCaseRequest;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuccessCaseService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RestHighLevelClient esClient;

    public Object search(SuccessCaseRequest successCaseRequest) {

        try {
            logger.info("SuccessCaseRequest :: " + successCaseRequest.toString());
            // 검색 Request
            Optional<SuccessCaseRequest> req = Optional.ofNullable(successCaseRequest);
            String keyword = req.map(SuccessCaseRequest::getKeyword).orElse("").trim();
            String keyword_detail = req.map(SuccessCaseRequest::getKeyword_detail).orElse("").trim();
            String case_type_code = req.map(SuccessCaseRequest::getCase_type_code).orElse("").trim();
            int page_index = req.map(SuccessCaseRequest::getPage_index).orElse(0);
            int page_size = req.map(SuccessCaseRequest::getPage_size).orElse(10);
            String sort_column = req.map(SuccessCaseRequest::getSort_column).orElse("").trim();
            String sort_direction = req.map(SuccessCaseRequest::getSort_direction).orElse("").trim();

            // 검색 Response
            SrchResponse response = new SrchResponse();
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            // keyword 검색 쿼리
            BoolQueryBuilder keywordQuery = QueryBuilders.boolQuery();
            BoolQueryBuilder keywordDetailQuery = QueryBuilders.boolQuery();

            if (StringUtils.hasText(keyword)) {
                keywordQuery.should(QueryBuilders.multiMatchQuery(keyword, "title.korean", "title.ngram"))
                        .should(QueryBuilders.multiMatchQuery(keyword, "project_summary.korean", "project_summary.ngram"))
                        .should(QueryBuilders.multiMatchQuery(keyword, "job_type.korean", "job_type.ngram"))
                        .should(QueryBuilders.multiMatchQuery(keyword, "keyword_name.korean", "keyword_name.ngram"));

                if (StringUtils.hasText(keyword_detail)) {
                    keywordDetailQuery.should(QueryBuilders.multiMatchQuery(keyword_detail, "title.korean", "title.ngram").operator(Operator.AND))
                            .should(QueryBuilders.multiMatchQuery(keyword_detail, "project_summary.korean", "project_summary.ngram").operator(Operator.AND))
                            .should(QueryBuilders.multiMatchQuery(keyword_detail, "job_type.korean", "job_type.ngram").operator(Operator.AND))
                            .should(QueryBuilders.multiMatchQuery(keyword_detail, "keyword_name.korean", "keyword_name.ngram").operator(Operator.AND));
                }
            }

            QueryBuilder MustQuery = QueryBuilders.boolQuery()
                    .must(keywordQuery)
                    .must(keywordDetailQuery);

            // filter 검색 쿼리
            BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();
            if (StringUtils.hasText(case_type_code))
                filterQuery.must(QueryBuilders.termQuery("case_type_code", case_type_code));


            // 최종 검색 쿼리
            QueryBuilder esQuery = QueryBuilders.boolQuery()
                    .filter(filterQuery)
                    .must(MustQuery);

            sourceBuilder.query(esQuery);
            System.out.println(esQuery.toString());

            // 검색 필드
            String[] includeFileds = new String[]{
                    "success_case_seq",
                    "case_type_code",
                    "case_type_code_name",
                    "display_yn",
                    "title",
                    "project_summary",
                    "job_type",
                    "order_no",
                    "keyword_name",
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
            String esIndex = "search-engine-talentbank-success-case";
            SearchRequest searchRequest = new SearchRequest(esIndex);
            searchRequest.source(sourceBuilder);
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);

            long totalCnt = searchResponse.getHits().getTotalHits().value;
            SearchHit[] hits = searchResponse.getHits().getHits();

            List dataList = new ArrayList<>();
            for (SearchHit hit : hits) {
                Map hitMap = hit.getSourceAsMap();
                hitMap.put("_score", hit.getScore());
                dataList.add(hitMap);
            }

            // keyword 형태소 분석
            AnalyzeRequest analyzeRequest = AnalyzeRequest.withIndexAnalyzer(esIndex, "korean_analyzer", keyword);
            AnalyzeResponse analyzeResponse = esClient.indices().analyze(analyzeRequest, RequestOptions.DEFAULT);
            List<AnalyzeResponse.AnalyzeToken> tokens = analyzeResponse.getTokens();
            String analyze = tokens.stream().map(v -> v.getTerm()).collect(Collectors.joining(","));

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

            response.setPath("/api/success-case/fullsearch");
            LocalDateTime timestamp = LocalDateTime.now();
            response.setTimestamp(timestamp);

            return response;
        }

    }
}
