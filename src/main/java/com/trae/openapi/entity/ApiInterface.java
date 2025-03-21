package com.trae.openapi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("api_interface")
public class ApiInterface {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String path;
    
    private String method;
    
    private String description;
    
    private String headers;
    
    private String requestExample;
    
    private String responseExample;
    
    private String apiKey;
    
    private Integer dailyLimit;
    
    private Integer status;
    
    private Long versionId;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private Boolean deleted;

    // AI测试配置
    private String testConfig;
    private String aiModelType;
    private Integer testCaseCount;
    private LocalDateTime lastTestTime;
    private Integer testInterval;
}