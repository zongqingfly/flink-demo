package com.fly.common.utilsExample;

import com.fly.common.utils.PropertiesLoader;

import java.util.Properties;

public class PropertiesLoaderExample {
    public static void main(String[] args) {
        // 获取工具类实例
        PropertiesLoader loader = PropertiesLoader.getInstance();

        // 方式1：从类路径加载
        Properties props1 = loader.loadFromClasspath("env.properties");
        String oracleIp = props1.getProperty("ORACLE_IP");
        System.out.println("oracleIp = " + oracleIp);

//        // 方式2：从文件系统加载
//        Properties props2 = loader.loadFromFileSystem("/path/to/config.properties");
//
//        // 方式3：使用缓存加载
//        Properties props3 = loader.loadWithCache("db.properties", true);
//
//        // 读取配置值（带默认值）
//        String dbUrl = loader.getString(props3, "db.url", "jdbc:mysql://localhost:3306/test");
//        int dbPort = loader.getInt(props3, "db.port", 3306);
//        boolean debug = loader.getBoolean(props3, "debug.mode", false);
//        double timeout = loader.getDouble(props3, "connection.timeout", 30.0);
//
//        System.out.println("数据库URL: " + dbUrl);
//        System.out.println("数据库端口: " + dbPort);
//        System.out.println("调试模式: " + debug);
//        System.out.println("超时时间: " + timeout);
//
//        // 打印所有配置
//        loader.printAll(props3);
//
//        // 保存配置到文件
//        Properties newProps = new Properties();
//        newProps.setProperty("app.name", "MyApplication");
//        newProps.setProperty("app.version", "1.0.0");
//        loader.saveToFile(newProps, "config/new.properties", "Application Configuration");
//
//        // 合并配置
//        Properties merged = new Properties();
//        loader.merge(merged, props1);
//        loader.merge(merged, props2);
    }
}
