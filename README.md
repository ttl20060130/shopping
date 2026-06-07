# 百战商城 (Baizhan Shopping)

> 基于 Spring Cloud Alibaba 微服务架构的全栈电商平台

## 🛠️ 技术栈

| 层级           | 技术                                                          |
| -------------- | ------------------------------------------------------------- |
| 后端框架       | Spring Boot 3.0.2 / Spring Cloud 2022.0.0 / Spring Cloud Alibaba 2022.0.0 |
| RPC 通信       | Apache Dubbo 3.2.4                                            |
| 注册 & 配置中心 | Nacos                                                         |
| 分布式事务     | Seata 1.7.0 (XA 模式)                                          |
| ORM            | MyBatis-Plus 3.5.4                                            |
| 数据库         | MySQL                                                         |
| 缓存           | Redis + Redisson 3.27.1                                       |
| 搜索引擎       | Elasticsearch                                                 |
| 消息队列       | Apache RocketMQ                                               |
| 流量控制       | Alibaba Sentinel                                              |
| 工具库         | Hutool 5.7.17 (布隆过滤器)                                     |
| 前端           | Vue 3 + Element Plus + Vue Router + Vuex                      |
| 构建工具       | Maven / Yarn                                                  |

## 📦 项目结构

```
├── shopping/                           # 后端微服务父项目 (20 个子模块)
│   ├── shopping_common/                # 公共模块（实体类、服务接口、工具类）
│   ├── shopping_goods_service/         # 商品服务 [RocketMQ]
│   ├── shopping_admin_service/         # 管理员服务
│   ├── shopping_category_service/      # 分类/广告服务
│   ├── shopping_file_service/          # 文件服务
│   ├── shopping_search_service/        # 搜索服务 [Elasticsearch]
│   ├── shopping_message_service/       # 短信服务
│   ├── shopping_user_service/          # 用户服务
│   ├── shopping_cart_service/          # 购物车服务 [RocketMQ]
│   ├── shopping_order_service/         # 订单服务 [Seata + RocketMQ]
│   ├── shopping_pay_service/           # 支付服务 [Seata]
│   ├── shopping_seckill_service/       # 秒杀服务 [Redisson + Sentinel]
│   ├── shopping_manager_api/           # 管理后台 API 网关
│   ├── shopping_category_customer_api/ # 分类用户端 API
│   ├── shopping_search_customer_api/   # 搜索用户端 API
│   ├── shopping_user_customer_api/     # 用户端 API
│   ├── shopping_cart_customer_api/     # 购物车用户端 API
│   ├── shopping_order_customer_api/    # 订单用户端 API [Seata]
│   └── shopping_seckill_customer_api/  # 秒杀用户端 API [Sentinel]
├── baizhan_shopping/                   # 商城前台前端 (Vue 3)
├── vue3-admin-main/                    # 管理后台前端 (Vue 3 + Element Plus)
├── DEFAULT_GROUP/                      # Nacos 配置文件
└── baizhanshopping.sql                 # 数据库建表 + 初始化数据
```

## 🚀 快速开始

### 环境准备

| 组件             | 说明                              |
| ---------------- | --------------------------------- |
| JDK 17+          | Java 运行环境                     |
| Maven 3.8+       | 后端构建                          |
| MySQL 8.0+       | 关系型数据库                      |
| Redis            | 缓存 + 分布式锁 (Redisson)        |
| Nacos Server     | 注册中心 & 配置中心               |
| Seata TC Server  | 分布式事务协调器                  |
| RocketMQ         | 消息队列                          |
| Elasticsearch    | 搜索引擎                          |
| Sentinel Dashboard | 流量控制面板（可选）            |
| Node.js 16+      | 前端运行环境                      |

### 1. 启动基础设施

```bash
# 启动 Nacos（注册中心 & 配置中心）
# 启动 Seata TC Server（分布式事务协调器）
# 启动 RocketMQ NameServer + Broker
# 启动 Elasticsearch
# 启动 Redis
# 创建 MySQL 数据库 baizhanshopping，导入 baizhanshopping.sql
```

### 2. 导入 Nacos 配置

将 `DEFAULT_GROUP/` 目录下的所有 YAML 文件导入到 Nacos 配置中心。

### 3. 启动后端服务

```bash
cd shopping
mvn clean install -DskipTests

# 按顺序启动各服务（端口范围：9001 - 9008）
```

### 4. 启动前端

```bash
# 管理后台
cd vue3-admin-main
yarn install && yarn serve
# 访问 http://localhost:8080

# 商城前台
cd baizhan_shopping/baizhan_shopping
yarn install && yarn serve
```

## 🏗️ 架构说明

### 服务分层

- **服务提供者（`*_service`）**：实现具体业务逻辑，通过 Dubbo 暴露 RPC 接口
- **API 层（`*_customer_api` / `*_manager_api`）**：对外提供 HTTP REST 接口，通过 Dubbo 调用服务提供者
- **服务注册与发现**：所有服务注册到 Nacos，Dubbo 从 Nacos 发现服务提供者

### 关键技术应用

| 技术          | 应用场景                                               |
| ------------- | ------------------------------------------------------ |
| **Dubbo**     | 微服务间 RPC 通信，Nacos 作为注册中心                   |
| **Seata**     | 订单 + 支付服务的分布式事务（XA 模式）                   |
| **RocketMQ**  | 商品上下架同步购物车、订单异步处理                       |
| **Redisson**  | 秒杀业务分布式锁，防止超卖                              |
| **Sentinel**  | 秒杀接口流量控制与熔断降级                               |
| **Hutool**    | 布隆过滤器，缓存穿透防护                                |
| **Elasticsearch** | 商品全文搜索                                       |

## 📄 数据库

- 数据库名：`baizhanshopping`
- 建表脚本：`baizhanshopping.sql`（位于项目根目录）
- 所有业务表使用 `bz_` 前缀
