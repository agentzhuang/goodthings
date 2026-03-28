package com.goodthings.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class CreateItemDTO {
    @NotBlank(message = "标题不能为空")
    private String title;

    private String description;

    @NotBlank(message = "媒体类型不能为空")
    private Integer mediaType;

    @NotBlank(message = "封面URL不能为空")
    private String coverUrl;

    @NotBlank(message = "媒体URL不能为空")
    private String mediaUrl;

    private Integer width;
    private Integer height;
    private Integer duration;
    private List<Long> tagIds;
}
