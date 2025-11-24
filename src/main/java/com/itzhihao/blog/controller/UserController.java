package com.itzhihao.blog.controller;

import com.itzhihao.blog.common.Result;
import com.itzhihao.blog.entity.User;
import com.itzhihao.blog.entity.Space;
import com.itzhihao.blog.service.UserService;
import com.itzhihao.blog.service.SpaceService;
import com.itzhihao.blog.util.SessionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final SpaceService spaceService;
    
    @PostMapping("/register")
    public Result<User> register(@RequestParam String username,
                                @RequestParam String password,
                                @RequestParam String email,
                                @RequestParam(required = false) String nickname) {
        try {
            User user = userService.register(username, password, email, nickname);
            // 创建用户空间
            spaceService.createSpace(user.getId());
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/login")
    public Result<User> login(@RequestParam String username,
                             @RequestParam String password,
                             HttpSession session) {
        try {
            User user = userService.login(username, password);
            // 保存用户信息到session
            SessionUtil.setLoginUser(session, user);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        SessionUtil.removeLoginUser(session);
        return Result.success();
    }
    
    @GetMapping("/info")
    public Result<User> getUserInfo(HttpSession session) {
        User user = SessionUtil.getLoginUser(session);
        if (user == null) {
            return Result.error("未登录");
        }
        return Result.success(user);
    }
    
    @PostMapping("/update")
    public Result<Void> updateUser(@RequestBody User user, HttpSession session) {
        try {
            User currentUser = SessionUtil.getLoginUser(session);
            if (currentUser == null) {
                return Result.error("未登录");
            }
            
            // 只允许更新自己的信息
            user.setId(currentUser.getId());
            userService.update(user);
            
            // 更新session中的用户信息
            SessionUtil.setLoginUser(session, user);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/space")
    public Result<Space> getUserSpace(HttpSession session) {
        User user = SessionUtil.getLoginUser(session);
        if (user == null) {
            return Result.error("未登录");
        }
        
        Space space = spaceService.findByUserId(user.getId());
        return Result.success(space);
    }
}