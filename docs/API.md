# API 接口文档

---

## 接口规范

### 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1703001234567
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 访问地址

所有 API 通过网关访问：`http://localhost:8080/api/{service}/{endpoint}`

---

## 订单管理服务 API

### 1. 创建订单

**接口地址：** `POST /api/order/create`

**请求参数：**

```json
{
  "customerName": "张三",
  "customerType": "INDIVIDUAL",
  "vehicleModel": "特斯拉Model3",
  "quantity": 1,
  "totalAmount": 299999.00,
  "priority": 2,
  "deliveryDate": "2025-02-01T00:00:00",
  "remark": "定制化需求"
}
```

**参数说明：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| customerName | String | 是 | 客户名称 |
| customerType | String | 是 | 客户类型：INDIVIDUAL-个人，DEALER-经销商，CUSTOM-定制 |
| vehicleModel | String | 是 | 车型 |
| quantity | Integer | 是 | 数量 |
| totalAmount | Decimal | 否 | 总金额 |
| priority | Integer | 否 | 优先级：1-低，2-普通，3-高，4-紧急 |
| deliveryDate | DateTime | 否 | 交付日期 |
| remark | String | 否 | 备注 |

**响应示例：**
```json
{
  "code": 200,
  "message": "订单创建成功",
  "data": {
    "id": 1,
    "orderNo": "ORD202501010001",
    "customerName": "张三",
    "vehicleModel": "特斯拉Model3",
    "quantity": 1,
    "status": 0,
    "statusDesc": "待处理",
    "createTime": "2025-01-01T10:00:00"
  }
}
```

![image-20251220112707768](D:\JAVA\Codes\SmartManufacturing\docs\API.assets\image-20251220112707768.png)

---



### 2. 查询订单详情

**接口地址：** `GET /api/order/{id}`

**路径参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 订单ID |

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "orderNo": "ORD202501010001",
    "customerName": "张三",
    "customerType": "INDIVIDUAL",
    "vehicleModel": "特斯拉Model3",
    "quantity": 1,
    "totalAmount": 299999.00,
    "priority": 2,
    "priorityDesc": "普通",
    "status": 1,
    "statusDesc": "已确认",
    "deliveryDate": "2025-02-01T00:00:00",
    "productionPlanId": "PLAN-20250101001",
    "createTime": "2025-01-01T10:00:00",
    "updateTime": "2025-01-01T10:30:00"
  }
}
```

![image-20251220112807880](D:\JAVA\Codes\SmartManufacturing\docs\API.assets\image-20251220112807880.png)

---



### 3. 订单列表查询

**接口地址：** `GET /api/order/list`

**请求参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Integer | 否 | 页码，默认1 |
| size | Integer | 否 | 每页数量，默认10 |
| status | Integer | 否 | 订单状态筛选 |

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "orderNo": "ORD202501010001",
        "customerName": "张三",
        "vehicleModel": "特斯拉Model3",
        "quantity": 1,
        "status": 1,
        "statusDesc": "已确认"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

![image-20251220113843993](D:\JAVA\Codes\SmartManufacturing\docs\API.assets\image-20251220113843993.png)

---



### 4. 更新订单状态

**接口地址：** `PUT /api/order/{id}/status`

**路径参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 订单ID |

**请求参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | Integer | 是 | 状态：0-待处理，1-已确认，2-生产中，3-已完成，4-已取消 |

**响应示例：**
```json
{
  "code": 200,
  "message": "订单状态更新成功",
  "data": {
    "id": 1,
    "orderNo": "ORD202501010001",
    "status": 1,
    "statusDesc": "已确认"
  }
}
```

![image-20251220114028422](D:\JAVA\Codes\SmartManufacturing\docs\API.assets\image-20251220114028422.png)

---



### 5. 取消订单

**接口地址：** `DELETE /api/order/{id}`

**路径参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 订单ID |

**响应示例：**

```json
{
  "code": 200,
  "message": "订单取消成功"
}
```

![image-20251220114619333](D:\JAVA\Codes\SmartManufacturing\docs\API.assets\image-20251220114619333.png)

---

### 6. 触发生产

**接口地址：** `POST /api/order/{id}/trigger-production`

**路径参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 订单ID |

**响应示例：**
```json
{
  "code": 200,
  "message": "生产计划已创建",
  "data": {
    "id": 1,
    "orderNo": "ORD202501010001",
    "productionPlanId": "PLAN-20250101001",
    "status": 2,
    "statusDesc": "生产中"
  }
}


```

---

![image-20251220114142439](D:\JAVA\Codes\SmartManufacturing\docs\API.assets\image-20251220114142439.png)

## 生产计划服务 API

### 1. 创建生产计划

**接口地址：** `POST /api/production/plan/create`

**请求参数：**
```json
{
  "orderId": 1,
  "orderNo": "ORD202501010001",
  "vehicleModel": "特斯拉Model3",
  "quantity": 1,
  "priority": 2,
  "deliveryDate": "2025-02-01T00:00:00"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "生产计划创建成功",
  "data": {
    "planId": "PLAN-20250101001",
    "status": "success",
    "message": "生产计划创建成功"
  }
}
```

![image-20251220165553778](D:\JAVA\Codes\SmartManufacturing\docs\API.assets\image-20251220165553778.png)

---

### 2. 查询生产计划

**接口地址：** `GET /api/production/plan/{planId}`

**路径参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| planId | String | 是 | 生产计划ID |

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "plan": {
      "id": 1,
      "planId": "PLAN-20250101001",
      "orderNo": "ORD202501010001",
      "vehicleModel": "特斯拉Model3",
      "quantity": 1,
      "status": 2,
      "workshopCode": "WORKSHOP-01",
      "planStartTime": "2025-01-15T08:00:00",
      "planEndTime": "2025-01-22T18:00:00",
      "completedQuantity": 0
    }
  }
}
```

