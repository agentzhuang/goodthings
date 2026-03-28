package com.goodthings.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("cms_like")
public class CmsLike {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long itemId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
