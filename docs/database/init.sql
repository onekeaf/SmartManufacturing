-- =====================================================
-- 智能制造系统数据库初始化脚本
-- Smart Manufacturing System Database Initialization
-- =====================================================

-- =====================================================
-- 1. 创建数据库
-- =====================================================

CREATE DATABASE IF NOT EXISTS smart_manufact_order CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS smart_manufact_production CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS smart_manufact_equipment CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS smart_manufact_inventory CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS smart_manufact_quality CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- =====================================================
-- 2. 订单服务数据库 (smart_manufact_order)
-- =====================================================

USE smart_manufact_order;

DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
  `customer_name` VARCHAR(100) NOT NULL COMMENT '客户名称',
  `customer_type` VARCHAR(20) COMMENT '客户类型：INDIVIDUAL-个人，DEALER-经销商，CUSTOM-定制',
  `vehicle_model` VARCHAR(50) NOT NULL COMMENT '车型',
  `quantity` INT NOT NULL COMMENT '数量',
  `total_amount` DECIMAL(15,2) COMMENT '总金额',
  `priority` INT DEFAULT 2 COMMENT '优先级：1-低，2-普通，3-高，4-紧急',
  `status` INT DEFAULT 0 COMMENT '状态：0-待处理，1-已确认，2-生产中，3-已完成，4-已取消',
  `delivery_date` DATETIME COMMENT '交付日期',
  `production_plan_id` VARCHAR(50) COMMENT '生产计划ID',
  `workshop_code` VARCHAR(20) COMMENT '车间编号',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` VARCHAR(50) COMMENT '创建人',
  `update_by` VARCHAR(50) COMMENT '更新人',
  `deleted` INT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  `remark` VARCHAR(500) COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_status` (`status`),
  KEY `idx_priority` (`priority`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 插入测试数据
INSERT INTO `t_order` (`order_no`, `customer_name`, `customer_type`, `vehicle_model`, `quantity`, `total_amount`, `priority`, `status`, `delivery_date`, `create_by`) VALUES
('ORD202501010001', '张三', 'INDIVIDUAL', '特斯拉Model3', 1, 299999.00, 2, 1, '2025-02-01 00:00:00', 'system'),
('ORD202501010002', '李四汽车经销商', 'DEALER', '比亚迪汉EV', 10, 2800000.00, 3, 1, '2025-01-25 00:00:00', 'system'),
('ORD202501010003', '王五', 'INDIVIDUAL', '蔚来ET7', 1, 458000.00, 2, 0, '2025-02-15 00:00:00', 'system'),
('ORD202501010004', '赵六汽车城', 'DEALER', '小鹏P7', 5, 1750000.00, 4, 1, '2025-01-20 00:00:00', 'system');

-- =====================================================
-- 3. 生产计划服务数据库 (smart_manufact_production)
-- =====================================================

USE smart_manufact_production;

DROP TABLE IF EXISTS `t_production_plan`;
CREATE TABLE `t_production_plan` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `plan_id` VARCHAR(50) NOT NULL COMMENT '计划ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
  `vehicle_model` VARCHAR(50) NOT NULL COMMENT '车型',
  `quantity` INT NOT NULL COMMENT '生产数量',
  `priority` INT DEFAULT 2 COMMENT '优先级',
  `status` INT DEFAULT 1 COMMENT '状态：1-待开始，2-进行中，3-已完成，4-已暂停',
  `workshop_code` VARCHAR(20) COMMENT '车间编号',
  `plan_start_time` DATETIME COMMENT '计划开始时间',
  `plan_end_time` DATETIME COMMENT '计划结束时间',
  `actual_start_time` DATETIME COMMENT '实际开始时间',
  `actual_end_time` DATETIME COMMENT '实际结束时间',
  `completed_quantity` INT DEFAULT 0 COMMENT '已完成数量',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` VARCHAR(50) COMMENT '创建人',
  `update_by` VARCHAR(50) COMMENT '更新人',
  `deleted` INT DEFAULT 0 COMMENT '删除标记',
  `remark` VARCHAR(500) COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_plan_id` (`plan_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_status` (`status`),
  KEY `idx_workshop` (`workshop_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产计划表';

-- 插入测试数据
INSERT INTO `t_production_plan` (`plan_id`, `order_id`, `order_no`, `vehicle_model`, `quantity`, `priority`, `status`, `workshop_code`, `plan_start_time`, `plan_end_time`, `completed_quantity`, `create_by`) VALUES
('PLAN-20250101001', 1, 'ORD202501010001', '特斯拉Model3', 1, 2, 2, 'WORKSHOP-01', '2025-01-15 08:00:00', '2025-01-22 18:00:00', 0, 'system'),
('PLAN-20250101002', 2, 'ORD202501010002', '比亚迪汉EV', 10, 3, 1, 'WORKSHOP-01', '2025-01-16 08:00:00', '2025-01-24 18:00:00', 0, 'system');

-- =====================================================
-- 4. 设备监控服务数据库 (smart_manufact_equipment)
-- =====================================================

USE smart_manufact_equipment;

DROP TABLE IF EXISTS `t_equipment`;
CREATE TABLE `t_equipment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `equipment_code` VARCHAR(50) NOT NULL COMMENT '设备编号',
  `equipment_name` VARCHAR(100) NOT NULL COMMENT '设备名称',
  `equipment_type` VARCHAR(50) COMMENT '设备类型：STAMPING-冲压机，WELDING-焊接机器人，PAINTING-涂装设备，ASSEMBLY-装配线',
  `workshop_code` VARCHAR(20) COMMENT '车间编号',
  `status` INT DEFAULT 2 COMMENT '状态：1-运行中，2-空闲，3-维护中，4-故障，5-离线',
  `oee` DECIMAL(5,2) COMMENT 'OEE设备综合效率(%)',
  `running_hours` INT DEFAULT 0 COMMENT '运行小时数',
  `last_maintenance_time` DATETIME COMMENT '上次维护时间',
  `next_maintenance_time` DATETIME COMMENT '下次维护时间',
  `current_task` VARCHAR(100) COMMENT '当前任务',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` VARCHAR(50) COMMENT '创建人',
  `update_by` VARCHAR(50) COMMENT '更新人',
  `deleted` INT DEFAULT 0 COMMENT '删除标记',
  `remark` VARCHAR(500) COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_equipment_code` (`equipment_code`),
  KEY `idx_workshop` (`workshop_code`),
  KEY `idx_status` (`status`),
  KEY `idx_type` (`equipment_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备表';

-- 插入测试数据
INSERT INTO `t_equipment` (`equipment_code`, `equipment_name`, `equipment_type`, `workshop_code`, `status`, `oee`, `running_hours`, `last_maintenance_time`, `next_maintenance_time`, `create_by`) VALUES
('EQ-STAMP-001', '冲压机1号', 'STAMPING', 'WORKSHOP-01', 2, 85.50, 1200, '2024-12-01 10:00:00', '2025-03-01 10:00:00', 'system'),
('EQ-STAMP-002', '冲压机2号', 'STAMPING', 'WORKSHOP-01', 2, 88.20, 1100, '2024-12-05 10:00:00', '2025-03-05 10:00:00', 'system'),
('EQ-WELD-001', '焊接机器人1号', 'WELDING', 'WORKSHOP-01', 2, 90.20, 1500, '2024-11-20 10:00:00', '2025-02-20 10:00:00', 'system'),
('EQ-WELD-002', '焊接机器人2号', 'WELDING', 'WORKSHOP-01', 1, 92.50, 1450, '2024-11-25 10:00:00', '2025-02-25 10:00:00', 'system'),
('EQ-PAINT-001', '涂装设备1号', 'PAINTING', 'WORKSHOP-01', 2, 88.00, 1000, '2024-12-10 10:00:00', '2025-03-10 10:00:00', 'system'),
('EQ-PAINT-002', '涂装设备2号', 'PAINTING', 'WORKSHOP-01', 2, 86.30, 980, '2024-12-12 10:00:00', '2025-03-12 10:00:00', 'system'),
('EQ-ASSY-001', '装配线1号', 'ASSEMBLY', 'WORKSHOP-01', 2, 92.30, 1800, '2024-11-15 10:00:00', '2025-02-15 10:00:00', 'system'),
('EQ-ASSY-002', '装配线2号', 'ASSEMBLY', 'WORKSHOP-01', 2, 91.80, 1750, '2024-11-18 10:00:00', '2025-02-18 10:00:00', 'system');

-- =====================================================
-- 5. 库存管理服务数据库 (smart_manufact_inventory)
-- =====================================================

USE smart_manufact_inventory;

DROP TABLE IF EXISTS `t_inventory`;
CREATE TABLE `t_inventory` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `material_code` VARCHAR(50) NOT NULL COMMENT '物料编号',
  `material_name` VARCHAR(100) NOT NULL COMMENT '物料名称',
  `material_type` VARCHAR(50) COMMENT '物料类型：RAW_MATERIAL-原材料，SEMI_FINISHED-半成品，FINISHED-成品',
  `unit` VARCHAR(20) COMMENT '单位：KG-千克，PCS-件，SET-套',
  `quantity` INT DEFAULT 0 COMMENT '库存数量',
  `safety_stock` INT DEFAULT 0 COMMENT '安全库存',
  `reorder_point` INT DEFAULT 0 COMMENT '再订货点',
  `warehouse_location` VARCHAR(50) COMMENT '仓库位置',
  `supplier_code` VARCHAR(50) COMMENT '供应商编号',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` VARCHAR(50) COMMENT '创建人',
  `update_by` VARCHAR(50) COMMENT '更新人',
  `deleted` INT DEFAULT 0 COMMENT '删除标记',
  `remark` VARCHAR(500) COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_material_code` (`material_code`),
  KEY `idx_material_type` (`material_type`),
  KEY `idx_quantity` (`quantity`),
  KEY `idx_warehouse` (`warehouse_location`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- 插入测试数据
INSERT INTO `t_inventory` (`material_code`, `material_name`, `material_type`, `unit`, `quantity`, `safety_stock`, `reorder_point`, `warehouse_location`, `supplier_code`, `create_by`) VALUES
-- 原材料
('MAT-STEEL-001', '钢板-Q235', 'RAW_MATERIAL', 'KG', 50000, 10000, 15000, 'A区-01', 'SUP-001', 'system'),
('MAT-STEEL-002', '铝合金板材', 'RAW_MATERIAL', 'KG', 30000, 8000, 12000, 'A区-02', 'SUP-002', 'system'),
('MAT-PART-001', '车门零部件', 'RAW_MATERIAL', 'PCS', 5000, 1000, 1500, 'B区-01', 'SUP-003', 'system'),
('MAT-PART-002', '发动机零部件', 'RAW_MATERIAL', 'PCS', 3000, 500, 800, 'B区-02', 'SUP-004', 'system'),
('MAT-PART-003', '底盘零部件', 'RAW_MATERIAL', 'PCS', 4000, 800, 1200, 'B区-03', 'SUP-005', 'system'),
('MAT-ELEC-001', '电池组', 'RAW_MATERIAL', 'SET', 500, 100, 150, 'B区-04', 'SUP-006', 'system'),
('MAT-ELEC-002', '电机', 'RAW_MATERIAL', 'PCS', 800, 150, 200, 'B区-05', 'SUP-007', 'system'),
-- 半成品
('MAT-SEMI-001', '车身半成品', 'SEMI_FINISHED', 'PCS', 500, 100, 150, 'C区-01', NULL, 'system'),
('MAT-SEMI-002', '底盘半成品', 'SEMI_FINISHED', 'PCS', 450, 90, 130, 'C区-02', NULL, 'system'),
('MAT-SEMI-003', '内饰半成品', 'SEMI_FINISHED', 'SET', 600, 120, 180, 'C区-03', NULL, 'system'),
-- 成品
('MAT-FINISH-001', '特斯拉Model3成品', 'FINISHED', 'PCS', 50, 10, 20, 'D区-01', NULL, 'system'),
('MAT-FINISH-002', '比亚迪汉EV成品', 'FINISHED', 'PCS', 80, 15, 25, 'D区-02', NULL, 'system'),
('MAT-FINISH-003', '蔚来ET7成品', 'FINISHED', 'PCS', 30, 8, 15, 'D区-03', NULL, 'system'),
('MAT-FINISH-004', '小鹏P7成品', 'FINISHED', 'PCS', 60, 12, 20, 'D区-04', NULL, 'system');

-- =====================================================
-- 6. 质量管理服务数据库 (smart_manufact_quality)
-- =====================================================

USE smart_manufact_quality;

DROP TABLE IF EXISTS `t_quality_inspection`;
CREATE TABLE `t_quality_inspection` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `inspection_no` VARCHAR(50) NOT NULL COMMENT '检验单号',
  `order_id` BIGINT COMMENT '订单ID',
  `order_no` VARCHAR(50) COMMENT '订单号',
  `production_plan_id` VARCHAR(50) COMMENT '生产计划ID',
  `inspection_type` VARCHAR(50) COMMENT '检验类型：STAMPING-冲压件检验，WELDING-焊接质量，PAINTING-涂装厚度，ROAD_TEST-整车路试',
  `inspection_stage` VARCHAR(50) COMMENT '检验阶段：IQC-来料，IPQC-过程，FQC-成品，OQC-出货',
  `status` INT DEFAULT 1 COMMENT '状态：1-待检验，2-合格，3-不合格',
  `inspector_name` VARCHAR(50) COMMENT '检验员',
  `inspection_time` DATETIME COMMENT '检验时间',
  `defect_description` TEXT COMMENT '缺陷描述',
  `defect_level` VARCHAR(20) COMMENT '缺陷等级：LOW-低，MEDIUM-中，HIGH-高，CRITICAL-严重',
  `quality_score` DECIMAL(5,2) COMMENT '质量评分(0-100)',
  `correction_action` VARCHAR(500) COMMENT '纠正措施',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` VARCHAR(50) COMMENT '创建人',
  `update_by` VARCHAR(50) COMMENT '更新人',
  `deleted` INT DEFAULT 0 COMMENT '删除标记',
  `remark` VARCHAR(500) COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_inspection_no` (`inspection_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_status` (`status`),
  KEY `idx_type` (`inspection_type`),
  KEY `idx_stage` (`inspection_stage`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质量检验表';

-- 插入测试数据
INSERT INTO `t_quality_inspection` (`inspection_no`, `order_id`, `order_no`, `production_plan_id`, `inspection_type`, `inspection_stage`, `status`, `inspector_name`, `inspection_time`, `quality_score`, `create_by`) VALUES
('QI20250101001', 1, 'ORD202501010001', 'PLAN-20250101001', 'STAMPING', 'IPQC', 2, '质检员-张三', '2025-01-15 10:30:00', 95.50, 'system'),
('QI20250101002', 1, 'ORD202501010001', 'PLAN-20250101001', 'WELDING', 'IPQC', 2, '质检员-李四', '2025-01-16 14:20:00', 92.80, 'system'),
('QI20250101003', 1, 'ORD202501010001', 'PLAN-20250101001', 'PAINTING', 'IPQC', 2, '质检员-王五', '2025-01-18 09:15:00', 94.20, 'system'),
('QI20250101004', 2, 'ORD202501010002', 'PLAN-20250101002', 'STAMPING', 'IPQC', 2, '质检员-张三', '2025-01-16 11:00:00', 96.30, 'system'),
('QI20250101005', 2, 'ORD202501010002', 'PLAN-20250101002', 'WELDING', 'IPQC', 1, '质检员-李四', '2025-01-17 15:30:00', NULL, 'system');

-- =====================================================
-- 初始化完成
-- =====================================================

SELECT '数据库初始化完成！' AS message;
SELECT 'Database initialization completed!' AS message;
