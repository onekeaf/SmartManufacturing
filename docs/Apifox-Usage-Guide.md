# Apifox 测试集合使用指南

## 📋 概述

本文档说明如何使用 `SmartManufact-Apifox-Collection.json` (OpenAPI 3.0 格式) 测试集合来测试智能制造系统的所有 API 接口。

## 🚀 快速开始

### 1. 导入 Apifox 集合

#### 方式一：通过文件导入（推荐）

1. 打开 Apifox 应用
2. 点击左上角的 **导入** 按钮
3. 选择 **OpenAPI/Swagger** 格式
4. 点击 **选择文件**，选择 `docs/SmartManufact-Apifox-Collection.json`
5. 点击 **确认导入**

#### 方式二：通过 URL 导入

如果你的文件托管在服务器上，可以直接输入 URL 导入。

### 2. 配置环境

Apifox 会自动识别 OpenAPI 文件中的 servers 配置，你可以在接口调试时选择不同的服务器：

| 服务器 | 地址 | 说明 |
|--------|------|------|
| 网关服务 | `http://localhost:8080` | **推荐使用**，统一网关入口 |
| 订单服务 | `http://localhost:8081` | 订单服务直连（调试用） |
| 生产计划服务 | `http://localhost:8082` | 生产计划服务直连（调试用） |
| 设备监控服务 | `http://localhost:8083` | 设备监控服务直连（调试用） |
| 库存管理服务 | `http://localhost:8084` | 库存管理服务直连（调试用） |
| 质量管理服务 | `http://localhost:8085` | 质量管理服务直连（调试用） |

**修改服务器地址：**
1. 点击项目设置
2. 选择 **环境管理**
3. 添加或修改环境变量

### 3. 创建环境（可选）

为了方便在不同环境间切换，建议创建以下环境：

#### 开发环境
```
base_url: http://localhost:8080
```

#### 测试环境
```
base_url: http://test-server:8080
```

#### 生产环境
```
base_url: https://api.smartmanufact.com
```

## 📦 API 接口说明

### 1. 订单服务 (6个接口)

#### 1.1 创建订单
- **接口**: `POST /api/order/create`
- **说明**: 创建新的订单
- **请求示例**:
```json
{
  "customerName": "测试客户",
  "customerType": "INDIVIDUAL",
  "vehicleModel": "特斯拉Model3",
  "quantity": 1,
  "totalAmount": 299999.00,
  "priority": 2,
  "deliveryDate": "2025-03-01 00:00:00",
  "remark": "测试订单"
}
```

#### 1.2 根据ID查询订单
- **接口**: `GET /api/order/{id}`
- **参数**: id (路径参数) - 订单ID

#### 1.3 查询订单列表
- **接口**: `GET /api/order/list`
- **参数**: 
  - page: 页码 (默认1)
  - size: 每页数量 (默认10)
  - status: 订单状态 (可选)

#### 1.4 更新订单状态
- **接口**: `PUT /api/order/{id}/status`
- **参数**: 
  - id: 订单ID
  - status: 订单状态 (0-待处理，1-已确认，2-生产中，3-已完成，4-已取消)

#### 1.5 取消订单
- **接口**: `DELETE /api/order/{id}`
- **参数**: id - 订单ID

#### 1.6 触发生产
- **接口**: `POST /api/order/{id}/trigger-production`
- **参数**: id - 订单ID

### 2. 生产计划服务 (3个接口)

#### 2.1 创建生产计划
- **接口**: `POST /api/production/plan/create`
- **请求示例**:
```json
{
  "orderId": 1,
  "orderNo": "ORD202501010001",
  "vehicleModel": "特斯拉Model3",
  "quantity": 1,
  "priority": 2,
  "workshopCode": "WORKSHOP-01",
  "planStartTime": "2025-01-20 08:00:00",
  "planEndTime": "2025-01-27 18:00:00"
}
```

#### 2.2 根据计划ID查询
- **接口**: `GET /api/production/plan/{planId}`
- **参数**: planId - 生产计划ID

#### 2.3 更新计划状态
- **接口**: `PUT /api/production/plan/{planId}/status`
- **参数**: 
  - planId: 生产计划ID
  - status: 计划状态 (1-待开始，2-进行中，3-已完成，4-已暂停)

### 3. 设备监控服务 (6个接口)

#### 3.1 根据ID查询设备
- **接口**: `GET /api/equipment/{id}`
- **参数**: id - 设备ID

#### 3.2 根据设备编号查询
- **接口**: `GET /api/equipment/code/{equipmentCode}`
- **参数**: equipmentCode - 设备编号
- **说明**: 带 Sentinel 限流保护

#### 3.3 查询设备列表
- **接口**: `GET /api/equipment/list`
- **参数**: 
  - workshopCode: 车间编号 (可选)
  - status: 设备状态 (可选)

