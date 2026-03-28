package com.goodthings.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("cms_tag")
public class CmsTag {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String color;
    private Integer useCount;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
