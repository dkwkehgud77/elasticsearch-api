package com.talentbank.search.service;

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
public class ProjectProductService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RestHighLevelClient esClient;

    public Object search(ProjectProductRequest projectProductRequest) {

        try {
            logger.info("ProjectProductRequest :: " + projectProductRequest.toString());
            // 검색 Request
            Optional<ProjectProductRequest> req = Optional.ofNullable(projectProductRequest);
            String keyword = req.map(ProjectProductRequest::getKeyword).orElse("").trim();
            String keyword_detail = req.map(ProjectProductRequest::getKeyword_detail).orElse("").trim();
            String job_field_code = req.map(ProjectProductRequest::getJob_field_code).orElse("").trim();
            String product_type = req.map(ProjectProductRequest::getProduct_type).orElse("").trim();
            int page_index = req.map(ProjectProductRequest::getPage_index).orElse(0);
            int page_size = req.map(ProjectProductRequest::getPage_size).orElse(10);
            String sort_column = req.map(ProjectProductRequest::getSort_column).orElse("").trim();
            String sort_direction = req.map(ProjectProductRequest::getSort_direction).orElse("").trim();

            // 검색 Response
            SrchResponse response = new SrchResponse();
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            // keyword 검색 쿼리
            BoolQueryBuilder keywordQuery = QueryBuilders.boolQuery();
            BoolQueryBuilder keywordDetailQuery = QueryBuilders.boolQuery();

            if (StringUtils.hasText(keyword)) {
                keywordQuery.should(QueryBuilders.multiMatchQuery(keyword, "product_title.korean", "product_title.ngram"))
                        .should(QueryBuilders.multiMatchQuery(keyword, "company_worries.korean", "company_worries.ngram"))
                        .should(QueryBuilders.multiMatchQuery(keyword, "content.korean", "content.ngram"))
                        .should(QueryBuilders.multiMatchQuery(keyword, "outputs.korean", "outputs.ngram"))
                        .should(QueryBuilders.multiMatchQuery(keyword, "benefit.korean", "benefit.ngram"))
                        .should(QueryBuilders.multiMatchQuery(keyword, "expert_skill.korean", "expert_skill.ngram"))
                        .should(QueryBuilders.multiMatchQuery(keyword, "product_keyword_name_str.korean", "product_keyword_name_str.ngram"))
                        .should(QueryBuilders.wildcardQuery("min_price.keyword", String.format("*%s*", keyword)))
                        .should(QueryBuilders.wildcardQuery("max_price.keyword", String.format("*%s*", keyword)));
//                        .should(QueryBuilders.matchQuery("view_count.keyword", String.format("*%s*", keyword)))
//                        .should(QueryBuilders.matchQuery("regist_date.keyword", String.format("*%s*", keyword)));

                if (StringUtils.hasText(keyword_detail)) {
                    keywordDetailQuery.should(QueryBuilders.multiMatchQuery(keyword_detail, "product_title.korean", "product_title.ngram").operator(Operator.AND))
                            .should(QueryBuilders.multiMatchQuery(keyword_detail, "company_worries.korean", "company_worries.ngram").operator(Operator.AND))
                            .should(QueryBuilders.multiMatchQuery(keyword_detail, "content.korean", "content.ngram").operator(Operator.AND))
                            .should(QueryBuilders.multiMatchQuery(keyword_detail, "outputs.korean", "outputs.ngram").operator(Operator.AND))
                            .should(QueryBuilders.multiMatchQuery(keyword_detail, "benefit.korean", "benefit.ngram").operator(Operator.AND))
                            .should(QueryBuilders.multiMatchQuery(keyword_detail, "expert_skill.korean", "expert_skill.ngram").operator(Operator.AND))
                            .should(QueryBuilders.multiMatchQuery(keyword_detail, "product_keyword_name_str.korean", "product_keyword_name_str.ngram").operator(Operator.AND))
                            .should(QueryBuilders.wildcardQuery("min_price.keyword", String.format("*%s*", keyword_detail)))
                            .should(QueryBuilders.wildcardQuery("max_price.keyword", String.format("*%s*", keyword_detail)));
//                            .should(QueryBuilders.matchQuery("view_count", String.format("*%s*", keyword_detail)))
//                            .should(QueryBuilders.matchQuery("regist_date", String.format("*%s*", keyword_detail)));
                }
            }

            QueryBuilder MustQuery = QueryBuilders.boolQuery()
                    .must(keywordQuery)
                    .must(keywordDetailQuery);

            // filter 검색 쿼리
            BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();
            if (StringUtils.hasText(product_type))
                filterQuery.must(QueryBuilders.termQuery("product_type", product_type));

            if (StringUtils.hasText(job_field_code))
                filterQuery.must(QueryBuilders.wildcardQuery("product_jobfield_code_str2", String.format("*%s*", job_field_code)));


            // 최종 검색 쿼리
            QueryBuilder esQuery = QueryBuilders.boolQuery()
                    .filter(filterQuery)
                    .must(MustQuery);

            sourceBuilder.query(esQuery);
//            System.out.println(esQuery.toString());
            logger.debug(esQuery.toString());

            // 검색 필드
            String[] includeFileds = new String[]{
                    "project_product_seq",
                    "product_title",
                    "product_status_code",
                    "product_visible_yn",
                    "product_type",
                    "company_worries",
                    "content",
                    "outputs",
                    "benefit",
                    "term",
                    "term_file_seq",
                    "min_price",
                    "max_price",
                    "expert_skill",
                    "view_count",
                    "regist_date",
                    "product_jobfield_code_str",
                    "product_jobfield_name_str",
                    "product_jobfield_code_str2",
                    "product_jobfield_name_str2",
                    "tag_seq",
                    "product_keyword_name_str",
                    "expert_seq",
                    "product_expert_img_seq",
                    "product_expert_confirm_type_code",
                    "product_expert_offer_receive_yn",
                    "product_expert_id",
                    "product_user_status_code",
                    "product_user_testId_yn",
                    "confirm_display",
                    "offer_receive_display",
                    "user_status_display",
                    "user_testId_display",
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
            String esIndex = "search-engine-talentbank-product";
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

//            logger.info("response :: " + response);
            return response;

        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();

            ErrorResponse response = new ErrorResponse();
            response.setStatus(500);
            response.setError("Internal Server Error");

            String message = e.getMessage().toString();
            response.setMessage(message);

            response.setPath("/api/project/product/fullsearch");
            LocalDateTime timestamp = LocalDateTime.now();
            response.setTimestamp(timestamp);

            return response;
        }

    }
}
