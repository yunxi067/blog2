package com.itzhihao.blog.service;

import com.itzhihao.blog.entity.User;
import com.itzhihao.blog.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    
    @Transactional
    public User register(String username, String password, String email, String nickname) {
        // 检查用户名是否已存在
        User existUser = userMapper.findByUsername(username);
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        existUser = userMapper.findByEmail(email);
        if (existUser != null) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        user.setEmail(email);
        user.setNickname(nickname != null ? nickname : username);
        user.setRole(0); // 普通用户
        user.setStatus(0); // 正常状态
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        userMapper.insert(user);
        return user;
    }
    
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        if (user.getStatus() == 1) {
            throw new RuntimeException("账户已被冻结");
        }
        
        return user;
    }
    
    public User findById(Long id) {
        return userMapper.findById(id);
    }
    
    public void updateStatus(Long userId, Integer status) {
        userMapper.updateStatus(userId, status);
    }
    
    public void update(User user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
    }
}