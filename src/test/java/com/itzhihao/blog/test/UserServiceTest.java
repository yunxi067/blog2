package com.itzhihao.blog.test;

import com.itzhihao.blog.entity.User;
import com.itzhihao.blog.entity.Space;
import com.itzhihao.blog.service.UserService;
import com.itzhihao.blog.service.SpaceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;
    
    @Autowired
    private SpaceService spaceService;

    @Test
    public void testRegisterUser() {
        // 测试用户注册
        User user = userService.register("testuser", "password123", "test@example.com", "测试用户");
        
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("测试用户", user.getNickname());
        assertEquals(0, user.getRole()); // 普通用户
        assertEquals(0, user.getStatus()); // 正常状态
        
        // 验证空间是否自动创建
        Space space = spaceService.findByUserId(user.getId());
        assertNotNull(space);
        assertEquals(user.getId(), space.getUserId());
        assertTrue(space.getTotalSize() > 0); // 默认空间大小
        assertEquals(0L, space.getUsedSize());
        assertEquals(0L, space.getTotalDownloads());
    }

    @Test
    public void testLoginUser() {
        // 先注册用户
        User registeredUser = userService.register("logintest", "password123", "login@example.com", "登录测试");
        
        // 测试登录
        User loginUser = userService.login("logintest", "password123");
        
        assertNotNull(loginUser);
        assertEquals(registeredUser.getId(), loginUser.getId());
        assertEquals("logintest", loginUser.getUsername());
    }

    @Test
    public void testDuplicateUsername() {
        // 注册第一个用户
        userService.register("duplicate", "password123", "duplicate1@example.com", "重复用户1");
        
        // 尝试注册相同用户名，应该抛出异常
        assertThrows(RuntimeException.class, () -> {
            userService.register("duplicate", "password456", "duplicate2@example.com", "重复用户2");
        });
    }

    @Test
    public void testSpaceManagement() {
        // 创建用户和空间
        User user = userService.register("spacetest", "password123", "space@example.com", "空间测试");
        Space space = spaceService.findByUserId(user.getId());
        
        // 测试空间更新
        boolean updated = spaceService.updateUsedSize(space.getId(), 1024 * 1024); // 1MB
        assertTrue(updated);
        
        // 验证空间使用量
        Space updatedSpace = spaceService.findById(space.getId());
        assertEquals(1024 * 1024, updatedSpace.getUsedSize());
        
        // 测试空间释放
        spaceService.freeSpace(space.getId(), 512 * 1024); // 释放512KB
        Space freedSpace = spaceService.findById(space.getId());
        assertEquals(512 * 1024, freedSpace.getUsedSize());
    }
}