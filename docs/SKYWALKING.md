# Apache SkyWalking 链路追踪集成指南

## 📋 目录
- [简介](#简介)
- [下载与安装](#下载与安装)
- [配置说明](#配置说明)
- [服务集成](#服务集成)
- [启动与验证](#启动与验证)
- [链路追踪演示](#链路追踪演示)

---

## 简介

Apache SkyWalking 是一个开源的应用性能监控（APM）系统，专为微服务、云原生和容器化架构设计。本项目使用 SkyWalking 实现分布式链路追踪，满足试题要求的服务调用链路拓扑展示。

### 核心功能
- **分布式链路追踪**：追踪请求在微服务间的完整调用链路
- **服务拓扑图**：自动生成服务间的调用关系拓扑图
- **性能指标监控**：监控服务响应时间、吞吐量、错误率等
- **告警功能**：支持自定义告警规则

---

## 下载与安装

### 1. 下载 SkyWalking

```bash
# 创建目录
mkdir -p ~/software/skywalking
cd ~/software/skywalking

# 下载 SkyWalking 9.5.0 (推荐版本)
wget https://archive.apache.org/dist/skywalking/9.5.0/apache-skywalking-apm-9.5.0.tar.gz

# 解压
tar -zxvf apache-skywalking-apm-9.5.0.tar.gz
cd apache-skywalking-apm-bin
```

### 2. 目录结构

```
apache-skywalking-apm-bin/
├── agent/                    # Java Agent 目录
│   ├── skywalking-agent.jar  # Agent JAR 文件
│   ├── config/               # Agent 配置
│   └── plugins/              # 插件目录
├── bin/                      # 启动脚本
│   ├── startup.sh            # Linux/Mac 启动脚本
│   └── startup.bat           # Windows 启动脚本
├── config/                   # OAP 服务配置
└── webapp/                   # UI 界面
```

---

## 配置说明

### 1. OAP 服务配置

编辑 `config/application.yml`（可选，默认配置即可使用）：

```yaml
cluster:
  standalone:  # 单机模式

storage:
  selector: ${SW_STORAGE:h2}  # 默认使用 H2 数据库（生产环境建议使用 MySQL/ES）
  
core:
  default:
    restHost: ${SW_CORE_REST_HOST:0.0.0.0}
    restPort: ${SW_CORE_REST_PORT:12800}
    gRPCHost: ${SW_CORE_GRPC_HOST:0.0.0.0}
    gRPCPort: ${SW_CORE_GRPC_PORT:11800}
```

### 2. Agent 配置

编辑 `agent/config/agent.config`：

```properties
# 服务名称（每个微服务需要不同的名称）
agent.service_name=${SW_AGENT_NAME:smart-manufact-service}

# OAP 服务地址
collector.backend_service=${SW_AGENT_COLLECTOR_BACKEND_SERVICES:127.0.0.1:11800}

# 采样率（生产环境建议调整）
agent.sample_n_per_3_secs=${SW_AGENT_SAMPLE:-1}
```

---

## 服务集成

### 方式一：启动时添加 Java Agent（推荐）

为每个微服务添加 JVM 参数：

```bash
# 订单服务 (8081)
java -javaagent:/path/to/skywalking/agent/skywalking-agent.jar \
     -Dskywalking.agent.service_name=smart-manufact-order \
     -Dskywalking.collector.backend_service=127.0.0.1:11800 \
     -jar smart-manufact-order/target/smart-manufact-order-1.0.0.jar

# 生产计划服务 (8082)
java -javaagent:/path/to/skywalking/agent/skywalking-agent.jar \
     -Dskywalking.agent.service_name=smart-manufact-production \
     -Dskywalking.collector.backend_service=127.0.0.1:11800 \
     -jar smart-manufact-production/target/smart-manufact-production-1.0.0.jar

# 设备监控服务 (8083)
java -javaagent:/path/to/skywalking/agent/skywalking-agent.jar \
     -Dskywalking.agent.service_name=smart-manufact-equipment \
     -Dskywalking.collector.backend_service=127.0.0.1:11800 \
     -jar smart-manufact-equipment/target/smart-manufact-equipment-1.0.0.jar

# 库存管理服务 (8084)
java -javaagent:/path/to/skywalking/agent/skywalking-agent.jar \
     -Dskywalking.agent.service_name=smart-manufact-inventory \
     -Dskywalking.collector.backend_service=127.0.0.1:11800 \
     -jar smart-manufact-inventory/target/smart-manufact-inventory-1.0.0.jar

# 质量管理服务 (8085)
java -javaagent:/path/to/skywalking/agent/skywalking-agent.jar \
     -Dskywalking.agent.service_name=smart-manufact-quality \
     -Dskywalking.collector.backend_service=127.0.0.1:11800 \
     -jar smart-manufact-quality/target/smart-manufact-quality-1.0.0.jar

# API 网关 (8080)
java -javaagent:/path/to/skywalking/agent/skywalking-agent.jar \
     -Dskywalking.agent.service_name=smart-manufact-gateway \
     -Dskywalking.collector.backend_service=127.0.0.1:11800 \
     -jar smart-manufact-gateway/target/smart-manufact-gateway-1.0.0.jar
```

### 方式二：在 IDE 中配置（开发环境）

**IntelliJ IDEA 配置：**

1. 打开 Run/Debug Configurations
2. 选择对应的 Application
3. 在 VM options 中添加：

```
-javaagent:/Users/jieming/software/skywalking/agent/skywalking-agent.jar
-Dskywalking.agent.service_name=smart-manufact-order
-Dskywalking.collector.backend_service=127.0.0.1:11800
```

### 方式三：使用启动脚本

创建 `start-with-skywalking.sh`：

```bash
#!/bin/bash

# SkyWalking Agent 路径
SKYWALKING_AGENT="/Users/jieming/software/skywalking/agent/skywalking-agent.jar"
SKYWALKING_COLLECTOR="127.0.0.1:11800"

# 项目根目录
PROJECT_ROOT="/Users/jieming/IdeaProjects/SmartManufact"

# 启动网关
echo "启动 API 网关..."
java -javaagent:${SKYWALKING_AGENT} \
     -Dskywalking.agent.service_name=smart-manufact-gateway \
     -Dskywalking.collector.backend_service=${SKYWALKING_COLLECTOR} \
     -jar ${PROJECT_ROOT}/smart-manufact-gateway/target/smart-manufact-gateway-1.0.0.jar &

sleep 5

# 启动订单服务
echo "启动订单服务..."
java -javaagent:${SKYWALKING_AGENT} \
     -Dskywalking.agent.service_name=smart-manufact-order \
     -Dskywalking.collector.backend_service=${SKYWALKING_COLLECTOR} \
     -jar ${PROJECT_ROOT}/smart-manufact-order/target/smart-manufact-order-1.0.0.jar &

sleep 3

# 启动生产计划服务
echo "启动生产计划服务..."
java -javaagent:${SKYWALKING_AGENT} \
     -Dskywalking.agent.service_name=smart-manufact-production \
     -Dskywalking.collector.backend_service=${SKYWALKING_COLLECTOR} \
     -jar ${PROJECT_ROOT}/smart-manufact-production/target/smart-manufact-production-1.0.0.jar &

sleep 3

# 启动设备监控服务
echo "启动设备监控服务..."
java -javaagent:${SKYWALKING_AGENT} \
     -Dskywalking.agent.service_name=smart-manufact-equipment \
     -Dskywalking.collector.backend_service=${SKYWALKING_COLLECTOR} \
     -jar ${PROJECT_ROOT}/smart-manufact-equipment/target/smart-manufact-equipment-1.0.0.jar &

sleep 3

# 启动库存管理服务
echo "启动库存管理服务..."
java -javaagent:${SKYWALKING_AGENT} \
     -Dskywalking.agent.service_name=smart-manufact-inventory \
     -Dskywalking.collector.backend_service=${SKYWALKING_COLLECTOR} \
     -jar ${PROJECT_ROOT}/smart-manufact-inventory/target/smart-manufact-inventory-1.0.0.jar &

sleep 3

# 启动质量管理服务
echo "启动质量管理服务..."
java -javaagent:${SKYWALKING_AGENT} \
     -Dskywalking.agent.service_name=smart-manufact-quality \
     -Dskywalking.collector.backend_service=${SKYWALKING_COLLECTOR} \
     -jar ${PROJECT_ROOT}/smart-manufact-quality/target/smart-manufact-quality-1.0.0.jar &

echo "所有服务启动完成！"
```

---

## 启动与验证

### 1. 启动 SkyWalking OAP 和 UI

```bash
cd ~/software/skywalking/apache-skywalking-apm-bin

# Linux/Mac
bin/startup.sh

# Windows
bin\startup.bat
```

启动后：
- **OAP 服务**：http://localhost:11800 (gRPC), http://localhost:12800 (REST)
- **UI 界面**：http://localhost:8080

### 2. 启动微服务

按照上述方式启动所有微服务（带 SkyWalking Agent）

### 3. 访问 SkyWalking UI

打开浏览器访问：http://localhost:8080

---

## 链路追踪演示

### 场景1：订单下达流程

**调用链路：** 网关 → 订单服务 → 生产计划服务 → 库存服务 → 设备监控服务

**测试步骤：**

```bash
# 1. 创建订单
curl -X POST http://localhost:8080/api/order/create \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "测试客户",
    "customerType": "INDIVIDUAL",
    "vehicleModel": "特斯拉Model3",
    "quantity": 1,
    "totalAmount": 299999,
    "priority": 2,
    "deliveryDate": "2025-02-01T00:00:00"
  }'

# 2. 确认订单
curl -X PUT "http://localhost:8080/api/order/1/status?status=1"

# 3. 触发生产
curl -X POST http://localhost:8080/api/order/1/trigger-production
```

**在 SkyWalking UI 中查看：**
1. 进入 "Topology" 页面，查看服务拓扑图
2. 进入 "Trace" 页面，查看完整调用链路
3. 可以看到：Gateway → Order → Production → Inventory → Equipment 的完整链路

### 场景2：质量问题追溯流程

**调用链路：** 网关 → 质量服务 → 订单服务 → 生产计划服务 → 设备监控服务

**测试步骤：**

```bash
# 查询质量检验记录
curl http://localhost:8080/api/quality/inspection/list
```

### 场景3：设备异常处理流程

**调用链路：** 网关 → 设备监控服务 → 生产计划服务 → 订单服务

**测试步骤：**

```bash
# 查询设备状态
curl http://localhost:8080/api/equipment/list

# 查询特定设备
curl http://localhost:8080/api/equipment/1/status
```

---

## SkyWalking UI 功能说明

### 1. Dashboard（仪表盘）
- 服务概览
- 响应时间统计
- 吞吐量统计
- 错误率统计

### 2. Topology（拓扑图）
- **全局拓扑**：展示所有服务间的调用关系
- **服务拓扑**：展示单个服务的上下游关系
- **实时更新**：动态展示服务调用情况

### 3. Trace（链路追踪）
- **Trace 列表**：查看所有请求的追踪记录
- **Trace 详情**：查看单个请求的完整调用链
- **Span 详情**：查看每个服务调用的详细信息

### 4. Service（服务监控）
- 服务列表
- 服务性能指标
- 服务端点（Endpoint）监控

### 5. Database（数据库监控）
- 数据库调用统计
- SQL 语句追踪

---

## 答辩演示要点

### 1. 展示服务拓扑图
- 打开 Topology 页面
- 展示 6 个服务（Gateway + 5 个业务服务）的拓扑关系
- 说明服务间的调用关系符合试题要求

### 2. 展示链路追踪
- 执行订单下达流程
- 在 Trace 页面找到对应的 Trace
- 展示完整的调用链路：Gateway → Order → Production → Inventory → Equipment
- 说明每个服务的响应时间和调用顺序

### 3. 展示性能指标
- 展示各服务的响应时间
- 展示服务的吞吐量（TPS）
- 展示服务的成功率

### 4. 说明技术实现
- 使用 Java Agent 方式集成，无侵入性
- 自动采集服务调用数据
- 支持 Spring Cloud、Feign、MyBatis 等框架

---

## 常见问题

### 1. Agent 无法连接到 OAP

**解决方案：**
- 检查 OAP 服务是否启动：`netstat -an | grep 11800`
- 检查防火墙设置
- 确认 `collector.backend_service` 配置正确

### 2. UI 界面无数据

**解决方案：**
- 确保服务已经有请求流量
- 等待 1-2 分钟让数据采集和聚合
- 检查时间范围选择是否正确

### 3. 服务名称显示不正确

**解决方案：**
- 检查 `-Dskywalking.agent.service_name` 参数是否正确
- 重启服务使配置生效

---

## 总结

通过集成 Apache SkyWalking，本项目实现了：

✅ **分布式链路追踪**：完整追踪请求在微服务间的调用路径  
✅ **服务拓扑可视化**：自动生成服务调用关系图  
✅ **性能监控**：实时监控服务性能指标  
✅ **满足试题要求**：完整展示三个业务场景的调用链路

这为答辩提供了强有力的技术支撑！
