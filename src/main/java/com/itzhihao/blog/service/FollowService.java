package com.itzhihao.blog.service;

import com.itzhihao.blog.entity.Follow;
import com.itzhihao.blog.mapper.FollowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {
    
    private final FollowMapper followMapper;
    private final NotificationService notificationService;
    
    @Transactional
    public void follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new RuntimeException("不能关注自己");
        }
        
        boolean isFollowing = followMapper.isFollowing(followerId, followingId);
        if (isFollowing) {
            throw new RuntimeException("已经关注了该用户");
        }
        
        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        follow.setCreateTime(LocalDateTime.now());
        
        followMapper.insert(follow);
        
        // 发送通知
        notificationService.sendFollowNotification(followingId, followerId);
    }
    
    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        followMapper.delete(followerId, followingId);
    }
    
    public boolean isFollowing(Long followerId, Long followingId) {
        return followMapper.isFollowing(followerId, followingId);
    }
    
    public List<Follow> getFollowers(Long userId) {
        return followMapper.findFollowers(userId);
    }
    
    public List<Follow> getFollowing(Long userId) {
        return followMapper.findFollowing(userId);
    }
    
    public int getFollowersCount(Long userId) {
        return followMapper.countFollowers(userId);
    }
    
    public int getFollowingCount(Long userId) {
        return followMapper.countFollowing(userId);
    }
}