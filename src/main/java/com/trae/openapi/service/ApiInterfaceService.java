package com.trae.openapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.trae.openapi.entity.ApiInterface;

public interface ApiInterfaceService extends IService<ApiInterface> {
    /**
     * 生成API密钥
     * @param id API接口ID
     * @return 生成的密钥
     */
    String generateApiKey(Long id);
    
    /**
     * 验证API密钥
     * @param apiKey API密钥
     * @return 是否有效
     */
    boolean validateApiKey(String apiKey);
    
    /**
     * 检查接口调用次数是否超限
     * @param id API接口ID
     * @return 是否超限
     */
    boolean checkDailyLimit(Long id);
}