# 社区食堂点餐系统

## 项目概述

**社区食堂点餐系统**是一个完整的在线点餐解决方案，包含商家管理后台和用户微信小程序双端应用。系统实现了从菜品展示、购物车管理、订单处理、支付结算到评价反馈的完整业务流程，并集成了协同过滤推荐算法，为用户提供个性化菜品推荐。

### 系统特点

- **双端分离架构**：商家管理后台（Web端）与用户小程序（移动端）完全分离
- **完整的业务闭环**：浏览 → 加购 → 下单 → 支付 → 评价的全流程支持
- **个性化推荐**：基于协同过滤算法的菜品推荐系统
- **微信生态集成**：支持微信登录、微信支付
- **商家智能管理**：支持营业时间设置、休假管理、订单实时处理
- **安全可靠**：Spring Security + JWT双重认证，前后端分离安全架构

### 适用场景

- 社区食堂、校园食堂等集中式餐饮场所
- 中小型餐厅的线上点餐系统
- 毕业设计、课程设计项目参考
- 企业内部餐厅管理系统

## 技术栈

### 后端技术栈

| 组件 | 技术选型 | 版本 | 说明 |
|------|----------|------|------|
| **开发语言** | Java | JDK 8 | 企业级应用开发 |
| **后端框架** | Spring Boot | 2.7.18 | 快速开发框架 |
| **安全框架** | Spring Security + JWT | - | 认证与授权 |
| **ORM框架** | MyBatis-Plus | 3.5.3.1 | 简化数据库操作 |
| **数据库** | MySQL | 8.0+ | 关系型数据库 |
| **缓存** | Redis | - | 会话缓存、数据缓存 |
| **构建工具** | Maven | - | 项目构建与依赖管理 |
| **工具包** | Hutool | - | Java工具类库 |
| **图片处理** | Thumbnailator | - | 图片压缩与处理 |
| **JSON处理** | Fastjson | - | 高性能JSON库 |
| **JWT处理** | JJWT | - | JWT令牌生成与验证 |
| **微信支付** | wechatpay-apache-httpclient | - | 微信支付SDK |

### 前端技术栈

#### 商家管理后台（Vue 3）
| 组件 | 技术选型 | 版本 | 说明 |
|------|----------|------|------|
| **前端框架** | Vue 3 | 3.x | 渐进式JavaScript框架 |
| **构建工具** | Vite | 4.x | 下一代前端构建工具 |
| **UI框架** | Element Plus | 2.x | 基于Vue 3的UI组件库 |
| **状态管理** | Pinia | 2.x | Vue状态管理库 |
| **路由管理** | Vue Router | 4.x | 前端路由管理 |
| **HTTP客户端** | Axios | 1.x | 基于Promise的HTTP客户端 |
| **CSS预处理器** | Sass | - | CSS扩展语言 |

#### 用户端（Uni-app微信小程序）
| 组件 | 技术选型 | 版本 | 说明 |
|------|----------|------|------|
| **跨端框架** | Uni-app | 3.x | 使用Vue.js开发跨平台应用 |
| **UI框架** | 微信小程序原生组件 | - | 微信小程序UI组件 |
| **构建工具** | Vite + HBuilderX | - | 开发与构建工具 |
| **API调用** | 微信小程序API | - | 微信原生API |

### 开发工具与环境

- **IDE**：IntelliJ IDEA（后端）、VS Code（前端）、HBuilderX（小程序）
- **版本控制**：Git
- **数据库管理**：Navicat、MySQL Workbench
- **API测试**：Postman、Apifox
- **部署环境**：Windows/Linux + Nginx + Tomcat

## 系统架构

### 整体架构图

```
┌─────────────────────────────────────────────────────────────┐
│                       用户端（微信小程序）                     │
│   ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐      │
│   │  首页   │  │ 菜品列表 │  │ 购物车  │  │ 我的订单 │      │
│   └─────────┘  └─────────┘  └─────────┘  └─────────┘      │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Spring Boot后端服务                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │  API网关层  │  │ 业务逻辑层  │  │ 数据访问层  │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
│         │               │               │                  │
│         ▼               ▼               ▼                  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │ 控制器层    │  │ 服务层      │  │ 映射层      │        │
│  │ Controller │  │ Service    │  │ Mapper     │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       数据存储层                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │   MySQL     │  │    Redis    │  │  文件存储   │        │
│  │  数据库     │  │    缓存     │  │  (图片等)   │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       商家管理后台                            │
│   ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐      │
│   │ 登录页  │  │ 菜品管理 │  │ 订单管理 │  │ 评价管理 │      │
│   └─────────┘  └─────────┘  └─────────┘  └─────────┘      │
└─────────────────────────────────────────────────────────────┘
```

### 架构特点

