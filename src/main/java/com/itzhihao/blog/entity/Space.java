package com.itzhihao.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Space {
    private Long id;
    private Long userId;
    private Long totalSize; // 总空间大小(字节)
    private Long usedSize; // 已用空间大小(字节)
    private Integer status; // 0:正常 1:冻结
    private Long totalDownloads; // 总下载次数
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 计算剩余空间
    public Long getRemainingSize() {
        return totalSize - usedSize;
    }
    
    // 获取空间使用率
    public double getUsagePercentage() {
        if (totalSize == 0) return 0;
        return (double) usedSize / totalSize * 100;
    }
}