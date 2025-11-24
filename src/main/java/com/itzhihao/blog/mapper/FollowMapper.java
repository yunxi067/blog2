package com.itzhihao.blog.mapper;

import com.itzhihao.blog.entity.Follow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FollowMapper {
    
    int insert(Follow follow);
    
    int delete(@Param("followerId") Long followerId, @Param("followingId") Long followingId);
    
    boolean isFollowing(@Param("followerId") Long followerId, @Param("followingId") Long followingId);
    
    List<Follow> findFollowers(@Param("userId") Long userId);
    
    List<Follow> findFollowing(@Param("userId") Long userId);
    
    int countFollowers(@Param("userId") Long userId);
    
    int countFollowing(@Param("userId") Long userId);
}