1. **前后端分离**：前端与后端通过RESTful API通信，独立部署
2. **分层架构**：Controller → Service → Mapper → Entity 清晰的分层设计
3. **微服务思想**：功能模块化，便于扩展和维护
4. **缓存策略**：Redis缓存热点数据，提升系统性能
5. **安全防护**：JWT令牌验证、参数校验、SQL注入防护

### 目录结构

```
社区食堂系统/
├── Java/                            # Spring Boot后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/canteen/communitycanteenosystem/
│   │   │   │   ├── common/          # 通用类（异常、响应结果）
│   │   │   │   ├── config/          # 配置类（安全、跨域、Redis等）
│   │   │   │   ├── controller/      # 控制器层
│   │   │   │   ├── dto/             # 数据传输对象
│   │   │   │   ├── entity/          # 实体类
│   │   │   │   ├── interceptor/     # 拦截器
│   │   │   │   ├── mapper/          # 数据访问层接口
│   │   │   │   ├── service/         # 服务接口与实现
│   │   │   │   └── util/            # 工具类（JWT、推荐算法等）
│   │   │   └── resources/
│   │   │       ├── application.yml  # 主配置文件
│   │   │       └── mapper/          # MyBatis XML映射文件
│   │   └── test/                    # 测试代码
│   └── pom.xml                      # Maven依赖配置
│
├── vue/                             # 商家管理后台（Vue 3）
│   ├── src/
│   │   ├── api/                     # API接口封装
│   │   ├── assets/                  # 静态资源
│   │   ├── components/              # 公共组件
│   │   ├── router/                  # 路由配置
│   │   ├── store/                   # Pinia状态管理
│   │   ├── utils/                   # 工具函数
│   │   └── views/                   # 页面组件
│   ├── index.html                   # 入口HTML
│   ├── package.json                 # 依赖配置
│   └── vite.config.js               # Vite配置
│
├── uniapp/                          # 微信小程序（Uni-app）
│   ├── pages/                       # 页面文件
│   ├── static/                      # 静态资源
│   ├── api/                         # API接口封装
│   ├── utils/                       # 工具函数
│   ├── App.vue                      # 应用入口
│   ├── main.js                      # 主JS文件
│   ├── pages.json                   # 页面配置
│   ├── manifest.json                # 应用配置
│   └── uni.scss                     # 全局样式
│
└── canteen_order_system(2).sql      # 数据库初始化脚本
```

## 功能模块

### 1. 用户模块（微信小程序端）

| 功能 | 描述 | 实现技术 |
|------|------|----------|
| **微信登录** | 用户通过微信一键登录，获取用户基本信息 | 微信小程序登录API、JWT |
| **用户信息管理** | 查看和修改个人信息（昵称、头像、手机号等） | Vue组件、REST API |
| **地址管理** | 添加、编辑、删除收货地址 | 本地存储、后端API |
| **我的订单** | 查看历史订单、订单状态跟踪 | 列表展示、状态机 |
| **我的评价** | 查看已发布的评价记录 | 列表展示、分页加载 |

### 2. 菜品模块

| 功能 | 描述 | 实现技术 |
|------|------|----------|
| **菜品分类** | 多级分类管理（如：主食、饮品、小吃等） | 树形结构、分类API |
| **菜品展示** | 图片、名称、价格、描述、库存等信息展示 | 瀑布流/列表布局 |
| **菜品搜索** | 按名称、分类、价格范围搜索菜品 | 模糊搜索、条件查询 |
| **菜品详情** | 菜品详细信息、用户评价、推荐搭配 | 详情页设计 |
| **菜品管理**（商家端） | 菜品CRUD操作、图片上传、库存管理 | Element Plus表格、表单 |

### 3. 购物车模块

| 功能 | 描述 | 实现技术 |
|------|------|----------|
| **添加菜品** | 将菜品加入购物车，选择规格、数量 | 本地存储、状态管理 |
| **购物车管理** | 修改数量、删除菜品、清空购物车 | Pinia状态管理 |
| **实时计算** | 自动计算总价、优惠金额 | 计算属性、响应式 |
| **批量操作** | 全选、反选、批量删除 | 复选框控制 |

### 4. 订单模块

| 功能 | 描述 | 实现技术 |
|------|------|----------|
| **订单创建** | 从购物车生成订单，选择收货地址、支付方式 | 订单DTO、事务管理 |
| **订单状态** | 待支付、待制作、制作中、待取餐、已完成、已取消 | 状态机设计 |
| **订单支付** | 集成微信支付，支持退款操作 | 微信支付API、回调处理 |
| **订单管理**（商家端） | 查看所有订单、修改订单状态、订单统计 | 表格展示、状态筛选 |
| **订单查询** | 按订单号、时间范围、状态查询订单 | 条件查询、分页 |

### 5. 支付模块

