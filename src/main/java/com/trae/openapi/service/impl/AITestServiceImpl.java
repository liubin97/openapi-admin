package com.trae.openapi.service.impl;

import com.trae.openapi.entity.ApiInterface;
import com.trae.openapi.entity.TestCase;
import com.trae.openapi.entity.TestExecution;
import com.trae.openapi.service.AITestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AITestServiceImpl implements AITestService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Override
    public int generateTestCases(ApiInterface apiInterface) {
        // 调用AI模型生成测试用例
        List<TestCase> testCases = new ArrayList<>();
        
        // 根据API接口定义生成测试用例
        TestCase testCase = new TestCase();
        testCase.setApiId(apiInterface.getId());
        testCase.setName("测试用例-" + System.currentTimeMillis());
        testCase.setRequestMethod(apiInterface.getMethod());
        testCase.setRequestPath(apiInterface.getPath());
        testCase.setRequestHeaders(apiInterface.getHeaders());
        testCase.setRequestBody(apiInterface.getRequestExample());
        testCase.setExpectedResponse(apiInterface.getResponseExample());
        testCase.setExpectedStatusCode("200");
        testCase.setCreateTime(LocalDateTime.now());
        
        // 保存测试用例
        saveTestCase(testCase);
        testCases.add(testCase);
        
        return testCases.size();
    }

    @Override
    public void executeAITest(ApiInterface apiInterface) {
        // 获取API接口的测试用例
        List<TestCase> testCases = getTestCases(apiInterface.getId());
        
        for (TestCase testCase : testCases) {
            TestExecution execution = new TestExecution();
            execution.setApiId(apiInterface.getId());
            execution.setTestCaseId(testCase.getId());
            execution.setCreateTime(LocalDateTime.now());
            
            try {
                // 构建请求
                HttpHeaders headers = new HttpHeaders();
                if (testCase.getRequestHeaders() != null) {
                    // 解析请求头
                    String[] headerLines = testCase.getRequestHeaders().split("\n");
                    for (String headerLine : headerLines) {
                        String[] headerParts = headerLine.split(":", 2);
                        if (headerParts.length == 2) {
                            headers.add(headerParts[0].trim(), headerParts[1].trim());
                        }
                    }
                }
                
                HttpEntity<?> requestEntity = new HttpEntity<>(testCase.getRequestBody(), headers);
                
                // 发送请求并获取响应
                ResponseEntity<String> response = restTemplate.exchange(
                    testCase.getRequestPath(),
                    HttpMethod.valueOf(testCase.getRequestMethod()),
                    requestEntity,
                    String.class
                );
                
                // 验证响应
                execution.setActualResponse(response.getBody());
                execution.setActualStatusCode(String.valueOf(response.getStatusCodeValue()));
                
                if (response.getStatusCodeValue() == Integer.parseInt(testCase.getExpectedStatusCode())) {
                    execution.setStatus("SUCCESS");
                } else {
                    execution.setStatus("FAILED");
                    execution.setErrorMessage("状态码不匹配");
                }
                
            } catch (Exception e) {
                execution.setStatus("ERROR");
                execution.setErrorMessage(e.getMessage());
            }
            
            // 保存执行结果
            saveTestExecution(execution);
        }
    }

    @Override
    public LocalDateTime getLastTestTime(Long apiId) {
        String sql = "SELECT MAX(create_time) FROM test_execution WHERE api_id = ?";
        return jdbcTemplate.queryForObject(sql, LocalDateTime.class, apiId);
    }

    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    private void scheduledAITest() {
        // 获取所有需要定时测试的API接口
        String sql = "SELECT * FROM api_interface WHERE enable_scheduled_test = true";
        List<Map<String, Object>> apis = jdbcTemplate.queryForList(sql);
        
        for (Map<String, Object> api : apis) {
            ApiInterface apiInterface = new ApiInterface();
            apiInterface.setId(Long.valueOf(api.get("id").toString()));
            apiInterface.setName((String) api.get("name"));
            apiInterface.setMethod((String) api.get("method"));
            apiInterface.setPath((String) api.get("path"));
            
            // 执行自动化测试
            executeAITest(apiInterface);
        }
    }
    
    private void saveTestCase(TestCase testCase) {
        String sql = "INSERT INTO test_case (api_id, name, request_method, request_path, request_headers, request_body, expected_response, expected_status_code, create_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
            testCase.getApiId(),
            testCase.getName(),
            testCase.getRequestMethod(),
            testCase.getRequestPath(),
            testCase.getRequestHeaders(),
            testCase.getRequestBody(),
            testCase.getExpectedResponse(),
            testCase.getExpectedStatusCode(),
            testCase.getCreateTime()
        );
    }
    
    private List<TestCase> getTestCases(Long apiId) {
        String sql = "SELECT * FROM test_case WHERE api_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            TestCase testCase = new TestCase();
            testCase.setId(rs.getLong("id"));
            testCase.setApiId(rs.getLong("api_id"));
            testCase.setName(rs.getString("name"));
            testCase.setRequestMethod(rs.getString("request_method"));
            testCase.setRequestPath(rs.getString("request_path"));
            testCase.setRequestHeaders(rs.getString("request_headers"));
            testCase.setRequestBody(rs.getString("request_body"));
            testCase.setExpectedResponse(rs.getString("expected_response"));
            testCase.setExpectedStatusCode(rs.getString("expected_status_code"));
            testCase.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
            return testCase;
        }, apiId);
    }
    
    private void saveTestExecution(TestExecution execution) {
        String sql = "INSERT INTO test_execution (api_id, test_case_id, status, actual_response, actual_status_code, error_message, create_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
            execution.getApiId(),
            execution.getTestCaseId(),
            execution.getStatus(),
            execution.getActualResponse(),
            execution.getActualStatusCode(),
            execution.getErrorMessage(),
            execution.getCreateTime()
        );
    }
}