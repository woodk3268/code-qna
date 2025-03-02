package com.codeqna.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FileConfigDTO {
    private Integer maxFileNum;
    private Integer maxFileSize;
    private String fileExt;
}