| 功能 | 描述 | 实现技术 |
|------|------|----------|
| **微信支付** | 调用微信支付接口，完成支付流程 | 微信支付SDK、签名验证 |
| **支付回调** | 处理微信支付结果通知，更新订单状态 | 异步通知、幂等处理 |
| **退款功能** | 支持订单退款操作 | 微信退款API |
| **支付记录** | 记录所有支付流水，便于对账 | 支付流水表 |

### 6. 评价模块

| 功能 | 描述 | 实现技术 |
|------|------|----------|
| **订单评价** | 用户对已完成订单进行评价（评分、文字、图片） | 富文本编辑、图片上传 |
| **菜品评价** | 对订单中的具体菜品进行评价 | 多级评价体系 |
| **评价回复**（商家端） | 商家对用户评价进行回复 | 回复功能 |
| **评价展示** | 在菜品详情页展示所有评价 | 分页展示、评分统计 |
| **评价管理**（商家端） | 查看、回复、隐藏用户评价 | 评价管理后台 |

### 7. 推荐模块

| 功能 | 描述 | 实现技术 |
|------|------|----------|
| **协同过滤算法** | 基于用户行为和菜品相似度的推荐 | ItemCF、UserCF算法 |
| **热门推荐** | 根据销量、评分等指标推荐热门菜品 | 统计计算 |
| **个性化推荐** | 根据用户历史行为推荐可能喜欢的菜品 | 用户画像、行为分析 |
| **实时推荐** | 基于实时用户行为的动态推荐 | 实时计算 |
| **推荐结果缓存** | 缓存推荐结果，提升响应速度 | Redis缓存 |

### 8. 商家管理模块（Web后台）

| 功能 | 描述 | 实现技术 |
|------|------|----------|
| **商家登录** | 商家账号密码登录，JWT令牌认证 | Spring Security、JWT |
| **营业时间设置** | 设置每日营业时间段 | 时间选择组件 |
| **休假管理** | 设置节假日、特殊日期休息 | 日历组件、批量设置 |
| **数据统计** | 销售额、订单量、菜品销量等统计 | 图表展示（ECharts） |
| **账号管理** | 修改密码、安全设置 | 表单验证、密码加密 |

### 9. 系统管理模块

| 功能 | 描述 | 实现技术 |
|------|------|----------|
| **权限管理** | 角色权限控制，不同角色访问不同功能 | RBAC模型 |
| **操作日志** | 记录关键操作日志，便于审计 | AOP切面 |
| **系统监控** | 监控系统运行状态、性能指标 | Spring Boot Actuator |
| **数据备份** | 定期备份数据库，防止数据丢失 | 定时任务、SQL导出 |

## 数据库设计

### 核心数据表（10张）

#### 1. 用户表 (`t_user`)
| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键ID | PK, AUTO_INCREMENT |
| openid | VARCHAR(100) | 微信OpenID | UNIQUE |
| nickname | VARCHAR(50) | 用户昵称 | |
| avatar | VARCHAR(500) | 头像URL | |
| phone | VARCHAR(20) | 手机号 | |
| create_time | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |

#### 2. 商家表 (`t_merchant`)
| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键ID | PK, AUTO_INCREMENT |
| username | VARCHAR(50) | 用户名 | UNIQUE |
| password | VARCHAR(100) | 密码（加密） | |
| shop_name | VARCHAR(100) | 店铺名称 | |
| shop_address | VARCHAR(200) | 店铺地址 | |
| business_hours | VARCHAR(100) | 营业时间 | |
| create_time | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |

#### 3. 菜品表 (`t_dish`)
| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键ID | PK, AUTO_INCREMENT |
| name | VARCHAR(100) | 菜品名称 | |
| category_id | BIGINT | 分类ID | FK |
| price | DECIMAL(10,2) | 价格 | |
| image | VARCHAR(500) | 菜品图片 | |
| description | TEXT | 菜品描述 | |
| stock | INT | 库存数量 | DEFAULT 0 |
| sales | INT | 销量 | DEFAULT 0 |
| status | TINYINT | 状态（0下架，1上架） | DEFAULT 1 |

#### 4. 菜品分类表 (`t_dish_category`)
| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键ID | PK, AUTO_INCREMENT |
| name | VARCHAR(50) | 分类名称 | |
| parent_id | BIGINT | 父分类ID | DEFAULT 0 |
| sort | INT | 排序号 | DEFAULT 0 |
| status | TINYINT | 状态（0禁用，1启用） | DEFAULT 1 |

#### 5. 订单表 (`t_order`)
| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键ID | PK, AUTO_INCREMENT |
| order_no | VARCHAR(50) | 订单号 | UNIQUE |
| user_id | BIGINT | 用户ID | FK |
| total_amount | DECIMAL(10,2) | 订单总金额 | |
| status | TINYINT | 订单状态 | 索引 |
| payment_method | VARCHAR(20) | 支付方式 | |
| payment_time | DATETIME | 支付时间 | |
| create_time | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |

