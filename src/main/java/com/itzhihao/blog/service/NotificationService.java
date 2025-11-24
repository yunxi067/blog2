package com.itzhihao.blog.service;

import com.itzhihao.blog.entity.Notification;
import com.itzhihao.blog.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationMapper notificationMapper;
    
    @Transactional
    public void sendNotification(Long userId, Long fromUserId, String title, String content, String type) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setFromUserId(fromUserId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setStatus(0); // 未读
        notification.setCreateTime(LocalDateTime.now());
        
        notificationMapper.insert(notification);
    }
    
    public void sendFollowNotification(Long userId, Long followerId) {
        sendNotification(userId, followerId, "新的关注", "有人关注了你", "follow");
    }
    
    public void sendFileUploadNotification(Long userId, Long fromUserId, String fileName) {
        String content = "关注用户上传了新文件：" + fileName;
        sendNotification(userId, fromUserId, "新文件上传", content, "file_upload");
    }
    
    public List<Notification> getUserNotifications(Long userId) {
        return notificationMapper.findByUserId(userId);
    }
    
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationMapper.findUnreadByUserId(userId);
    }
    
    public int getUnreadCount(Long userId) {
        return notificationMapper.countUnread(userId);
    }
    
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationMapper.markAsRead(notificationId);
    }
    
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationMapper.markAllAsRead(userId);
    }
    
    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationMapper.deleteById(notificationId);
    }
}