package com.goodthings.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("cms_oss_file")
public class CmsOssFile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String bucket;
    private String objectName;
    private String originalName;
    private Long fileSize;
    private String mediaType;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
