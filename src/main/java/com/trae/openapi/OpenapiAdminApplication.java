package com.trae.openapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.trae.openapi.mapper")
public class OpenapiAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(OpenapiAdminApplication.class, args);
    }
}