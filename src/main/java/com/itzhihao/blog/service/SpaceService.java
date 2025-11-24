package com.itzhihao.blog.service;

import com.itzhihao.blog.entity.Space;
import com.itzhihao.blog.mapper.SpaceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SpaceService {
    
    private final SpaceMapper spaceMapper;
    private static final long DEFAULT_SPACE_SIZE = 100 * 1024 * 1024L; // 100MB
    
    @Transactional
    public Space createSpace(Long userId) {
        Space space = new Space();
        space.setUserId(userId);
        space.setTotalSize(DEFAULT_SPACE_SIZE);
        space.setUsedSize(0L);
        space.setStatus(0); // 正常
        space.setTotalDownloads(0L);
        space.setCreateTime(LocalDateTime.now());
        space.setUpdateTime(LocalDateTime.now());
        
        spaceMapper.insert(space);
        return space;
    }
    
    public Space findByUserId(Long userId) {
        return spaceMapper.findByUserId(userId);
    }
    
    public Space findById(Long id) {
        return spaceMapper.findById(id);
    }
    
    @Transactional
    public boolean updateUsedSize(Long spaceId, long fileSize) {
        Space space = spaceMapper.findById(spaceId);
        if (space == null) {
            throw new RuntimeException("空间不存在");
        }
        
        long newUsedSize = space.getUsedSize() + fileSize;
        if (newUsedSize > space.getTotalSize()) {
            return false; // 空间不足
        }
        
        space.setUsedSize(newUsedSize);
        space.setUpdateTime(LocalDateTime.now());
        spaceMapper.update(space);
        return true;
    }
    
    public void freeSpace(Long spaceId, long fileSize) {
        Space space = spaceMapper.findById(spaceId);
        if (space == null) {
            return;
        }
        
        long newUsedSize = Math.max(0, space.getUsedSize() - fileSize);
        space.setUsedSize(newUsedSize);
        space.setUpdateTime(LocalDateTime.now());
        spaceMapper.update(space);
    }
    
    public void updateStatus(Long spaceId, Integer status) {
        spaceMapper.updateStatus(spaceId, status);
    }
    
    @Transactional
    public void expandSpace(Long spaceId, long additionalSize) {
        Space space = spaceMapper.findById(spaceId);
        if (space == null) {
            throw new RuntimeException("空间不存在");
        }
        
        space.setTotalSize(space.getTotalSize() + additionalSize);
        space.setUpdateTime(LocalDateTime.now());
        spaceMapper.update(space);
    }
    
    public void incrementDownloadCount(Long spaceId) {
        spaceMapper.incrementDownloadCount(spaceId);
    }
}