package com.trae.openapi.service;

import java.time.LocalDateTime;

import com.trae.openapi.entity.ApiInterface;

public interface AITestService {
    /**
     * 生成AI测试用例
     * @param apiInterface API接口实体
     * @return 生成的测试用例数量
     */
    int generateTestCases(ApiInterface apiInterface);

    /**
     * 执行自动化测试
     * @param apiInterface 待测试的API接口
     */
    void executeAITest(ApiInterface apiInterface);

    /**
     * 获取最后一次测试时间
     * @param apiId API接口ID
     * @return 最后一次测试时间
     */
    LocalDateTime getLastTestTime(Long apiId);
}