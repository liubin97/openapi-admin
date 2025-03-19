package com.trae.openapi.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TestCase {
    private Long id;
    private Long apiId;
    private String name;
    private String description;
    private String requestMethod;
    private String requestPath;
    private String requestHeaders;
    private String requestBody;
    private String expectedResponse;
    private String expectedStatusCode;
    private String assertions;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}