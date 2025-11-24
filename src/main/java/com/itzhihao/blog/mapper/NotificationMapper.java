package com.itzhihao.blog.mapper;

import com.itzhihao.blog.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {
    
    int insert(Notification notification);
    
    Notification findById(@Param("id") Long id);
    
    List<Notification> findByUserId(@Param("userId") Long userId);
    
    List<Notification> findUnreadByUserId(@Param("userId") Long userId);
    
    int markAsRead(@Param("id") Long id);
    
    int markAllAsRead(@Param("userId") Long userId);
    
    int deleteById(@Param("id") Long id);
    
    int countUnread(@Param("userId") Long userId);
}