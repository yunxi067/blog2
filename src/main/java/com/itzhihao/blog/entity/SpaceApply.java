package com.itzhihao.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SpaceApply {
    private Long id;
    private Long userId;
    private Long spaceId;
    private Long requestedSize; // 申请扩容大小
    private Integer status; // 0:待审核 1:通过 2:驳回
    private String reason; // 申请理由
    private String reviewComment; // 审核意见
    private LocalDateTime createTime;
    private LocalDateTime reviewTime;
    private Long reviewerId;
}