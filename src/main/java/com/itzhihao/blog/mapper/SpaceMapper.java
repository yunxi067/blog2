package com.itzhihao.blog.mapper;

import com.itzhihao.blog.entity.Space;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SpaceMapper {
    
    int insert(Space space);
    
    Space findById(@Param("id") Long id);
    
    Space findByUserId(@Param("userId") Long userId);
    
    int update(Space space);
    
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    int updateUsedSize(@Param("id") Long id, @Param("usedSize") Long usedSize);
    
    int incrementDownloadCount(@Param("id") Long id);
    
    int deleteById(@Param("id") Long id);
}