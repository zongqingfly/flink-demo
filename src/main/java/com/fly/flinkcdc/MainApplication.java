package com.fly.flinkcdc;

/**
 * Flink CDC Demo 主应用程序
 * 
 * 包含以下数据库的 CDC 示例：
 * 1. MySQL CDC
 * 2. PostgreSQL CDC  
 * 3. MongoDB CDC
 * 4. Oracle CDC
 * 5. SQL Server CDC
 * 
 * 运行说明：
 * 1. 修改各示例中的数据库连接参数
 * 2. 确保数据库已正确配置 CDC
 * 3. 运行对应的 main 方法
 */
public class MainApplication {
    
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("      Flink CDC Demo 应用程序");
        System.out.println("==========================================");
        System.out.println("\n可用示例：");
        System.out.println("1. MySQL CDC 示例");
        System.out.println("2. PostgreSQL CDC 示例");
        System.out.println("3. MongoDB CDC 示例");
        System.out.println("4. Oracle CDC 示例");
        System.out.println("5. SQL Server CDC 示例");
        System.out.println("\n运行说明：");
        System.out.println("- 修改示例中的数据库连接参数");
        System.out.println("- 确保数据库已正确配置 CDC");
        System.out.println("- 运行对应的 main 方法");
        System.out.println("\n配置指南：");
        System.out.println("- MySQL: 需要开启 binlog");
        System.out.println("- PostgreSQL: 需要启用逻辑复制");
        System.out.println("- MongoDB: 需要副本集支持");
        System.out.println("- Oracle: 需要开启归档日志和补充日志");
        System.out.println("- SQL Server: 需要启用数据库 CDC");
    }

}