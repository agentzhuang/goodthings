package com.goodthings.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("cms_item_collection")
public class CmsItemCollection {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long itemId;
    private Long collectionId;
    private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
