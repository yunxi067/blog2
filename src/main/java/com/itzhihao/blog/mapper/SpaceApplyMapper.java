package com.itzhihao.blog.mapper;

import com.itzhihao.blog.entity.SpaceApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SpaceApplyMapper {
    
    int insert(SpaceApply spaceApply);
    
    SpaceApply findById(@Param("id") Long id);
    
    List<SpaceApply> findByUserId(@Param("userId") Long userId);
    
    List<SpaceApply> findByStatus(@Param("status") Integer status);
    
    List<SpaceApply> findAll();
    
    int update(SpaceApply spaceApply);
    
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, 
                    @Param("reviewComment") String reviewComment, 
                    @Param("reviewerId") Long reviewerId);
    
    int deleteById(@Param("id") Long id);
}