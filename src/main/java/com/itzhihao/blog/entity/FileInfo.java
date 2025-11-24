package com.itzhihao.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FileInfo {
    private Long id;
    private Long userId;
    private Long spaceId;
    private String fileName;
    private String originalName;
    private String filePath;
    private String contentType;
    private Long fileSize;
    private String category; // 一级分类
    private String subCategory; // 二级分类
    private Integer status; // 0:正常 1:冻结
    private Long downloadCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}