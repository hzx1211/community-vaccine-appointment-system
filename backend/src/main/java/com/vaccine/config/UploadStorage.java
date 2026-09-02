package com.vaccine.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 统一解析上传目录，避免因从项目根目录或 backend 目录启动而找不到图片。
 */
@Component
public class UploadStorage {

    @Value("${file.upload-path:uploads}")
    private String configuredPath;

    private Path rootPath;

    @PostConstruct
    public void initialize() throws IOException {
        rootPath = resolveRootPath(configuredPath);
        Files.createDirectories(rootPath);
    }

    public Path getRootPath() {
        return rootPath;
    }

    public String getResourceLocation() {
        String location = rootPath.toUri().toString();
        return location.endsWith("/") ? location : location + "/";
    }

    private Path resolveRootPath(String pathValue) {
        Path configured = Paths.get(pathValue);
        if (configured.isAbsolute()) {
            return configured.normalize();
        }

        Path workingDirectory = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        // Maven/IDEA 通常从 backend 模块运行；项目资源则位于其父目录的 uploads 下。
        if ("backend".equalsIgnoreCase(workingDirectory.getFileName().toString())
                && workingDirectory.getParent() != null) {
            return workingDirectory.getParent().resolve(configured).normalize();
        }
        return workingDirectory.resolve(configured).normalize();
    }
}