#### 6. 订单明细表 (`t_order_item`)
| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键ID | PK, AUTO_INCREMENT |
| order_id | BIGINT | 订单ID | FK, 索引 |
| dish_id | BIGINT | 菜品ID | FK |
| quantity | INT | 数量 | |
| price | DECIMAL(10,2) | 单价 | |
| subtotal | DECIMAL(10,2) | 小计 | |

#### 7. 购物车表 (`t_shopping_cart`)
| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 用户ID | FK, 索引 |
| dish_id | BIGINT | 菜品ID | FK |
| quantity | INT | 数量 | DEFAULT 1 |
| create_time | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |

#### 8. 订单评价表 (`t_evaluation`)
| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键ID | PK, AUTO_INCREMENT |
| order_id | BIGINT | 订单ID | FK, UNIQUE |
| user_id | BIGINT | 用户ID | FK |
| rating | TINYINT | 总体评分（1-5星） | |
| content | TEXT | 评价内容 | |
| images | VARCHAR(1000) | 评价图片（JSON数组） | |
| create_time | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |

#### 9. 菜品评价表 (`t_dish_evaluation`)
| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键ID | PK, AUTO_INCREMENT |
| evaluation_id | BIGINT | 订单评价ID | FK |
| dish_id | BIGINT | 菜品ID | FK |
| rating | TINYINT | 菜品评分（1-5星） | |
| content | VARCHAR(500) | 菜品评价内容 | |

#### 10. 用户行为表 (`t_user_behavior`)
| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键ID | PK, AUTO_INCREMENT |
| user_id | BIGINT | 用户ID | FK, 索引 |
| dish_id | BIGINT | 菜品ID | FK |
| behavior_type | TINYINT | 行为类型（1浏览，2加购，3购买，4评价） | |
| create_time | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |

### 索引设计

1. **主键索引**：所有表的主键ID
2. **唯一索引**：`t_user.openid`, `t_order.order_no`, `t_evaluation.order_id`
3. **外键索引**：所有外键字段
4. **复合索引**：高频查询字段组合索引
5. **状态索引**：订单状态、菜品状态等

### 数据库优化

1. **分表策略**：订单表可按时间分表，用户行为表可按用户ID分表
2. **读写分离**：主库写操作，从库读操作
3. **查询优化**：合理使用索引，避免全表扫描
4. **连接池**：使用Druid连接池，管理数据库连接

## API接口设计

### 接口规范

- **协议**：HTTP/HTTPS
- **数据格式**：JSON
- **编码**：UTF-8
- **认证方式**：JWT Bearer Token
- **响应格式**：统一响应格式

