package com.trae.openapi.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TestExecution {
    private Long id;
    private Long apiId;
    private Long testCaseId;
    private String status;  // SUCCESS, FAILED, ERROR
    private String actualResponse;
    private String actualStatusCode;
    private String errorMessage;
    private String executionTime;
    private LocalDateTime createTime;
    private String environment;
    private String executedBy;
    private Boolean isScheduled;
}