![image-20251220165640519](D:\JAVA\Codes\SmartManufacturing\docs\API.assets\image-20251220165640519.png)

---

### 3. 更新计划状态

**接口地址：** `PUT /api/production/plan/{planId}/status`

**请求参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | Integer | 是 | 状态：1-待开始，2-进行中，3-已完成，4-已暂停 |

![image-20251220165912756](D:\JAVA\Codes\SmartManufacturing\docs\API.assets\image-20251220165912756.png)

---

## 设备监控服务 API

### 1. 设备列表

**接口地址：** `GET /api/equipment/list`

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "equipmentCode": "EQ-STAMP-001",
      "equipmentName": "冲压机1号",
      "equipmentType": "STAMPING",
      "workshopCode": "WORKSHOP-01",
      "status": 2,
      "statusDesc": "空闲",
      "oee": 85.50
    }
  ]
}
```

![image-20251220165951880](D:\JAVA\Codes\SmartManufacturing\docs\API.assets\image-20251220165951880.png)

---

### 2. 查询设备详情

**接口地址：** `GET /api/equipment/{id}`

**路径参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 设备ID |

![image-20251220170014210](D:\JAVA\Codes\SmartManufacturing\docs\API.assets\image-20251220170014210.png)

---

### 3. 获取设备统计信息

**接口地址：** `GET /api/equipment/statistics`

![image-20251220170249715](D:\JAVA\Codes\SmartManufacturing\docs\API.assets\image-20251220170249715.png)

---

### 4. 检查设备可用性

**接口地址：** `POST /api/equipment/check-availability`

**请求参数：**
```json
{
  "workshopCode": "WORKSHOP-01"
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "available": true,
    "availableCount": 8,
    "totalCount": 8
  }
}
```

![image-20251220170150068](D:\JAVA\Codes\SmartManufacturing\docs\API.assets\image-20251220170150068.png)

---

## 库存管理服务 API

### 1. 库存列表

**接口地址：** `GET /api/inventory/list`

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "materialCode": "MAT-STEEL-001",
      "materialName": "钢板-Q235",
      "materialType": "RAW_MATERIAL",
      "quantity": 50000,
      "safetyStock": 10000,
      "unit": "KG"
    }
  ]
}
```

![image-20251220170310950](D:\JAVA\Codes\SmartManufacturing\docs\API.assets\image-20251220170310950.png)

---

### 2. 查询库存详情

**接口地址：** `GET /api/inventory/{id}`![image-20251220170357362](D:\JAVA\Codes\SmartManufacturing\docs\API.assets\image-20251220170357362.png)

---

### 3. 检查物料可用性

**接口地址：** `POST /api/inventory/check-availability`

**请求参数：**

```json
{
  "vehicleModel": "特斯拉Model3",
  "quantity": 1
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "available": true,
    "message": "物料充足"
  }
}
```

![image-20251220170500671](D:\JAVA\Codes\SmartManufacturing\docs\API.assets\image-20251220170500671.png)

---

## 质量管理服务 API

### 1. 质量检验列表

**接口地址：** `GET /api/quality/inspection/list`

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "inspectionNo": "QI20250101001",
      "orderNo": "ORD202501010001",
      "inspectionType": "STAMPING",
      "inspectionStage": "IPQC",
      "status": 2,
      "statusDesc": "合格",
      "qualityScore": 95.50,
      "inspectorName": "质检员-张三",
      "inspectionTime": "2025-01-15T10:30:00"
    }
  ]
}
```

![image-20251220170521249](D:\JAVA\Codes\SmartManufacturing\docs\API.assets\image-20251220170521249.png)

---

### 2. 查询检验详情

**接口地址：** `GET /api/quality/inspection/{id}`

![image-20251220170550583](D:\JAVA\Codes\SmartManufacturing\docs\API.assets\image-20251220170550583.png)

---

### 3. 创建检验记录

**接口地址：** `POST /api/quality/inspection/create`

**请求参数：**
```json
{
  "orderId": 1,
  "orderNo": "ORD202501010001",
  "productionPlanId": "PLAN-20250101001",
  "inspectionType": "STAMPING",
  "inspectionStage": "IPQC",
  "inspectorName": "质检员-张三",
  "qualityScore": 95.50,
  "remark": "检验合格"
}
```

![image-20251220170610056](D:\JAVA\Codes\SmartManufacturing\docs\API.assets\image-20251220170610056.png)

---

## 错误码说明

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 1001 | 订单不存在 | 检查订单ID是否正确 |
| 1002 | 订单状态不正确 | 检查订单当前状态 |
| 2001 | 生产计划不存在 | 检查计划ID是否正确 |
| 2002 | 物料库存不足 | 补充库存后重试 |
| 2003 | 设备不可用 | 等待设备恢复后重试 |
| 3001 | 设备不存在 | 检查设备ID是否正确 |
| 4001 | 库存不足 | 补充库存 |
| 5001 | 质量检验记录不存在 | 检查记录ID |

## 

---

## 注意事项

1. 所有时间格式使用 ISO 8601 标准：`yyyy-MM-ddTHH:mm:ss`
2. 金额字段保留两位小数
3. 分页查询默认每页10条记录
4. 所有接口支持跨域访问

