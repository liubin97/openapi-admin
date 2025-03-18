package com.trae.openapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trae.openapi.entity.ApiVersion;
import com.trae.openapi.mapper.ApiVersionMapper;
import com.trae.openapi.service.ApiVersionService;
import org.springframework.stereotype.Service;

@Service
public class ApiVersionServiceImpl extends ServiceImpl<ApiVersionMapper, ApiVersion> implements ApiVersionService {
}