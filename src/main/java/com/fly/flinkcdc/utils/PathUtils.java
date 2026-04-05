package com.fly.flinkcdc.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Classpath 资源路径工具类
 * 支持从 classpath 加载资源并返回可访问的文件系统路径
 */
public class PathUtils {

    /**
     * 从 classpath 加载资源，返回文件系统路径
     * 如果资源在 jar 包中，会自动复制到临时目录
     *
     * @param filename classpath 中的资源路径（例如：config.yaml 或 subdir/config.yaml）
     * @return 资源的绝对文件系统路径
     * @throws IOException 如果资源不存在或复制失败
     */
    public static String loadClassPath(String filename) throws IOException, URISyntaxException {
        return loadClassPath(filename, false);
    }

    /**
     * 从 classpath 加载资源，返回文件系统路径
     *
     * @param filename      classpath 中的资源路径
     * @param keepExtension 是否保留原文件扩展名（用于生成临时文件名）
     * @return 资源的绝对文件系统路径
     * @throws IOException 如果资源不存在或复制失败
     */
    public static String loadClassPath(String filename, boolean keepExtension) throws IOException, URISyntaxException {
        URL resourceUrl = getResourceUrl(filename);
        
        // 处理不同协议的资源
        if ("file".equals(resourceUrl.getProtocol())) {
            // 文件系统路径
            return new File(resourceUrl.toURI()).getAbsolutePath();
        } else if ("jar".equals(resourceUrl.getProtocol())) {
            // jar 包中的资源，需要复制到临时文件
            return copyToTempFile(resourceUrl, filename, keepExtension);
        } else {
            throw new IOException("不支持的资源协议: " + resourceUrl.getProtocol());
        }
    }

    /**
     * 获取 classpath 资源的 URL
     *
     * @param filename 资源路径
     * @return 资源 URL
     * @throws IOException 如果资源不存在
     */
    public static URL getResourceUrl(String filename) throws IOException {
        // 尝试使用当前线程的 ContextClassLoader
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = PathUtils.class.getClassLoader();
        }
        
        URL resourceUrl = classLoader.getResource(filename);
        if (resourceUrl == null) {
            throw new IOException("资源未找到: " + filename + " (请确保文件在 classpath 中)");
        }
        
        // 处理 URL 编码
        String decodedPath = URLDecoder.decode(resourceUrl.getPath(), StandardCharsets.UTF_8.name());
        return new URL(resourceUrl.getProtocol(), resourceUrl.getHost(), resourceUrl.getPort(), decodedPath);
    }

    /**
     * 将 jar 包中的资源复制到临时文件
     *
     * @param resourceUrl   资源 URL
     * @param filename      原始文件名
     * @param keepExtension 是否保留原文件扩展名
     * @return 临时文件路径
     * @throws IOException 如果复制失败
     */
    private static String copyToTempFile(URL resourceUrl, String filename, boolean keepExtension) throws IOException {
        // 生成临时文件名
        String tempFileName;
        if (keepExtension) {
            String extension = "";
            int lastDot = filename.lastIndexOf('.');
            if (lastDot > 0) {
                extension = filename.substring(lastDot);
            }
            tempFileName = "classpath_" + System.currentTimeMillis() + "_" + Math.abs(filename.hashCode()) + extension;
        } else {
            tempFileName = "classpath_" + System.currentTimeMillis() + "_" + Math.abs(filename.hashCode());
        }
        
        Path tempFile = Files.createTempFile(tempFileName, "");
        tempFile.toFile().deleteOnExit();
        
        try (InputStream inputStream = resourceUrl.openStream()) {
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }
        
        return tempFile.toAbsolutePath().toString();
    }

    /**
     * 批量加载 classpath 下的所有匹配文件
     *
     * @param directory classpath 中的目录路径（例如：config/）
     * @param suffix    文件后缀过滤（例如：.yaml），为 null 则不过滤
     * @return 文件路径列表
     * @throws IOException 如果目录不存在
     */
    public static java.util.List<String> loadAllFromClasspath(String directory, String suffix) throws IOException {
        java.util.List<String> results = new java.util.ArrayList<>();
        
        // 注意：这种方式只能获取已知文件列表，如果需要扫描目录，建议使用 Spring 的 PathMatchingResourcePatternResolver
        // 这里提供简单的单个文件加载方式，批量扫描需要额外的依赖或使用反射
        
        throw new UnsupportedOperationException(
            "批量扫描 classpath 目录需要 Spring 框架支持。请使用 Spring 的 PathMatchingResourcePatternResolver 或手动指定文件列表"
        );
    }

    /**
     * 检查 classpath 中是否存在指定资源
     *
     * @param filename 资源路径
     * @return 是否存在
     */
    public static boolean exists(String filename) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = PathUtils.class.getClassLoader();
        }
        return classLoader.getResource(filename) != null;
    }

    /**
     * 读取 classpath 资源为字符串
     *
     * @param filename 资源路径
     * @return 文件内容字符串
     * @throws IOException 如果读取失败
     */
    public static String readAsString(String filename) throws IOException {
        URL resourceUrl = getResourceUrl(filename);
        try (InputStream inputStream = resourceUrl.openStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * 读取 classpath 资源为字节数组
     *
     * @param filename 资源路径
     * @return 文件内容字节数组
     * @throws IOException 如果读取失败
     */
    public static byte[] readAsBytes(String filename) throws IOException {
        URL resourceUrl = getResourceUrl(filename);
        try (InputStream inputStream = resourceUrl.openStream()) {
            return inputStream.readAllBytes();
        }
    }
}