### 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1678886400000
}
```

### 错误码定义

| 错误码 | 说明 | HTTP状态码 |
|--------|------|------------|
| 200 | 成功 | 200 OK |
| 400 | 请求参数错误 | 400 Bad Request |
| 401 | 未授权/Token失效 | 401 Unauthorized |
| 403 | 权限不足 | 403 Forbidden |
| 404 | 资源不存在 | 404 Not Found |
| 500 | 服务器内部错误 | 500 Internal Server Error |

### 核心API接口

#### 1. 用户相关接口

| 接口 | 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|------|
| 微信登录 | POST | `/api/user/wxLogin` | 微信小程序登录 | 公开 |
| 获取用户信息 | GET | `/api/user/info` | 获取当前用户信息 | 用户 |
| 更新用户信息 | PUT | `/api/user/update` | 更新用户信息 | 用户 |
| 用户地址列表 | GET | `/api/user/addresses` | 获取用户地址列表 | 用户 |
| 添加地址 | POST | `/api/user/address` | 添加新地址 | 用户 |

#### 2. 菜品相关接口

| 接口 | 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|------|
| 菜品分类列表 | GET | `/api/dish/categories` | 获取菜品分类树 | 公开 |
| 菜品列表 | GET | `/api/dish/list` | 获取菜品列表（可分页） | 公开 |
| 菜品详情 | GET | `/api/dish/{id}` | 获取菜品详情 | 公开 |
| 搜索菜品 | GET | `/api/dish/search` | 搜索菜品 | 公开 |
| 添加菜品 | POST | `/api/dish/add` | 添加菜品（商家） | 商家 |
| 更新菜品 | PUT | `/api/dish/update` | 更新菜品（商家） | 商家 |
| 删除菜品 | DELETE | `/api/dish/{id}` | 删除菜品（商家） | 商家 |

#### 3. 购物车相关接口

| 接口 | 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|------|
| 购物车列表 | GET | `/api/cart/list` | 获取用户购物车列表 | 用户 |
| 添加购物车 | POST | `/api/cart/add` | 添加菜品到购物车 | 用户 |
| 更新数量 | PUT | `/api/cart/update` | 更新购物车菜品数量 | 用户 |
| 删除菜品 | DELETE | `/api/cart/{id}` | 从购物车删除菜品 | 用户 |
| 清空购物车 | DELETE | `/api/cart/clear` | 清空购物车 | 用户 |

#### 4. 订单相关接口

| 接口 | 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|------|
| 创建订单 | POST | `/api/order/create` | 从购物车创建订单 | 用户 |
| 订单列表 | GET | `/api/order/list` | 获取用户订单列表 | 用户 |
| 订单详情 | GET | `/api/order/{id}` | 获取订单详情 | 用户 |
| 取消订单 | PUT | `/api/order/cancel/{id}` | 取消订单 | 用户 |
| 订单支付 | POST | `/api/order/pay` | 发起订单支付 | 用户 |
| 支付回调 | POST | `/api/order/payCallback` | 微信支付回调 | 公开 |
| 商家订单列表 | GET | `/api/order/merchant/list` | 商家获取订单列表 | 商家 |
| 更新订单状态 | PUT | `/api/order/status` | 商家更新订单状态 | 商家 |

#### 5. 评价相关接口

| 接口 | 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|------|
| 提交评价 | POST | `/api/evaluation/submit` | 提交订单评价 | 用户 |
| 评价列表 | GET | `/api/evaluation/list` | 获取菜品评价列表 | 公开 |
| 商家回复 | POST | `/api/evaluation/reply` | 商家回复评价 | 商家 |
| 评价统计 | GET | `/api/evaluation/stats` | 获取评价统计数据 | 商家 |

#### 6. 推荐相关接口

| 接口 | 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|------|
| 热门推荐 | GET | `/api/recommend/hot` | 获取热门菜品推荐 | 公开 |
| 个性化推荐 | GET | `/api/recommend/personal` | 获取个性化推荐 | 用户 |
| 实时推荐 | GET | `/api/recommend/realtime` | 基于实时行为的推荐 | 用户 |

#### 7. 商家管理接口

| 接口 | 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|------|
| 商家登录 | POST | `/api/merchant/login` | 商家后台登录 | 公开 |
| 商家信息 | GET | `/api/merchant/info` | 获取商家信息 | 商家 |
| 更新营业时间 | PUT | `/api/merchant/businessHours` | 更新营业时间 | 商家 |
| 设置休假 | POST | `/api/merchant/holiday` | 设置休假日期 | 商家 |
| 数据统计 | GET | `/api/merchant/stats` | 获取商家统计数据 | 商家 |

### API文档

- **在线文档**：使用Swagger/OpenAPI生成在线API文档
- **离线文档**：Postman集合、API Markdown文档
- **接口测试**：提供Postman测试集合

## 部署指南

### 环境要求

#### 后端环境
- **操作系统**：Windows 10+/Linux (CentOS 7+/Ubuntu 18.04+)
- **Java环境**：JDK 8+
- **数据库**：MySQL 8.0+
- **缓存**：Redis 5.0+
- **构建工具**：Maven 3.6+
- **Web服务器**：Tomcat 9+ (Spring Boot内嵌)

#### 前端环境
- **Node.js**：14.0+
- **包管理**：npm 6.0+ 或 yarn 1.22+
- **构建工具**：Vite 4+

### 数据库部署

1. **安装MySQL 8.0+**
2. **创建数据库**
   ```sql
   CREATE DATABASE canteen_order_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. **导入数据**
   ```bash
   mysql -u root -p canteen_order_system < canteen_order_system(2).sql
   ```
4. **配置数据库用户**
   ```sql
   CREATE USER 'canteen_user'@'%' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON canteen_order_system.* TO 'canteen_user'@'%';
   FLUSH PRIVILEGES;
   ```

### Redis部署

1. **安装Redis**
   ```bash
   # Ubuntu/Debian
   sudo apt-get install redis-server

   # CentOS/RHEL
   sudo yum install redis
   ```

2. **启动Redis**
   ```bash
   sudo systemctl start redis
   sudo systemctl enable redis
   ```

3. **验证安装**
   ```bash
   redis-cli ping
   # 应返回 PONG
   ```

### 后端部署

#### 方式一：JAR包部署（推荐）

1. **打包项目**
   ```bash
   cd Java
   mvn clean package -DskipTests
   ```

2. **运行JAR包**
   ```bash
   java -jar target/community-canteen-system-1.0.0.jar
   ```

3. **后台运行**
   ```bash
   nohup java -jar target/community-canteen-system-1.0.0.jar > app.log 2>&1 &
   ```

#### 方式二：Docker部署

1. **构建Docker镜像**
   ```dockerfile
   FROM openjdk:8-jre-alpine
   COPY target/community-canteen-system-1.0.0.jar app.jar
   ENTRYPOINT ["java", "-jar", "/app.jar"]
   ```

2. **运行容器**
   ```bash
   docker build -t community-canteen-system .
   docker run -d -p 8080:8080 --name canteen-backend community-canteen-system
   ```

