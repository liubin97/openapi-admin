package com.trae.openapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trae.openapi.entity.User;
import com.trae.openapi.mapper.UserMapper;
import com.trae.openapi.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

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
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public void register(String username, String password) {
        User existUser = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getDeleted, 0));
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        String salt = UUID.randomUUID().toString().substring(0, 6);
        user.setSalt(salt);
        user.setPassword(DigestUtils.md5DigestAsHex((password + salt).getBytes()));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setDeleted(0);
        this.save(user);
    }
}