#### 3.4 检查设备可用性
- **接口**: `GET /api/equipment/check-availability`
- **参数**: workshopCode - 车间编号

#### 3.5 更新设备状态
- **接口**: `PUT /api/equipment/{equipmentCode}/status`
- **参数**: 
  - equipmentCode: 设备编号
  - status: 设备状态 (1-运行中，2-空闲，3-维护中，4-故障，5-离线)

#### 3.6 获取设备统计信息
- **接口**: `GET /api/equipment/statistics`
- **参数**: workshopCode - 车间编号 (可选)

### 4. 库存管理服务 (6个接口)

#### 4.1 根据ID查询库存
- **接口**: `GET /api/inventory/{id}`
- **参数**: id - 库存ID

#### 4.2 根据物料编号查询
- **接口**: `GET /api/inventory/material/{materialCode}`
- **参数**: materialCode - 物料编号

#### 4.3 查询库存列表
- **接口**: `GET /api/inventory/list`
- **参数**: materialType - 物料类型 (RAW_MATERIAL/SEMI_FINISHED/FINISHED)

#### 4.4 检查物料可用性
- **接口**: `GET /api/inventory/check-availability`
- **参数**: 
  - vehicleModel: 车型
  - quantity: 数量

#### 4.5 更新库存数量
- **接口**: `PUT /api/inventory/{materialCode}/quantity`
- **参数**: 
  - materialCode: 物料编号
  - quantity: 库存数量

#### 4.6 查询低库存物料
- **接口**: `GET /api/inventory/low-stock`
- **说明**: 查询低于安全库存的物料列表

### 5. 质量管理服务 (6个接口)

#### 5.1 创建质检记录
- **接口**: `POST /api/quality/inspection/create`
- **请求示例**:
```json
{
  "orderId": 1,
  "orderNo": "ORD202501010001",
  "productionPlanId": "PLAN-20250101001",
  "inspectionType": "STAMPING",
  "inspectionStage": "IPQC",
  "inspectorName": "质检员-测试",
  "qualityScore": 95.5,
  "remark": "测试质检记录"
}
```

#### 5.2 根据ID查询质检记录
- **接口**: `GET /api/quality/inspection/{id}`
- **参数**: id - 质检记录ID

#### 5.3 根据检验单号查询
- **接口**: `GET /api/quality/inspection/no/{inspectionNo}`
- **参数**: inspectionNo - 检验单号

#### 5.4 查询质检记录列表
- **接口**: `GET /api/quality/inspection/list`
- **参数**: 
  - orderId: 订单ID (可选)
  - inspectionType: 检验类型 (可选)

#### 5.5 更新质检状态
- **接口**: `PUT /api/quality/inspection/{inspectionNo}/status`
- **参数**: 
  - inspectionNo: 检验单号
  - status: 质检状态 (1-待检验，2-合格，3-不合格)

#### 5.6 获取质量统计信息
- **接口**: `GET /api/quality/inspection/statistics`
- **参数**: 
  - startDate: 开始日期 (可选)
  - endDate: 结束日期 (可选)

## 🎯 完整业务流程测试

### 场景一：订单到生产流程

```
1. 创建订单
   POST /api/order/create
   
2. 查询订单详情
   GET /api/order/1
   
3. 更新订单状态为已确认
   PUT /api/order/1/status?status=1
   
4. 触发生产计划
   POST /api/order/1/trigger-production
   
5. 查询生产计划
   GET /api/production/plan/PLAN-20250101001
   
6. 更新生产计划状态为进行中
   PUT /api/production/plan/PLAN-20250101001/status?status=2
```

### 场景二：设备和库存检查

```
1. 检查设备可用性
   GET /api/equipment/check-availability?workshopCode=WORKSHOP-01
   
2. 查询设备列表
   GET /api/equipment/list?workshopCode=WORKSHOP-01&status=2
   
3. 检查物料可用性
   GET /api/inventory/check-availability?vehicleModel=特斯拉Model3&quantity=1
   
4. 查询低库存物料
   GET /api/inventory/low-stock
```

### 场景三：质量检验流程

```
1. 创建质检记录
   POST /api/quality/inspection/create
   
2. 查询质检记录
   GET /api/quality/inspection/1
   
3. 更新质检状态为合格
   PUT /api/quality/inspection/QI20250101001/status?status=2
   
4. 获取质量统计
   GET /api/quality/inspection/statistics?startDate=2025-01-01&endDate=2025-12-31
```

## 📊 统一响应格式

