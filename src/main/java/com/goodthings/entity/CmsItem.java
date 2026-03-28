package com.goodthings.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("cms_item")
public class CmsItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private Integer mediaType;
    private String coverUrl;
    private String mediaUrl;
    private Integer width;
    private Integer height;
    private Integer duration;
    private Integer likeCount;
    private Integer commentCount;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer delFlag;
}
