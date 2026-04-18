package com.goodthings.controller;

import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.goodthings.common.Result;
import com.goodthings.config.OssProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/oss")
@RequiredArgsConstructor
public class OssController {

    private final OssProperties ossProperties;

    @PostMapping("/signature")
    public Result<?> getSignature(@RequestBody Map<String, String> params) {
        String filename = params.get("filename");
        String contentType = params.get("contentType");

        // 生成objectName
        LocalDate now = LocalDate.now();
        String objectName = String.format("items/%d/%02d/%02d/%s_%s",
                now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                UUID.randomUUID().toString().substring(0, 8),
                filename);

        // 这里返回预签名URL（实际项目中需要用OSS SDK生成）
        String uploadUrl = String.format("https://%s.%s/%s",
                ossProperties.getBucketName(),
                ossProperties.getEndpoint(),
                objectName);

        Map<String, String> data = new HashMap<>();
        data.put("uploadUrl", uploadUrl);
        data.put("objectName", objectName);
        data.put("coverUrl", ossProperties.getUrlPrefix() + "/" + objectName);

        return Result.success(data);
    }
}
