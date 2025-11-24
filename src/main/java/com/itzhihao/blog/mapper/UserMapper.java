package com.itzhihao.blog.mapper;

import com.itzhihao.blog.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    
    int insert(User user);
    
    User findById(@Param("id") Long id);
    
    User findByUsername(@Param("username") String username);
    
    User findByEmail(@Param("email") String email);
    
    int update(User user);
    
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    int deleteById(@Param("id") Long id);
}