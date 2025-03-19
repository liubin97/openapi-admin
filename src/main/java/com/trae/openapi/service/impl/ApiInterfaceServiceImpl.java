package com.trae.openapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trae.openapi.entity.ApiInterface;
import com.trae.openapi.mapper.ApiInterfaceMapper;
import com.trae.openapi.service.ApiInterfaceService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ApiInterfaceServiceImpl extends ServiceImpl<ApiInterfaceMapper, ApiInterface> implements ApiInterfaceService {

    @Override
    public String generateApiKey(Long id) {
        ApiInterface apiInterface = this.getById(id);
        if (apiInterface == null) {
            throw new RuntimeException("API接口不存在");
        }
        String apiKey = UUID.randomUUID().toString().replace("-", "");
        apiInterface.setApiKey(apiKey);
        this.updateById(apiInterface);
        return apiKey;
    }

    @Override
    public boolean validateApiKey(String apiKey) {
        return this.lambdaQuery()
                .eq(ApiInterface::getApiKey, apiKey)
                .eq(ApiInterface::getStatus, 1)
                .eq(ApiInterface::getDeleted, false)
                .exists();
    }

    @Override
    public boolean checkDailyLimit(Long id) {
        // TODO: 实现接口调用次数统计和限制
        return true;
    }
}