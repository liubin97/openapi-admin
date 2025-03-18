package com.trae.openapi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trae.openapi.entity.ApiVersion;
import com.trae.openapi.service.ApiVersionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/version")
public class ApiVersionController {
    @Autowired
    private ApiVersionService apiVersionService;
    
    @GetMapping("/page")
    public Page<ApiVersion> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String version,
            @RequestParam(required = false) Integer status) {
        Page<ApiVersion> page = new Page<>(current, size);
        LambdaQueryWrapper<ApiVersion> wrapper = new LambdaQueryWrapper<ApiVersion>()
                .like(version != null, ApiVersion::getVersion, version)
                .eq(status != null, ApiVersion::getStatus, status)
                .orderByDesc(ApiVersion::getCreateTime);
        return apiVersionService.page(page, wrapper);
    }
    
    @PostMapping
    public boolean save(@RequestBody @Valid ApiVersion apiVersion) {
        return apiVersionService.save(apiVersion);
    }
    
    @PutMapping
    public boolean update(@RequestBody @Valid ApiVersion apiVersion) {
        return apiVersionService.updateById(apiVersion);
    }
    
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return apiVersionService.removeById(id);
    }
    
    @GetMapping("/{id}")
    public ApiVersion getById(@PathVariable Long id) {
        return apiVersionService.getById(id);
    }
}