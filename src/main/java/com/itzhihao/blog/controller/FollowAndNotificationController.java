package com.itzhihao.blog.controller;

import com.itzhihao.blog.common.Result;
import com.itzhihao.blog.entity.Follow;
import com.itzhihao.blog.entity.Notification;
import com.itzhihao.blog.service.FollowService;
import com.itzhihao.blog.service.NotificationService;
import com.itzhihao.blog.util.SessionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {
    
    private final FollowService followService;
    
    @PostMapping("/{followingId}")
    public Result<Void> follow(@PathVariable Long followingId, HttpSession session) {
        try {
            Long followerId = SessionUtil.getUserId(session);
            if (followerId == null) {
                return Result.error("未登录");
            }
            
            followService.follow(followerId, followingId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{followingId}")
    public Result<Void> unfollow(@PathVariable Long followingId, HttpSession session) {
        try {
            Long followerId = SessionUtil.getUserId(session);
            if (followerId == null) {
                return Result.error("未登录");
            }
            
            followService.unfollow(followerId, followingId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/check/{followingId}")
    public Result<Boolean> checkFollowing(@PathVariable Long followingId, HttpSession session) {
        try {
            Long followerId = SessionUtil.getUserId(session);
            if (followerId == null) {
                return Result.error("未登录");
            }
            
            boolean isFollowing = followService.isFollowing(followerId, followingId);
            return Result.success(isFollowing);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/followers/{userId}")
    public Result<List<Follow>> getFollowers(@PathVariable Long userId) {
        try {
            List<Follow> followers = followService.getFollowers(userId);
            return Result.success(followers);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/following/{userId}")
    public Result<List<Follow>> getFollowing(@PathVariable Long userId) {
        try {
            List<Follow> following = followService.getFollowing(userId);
            return Result.success(following);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/stats/{userId}")
    public Result<Object> getFollowStats(@PathVariable Long userId) {
        try {
            int followersCount = followService.getFollowersCount(userId);
            int followingCount = followService.getFollowingCount(userId);
            
            return Result.success(new Object() {
                public final int followers = followersCount;
                public final int following = followingCount;
            });
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
class NotificationController {
    
    private final NotificationService notificationService;
    
    @GetMapping("/list")
    public Result<List<Notification>> getNotifications(HttpSession session) {
        try {
            Long userId = SessionUtil.getUserId(session);
            if (userId == null) {
                return Result.error("未登录");
            }
            
            List<Notification> notifications = notificationService.getUserNotifications(userId);
            return Result.success(notifications);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/unread")
    public Result<List<Notification>> getUnreadNotifications(HttpSession session) {
        try {
            Long userId = SessionUtil.getUserId(session);
            if (userId == null) {
                return Result.error("未登录");
            }
            
            List<Notification> notifications = notificationService.getUnreadNotifications(userId);
            return Result.success(notifications);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount(HttpSession session) {
        try {
            Long userId = SessionUtil.getUserId(session);
            if (userId == null) {
                return Result.error("未登录");
            }
            
            int count = notificationService.getUnreadCount(userId);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/read/{notificationId}")
    public Result<Void> markAsRead(@PathVariable Long notificationId, HttpSession session) {
        try {
            Long userId = SessionUtil.getUserId(session);
            if (userId == null) {
                return Result.error("未登录");
            }
            
            notificationService.markAsRead(notificationId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/read-all")
    public Result<Void> markAllAsRead(HttpSession session) {
        try {
            Long userId = SessionUtil.getUserId(session);
            if (userId == null) {
                return Result.error("未登录");
            }
            
            notificationService.markAllAsRead(userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{notificationId}")
    public Result<Void> deleteNotification(@PathVariable Long notificationId, HttpSession session) {
        try {
            Long userId = SessionUtil.getUserId(session);
            if (userId == null) {
                return Result.error("未登录");
            }
            
            notificationService.deleteNotification(notificationId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}