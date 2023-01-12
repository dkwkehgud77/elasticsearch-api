package com.talentbank.search.controller;

import com.talentbank.search.dto.request.*;
import com.talentbank.search.dto.response.SrchResponse;
import com.talentbank.search.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Api(tags = {"TalentBank SEARCH-API Controller"})
@RestController
@RequestMapping("/api")
@Component
@RequiredArgsConstructor
public class ApiController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ProjectService projectService;
    private final ProjectProductService projectProductService;
    private final ExpertFrontService expertFrontService;
    private final ExpertAdminService expertAdminService;
    private final ExpertAdminPartService expertAdminPartService;

    private final OnlineConsultService onlineConsultService;

    private final SuccessCaseService successCaseService;

    @PostMapping("/project/front/fullsearch")
    @ApiOperation(value = "프로젝트 검색")
    public Object projectSearch(@RequestBody ProjectRequest projectRequest) {
        Object response = projectService.search(projectRequest);
        return response;
    }

    @PostMapping("/project/product/fullsearch")
    @ApiOperation(value = "프로젝트-상품 검색")
    public Object projectProductSearch(@RequestBody ProjectProductRequest projectProductRequest) {
        Object response = projectProductService.search(projectProductRequest);
        return response;
    }

    @PostMapping("/expert/front/fullsearch")
    @ApiOperation(value = "전문가-프론트 검색")
    public Object expertFrontSearch(@RequestBody ExpertFrontRequest expertFrontRequest) {
        Object response = expertFrontService.search(expertFrontRequest);
        return response;
    }

    @PostMapping("/expert/admin/fullsearch")
    @ApiOperation(value = "전문가-어드민 검색")
    public Object expertAdminSearch(@RequestBody ExpertAdminRequest expertAdminRequest) {
        Object response = expertAdminService.search(expertAdminRequest);
        return response;
    }

    @PostMapping("/expert/admin/partsearch")
    @ApiOperation(value = "전문가-어드민 파트 검색")
    public Object expertAdminPartSearch(@RequestBody ExpertAdminPartRequest expertAdminPartRequest) {
        Object response = expertAdminPartService.search(expertAdminPartRequest);
        return response;
    }

    @PostMapping("/success-case/fullsearch")
    @ApiOperation(value = "성공-사례 검색")
    public Object successCaseSearch(@RequestBody SuccessCaseRequest successCaseRequest) {
        Object response = successCaseService.search(successCaseRequest);
        return response;
    }

    @PostMapping("/online-consult/fullsearch")
    @ApiOperation(value = "온라인-자문 검색")
    public Object onlineConsultSearch(@RequestBody OnlineConsultRequest onlineConsultRequest) {
        Object response = onlineConsultService.search(onlineConsultRequest);
        return response;
    }

}








