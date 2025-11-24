package com.itzhihao.blog.controller;

import com.itzhihao.blog.common.Result;
import com.itzhihao.blog.entity.User;
import com.itzhihao.blog.entity.Space;
import com.itzhihao.blog.entity.FileInfo;
import com.itzhihao.blog.entity.SpaceApply;
import com.itzhihao.blog.service.UserService;
import com.itzhihao.blog.service.SpaceService;
import com.itzhihao.blog.service.FileService;
import com.itzhihao.blog.service.SpaceApplyService;
import com.itzhihao.blog.util.SessionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final UserService userService;
    private final SpaceService spaceService;
    private final FileService fileService;
    private final SpaceApplyService spaceApplyService;
    
    // 权限检查方法
    private boolean checkAdminPermission(HttpSession session) {
        return SessionUtil.isAdmin(session);
    }
    
    // ==================== 用户管理 ====================
    
    @GetMapping("/users")
    public Result<List<User>> getAllUsers(HttpSession session) {
        if (!checkAdminPermission(session)) {
            return Result.error("无权限");
        }
        
        try {
            // 这里需要实现获取所有用户的方法
            return Result.success(List.of());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/user/{userId}/freeze")
    public Result<Void> freezeUser(@PathVariable Long userId,
                                   @RequestParam Integer status,
                                   HttpSession session) {
        if (!checkAdminPermission(session)) {
            return Result.error("无权限");
        }
        
        try {
            userService.updateStatus(userId, status);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // ==================== 空间管理 ====================
    
    @GetMapping("/spaces")
    public Result<List<Space>> getAllSpaces(HttpSession session) {
        if (!checkAdminPermission(session)) {
            return Result.error("无权限");
        }
        
        try {
            // 这里需要实现获取所有空间的方法
            return Result.success(List.of());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/space/{spaceId}/freeze")
    public Result<Void> freezeSpace(@PathVariable Long spaceId,
                                    @RequestParam Integer status,
                                    HttpSession session) {
        if (!checkAdminPermission(session)) {
            return Result.error("无权限");
        }
        
        try {
            spaceService.updateStatus(spaceId, status);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // ==================== 文件管理 ====================
    
    @GetMapping("/files")
    public Result<List<FileInfo>> getAllFiles(HttpSession session) {
        if (!checkAdminPermission(session)) {
            return Result.error("无权限");
        }
        
        try {
            // 获取所有文件（需要实现对应方法）
            return Result.success(List.of());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/file/{fileId}/freeze")
    public Result<Void> freezeFile(@PathVariable Long fileId,
                                   @RequestParam Integer status,
                                   HttpSession session) {
        if (!checkAdminPermission(session)) {
            return Result.error("无权限");
        }
        
        try {
            fileService.updateFileStatus(fileId, status);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/file/{fileId}")
    public Result<Void> deleteFile(@PathVariable Long fileId, HttpSession session) {
        if (!checkAdminPermission(session)) {
            return Result.error("无权限");
        }
        
        try {
            fileService.deleteFile(fileId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // ==================== 扩容申请管理 ====================
    
    @GetMapping("/applies")
    public Result<List<SpaceApply>> getAllApplies(HttpSession session) {
        if (!checkAdminPermission(session)) {
            return Result.error("无权限");
        }
        
        try {
            List<SpaceApply> applies = spaceApplyService.findAll();
            return Result.success(applies);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/applies/pending")
    public Result<List<SpaceApply>> getPendingApplies(HttpSession session) {
        if (!checkAdminPermission(session)) {
            return Result.error("无权限");
        }
        
        try {
            List<SpaceApply> applies = spaceApplyService.findPendingApplies();
            return Result.success(applies);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/apply/{applyId}/approve")
    public Result<Void> approveApply(@PathVariable Long applyId,
                                    @RequestParam String reviewComment,
                                    HttpSession session) {
        if (!checkAdminPermission(session)) {
            return Result.error("无权限");
        }
        
        try {
            Long reviewerId = SessionUtil.getUserId(session);
            spaceApplyService.approveApply(applyId, reviewComment, reviewerId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/apply/{applyId}/reject")
    public Result<Void> rejectApply(@PathVariable Long applyId,
                                   @RequestParam String reviewComment,
                                   HttpSession session) {
        if (!checkAdminPermission(session)) {
            return Result.error("无权限");
        }
        
        try {
            Long reviewerId = SessionUtil.getUserId(session);
            spaceApplyService.rejectApply(applyId, reviewComment, reviewerId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // ==================== 统计信息 ====================
    
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats(HttpSession session) {
        if (!checkAdminPermission(session)) {
            return Result.error("无权限");
        }
        
        try {
            Map<String, Object> stats = new HashMap<>();
            // 这里需要实现统计数据的获取
            stats.put("totalUsers", 0);
            stats.put("totalSpaces", 0);
            stats.put("totalFiles", 0);
            stats.put("totalStorage", 0L);
            stats.put("pendingApplies", 0);
            
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}