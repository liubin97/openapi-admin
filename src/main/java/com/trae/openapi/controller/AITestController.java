package com.trae.openapi.controller;

import com.trae.openapi.common.BaseResponse;
import com.trae.openapi.common.ResultUtils;
import com.trae.openapi.entity.ApiInterface;
import com.trae.openapi.service.AITestService;
import com.trae.openapi.service.ApiInterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/test")
public class AITestController {

    @Autowired
    private AITestService aiTestService;

    @Autowired
    private ApiInterfaceService apiInterfaceService;

    @PostMapping("/generate/{apiId}")
    public BaseResponse<Integer> generateTestCases(@PathVariable Long apiId) {
        ApiInterface apiInterface = apiInterfaceService.getById(apiId);
        if (apiInterface == null) {
            return ResultUtils.error( "API接口不存在");
        }
        int count = aiTestService.generateTestCases(apiInterface);
        return ResultUtils.success(count);
    }

    @PostMapping("/execute/{apiId}")
    public BaseResponse<Void> executeTest(@PathVariable Long apiId) {
        ApiInterface apiInterface = apiInterfaceService.getById(apiId);
        if (apiInterface == null) {
            return ResultUtils.error( "API接口不存在");
        }
        aiTestService.executeAITest(apiInterface);
        return ResultUtils.success(null);
    }

    @GetMapping("/last-test-time/{apiId}")
    public BaseResponse<LocalDateTime> getLastTestTime(@PathVariable Long apiId) {
        LocalDateTime lastTestTime = aiTestService.getLastTestTime(apiId);
        return ResultUtils.success(lastTestTime);
    }
}