所有接口返回统一的 JSON 格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
  },
  "timestamp": "2025-01-17T15:30:00"
}
```

**字段说明：**
- `code`: 状态码（200表示成功）
- `message`: 提示信息
- `data`: 返回的业务数据
- `timestamp`: 响应时间戳

## 🔧 Apifox 特色功能

### 1. 自动化测试

Apifox 支持为每个接口编写测试脚本：

```javascript
// 示例：验证响应状态码
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

// 示例：验证响应数据
pm.test("Response has data", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.code).to.eql(200);
    pm.expect(jsonData.data).to.not.be.null;
});
```

### 2. Mock 服务

Apifox 可以根据 OpenAPI 规范自动生成 Mock 数据，方便前端开发：

1. 点击接口右上角的 **Mock** 按钮
2. 启用 Mock 服务
3. 使用 Mock URL 进行测试

### 3. 接口文档

Apifox 会自动生成美观的接口文档：

1. 点击 **文档** 标签
2. 可以在线查看或导出为 HTML/Markdown
3. 支持分享给团队成员

### 4. 测试场景

创建测试场景，批量执行接口：

1. 点击 **测试场景**
2. 创建新场景
3. 添加接口到场景中
4. 配置接口间的数据传递
5. 运行场景测试

### 5. 环境变量

使用环境变量管理不同环境的配置：

```
// 在前置脚本中设置变量
pm.environment.set("orderId", jsonData.data.id);

// 在后续请求中使用
{{orderId}}
```

## 📝 数据字典

### 订单状态 (status)
- `0`: 待处理
- `1`: 已确认
- `2`: 生产中
- `3`: 已完成
- `4`: 已取消

### 客户类型 (customerType)
- `INDIVIDUAL`: 个人客户
- `DEALER`: 经销商

### 优先级 (priority)
- `1`: 低
- `2`: 普通
- `3`: 高
- `4`: 紧急

### 生产计划状态
- `1`: 待开始
- `2`: 进行中
- `3`: 已完成
- `4`: 已暂停

### 设备状态
- `1`: 运行中
- `2`: 空闲
- `3`: 维护中
- `4`: 故障
- `5`: 离线

### 设备类型 (equipmentType)
- `STAMPING`: 冲压机
- `WELDING`: 焊接机器人
- `PAINTING`: 涂装设备
- `ASSEMBLY`: 装配线

### 物料类型 (materialType)
- `RAW_MATERIAL`: 原材料
- `SEMI_FINISHED`: 半成品
- `FINISHED`: 成品

### 检验类型 (inspectionType)
- `STAMPING`: 冲压件检验
- `WELDING`: 焊接质量
- `PAINTING`: 涂装厚度
- `ROAD_TEST`: 整车路试

### 检验阶段 (inspectionStage)
- `IQC`: 来料检验
- `IPQC`: 过程检验
- `FQC`: 成品检验
- `OQC`: 出货检验

### 质检状态
- `1`: 待检验
- `2`: 合格
- `3`: 不合格

## 🐛 常见问题

### 1. 导入失败

**问题**: 导入 JSON 文件时提示格式错误

**解决方案**:
- 确认文件是 OpenAPI 3.0 格式
- 检查 JSON 格式是否正确
- 尝试使用 Apifox 的 **智能识别** 功能

### 2. 接口调用失败

**问题**: 接口返回 404 或连接超时

**解决方案**:
- 检查服务是否启动
- 确认端口号是否正确
- 检查网关路由配置
- 确认服务已注册到 Nacos

### 3. 参数错误

**问题**: 接口返回参数校验失败

**解决方案**:
- 查看接口文档中的参数说明
- 确认必填参数都已提供
- 检查参数类型是否正确
- 查看枚举值是否在允许范围内

### 4. 认证失败

**问题**: 接口返回 401 未授权

**解决方案**:
- 检查是否需要登录
- 确认 Token 是否过期
- 在请求头中添加认证信息

## 💡 最佳实践

### 1. 使用环境变量

将常用的值（如 baseUrl、token）设置为环境变量，方便切换环境。

### 2. 编写测试脚本

为关键接口编写自动化测试脚本，确保接口稳定性。

### 3. 使用测试场景

将相关的接口组织成测试场景，模拟真实业务流程。

### 4. 定期更新文档

当接口发生变化时，及时更新 OpenAPI 文档并重新导入。

### 5. 团队协作

使用 Apifox 的团队功能，与团队成员共享接口文档和测试用例。

## 📚 相关资源

- [Apifox 官方文档](https://www.apifox.cn/help/)
- [OpenAPI 规范](https://swagger.io/specification/)
- 项目数据库初始化脚本: `docs/database/init.sql`
- 项目部署文档: `docs/DEPLOYMENT.md`

## 📞 技术支持

如有问题，请联系开发团队或查看项目文档。

---

**版本**: v1.0.0  
**更新日期**: 2025-01-17  
**维护者**: 智能制造系统开发团队
