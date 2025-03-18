package com.trae.openapi.service;

public interface UserService {
    String login(String username, String password);
    void register(String username, String password);
}