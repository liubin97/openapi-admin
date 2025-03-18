package com.trae.openapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trae.openapi.entity.ApiVersion;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApiVersionMapper extends BaseMapper<ApiVersion> {
}