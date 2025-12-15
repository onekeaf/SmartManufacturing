# 智能制造系统部署文档

## 📋 目录
- [系统要求](#系统要求)
- [软件下载与安装](#软件下载与安装)
- [数据库初始化](#数据库初始化)
- [配置说明](#配置说明)
- [启动步骤](#启动步骤)
- [验证测试](#验证测试)
- [常见问题](#常见问题)

---

## 系统要求

### 硬件要求
- CPU: 4核心及以上
- 内存: 8GB 及以上（推荐 16GB）
- 磁盘: 20GB 可用空间

### 操作系统
- macOS 10.15+
- Windows 10/11
- Linux (Ubuntu 20.04+, CentOS 7+)

---

## 软件下载与安装

### 1. JDK 17

#### macOS (使用 Homebrew)
```bash
brew install openjdk@17

# 配置环境变量
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# 验证安装
java -version
```

#### Windows
1. 下载地址: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
2. 安装后配置环境变量:
   - `JAVA_HOME`: JDK安装路径
   - `Path`: 添加 `%JAVA_HOME%\bin`
3. 验证: 打开 CMD 运行 `java -version`

#### Linux
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# CentOS/RHEL
sudo yum install java-17-openjdk-devel

# 验证
java -version
```

---

### 2. MySQL 8.0

#### macOS (使用 Homebrew)
```bash
# 安装
brew install mysql@8.0

# 启动服务
brew services start mysql@8.0

# 设置 root 密码
mysql_secure_installation

# 登录测试
mysql -u root -p
```

#### Windows
1. 下载地址: https://dev.mysql.com/downloads/mysql/
2. 选择 MySQL Installer
3. 安装时选择 "Developer Default"
4. 设置 root 密码（建议设置为 `root`）
5. 完成安装后，MySQL 会自动启动

#### Linux
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install mysql-server-8.0
sudo systemctl start mysql
sudo mysql_secure_installation

# CentOS/RHEL
sudo yum install mysql-server
sudo systemctl start mysqld
sudo mysql_secure_installation
```

**MySQL 配置建议:**
- 端口: 3306 (默认)
- root 密码: root (或自定义，需同步修改 application.yml)
- 字符集: utf8mb4
- 排序规则: utf8mb4_unicode_ci

---

### 3. Nacos 2.2.1 (服务注册中心)

#### 下载
```bash
# 创建目录
mkdir -p ~/software/nacos
cd ~/software/nacos

# 下载 Nacos
wget https://github.com/alibaba/nacos/releases/download/2.2.1/nacos-server-2.2.1.tar.gz

# 解压
tar -zxvf nacos-server-2.2.1.tar.gz
cd nacos
```

#### 配置 (可选)
编辑 `conf/application.properties`:
```properties
# 服务端口
server.port=8848

# 数据库配置（可选，默认使用内嵌数据库）
# spring.datasource.platform=mysql
# db.num=1
# db.url.0=jdbc:mysql://localhost:3306/nacos?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true
# db.user=root
# db.password=root
```

#### 启动

**macOS/Linux:**
```bash
cd nacos/bin

# 单机模式启动
sh startup.sh -m standalone

# 查看日志
tail -f ../logs/start.out
```

**Windows:**
```cmd
cd nacos\bin

# 单机模式启动
startup.cmd -m standalone
```

#### 访问控制台
- URL: http://localhost:8848/nacos
- 默认账号: `nacos`
- 默认密码: `nacos`

---

### 4. Sentinel Dashboard 1.8.6 (可选)

#### 下载
```bash
# 创建目录
mkdir -p ~/software/sentinel
cd ~/software/sentinel

# 下载
wget https://github.com/alibaba/Sentinel/releases/download/1.8.6/sentinel-dashboard-1.8.6.jar
```

#### 启动
```bash
# 默认启动（端口 8080）
java -jar sentinel-dashboard-1.8.6.jar

# 自定义端口启动
java -Dserver.port=8858 -jar sentinel-dashboard-1.8.6.jar

# 后台启动
nohup java -Dserver.port=8858 -jar sentinel-dashboard-1.8.6.jar > sentinel.log 2>&1 &
```

#### 访问控制台
- URL: http://localhost:8858
- 默认账号: `sentinel`
- 默认密码: `sentinel`

---

### 5. Maven 3.6+ (构建工具)

#### macOS (使用 Homebrew)
```bash
brew install maven

# 验证
mvn -version
```

#### Windows
1. 下载地址: https://maven.apache.org/download.cgi
2. 解压到目录（如 `C:\Program Files\Apache\maven`）
3. 配置环境变量:
   - `MAVEN_HOME`: Maven 安装路径
   - `Path`: 添加 `%MAVEN_HOME%\bin`
4. 验证: `mvn -version`

#### Linux
```bash
# Ubuntu/Debian
sudo apt install maven

# CentOS/RHEL
sudo yum install maven

# 验证
mvn -version
```

**Maven 配置 (可选):**

编辑 `~/.m2/settings.xml` 添加阿里云镜像:
```xml
<mirrors>
  <mirror>
    <id>aliyunmaven</id>
    <mirrorOf>*</mirrorOf>
    <name>阿里云公共仓库</name>
    <url>https://maven.aliyun.com/repository/public</url>
  </mirror>
</mirrors>
```

---

## 数据库初始化

### 1. 执行 SQL 脚本

```bash
# 进入项目目录
cd /Users/jieming/IdeaProjects/SmartManufact

# 执行初始化脚本
mysql -u root -p < docs/database/init.sql

# 或者登录 MySQL 后执行
mysql -u root -p
source /Users/jieming/IdeaProjects/SmartManufact/docs/database/init.sql
```

### 2. 验证数据库

```sql
-- 查看创建的数据库
SHOW DATABASES LIKE 'smart_manufact%';

-- 查看订单表
USE smart_manufact_order;
SHOW TABLES;
SELECT * FROM t_order;

-- 查看设备表
USE smart_manufact_equipment;
SELECT * FROM t_equipment;

-- 查看库存表
USE smart_manufact_inventory;
SELECT * FROM t_inventory;
```

---

## 配置说明

### 1. 修改数据库密码（如果需要）

如果你的 MySQL root 密码不是 `root`，需要修改以下文件:

**订单服务:** `smart-manufact-order/src/main/resources/application.yml`
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smart_manufact_order?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 你的密码  # 修改这里
```

**其他服务类似修改:**
- `smart-manufact-production/src/main/resources/application.yml`
- `smart-manufact-equipment/src/main/resources/application.yml`
- `smart-manufact-inventory/src/main/resources/application.yml`
- `smart-manufact-quality/src/main/resources/application.yml`

### 2. 修改 Nacos 地址（如果需要）

如果 Nacos 不在本地或端口不是 8848，修改各服务的 `application.yml`:
```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: 你的Nacos地址:端口  # 如 192.168.1.100:8848
```

---

## 启动步骤

### 1. 启动基础服务

```bash
# 1. 启动 MySQL (如果未启动)
# macOS
brew services start mysql@8.0

# Linux
sudo systemctl start mysql

# 2. 启动 Nacos
cd ~/software/nacos/bin
sh startup.sh -m standalone

# 3. (可选) 启动 Sentinel Dashboard
cd ~/software/sentinel
nohup java -Dserver.port=8858 -jar sentinel-dashboard-1.8.6.jar > sentinel.log 2>&1 &
```

### 2. 编译项目

```bash
cd /Users/jieming/IdeaProjects/SmartManufact

# 清理并编译
mvn clean install -DskipTests

# 如果编译失败，可以尝试
mvn clean install -DskipTests -U
```

### 3. 启动微服务

**方式一: 使用 Maven 启动（开发环境推荐）**

打开 6 个终端窗口，分别执行:

```bash
# 终端1: 启动网关
cd smart-manufact-gateway
mvn spring-boot:run

# 终端2: 启动订单服务
cd smart-manufact-order
mvn spring-boot:run

# 终端3: 启动生产计划服务
cd smart-manufact-production
mvn spring-boot:run

# 终端4: 启动设备监控服务
cd smart-manufact-equipment
mvn spring-boot:run

# 终端5: 启动库存管理服务
cd smart-manufact-inventory
mvn spring-boot:run

# 终端6: 启动质量管理服务
cd smart-manufact-quality
mvn spring-boot:run
```

**方式二: 使用 JAR 包启动（生产环境）**

```bash
# 先打包
mvn clean package -DskipTests

# 启动各服务
java -jar smart-manufact-gateway/target/smart-manufact-gateway-1.0.0.jar &
java -jar smart-manufact-order/target/smart-manufact-order-1.0.0.jar &
java -jar smart-manufact-production/target/smart-manufact-production-1.0.0.jar &
java -jar smart-manufact-equipment/target/smart-manufact-equipment-1.0.0.jar &
java -jar smart-manufact-inventory/target/smart-manufact-inventory-1.0.0.jar &
java -jar smart-manufact-quality/target/smart-manufact-quality-1.0.0.jar &
```

**方式三: 使用 IDE 启动（最简单）**

在 IntelliJ IDEA 中:
1. 打开项目
2. 找到各服务的 Application 主类
3. 右键 -> Run 'xxxApplication'

启动顺序建议:
1. GatewayApplication (网关)
2. OrderApplication (订单)
3. ProductionApplication (生产计划)
4. EquipmentApplication (设备监控)
5. InventoryApplication (库存)
6. QualityApplication (质量)

### 4. 查看服务状态

访问 Nacos 控制台: http://localhost:8848/nacos

在 "服务管理" -> "服务列表" 中应该看到 6 个服务:
- smart-manufact-gateway
- smart-manufact-order
- smart-manufact-production
- smart-manufact-equipment
- smart-manufact-inventory
- smart-manufact-quality

---

## 验证测试

### 1. 健康检查

```bash
# 检查网关
curl http://localhost:8080

# 检查各服务（通过网关）
curl http://localhost:8080/api/order/list?page=1&size=10
curl http://localhost:8080/api/equipment/list
curl http://localhost:8080/api/inventory/list
curl http://localhost:8080/api/production/plan/PLAN-20250101001
curl http://localhost:8080/api/quality/inspection/list
```

### 2. 创建订单测试

```bash
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
```

### 3. 触发生产流程测试

```bash
# 1. 确认订单
curl -X PUT "http://localhost:8080/api/order/1/status?status=1"

# 2. 触发生产（会调用生产计划、库存、设备服务）
curl -X POST http://localhost:8080/api/order/1/trigger-production

# 3. 查看生产计划
curl http://localhost:8080/api/production/plan/list
```

### 4. 查看服务调用链路

访问 Sentinel 控制台: http://localhost:8858

在 "簇点链路" 中可以看到服务间的调用关系。

---

## 常见问题

### 1. 端口被占用

**问题:** 启动时提示端口被占用

**解决:**
```bash
# macOS/Linux 查看端口占用
lsof -i :8080
lsof -i :8081

# 杀死进程
kill -9 <PID>

# Windows 查看端口占用
netstat -ano | findstr :8080

# 杀死进程
taskkill /PID <PID> /F
```

### 2. 无法连接 MySQL

**问题:** 启动时报错 `Communications link failure`

**解决:**
1. 检查 MySQL 是否启动: `mysql -u root -p`
2. 检查密码是否正确
3. 检查 `application.yml` 中的数据库配置
4. 确保数据库已创建: `SHOW DATABASES LIKE 'smart_manufact%';`

### 3. 无法连接 Nacos

**问题:** 服务启动后在 Nacos 中看不到

**解决:**
1. 检查 Nacos 是否启动: 访问 http://localhost:8848/nacos
2. 检查 `application.yml` 中 Nacos 地址配置
3. 查看服务日志，搜索 "nacos" 关键词
4. 确保网络可达: `ping localhost`

### 4. Maven 编译失败

**问题:** `mvn clean install` 失败

**解决:**
```bash
# 清理本地仓库缓存
rm -rf ~/.m2/repository

# 使用阿里云镜像重新下载
mvn clean install -DskipTests -U

# 如果还是失败，逐个模块编译
cd smart-manufact-common
mvn clean install -DskipTests
```

### 5. 服务启动慢

**问题:** 服务启动需要很长时间

**解决:**
1. 增加 JVM 内存: `java -Xms512m -Xmx1024m -jar xxx.jar`
2. 跳过不必要的检查: 在 `application.yml` 中设置 `spring.cloud.nacos.discovery.enabled=false` (仅开发时)
3. 使用 SSD 硬盘
4. 关闭不必要的服务

### 6. Feign 调用失败

**问题:** 服务间调用报错 `FeignException`

**解决:**
1. 确保被调用服务已启动并注册到 Nacos
2. 检查 Feign 接口的 `@FeignClient` 注解中的服务名是否正确
3. 检查被调用服务的接口路径是否正确
4. 查看 Sentinel 控制台是否有熔断
5. 增加 Feign 超时时间:
```yaml
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 10000
```

---

## 服务端口列表

| 服务 | 端口 | 说明 |
|------|------|------|
| Nacos | 8848 | 服务注册中心 |
| Sentinel Dashboard | 8858 | 熔断监控 |
| Gateway | 8080 | API 网关 |
| Order Service | 8081 | 订单服务 |
| Production Service | 8082 | 生产计划服务 |
| Equipment Service | 8083 | 设备监控服务 |
| Inventory Service | 8084 | 库存管理服务 |
| Quality Service | 8085 | 质量管理服务 |

---

## 技术支持

如有问题，请联系:
- 邮箱: zhuotianrun.ztr@alibaba-inc.com
- 项目地址: /Users/jieming/IdeaProjects/SmartManufact

---

**祝你部署顺利！🎉**
