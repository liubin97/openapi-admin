package com.trae.openapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trae.openapi.entity.User;
import com.trae.openapi.mapper.UserMapper;
import com.trae.openapi.service.UserService;
import com.trae.openapi.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public String login(String username, String password) {
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getDeleted, 0));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        String encryptedPassword = DigestUtils.md5DigestAsHex((password + user.getSalt()).getBytes());
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return jwtUtil.generateToken(user.getId());
    }

    @Override
    public void register(String username, String password) {
        try {
            // 参数校验
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("用户名不能为空");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("密码不能为空");
            }

            // 检查用户名是否已存在
            User existUser = this.getOne(new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, username.trim())
                    .eq(User::getDeleted, 0));
            if (existUser != null) {
                throw new RuntimeException("用户名已存在");
            }

            // 创建新用户
            User user = new User();
            user.setUsername(username.trim());
            String salt = UUID.randomUUID().toString().substring(0, 6);
            user.setSalt(salt);
            user.setPassword(DigestUtils.md5DigestAsHex((password + salt).getBytes()));
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            user.setDeleted(0);

            // 保存用户信息
            boolean saved = this.save(user);
            if (!saved) {
                throw new RuntimeException("用户注册失败");
            }
        } catch (Exception e) {
            log.error("用户注册失败: username={}, error={}", username, e.getMessage(), e);
            throw new RuntimeException("用户注册失败: " + e.getMessage());
        }
    }
}