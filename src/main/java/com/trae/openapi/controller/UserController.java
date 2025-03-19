package com.trae.openapi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trae.openapi.common.BaseResponse;
import com.trae.openapi.common.ResultUtils;
import com.trae.openapi.entity.User;
import com.trae.openapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public BaseResponse<Page<User>> listUsers(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username
    ) {
        // 创建分页对象
        Page<User> page = new Page<>(current, pageSize);
        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(username != null, User::getUsername, username)
                .eq(User::getDeleted, 0)
                .orderByDesc(User::getCreateTime);
        // 执行查询
        Page<User> userPage = userService.page(page, queryWrapper);
        // 处理返回结果，过滤敏感信息
        userPage.getRecords().forEach(user -> {
            user.setPassword(null);
            user.setSalt(null);
        });
        return ResultUtils.success(userPage);
    }
}