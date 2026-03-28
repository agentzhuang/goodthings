package com.goodthings.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class OssSignatureDTO {
    @NotBlank(message = "文件名不能为空")
    private String filename;

    @NotBlank(message = "文件类型不能为空")
    private String contentType;

    private Long size;
}
