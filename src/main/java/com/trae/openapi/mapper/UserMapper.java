package com.trae.openapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trae.openapi.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}