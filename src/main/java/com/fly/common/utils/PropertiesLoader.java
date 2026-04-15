package com.fly.common.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Properties文件加载工具类
 * 支持多种加载方式：类路径、文件系统、相对路径
 * 支持缓存机制
 * 支持默认值
 */
public class PropertiesLoader {

    private static final Logger logger = Logger.getLogger(PropertiesLoader.class.getName());

    // 单例实例
    private static PropertiesLoader instance;

    // 缓存Properties对象
    private final Properties cacheProperties;

    /**
     * 私有构造函数
     */
    private PropertiesLoader() {
        cacheProperties = new Properties();
    }

    /**
     * 获取单例实例
     */
    public static synchronized PropertiesLoader getInstance() {
        if (instance == null) {
            instance = new PropertiesLoader();
        }
        return instance;
    }

    /**
     * 从类路径加载properties文件
     *
     * @param fileName 文件名（如：config.properties）
     * @return Properties对象
     */
    public Properties loadFromClasspath(String fileName) {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                logger.warning("找不到文件: " + fileName);
                return props;
            }
            props.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            logger.info("成功从类路径加载文件: " + fileName);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "加载properties文件失败: " + fileName, e);
        }
        return props;
    }

    /**
     * 从文件系统加载properties文件
     *
     * @param filePath 文件路径（绝对路径或相对路径）
     * @return Properties对象
     */
    public Properties loadFromFileSystem(String filePath) {
        Properties props = new Properties();
        File file = new File(filePath);

        if (!file.exists()) {
            logger.warning("文件不存在: " + filePath);
            return props;
        }

        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            props.load(isr);
            logger.info("成功从文件系统加载文件: " + filePath);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "加载properties文件失败: " + filePath, e);
        }
        return props;
    }

    /**
     * 加载并缓存properties文件
     *
     * @param fileName 文件名
     * @param useCache 是否使用缓存
     * @return Properties对象
     */
    public Properties loadWithCache(String fileName, boolean useCache) {
        if (useCache && cacheProperties.containsKey(fileName)) {
            logger.info("从缓存获取: " + fileName);
            return (Properties) cacheProperties.get(fileName);
        }

        Properties props = loadFromClasspath(fileName);
        if (useCache && !props.isEmpty()) {
            cacheProperties.put(fileName, props);
        }
        return props;
    }

    /**
     * 获取字符串值
     *
     * @param props        Properties对象
     * @param key          键名
     * @param defaultValue 默认值
     * @return 字符串值
     */
    public String getString(Properties props, String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    /**
     * 获取整数值
     *
     * @param props        Properties对象
     * @param key          键名
     * @param defaultValue 默认值
     * @return 整数值
     */
    public int getInt(Properties props, String key, int defaultValue) {
        String value = props.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            logger.warning("无法解析整数: " + key + "=" + value + ", 使用默认值: " + defaultValue);
            return defaultValue;
        }
    }

    /**
     * 获取长整数值
     *
     * @param props        Properties对象
     * @param key          键名
     * @param defaultValue 默认值
     * @return 长整数值
     */
    public long getLong(Properties props, String key, long defaultValue) {
        String value = props.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            logger.warning("无法解析长整数: " + key + "=" + value + ", 使用默认值: " + defaultValue);
            return defaultValue;
        }
    }

    /**
     * 获取布尔值
     *
     * @param props        Properties对象
     * @param key          键名
     * @param defaultValue 默认值
     * @return 布尔值
     */
    public boolean getBoolean(Properties props, String key, boolean defaultValue) {
        String value = props.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        String lowerValue = value.trim().toLowerCase();
        return lowerValue.equals("true") || lowerValue.equals("yes") || lowerValue.equals("1");
    }

    /**
     * 获取双精度浮点值
     *
     * @param props        Properties对象
     * @param key          键名
     * @param defaultValue 默认值
     * @return 双精度浮点值
     */
    public double getDouble(Properties props, String key, double defaultValue) {
        String value = props.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            logger.warning("无法解析双精度数: " + key + "=" + value + ", 使用默认值: " + defaultValue);
            return defaultValue;
        }
    }

    /**
     * 保存Properties到文件
     *
     * @param props    Properties对象
     * @param filePath 文件路径
     * @param comments 注释
     * @return 是否保存成功
     */
    public boolean saveToFile(Properties props, String filePath, String comments) {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
            props.store(osw, comments);
            logger.info("成功保存properties文件: " + filePath);
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "保存properties文件失败: " + filePath, e);
            return false;
        }
    }

    /**
     * 合并两个Properties对象（第二个会覆盖第一个）
     *
     * @param target 目标Properties
     * @param source 源Properties
     */
    public void merge(Properties target, Properties source) {
        if (source != null) {
            target.putAll(source);
        }
    }

    /**
     * 清除缓存
     */
    public void clearCache() {
        cacheProperties.clear();
        logger.info("缓存已清除");
    }

    /**
     * 打印所有配置项（用于调试）
     *
     * @param props Properties对象
     */
    public void printAll(Properties props) {
        if (props == null || props.isEmpty()) {
            System.out.println("Properties为空");
            return;
        }

        System.out.println("========== Properties内容 ==========");
        for (String key : props.stringPropertyNames()) {
            System.out.println(key + " = " + props.getProperty(key));
        }
        System.out.println("====================================");
    }
}