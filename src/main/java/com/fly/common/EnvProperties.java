package com.fly.common;

import com.fly.common.utils.PropertiesLoader;

import java.util.Properties;

public class EnvProperties {
    // 获取工具类实例
    static PropertiesLoader loader = PropertiesLoader.getInstance();

    // 方式1：从类路径加载
    static Properties props1 = loader.loadFromClasspath("env.properties");

    public static String ip() {
        return props1.getProperty("ORACLE_IP");
    }

    public static String mysqlPort() {
        return props1.getProperty("MYSQL_PORT");
    }

    public static String mysqlUser() {
        return props1.getProperty("MYSQL_USER");
    }

    public static String mysqlPassword() {
        return props1.getProperty("MYSQL_PASSWORD");
    }


    public static String getProperty(String key) {
        return props1.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return props1.getProperty(key, defaultValue);
    }
}
