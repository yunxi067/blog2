package com.itzhihao.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Follow {
    private Long id;
    private Long followerId; // 关注者ID
    private Long followingId; // 被关注者ID
    private LocalDateTime createTime;
}