#### 方式三：传统WAR包部署

1. **修改pom.xml**：设置packaging为war
2. **打包WAR**
   ```bash
   mvn clean package -DskipTests
   ```
3. **部署到Tomcat**：将WAR包复制到Tomcat的webapps目录

### 前端部署

#### 商家管理后台（Vue项目）

1. **安装依赖**
   ```bash
   cd vue
   npm install
   ```

2. **开发环境运行**
   ```bash
   npm run dev
   ```

3. **生产环境构建**
   ```bash
   npm run build
   ```

4. **部署到Nginx**
   ```nginx
   server {
       listen 80;
       server_name admin.yourdomain.com;

       location / {
           root /path/to/vue/dist;
           index index.html;
           try_files $uri $uri/ /index.html;
       }

       location /api {
           proxy_pass http://localhost:8080;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
       }
   }
   ```

#### 微信小程序（Uni-app项目）

1. **安装依赖**
   ```bash
   cd uniapp
   npm install
   ```

2. **开发环境运行**
   ```bash
   # H5端
   npm run dev:h5

   # 微信小程序（需安装HBuilderX）
   # 使用HBuilderX导入项目，点击运行到微信小程序
   ```

3. **生产环境构建**
   ```bash
   # H5端
   npm run build:h5

   # 微信小程序
   # 在HBuilderX中点击发行 -> 小程序-微信
   ```

4. **微信小程序发布**
   - 在微信公众平台注册小程序
   - 获取AppID
   - 配置服务器域名（需HTTPS）
   - 上传代码到微信平台
   - 提交审核，发布上线

### 配置文件说明

#### 后端配置文件 (`application.yml`)

```yaml
# 应用配置
server:
  port: 8080
  servlet:
    context-path: /

# 数据源配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/canteen_order_system?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: canteen_user
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

  # Redis配置
  redis:
    host: localhost
    port: 6379
    database: 0
    timeout: 3000
    password: # 如有密码

  # 文件上传配置
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

# MyBatis-Plus配置
mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.canteen.communitycanteenosystem.entity
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl # 开发环境开启SQL日志

# JWT配置
jwt:
  # 用户端JWT配置
  user:
    secret: user_jwt_secret_key_here
    expiration: 604800 # 7天（秒）

  # 商家端JWT配置
  merchant:
    secret: merchant_jwt_secret_key_here
    expiration: 86400 # 1天（秒）

# 微信小程序配置
wechat:
  appid: your_wechat_appid
  secret: your_wechat_secret
  mch-id: your_wechat_mch_id # 商户号
  api-key: your_wechat_api_key # API密钥
  cert-path: /path/to/apiclient_cert.p12 # 证书路径

# 文件存储路径
file:
  upload-dir: /path/to/upload
  access-path: /uploads/**
```

#### 前端配置文件

**Vue项目环境变量 (`.env`文件)**
```env
# 开发环境
VITE_API_BASE_URL=http://localhost:8080

# 生产环境
VITE_API_BASE_URL=https://api.yourdomain.com
```

**Uni-app项目配置 (`manifest.json`)**
```json
{
  "name": "社区食堂点餐",
  "appid": "your_wechat_appid",
  "description": "社区食堂点餐小程序",
  "versionName": "1.0.0",
  "versionCode": "100",
  "mp-weixin": {
    "appid": "your_wechat_appid",
    "setting": {
      "urlCheck": false,
      "es6": true,
      "postcss": true
    }
  }
}
```

### 部署验证

1. **后端服务验证**
   ```bash
   curl http://localhost:8080/api/health
   # 应返回 {"status": "UP"}
   ```

2. **数据库连接验证**
   - 检查应用日志，确保数据库连接成功
   - 验证表结构是否正确创建

3. **Redis连接验证**
   - 检查应用日志，确保Redis连接成功

4. **前端应用验证**
   - 访问商家管理后台，确保页面正常加载
   - 微信小程序开发者工具中预览，确保功能正常

### 性能优化建议

1. **数据库优化**
   - 添加合适的索引
   - 定期清理历史数据
   - 使用连接池配置

2. **缓存策略**
   - 热点数据缓存（菜品信息、用户信息）
   - 推荐结果缓存
   - 页面静态化缓存

3. **前端优化**
   - 图片懒加载
   - 组件按需加载
   - 代码分割

4. **监控告警**
   - 应用性能监控（APM）
   - 错误日志收集
   - 关键指标告警

## 开发指南

### 环境搭建

#### 后端开发环境

1. **安装JDK 8+**
   ```bash
   # 检查Java版本
   java -version
   ```

2. **安装Maven**
   ```bash
   # 检查Maven版本
   mvn -v
   ```

3. **安装IDE**
   - IntelliJ IDEA（推荐）
   - Eclipse
   - VS Code + Java扩展

