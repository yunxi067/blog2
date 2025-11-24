package com.itzhihao.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Notification {
    private Long id;
    private Long userId;
    private Long fromUserId; // 触发通知的用户ID
    private String title;
    private String content;
    private String type; // follow, file_upload, etc.
    private Integer status; // 0:未读 1:已读
    private LocalDateTime createTime;
}