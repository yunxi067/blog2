package com.itzhihao.blog.service;

import com.itzhihao.blog.entity.SpaceApply;
import com.itzhihao.blog.mapper.SpaceApplyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpaceApplyService {
    
    private final SpaceApplyMapper spaceApplyMapper;
    private final SpaceService spaceService;
    
    @Transactional
    public SpaceApply createApply(Long userId, Long spaceId, Long requestedSize, String reason) {
        SpaceApply apply = new SpaceApply();
        apply.setUserId(userId);
        apply.setSpaceId(spaceId);
        apply.setRequestedSize(requestedSize);
        apply.setStatus(0); // 待审核
        apply.setReason(reason);
        apply.setCreateTime(LocalDateTime.now());
        
        spaceApplyMapper.insert(apply);
        return apply;
    }
    
    public SpaceApply findById(Long id) {
        return spaceApplyMapper.findById(id);
    }
    
    public List<SpaceApply> findByUserId(Long userId) {
        return spaceApplyMapper.findByUserId(userId);
    }
    
    public List<SpaceApply> findPendingApplies() {
        return spaceApplyMapper.findByStatus(0);
    }
    
    public List<SpaceApply> findAll() {
        return spaceApplyMapper.findAll();
    }
    
    @Transactional
    public void approveApply(Long applyId, String reviewComment, Long reviewerId) {
        SpaceApply apply = spaceApplyMapper.findById(applyId);
        if (apply == null) {
            throw new RuntimeException("申请不存在");
        }
        
        if (apply.getStatus() != 0) {
            throw new RuntimeException("申请已处理");
        }
        
        // 扩容空间
        spaceService.expandSpace(apply.getSpaceId(), apply.getRequestedSize());
        
        // 更新申请状态
        spaceApplyMapper.updateStatus(applyId, 1, reviewComment, reviewerId);
    }
    
    @Transactional
    public void rejectApply(Long applyId, String reviewComment, Long reviewerId) {
        SpaceApply apply = spaceApplyMapper.findById(applyId);
        if (apply == null) {
            throw new RuntimeException("申请不存在");
        }
        
        if (apply.getStatus() != 0) {
            throw new RuntimeException("申请已处理");
        }
        
        // 更新申请状态
        spaceApplyMapper.updateStatus(applyId, 2, reviewComment, reviewerId);
    }
}