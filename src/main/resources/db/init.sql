-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `salt` VARCHAR(20) NOT NULL COMMENT '密码盐值',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建API信息表
CREATE TABLE IF NOT EXISTS `api_info` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT 'API名称',
    `path` VARCHAR(200) NOT NULL COMMENT 'API路径',
    `method` VARCHAR(10) NOT NULL COMMENT '请求方法',
    `description` VARCHAR(500) DEFAULT NULL COMMENT 'API描述',
    `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API信息表';

-- 创建API接口表
CREATE TABLE IF NOT EXISTS `api_interface` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `api_id` BIGINT NOT NULL COMMENT '关联API ID',
    `name` VARCHAR(100) NOT NULL COMMENT '接口名称',
    `path` VARCHAR(200) NOT NULL COMMENT '接口路径',
    `method` VARCHAR(10) NOT NULL COMMENT '请求方法',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '接口描述',
    `api_key` VARCHAR(100) NOT NULL COMMENT '接口密钥',
    `daily_limit` INT NOT NULL DEFAULT 1000 COMMENT '每日调用限额',
    `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_api_id` (`api_id`),
    FOREIGN KEY (`api_id`) REFERENCES `api_info`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API接口表';

-- 创建API版本表
CREATE TABLE IF NOT EXISTS `api_version` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `api_id` BIGINT NOT NULL COMMENT 'API ID',
    `version` VARCHAR(20) NOT NULL COMMENT '版本号',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-开发中，2-已发布，3-已废弃',
    `request_params` TEXT DEFAULT NULL COMMENT '请求参数',
    `response_params` TEXT DEFAULT NULL COMMENT '响应参数',
    `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_api_id` (`api_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API版本表';

-- 初始化管理员账户
INSERT IGNORE INTO `user` (`username`, `password`, `salt`) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', 'admin123');