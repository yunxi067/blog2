package com.itzhihao.blog.util;

import com.itzhihao.blog.entity.User;

import javax.servlet.http.HttpSession;

public class SessionUtil {
    
    private static final String USER_SESSION_KEY = "loginUser";
    
    public static void setLoginUser(HttpSession session, User user) {
        session.setAttribute(USER_SESSION_KEY, user);
    }
    
    public static User getLoginUser(HttpSession session) {
        return (User) session.getAttribute(USER_SESSION_KEY);
    }
    
    public static Long getUserId(HttpSession session) {
        User user = getLoginUser(session);
        return user != null ? user.getId() : null;
    }
    
    public static void removeLoginUser(HttpSession session) {
        session.removeAttribute(USER_SESSION_KEY);
    }
    
    public static boolean isLoggedIn(HttpSession session) {
        return getLoginUser(session) != null;
    }
    
    public static boolean isAdmin(HttpSession session) {
        User user = getLoginUser(session);
        return user != null && user.getRole() == 1;
    }
}