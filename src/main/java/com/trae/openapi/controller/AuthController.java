package com.trae.openapi.controller;

import com.trae.openapi.entity.User;
import com.trae.openapi.service.UserService;
import com.trae.openapi.vo.LoginRequest;
import com.trae.openapi.vo.LoginResponse;
import com.trae.openapi.vo.RegisterRequest;
import com.trae.openapi.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        String token = userService.login(request.getUsername(), request.getPassword());
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        return Result.success(response);
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid RegisterRequest request) {
        userService.register(request.getUsername(), request.getPassword());
        return Result.success();
    }
}