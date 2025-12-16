# 智能制造系统 - 汽车制造企业微服务架构

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2022.0.0-blue.svg)](https://spring.io/projects/spring-cloud)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## 📖 项目简介

本项目是为某大型汽车制造企业设计的智能制造微服务管理平台，采用微服务架构实现从订单接收到整车下线的全流程数字化管理。系统支持多车型混线生产、实时设备监控、精准库存管控、严格质量把控，并实现生产过程的全链路追溯。

### 核心特性

- ✅ **微服务架构**：6个独立微服务，高内聚低耦合
- ✅ **服务治理**：Nacos服务注册与发现、配置管理
- ✅ **流量控制**：Sentinel熔断降级、限流保护
- ✅ **链路追踪**：SkyWalking分布式追踪、服务拓扑
- ✅ **数据隔离**：每个服务独立数据库
- ✅ **高可用性**：支持多实例部署、负载均衡

---

## 🏗️ 系统架构

### 微服务模块

| 服务名称 | 端口 | 数据库 | 说明 |
|---------|------|--------|------|
| **API Gateway** | 8080 | - | 统一网关，路由转发、限流熔断 |
| **Order Service** | 8081 | smart_manufact_order | 订单管理、订单优先级调度 |
| **Production Service** | 8082 | smart_manufact_production | 生产计划、MPS/MRP、排产优化 |
| **Equipment Service** | 8083 | smart_manufact_equipment | 设备监控、OEE、预测性维护 |
| **Inventory Service** | 8084 | smart_manufact_inventory | 库存管理、JIT供应链 |
| **Quality Service** | 8085 | smart_manufact_quality | 质量检验、缺陷分析、追溯 |

### 基础组件

| 组件 | 端口 | 说明 |
|------|------|------|
| **Nacos** | 8848 | 服务注册中心、配置中心 |
| **Sentinel Dashboard** | 8858 | 流控规则配置、实时监控 |
| **SkyWalking OAP** | 11800/12800 | 链路追踪数据收集 |
| **SkyWalking UI** | 8080 | 链路追踪可视化界面 |
| **MySQL** | 3306 | 业务数据存储 |

---

## 🚀 快速开始

### 环境要求

- **JDK**: 17+
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Nacos**: 2.2.1+
- **操作系统**: macOS / Windows / Linux

### 1. 克隆项目

```bash
git clone <repository-url>
cd SmartManufact
```

### 2. 初始化数据库

```bash
# 登录 MySQL
mysql -u root -p

# 执行初始化脚本
source docs/database/init.sql
```

### 3. 启动基础服务

```bash
# 启动 Nacos（单机模式）
cd ~/software/nacos/bin
sh startup.sh -m standalone

# 启动 Sentinel Dashboard（可选）
java -Dserver.port=8858 -jar sentinel-dashboard-1.8.6.jar

# 启动 SkyWalking（可选）
cd ~/software/skywalking/bin
sh startup.sh
```

### 4. 编译项目

```bash
mvn clean install -DskipTests
```

### 5. 启动微服务

**方式一：使用 Maven**

```bash
# 启动网关
cd smart-manufact-gateway && mvn spring-boot:run

# 启动订单服务
cd smart-manufact-order && mvn spring-boot:run

# 启动生产计划服务
cd smart-manufact-production && mvn spring-boot:run

# 启动设备监控服务
cd smart-manufact-equipment && mvn spring-boot:run

# 启动库存管理服务
cd smart-manufact-inventory && mvn spring-boot:run

# 启动质量管理服务
cd smart-manufact-quality && mvn spring-boot:run
```

**方式二：使用 IDE**

在 IntelliJ IDEA 中依次运行各服务的 Application 主类。

### 6. 验证服务

访问 Nacos 控制台：http://localhost:8848/nacos  
默认账号密码：nacos / nacos

查看所有服务是否注册成功。

---

## 📚 文档

- [部署文档](docs/DEPLOYMENT.md) - 详细的部署和配置指南
- [架构文档](docs/ARCHITECTURE.md) - 系统架构设计说明
- [API文档](docs/API.md) - RESTful API 接口文档
- [SkyWalking集成](docs/SKYWALKING.md) - 链路追踪集成指南

---

## 🧪 测试

### API 测试

```bash
# 创建订单
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

# 查询订单列表
curl http://localhost:8080/api/order/list?page=1&size=10

# 触发生产
curl -X POST http://localhost:8080/api/order/1/trigger-production
```

### 业务流程测试

完整的订单下达流程：

1. 创建订单 → 2. 确认订单 → 3. 触发生产 → 4. 检查库存 → 5. 检查设备 → 6. 创建生产计划

详见 [API文档](docs/API.md)

---

## 🎯 核心业务场景

### 场景1：订单下达流程

```
Gateway → Order Service → Production Service → Inventory Service → Equipment Service
```

**调用链路：**
1. 用户通过网关创建订单
2. 订单服务保存订单信息
3. 触发生产时调用生产计划服务
4. 生产计划服务检查库存可用性
5. 生产计划服务检查设备可用性
6. 创建生产计划并返回

### 场景2：质量问题追溯流程

```
Gateway → Quality Service → Order Service → Production Service → Equipment Service
```

**调用链路：**
1. 质量服务记录检验结果
2. 追溯到对应订单
3. 查询生产计划
4. 定位使用的设备
5. 完成全链路追溯

### 场景3：设备异常处理流程

```
Gateway → Equipment Service → Production Service → Order Service
```

**调用链路：**
1. 设备监控发现异常
2. 通知生产计划服务
3. 暂停相关生产计划
4. 更新订单状态

---

## 🛠️ 技术栈

### 后端框架

- **Spring Boot** 3.0.2 - 微服务基础框架
- **Spring Cloud** 2022.0.0 - 微服务治理
- **Spring Cloud Alibaba** 2022.0.0.0 - 阿里巴巴微服务组件

### 服务治理

- **Nacos** 2.2.1 - 服务注册与配置中心
- **Sentinel** 1.8.6 - 流量控制与熔断降级
- **OpenFeign** - 声明式服务调用
- **LoadBalancer** - 客户端负载均衡

### 数据持久化

- **MySQL** 8.0 - 关系型数据库
- **MyBatis Plus** 3.5.3.1 - ORM 框架
- **Druid** 1.2.16 - 数据库连接池

### 监控追踪

- **Apache SkyWalking** 9.5.0 - 分布式链路追踪

### 工具库

- **Lombok** 1.18.30 - 简化 Java 代码
- **Hutool** 5.8.15 - Java 工具类库
- **FastJson2** 2.0.25 - JSON 处理

---

## 📊 监控与追踪

### Nacos 控制台

访问：http://localhost:8848/nacos  
查看服务注册、配置管理

### Sentinel Dashboard

访问：http://localhost:8858  
查看流控规则、实时监控、熔断降级

### SkyWalking UI

访问：http://localhost:8080  
查看服务拓扑、链路追踪、性能指标

---

## 🔧 配置说明

### 修改数据库密码

编辑各服务的 `application.yml`：

```yaml
spring:
  datasource:
    username: root
    password: your_password  # 修改为你的密码
```

### 修改 Nacos 地址

编辑各服务的 `application.yml`：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: your_nacos_host:8848  # 修改为你的 Nacos 地址
```

---

## 📁 项目结构

```
SmartManufact/
├── docs/                           # 文档目录
│   ├── database/                   # 数据库脚本
│   │   └── init.sql               # 初始化脚本
│   ├── DEPLOYMENT.md              # 部署文档
│   ├── ARCHITECTURE.md            # 架构文档
│   ├── API.md                     # API文档
│   └── SKYWALKING.md              # SkyWalking集成文档
├── smart-manufact-common/         # 公共模块
├── smart-manufact-gateway/        # API网关
├── smart-manufact-order/          # 订单服务
├── smart-manufact-production/     # 生产计划服务
├── smart-manufact-equipment/      # 设备监控服务
├── smart-manufact-inventory/      # 库存管理服务
├── smart-manufact-quality/        # 质量管理服务
├── pom.xml                        # 父POM
└── README.md                      # 项目说明
```

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

---

## 📄 许可证

本项目采用 Apache 2.0 许可证。详见 [LICENSE](LICENSE) 文件。

---

## 👥 联系方式

- **项目负责人**：智能制造系统开发团队
- **邮箱**：zhuotianrun.ztr@alibaba-inc.com
- **项目地址**：/Users/jieming/IdeaProjects/SmartManufact

---

## 🎓 答辩准备

### 演示要点

1. **微服务架构展示**
   - 展示 Nacos 中的 6 个服务
   - 说明服务拆分设计和边界

2. **服务调用链路**
   - 使用 SkyWalking 展示服务拓扑图
   - 演示订单下达的完整调用链路

3. **熔断降级**
   - 展示 Sentinel Dashboard
   - 演示流控规则和熔断效果

4. **数据库隔离**
   - 展示 5 个独立数据库
   - 说明数据隔离策略

5. **API 接口**
   - 使用 Postman/curl 调用 API
   - 展示完整业务流程

### 技术亮点

- ✨ 完整的微服务架构设计
- ✨ 服务注册与发现（Nacos）
- ✨ 流量控制与熔断降级（Sentinel）
- ✨ 分布式链路追踪（SkyWalking）
- ✨ 数据库隔离与独立部署
- ✨ RESTful API 设计规范
- ✨ 完整的业务场景实现

---

**祝你答辩顺利！🎉**
