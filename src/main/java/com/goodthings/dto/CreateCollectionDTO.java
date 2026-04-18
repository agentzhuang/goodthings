package com.goodthings.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class CreateCollectionDTO {
    @NotBlank(message = "收藏夹名称不能为空")
    private String name;

    private String description;
    private Integer isPublic;
}