4. **导入项目**
   - 使用IDE打开`Java`目录
   - Maven自动下载依赖

#### 前端开发环境

1. **安装Node.js 14+**
   ```bash
   node -v
   npm -v
   ```

2. **安装Vue CLI（可选）**
   ```bash
   npm install -g @vue/cli
   ```

3. **安装HBuilderX（小程序开发）**
   - 从官网下载HBuilderX
   - 安装微信开发者工具

### 代码规范

#### Java代码规范

1. **命名规范**
   - 类名：大驼峰，如`UserController`
   - 方法名：小驼峰，如`getUserInfo`
   - 变量名：小驼峰，如`userName`
   - 常量名：全大写+下划线，如`MAX_COUNT`

2. **包结构规范**
   ```
   com.canteen.communitycanteenosystem
   ├── common      # 通用类
   ├── config      # 配置类
   ├── controller  # 控制器
   ├── dto         # 数据传输对象
   ├── entity      # 实体类
   ├── mapper      # 数据访问层
   ├── service     # 服务层
   └── util        # 工具类
   ```

3. **注释规范**
   - 类注释：说明类的作用
   - 方法注释：说明方法功能、参数、返回值
   - 复杂逻辑注释：说明算法或业务逻辑

#### Vue代码规范

1. **组件规范**
   - 单文件组件：`ComponentName.vue`
   - 组件命名：大驼峰，如`UserInfo.vue`
   - 目录结构：按功能模块组织

2. **代码风格**
   - 使用Composition API
   - 合理使用响应式数据
   - 组件解耦，功能单一

3. **样式规范**
   - 使用Scoped CSS
   - 避免全局样式污染
   - 使用CSS预处理器（Sass）

### 开发流程

1. **需求分析**：明确功能需求，设计接口
2. **数据库设计**：设计表结构，编写SQL
3. **后端开发**：实现Controller、Service、Mapper
4. **前端开发**：实现页面组件，调用接口
5. **联调测试**：前后端联调，测试功能
6. **代码审查**：代码规范检查，功能验证
7. **部署上线**：部署到测试/生产环境

### 测试指南

#### 单元测试

1. **后端单元测试**
   ```java
   @SpringBootTest
   class UserServiceTest {
       @Autowired
       private UserService userService;

       @Test
       void testGetUserInfo() {
           // 测试代码
       }
   }
   ```

2. **前端单元测试**
   ```javascript
   // 使用Vitest或Jest进行测试
   import { describe, it, expect } from 'vitest'
   import { mount } from '@vue/test-utils'
   ```

#### 集成测试

1. **API接口测试**
   - 使用Postman进行接口测试
   - 编写自动化测试脚本

2. **端到端测试**
   - 小程序端到端测试
   - 商家后台端到端测试

#### 性能测试

1. **压力测试**
   - 使用JMeter进行并发测试
   - 测试接口响应时间、吞吐量

2. **负载测试**
   - 模拟真实用户行为
   - 测试系统在负载下的表现

### 常见问题解决

#### 后端常见问题

1. **数据库连接失败**
   - 检查数据库服务是否启动
   - 检查连接配置是否正确
   - 检查防火墙设置

2. **Redis连接失败**
   - 检查Redis服务是否启动
   - 检查密码配置是否正确
   - 检查网络连接

3. **JWT认证失败**
   - 检查Token是否过期
   - 检查Token签名是否正确
   - 检查请求头格式

#### 前端常见问题

1. **跨域问题**
   - 后端配置CORS
   - 开发环境使用代理
   - 生产环境配置Nginx反向代理

2. **微信登录失败**
   - 检查AppID配置
   - 检查域名白名单
   - 检查网络连接

3. **图片上传失败**
   - 检查文件大小限制
   - 检查文件类型限制
   - 检查服务器存储路径权限

### 版本控制

#### Git工作流

1. **分支策略**
   - `main`：主分支，生产环境代码
   - `develop`：开发分支，集成所有功能
   - `feature/*`：功能分支，开发新功能
   - `hotfix/*`：热修复分支，修复生产环境问题

2. **提交规范**
   ```
   feat: 新增功能
   fix: 修复bug
   docs: 文档更新
   style: 代码格式调整
   refactor: 代码重构
   test: 测试相关
   chore: 构建过程或辅助工具变动
   ```

#### 版本发布

1. **版本号规范**
   - 主版本号.次版本号.修订号，如`1.0.0`
   - 遵循语义化版本控制

2. **发布流程**
   - 功能开发完成
   - 代码审查通过
   - 测试环境验证
   - 生产环境发布
   - 版本标签记录

## 项目维护

### 监控与日志

#### 应用监控

1. **Spring Boot Actuator**
   ```yaml
   management:
     endpoints:
       web:
         exposure:
           include: health,info,metrics
   ```

