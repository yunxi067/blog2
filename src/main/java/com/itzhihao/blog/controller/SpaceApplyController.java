package com.itzhihao.blog.controller;

import com.itzhihao.blog.common.Result;
import com.itzhihao.blog.entity.SpaceApply;
import com.itzhihao.blog.service.SpaceApplyService;
import com.itzhihao.blog.util.SessionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/space-apply")
@RequiredArgsConstructor
public class SpaceApplyController {
    
    private final SpaceApplyService spaceApplyService;
    
    @PostMapping("/apply")
    public Result<SpaceApply> apply(@RequestParam Long requestedSize,
                                    @RequestParam String reason,
                                    HttpSession session) {
        try {
            Long userId = SessionUtil.getUserId(session);
            if (userId == null) {
                return Result.error("未登录");
            }
            
            // 获取用户空间ID
            var spaceService = spaceApplyService.getClass().getDeclaredField("spaceService");
            spaceService.setAccessible(true);
            var spaceServiceInstance = spaceService.get(spaceApplyService);
            
            var spaceMapper = spaceServiceInstance.getClass().getDeclaredField("spaceMapper");
            spaceMapper.setAccessible(true);
            var spaceMapperInstance = spaceMapper.get(spaceServiceInstance);
            
            var findByUserIdMethod = spaceMapperInstance.getClass().getMethod("findByUserId", Long.class);
            var space = findByUserIdMethod.invoke(spaceMapperInstance, userId);
            
            if (space == null) {
                return Result.error("用户空间不存在");
            }
            
            SpaceApply apply = spaceApplyService.createApply(userId, space.getId(), requestedSize, reason);
            return Result.success(apply);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/my-applies")
    public Result<List<SpaceApply>> getMyApplies(HttpSession session) {
        try {
            Long userId = SessionUtil.getUserId(session);
            if (userId == null) {
                return Result.error("未登录");
            }
            
            List<SpaceApply> applies = spaceApplyService.findByUserId(userId);
            return Result.success(applies);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/pending")
    public Result<List<SpaceApply>> getPendingApplies(HttpSession session) {
        try {
            // 只有管理员可以查看待审核申请
            if (!SessionUtil.isAdmin(session)) {
                return Result.error("无权限");
            }
            
            List<SpaceApply> applies = spaceApplyService.findPendingApplies();
            return Result.success(applies);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/approve/{applyId}")
    public Result<Void> approve(@PathVariable Long applyId,
                                @RequestParam String reviewComment,
                                HttpSession session) {
        try {
            // 只有管理员可以审批
            if (!SessionUtil.isAdmin(session)) {
                return Result.error("无权限");
            }
            
            Long reviewerId = SessionUtil.getUserId(session);
            spaceApplyService.approveApply(applyId, reviewComment, reviewerId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/reject/{applyId}")
    public Result<Void> reject(@PathVariable Long applyId,
                               @RequestParam String reviewComment,
                               HttpSession session) {
        try {
            // 只有管理员可以审批
            if (!SessionUtil.isAdmin(session)) {
                return Result.error("无权限");
            }
            
            Long reviewerId = SessionUtil.getUserId(session);
            spaceApplyService.rejectApply(applyId, reviewComment, reviewerId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}