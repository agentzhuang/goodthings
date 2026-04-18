package com.goodthings.controller;

import com.goodthings.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/oss")
public class OssController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload")
    public Result<?> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        try {
            LocalDate now = LocalDate.now();
            String subDir = String.format("%d/%02d/%02d",
                    now.getYear(), now.getMonthValue(), now.getDayOfMonth());
            Path dirPath = Paths.get(uploadDir, subDir);
            Files.createDirectories(dirPath);

            String originalFilename = file.getOriginalFilename();
            String ext = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString().substring(0, 8) + ext;

            Path filePath = dirPath.resolve(filename);
            file.transferTo(filePath.toFile());

            String relativeUrl = "/uploads/" + subDir + "/" + filename;

            Map<String, String> data = new HashMap<>();
            data.put("coverUrl", relativeUrl);
            data.put("objectName", subDir + "/" + filename);

            return Result.success(data);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
