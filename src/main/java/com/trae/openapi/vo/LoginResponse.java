package com.trae.openapi.vo;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    // 可根据需要添加用户信息字段
    // private UserInfo userInfo;
}