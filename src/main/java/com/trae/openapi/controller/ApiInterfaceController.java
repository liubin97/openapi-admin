package com.trae.openapi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trae.openapi.entity.ApiInterface;
import com.trae.openapi.service.ApiInterfaceService;
import com.trae.openapi.vo.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/apis")
public class ApiInterfaceController {
    @Autowired
    private ApiInterfaceService apiInterfaceService;
    
    @GetMapping("/page")
    public Result<Page<ApiInterface>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long versionId) {
        Page<ApiInterface> page = new Page<>(current, size);
        LambdaQueryWrapper<ApiInterface> wrapper = new LambdaQueryWrapper<ApiInterface>()
                .like(name != null, ApiInterface::getName, name)
                .eq(status != null, ApiInterface::getStatus, status)
                .eq(versionId != null, ApiInterface::getVersionId, versionId)
                .eq(ApiInterface::getDeleted, false)
                .orderByDesc(ApiInterface::getCreateTime);
        return Result.success(apiInterfaceService.page(page, wrapper));
    }
    
    @PostMapping
    public Result<Boolean> save(@RequestBody @Valid ApiInterface apiInterface) {
        return Result.success(apiInterfaceService.save(apiInterface));
    }
    
    @PutMapping
    public Result<Boolean> update(@RequestBody @Valid ApiInterface apiInterface) {
        return Result.success(apiInterfaceService.updateById(apiInterface));
    }
    
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        ApiInterface apiInterface = new ApiInterface();
        apiInterface.setId(id);
        apiInterface.setDeleted(true);
        return Result.success(apiInterfaceService.updateById(apiInterface));
    }
    
    @GetMapping("/{id}")
    public Result<ApiInterface> getById(@PathVariable Long id) {
        return Result.success(apiInterfaceService.getById(id));
    }
    
    @PostMapping("/{id}/key")
    public Result<String> generateApiKey(@PathVariable Long id) {
        return Result.success(apiInterfaceService.generateApiKey(id));
    }
    
    @GetMapping("/validate")
    public Result<Boolean> validateApiKey(@RequestParam String apiKey) {
        return Result.success(apiInterfaceService.validateApiKey(apiKey));
    }
}