# SmartWallet - 智能钱包管理系统

## 项目简介
SmartWallet是一个智能钱包管理系统，提供个人财务管理、收支记录、预算管理等功能。

## 技术栈
- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- Spring Security + JWT
- H2 Database
- Swagger/OpenAPI 3.0
- Lombok
- Maven

## 快速启动

### 前置要求
- JDK 17+
- Maven 3.6+

### 运行步骤
1. 克隆项目
2. 进入项目目录：`cd smartwallet`
3. 编译项目：`mvn clean install`
4. 运行项目：`mvn spring-boot:run`
5. 访问 Swagger UI：http://localhost:8080/swagger-ui.html
6. H2 控制台：http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:smartwallet)

## API文档
启动项目后访问：http://localhost:8080/swagger-ui.html

## 主要功能
1. 用户管理：注册、登录、个人信息管理
2. 账户管理：多账户支持、余额查询
3. 收支记录：收入支出记录、分类管理
4. 预算管理：月度预算设置和跟踪
5. 统计分析：收支统计、报表生成

## 默认测试数据
系统会自动初始化默认的分类数据。

## 接口示例

### 注册用户
```bash
POST /api/auth/register
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "nickname": "Test User"
}
```

### 登录
```bash
POST /api/auth/login
{
  "username": "testuser",
  "password": "password123"
}
```

### 创建账户（需要JWT Token）
```bash
POST /api/accounts
Authorization: Bearer <your-jwt-token>
{
  "name": "我的银行卡",
  "type": "BANK_CARD",
  "balance": 10000.00,
  "description": "工资卡"
}
```

## 开发者
SmartWallet Development Team
