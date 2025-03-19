package com.trae.openapi.config;

import com.trae.openapi.util.JwtUtil;
import com.trae.openapi.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行登录和注册接口
        if (request.getRequestURI().contains("/api/auth/")) {
            return true;
        }

        // 获取token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(Result.error("认证头格式错误").toString());
            return false;
        }
        String token = authHeader.substring(7);
        if (token == null || token.isEmpty()) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(Result.error("未登录").toString());
            return false;
        }

        // 验证token
        if (!jwtUtil.validateToken(token)) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(Result.error("token无效或已过期").toString());
            return false;
        }

        // 将用户信息存入request
        request.setAttribute("username", jwtUtil.getUsernameFromToken(token));
        return true;
    }
}