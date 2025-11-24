package com.itzhihao.blog.mapper;

import com.itzhihao.blog.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FileMapper {
    
    int insert(FileInfo fileInfo);
    
    FileInfo findById(@Param("id") Long id);
    
    List<FileInfo> findByUserId(@Param("userId") Long userId);
    
    List<FileInfo> findBySpaceId(@Param("spaceId") Long spaceId);
    
    List<FileInfo> findByCategory(@Param("userId") Long userId, @Param("category") String category);
    
    List<FileInfo> findByStatus(@Param("status") Integer status);
    
    int update(FileInfo fileInfo);
    
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    int incrementDownloadCount(@Param("id") Long id);
    
    int deleteById(@Param("id") Long id);
    
    int deleteByUserId(@Param("userId") Long userId);
}