-- 创建测试用例表
CREATE TABLE IF NOT EXISTS `test_case` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `api_id` BIGINT NOT NULL COMMENT 'API接口ID',
    `name` VARCHAR(100) NOT NULL COMMENT '测试用例名称',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '测试用例描述',
    `request_method` VARCHAR(10) NOT NULL COMMENT '请求方法',
    `request_path` VARCHAR(200) NOT NULL COMMENT '请求路径',
    `request_headers` TEXT DEFAULT NULL COMMENT '请求头',
    `request_body` TEXT DEFAULT NULL COMMENT '请求体',
    `expected_response` TEXT DEFAULT NULL COMMENT '预期响应',
    `expected_status_code` VARCHAR(10) NOT NULL COMMENT '预期状态码',
    `assertions` TEXT DEFAULT NULL COMMENT '断言规则',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_api_id` (`api_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试用例表';

-- 创建测试执行记录表
CREATE TABLE IF NOT EXISTS `test_execution` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `api_id` BIGINT NOT NULL COMMENT 'API接口ID',
    `test_case_id` BIGINT NOT NULL COMMENT '测试用例ID',
    `status` VARCHAR(20) NOT NULL COMMENT '执行状态：SUCCESS-成功，FAILED-失败，ERROR-错误',
    `actual_response` TEXT DEFAULT NULL COMMENT '实际响应',
    `actual_status_code` VARCHAR(10) DEFAULT NULL COMMENT '实际状态码',
    `error_message` TEXT DEFAULT NULL COMMENT '错误信息',
    `execution_time` VARCHAR(50) DEFAULT NULL COMMENT '执行时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `environment` VARCHAR(50) DEFAULT NULL COMMENT '执行环境',
    `executed_by` VARCHAR(50) DEFAULT NULL COMMENT '执行人',
    `is_scheduled` TINYINT NOT NULL DEFAULT 0 COMMENT '是否为定时执行：0-否，1-是',
    PRIMARY KEY (`id`),
    KEY `idx_api_id` (`api_id`),
    KEY `idx_test_case_id` (`test_case_id`),
    FOREIGN KEY (`test_case_id`) REFERENCES `test_case`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试执行记录表';

-- 修改API接口表，添加测试相关字段
ALTER TABLE `api_interface`
ADD COLUMN `headers` TEXT DEFAULT NULL COMMENT '接口请求头' AFTER `description`,
ADD COLUMN `request_example` TEXT DEFAULT NULL COMMENT '请求示例' AFTER `headers`,
ADD COLUMN `response_example` TEXT DEFAULT NULL COMMENT '响应示例' AFTER `request_example`,
ADD COLUMN `test_config` TEXT DEFAULT NULL COMMENT 'AI测试配置' AFTER `deleted`,
ADD COLUMN `ai_model_type` VARCHAR(50) DEFAULT NULL COMMENT 'AI模型类型' AFTER `test_config`,
ADD COLUMN `test_case_count` INT DEFAULT 0 COMMENT '测试用例数量' AFTER `ai_model_type`,
ADD COLUMN `last_test_time` DATETIME DEFAULT NULL COMMENT '最后测试时间' AFTER `test_case_count`,
ADD COLUMN `test_interval` INT DEFAULT NULL COMMENT '测试间隔（小时）' AFTER `last_test_time`,
ADD COLUMN `enable_scheduled_test` TINYINT NOT NULL DEFAULT 0 COMMENT '是否启用定时测试：0-否，1-是' AFTER `test_interval`;