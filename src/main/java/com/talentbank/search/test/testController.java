//package com.talentbank.search.controller;
//
//import com.talentbank.search.test.UserRequest;
//import com.talentbank.search.test.UserResponse;
//import com.talentbank.search.service.ProjectService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import io.swagger.annotations.ApiResponse;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@Api(tags = {"REST API CONTROLLER 3333333"})
//@RestController
//@RequestMapping("/test")
//@Component
//@RequiredArgsConstructor
//public class testController {
//
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//    private final ProjectService projectService;
//
//    @ApiOperation(value = "hello method", notes = "테스트 GET API")
//    @GetMapping("/hello/{name}")
//    public String hello(
//            @ApiParam(value = "사용자 이름")
//            @PathVariable String name,
//
//            @ApiParam(value = "사용자 나이")
//            @RequestParam int age){
//        return "hello";
//    }
//
//    @GetMapping("/user")
//    public UserResponse user(UserRequest userRequest){
//        return new UserResponse(userRequest.getName(), userRequest.getAge());
//    }
//
//    @PostMapping("/user")
//    @ApiResponse(code = 404, message = "not found")
//    public UserResponse post(@RequestBody UserRequest userRequest){
//        return new UserResponse(userRequest.getName(), userRequest.getAge());
//    }
//
//    @PostMapping("/test")
//    public void test(@RequestBody Map<String, Object> requestData){
//        requestData.forEach((key, value) -> System.out.println(key));
//    }
//
//}




//
//
//
//
//
//
//
