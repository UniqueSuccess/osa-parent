package com.goldencis.osa.strategy.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FileMon {
//
//    {"fileMon":{ "enable":0,"content":{"fileExt":"*.txt;*.doc;*.docx", "resourceManager":0 } }}
    private Integer enable;

    private FileTypeContent content;

    @Data
    public class FileTypeContent{
         //文件内容
         String fileExt;
         //仅审计 资源管理器
         Integer resourceManager;
    }
}
