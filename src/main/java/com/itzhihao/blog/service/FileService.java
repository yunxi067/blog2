package com.itzhihao.blog.service;

import com.itzhihao.blog.entity.FileInfo;
import com.itzhihao.blog.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {
    
    private final FileMapper fileMapper;
    private final SpaceService spaceService;
    
    @Transactional
    public FileInfo uploadFile(Long userId, Long spaceId, String fileName, String originalName, 
                             String filePath, String contentType, long fileSize, 
                             String category, String subCategory) {
        // 检查空间是否足够
        boolean spaceUpdated = spaceService.updateUsedSize(spaceId, fileSize);
        if (!spaceUpdated) {
            throw new RuntimeException("空间不足");
        }
        
        FileInfo fileInfo = new FileInfo();
        fileInfo.setUserId(userId);
        fileInfo.setSpaceId(spaceId);
        fileInfo.setFileName(fileName);
        fileInfo.setOriginalName(originalName);
        fileInfo.setFilePath(filePath);
        fileInfo.setContentType(contentType);
        fileInfo.setFileSize(fileSize);
        fileInfo.setCategory(category);
        fileInfo.setSubCategory(subCategory);
        fileInfo.setStatus(0); // 正常
        fileInfo.setDownloadCount(0L);
        fileInfo.setCreateTime(LocalDateTime.now());
        fileInfo.setUpdateTime(LocalDateTime.now());
        
        fileMapper.insert(fileInfo);
        return fileInfo;
    }
    
    public FileInfo findById(Long id) {
        return fileMapper.findById(id);
    }
    
    public List<FileInfo> findByUserId(Long userId) {
        return fileMapper.findByUserId(userId);
    }
    
    public List<FileInfo> findBySpaceId(Long spaceId) {
        return fileMapper.findBySpaceId(spaceId);
    }
    
    public List<FileInfo> findByCategory(Long userId, String category) {
        return fileMapper.findByCategory(userId, category);
    }
    
    @Transactional
    public void deleteFile(Long fileId) {
        FileInfo fileInfo = fileMapper.findById(fileId);
        if (fileInfo != null) {
            // 释放空间
            spaceService.freeSpace(fileInfo.getSpaceId(), fileInfo.getFileSize());
            // 删除文件记录
            fileMapper.deleteById(fileId);
        }
    }
    
    public void updateFileStatus(Long fileId, Integer status) {
        fileMapper.updateStatus(fileId, status);
    }
    
    public void updateFileInfo(FileInfo fileInfo) {
        fileInfo.setUpdateTime(LocalDateTime.now());
        fileMapper.update(fileInfo);
    }
    
    @Transactional
    public void incrementDownloadCount(Long fileId) {
        FileInfo fileInfo = fileMapper.findById(fileId);
        if (fileInfo != null) {
            fileMapper.incrementDownloadCount(fileId);
            spaceService.incrementDownloadCount(fileInfo.getSpaceId());
        }
    }
}