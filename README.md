# Flink CDC Demo 

基于 Java 17 和 Flink 1.20.3 的 CDC（Change Data Capture）示例项目。

## 项目概述

本项目提供了多个主流数据库的 Flink CDC 连接示例，包括：
- MySQL CDC
- PostgreSQL CDC
- MongoDB CDC
- Oracle CDC
- SQL Server CDC

## 环境要求

- **Java**: 17+
- **Maven**: 3.6+
- **Flink**: 1.20.3
- **Flink CDC**: 3.3.0

## 项目结构

```
src/main/java/com/example/flinkcdc/
├── MySQLCDCExample.java          # MySQL CDC 示例
├── PostgreSQLCDCExample.java     # PostgreSQL CDC 示例
├── MongoDBCDCExample.java       # MongoDB CDC 示例
├── OracleCDCExample.java        # Oracle CDC 示例
├── SQLServerCDCExample.java     # SQL Server CDC 示例
└── MainApplication.java         # 主应用程序
```

## 快速开始

### 1. 克隆项目

```bash
git clone <项目地址>
cd flink-cdc-demo
```

### 2. 修改配置

编辑各个示例文件，修改数据库连接参数：

```java
// 示例：MySQL CDC 配置
'hostname' = 'localhost',
'port' = '3306',
'username' = 'root',
'password' = '123456',
'database-name' = 'test_db',
'table-name' = 'users'
```

### 3. 构建项目

```bash
mvn clean package
```

### 4. 运行示例

```bash
# 运行 MySQL CDC 示例
java -cp target/flink-cdc-demo-1.0.0.jar \
     com.example.flinkcdc.MySQLCDCExample

# 运行其他示例类似
```

## 数据库配置指南

### MySQL CDC 配置

1. **开启 binlog**：
   ```ini
   [mysqld]
   server-id = 1
   log-bin = mysql-bin
   binlog_format = row
   binlog_row_image = full
   ```

2. **用户授权**：
   ```sql
   GRANT SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO '用户名'@'%';
   FLUSH PRIVILEGES;
   ```

### PostgreSQL CDC 配置

1. **修改 postgresql.conf**：
   ```ini
   wal_level = logical
   max_replication_slots = 10
   max_wal_senders = 10
   ```

2. **创建复制用户**：
   ```sql
   CREATE USER replication_user WITH REPLICATION LOGIN PASSWORD '密码';
   ```

3. **为表启用逻辑复制**：
   ```sql
   ALTER TABLE 表名 REPLICA IDENTITY FULL;
   ```

### MongoDB CDC 配置

1. **启动副本集**（单节点）：
   ```bash
   mongod --replSet rs0 --port 27017
   ```

2. **初始化副本集**：
   ```javascript
   rs.initiate({
     _id: "rs0",
     members: [{ _id: 0, host: "localhost:27017" }]
   })
   ```

### Oracle CDC 配置

1. **开启归档日志**：
   ```sql
   SHUTDOWN IMMEDIATE;
   STARTUP MOUNT;
   ALTER DATABASE ARCHIVELOG;
   ALTER DATABASE OPEN;
   ```

2. **开启补充日志**：
   ```sql
   ALTER DATABASE ADD SUPPLEMENTAL LOG DATA;
   ALTER DATABASE ADD SUPPLEMENTAL LOG DATA (PRIMARY KEY) COLUMNS;
   ```

3. **创建 LogMiner 用户**（详见代码中的配置指南）

### SQL Server CDC 配置

1. **启用数据库 CDC**：
   ```sql
   USE [数据库名];
   EXEC sys.sp_cdc_enable_db;
   ```

2. **为表启用 CDC**：
   ```sql
   EXEC sys.sp_cdc_enable_table
     @source_schema = N'架构名',
     @source_name = N'表名',
     @role_name = NULL;
   ```

## 示例说明

### 1. MySQL CDC 示例

监控 MySQL `users` 表的变化，包括：
- 插入、更新、删除操作
- 元数据（操作类型、时间戳）
- 数据过滤和转换

```java
// 运行
MySQLCDCExample.main(args);
```

### 2. PostgreSQL CDC 示例

监控 PostgreSQL `employees` 表的变化，包含：
- 嵌套字段处理
- 关联查询
- 实时统计

```java
// 运行
PostgreSQLCDCExample.main(args);
```

### 3. MongoDB CDC 示例

监控 MongoDB `employees` 集合的变化，支持：
- 文档字段访问
- 数组处理
- 实时告警

```java
// 运行
MongoDBCDCExample.main(args);
```

### 4. Oracle CDC 示例

监控 Oracle `EMPLOYEES` 表的变化，使用：
- LogMiner 策略
- 在线字典
- 跨表关联

```java
// 运行
OracleCDCExample.main(args);
```

### 5. SQL Server CDC 示例

监控 SQL Server `Employee` 表的变化，包括：
- CDC 表特性
- 实时分析
- 状态监控

```java
// 运行
SQLServerCDCExample.main(args);
```

## 测试数据

每个示例类中都提供了测试表创建脚本，例如：

### MySQL 测试数据
```sql
-- 创建测试数据库
CREATE DATABASE IF NOT EXISTS test_db;
USE test_db;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  age INT,
  email VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入测试数据
INSERT INTO users (name, age, email) VALUES
('张三', 25, 'zhangsan@example.com'),
('李四', 30, 'lisi@example.com');
```

## 常见问题

### Q1: 连接超时
- 检查数据库服务是否运行
- 检查网络连接和防火墙
- 验证连接参数

### Q2: 权限不足
- 确保用户有 CDC 所需权限
- 参考各数据库的授权语句

### Q3: CDC 未启用
- 按照配置指南启用数据库 CDC
- 重启数据库服务
- 验证配置是否生效

### Q4: 数据未捕获
- 检查表是否启用 CDC
- 验证数据变更操作
- 检查 Flink 日志

## 高级功能

### 1. 数据过滤
```sql
WHERE operation IN ('c', 'u', 'd')
  AND salary > 10000
```

### 2. 关联查询
```sql
SELECT e.*, d.department_name
FROM employees e
LEFT JOIN departments d ON e.department_id = d.department_id
```

### 3. 实时统计
```sql
SELECT department, 
       COUNT(*) as count,
       AVG(salary) as avg_salary
FROM employees
GROUP BY department
```

### 4. 数据转换
```java
Table filteredTable = sourceTable
    .filter("age > 18")
    .select("id, name, age, email, operation_type");
```

## 许可证

MIT License

## 联系

如有问题或建议，请通过以下方式联系：
- 提交 Issues
- 发送邮件

## 更新日志

### v1.0.0 (2024-xx-xx)
- 初始版本发布
- 支持 5 种数据库 CDC
- Java 17 + Flink 1.20.3

---

**注意**: 运行示例前请确保：
1. 数据库服务正常运行
2. CDC 功能已正确配置
3. 连接参数已修改为实际值
4. 有足够的权限访问数据库