2. **健康检查**
   - 数据库连接状态
   - Redis连接状态
   - 磁盘空间检查
   - 内存使用情况

#### 日志管理

1. **日志配置**
   ```yaml
   logging:
     level:
       com.canteen: DEBUG
     file:
       name: logs/application.log
     pattern:
       console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
   ```

2. **日志级别**
   - ERROR：错误日志
   - WARN：警告日志
   - INFO：信息日志
   - DEBUG：调试日志

### 数据备份

#### 数据库备份

1. **自动备份脚本**
   ```bash
   #!/bin/bash
   BACKUP_DIR="/backup/mysql"
   DATE=$(date +%Y%m%d_%H%M%S)
   mysqldump -u root -p canteen_order_system > $BACKUP_DIR/canteen_$DATE.sql
   ```

2. **备份策略**
   - 每日全量备份
   - 保留最近30天备份
   - 定期测试备份恢复

#### 文件备份

1. **上传文件备份**
   - 图片文件定期备份
   - 配置文件版本管理
   - 日志文件归档

### 安全维护

#### 安全更新

1. **依赖更新**
   - 定期更新依赖版本
   - 检查安全漏洞
   - 使用安全扫描工具

2. **漏洞修复**
   - 监控安全公告
   - 及时修复已知漏洞
   - 安全补丁更新

#### 访问控制

1. **权限管理**
   - 最小权限原则
   - 定期审计权限
   - 离职人员权限回收

2. **访问日志**
   - 记录关键操作
   - 分析异常访问
   - 安全事件追溯

### 性能调优

#### 数据库优化

1. **查询优化**
   - 使用EXPLAIN分析查询
   - 优化慢查询
   - 添加合适索引

2. **表结构优化**
   - 合理分表
   - 字段类型优化
   - 冗余数据清理

#### 应用优化

1. **代码优化**
   - 避免N+1查询
   - 使用连接池
   - 合理使用缓存

2. **JVM优化**
   - 堆内存配置
   - GC调优
   - 线程池配置

### 故障处理

#### 常见故障

1. **服务不可用**
   - 检查服务进程
   - 检查端口占用
   - 检查依赖服务

2. **数据库故障**
   - 检查数据库服务
   - 检查连接数限制
   - 检查磁盘空间

3. **缓存故障**
   - 检查Redis服务
   - 检查内存使用
   - 检查网络连接

#### 应急预案

1. **服务降级**
   - 非核心功能降级
   - 静态资源缓存
   - 限流保护

2. **数据恢复**
   - 备份数据恢复
   - 事务回滚
   - 数据补偿

## 贡献指南

### 代码贡献

1. **Fork仓库**
   - Fork项目到个人仓库
   - 克隆到本地开发

2. **创建分支**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **提交代码**
   ```bash
   git add .
   git commit -m "feat: 添加新功能"
   git push origin feature/your-feature-name
   ```

4. **创建Pull Request**
   - 在GitHub上创建PR
   - 描述修改内容
   - 等待代码审查

### 文档贡献

1. **文档更新**
   - 修正错误内容
   - 补充缺失信息
   - 更新过时内容

2. **翻译贡献**
   - 中文文档维护
   - 英文文档翻译
   - 多语言支持

### 问题反馈

1. **报告Bug**
   - 描述问题现象
   - 提供复现步骤
   - 提供环境信息

2. **功能建议**
   - 描述需求场景
   - 提出解决方案
   - 评估实现难度

### 社区交流

1. **讨论区**
   - 技术问题讨论
   - 功能需求讨论
   - 项目规划讨论

2. **联系方式**
   - GitHub Issues
   - 邮件列表
   - 即时通讯群组

## 许可证

本项目采用 **MIT许可证**。

```
MIT License

Copyright (c) 2023 社区食堂点餐系统项目组

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 致谢

感谢所有为项目做出贡献的开发者，包括但不限于：

- **项目发起人**：设计并实现了系统核心架构
- **前端开发者**：实现了商家后台和微信小程序
- **后端开发者**：实现了Spring Boot后端服务
- **测试人员**：保证了系统质量
- **文档贡献者**：编写和维护项目文档

特别感谢以下开源项目：

- Spring Boot - 后端框架
- Vue.js - 前端框架
- Uni-app - 跨端框架
- MyBatis-Plus - ORM框架
- Element Plus - UI组件库
- 微信小程序 - 移动端平台

## 更新日志

### v1.0.0 (2023-10-01)
- 初始版本发布
- 实现基础点餐功能
- 微信小程序端
- 商家管理后台
- 协同过滤推荐算法

### v1.1.0 (计划中)
- 多商家支持
- 优惠券系统
- 会员积分体系
- 数据分析报表
- 移动端App版本

---

**最后更新**：2026年3月3日

**文档维护**：社区食堂点餐系统项目组

**如有问题或建议**，请通过GitHub Issues反馈。
