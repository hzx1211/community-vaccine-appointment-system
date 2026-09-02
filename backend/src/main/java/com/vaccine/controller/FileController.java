package com.vaccine.controller;

import cn.hutool.core.util.IdUtil;
import com.vaccine.annotation.RequireRole;
import com.vaccine.common.Result;
import com.vaccine.config.UploadStorage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 文件上传控制器
 */
@Tag(name = "文件上传")
@RestController
@RequestMapping("/api/file")
public class FileController {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    @Autowired
    private UploadStorage uploadStorage;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    @RequireRole({"admin", "community_admin", "user"})
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = extractExtension(originalFilename);
        String contentType = file.getContentType();
        if (!ALLOWED_EXTENSIONS.contains(extension) || contentType == null
                || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            return Result.error(400, "仅支持 JPG、PNG、GIF、WEBP 图片");
        }

        // 按日期分目录存储
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path dirPath = uploadStorage.getRootPath().resolve(dateDir).normalize();
        Files.createDirectories(dirPath);

        // 生成唯一文件名
        String newFilename = IdUtil.simpleUUID() + extension;

        // 保存文件
        Path destination = dirPath.resolve(newFilename).normalize();
        if (!destination.startsWith(uploadStorage.getRootPath())) {
            return Result.error(400, "非法文件路径");
        }
        file.transferTo(destination.toFile());

        // 返回访问路径
        String url = "/uploads/" + dateDir + "/" + newFilename;
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        result.put("filename", newFilename);
        return Result.success(result);
    }

    private String extractExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int lastDot = filename.lastIndexOf('.');
        return lastDot >= 0 ? filename.substring(lastDot).toLowerCase(Locale.ROOT) : "";
    }
}
