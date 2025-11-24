<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>博客文件管理系统</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        .sidebar {
            min-height: 100vh;
            background-color: #f8f9fa;
        }
        .main-content {
            min-height: 100vh;
        }
        .file-item:hover {
            background-color: #f0f0f0;
            cursor: pointer;
        }
        .notification-badge {
            position: absolute;
            top: -5px;
            right: -5px;
            background-color: red;
            color: white;
            border-radius: 50%;
            padding: 2px 6px;
            font-size: 12px;
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- 侧边栏 -->
            <div class="col-md-2 sidebar p-3">
                <h5 class="mb-4">文件管理系统</h5>
                
                <div class="mb-3">
                    <c:if test="${empty sessionScope.loginUser}">
                        <a href="#loginModal" class="btn btn-primary w-100 mb-2" data-bs-toggle="modal">
                            <i class="bi bi-box-arrow-in-right"></i> 登录
                        </a>
                        <a href="#registerModal" class="btn btn-outline-primary w-100" data-bs-toggle="modal">
                            <i class="bi bi-person-plus"></i> 注册
                        </a>
                    </c:if>
                    
                    <c:if test="${not empty sessionScope.loginUser}">
                        <div class="card">
                            <div class="card-body">
                                <h6 class="card-subtitle mb-2 text-muted">
                                    <i class="bi bi-person-circle"></i> 
                                    ${sessionScope.loginUser.nickname}
                                </h6>
                                <p class="card-text small">
                                    <c:if test="${sessionScope.loginUser.role == 1}">
                                        <span class="badge bg-danger">管理员</span>
                                    </c:if>
                                    <c:if test="${sessionScope.loginUser.role == 0}">
                                        <span class="badge bg-primary">用户</span>
                                    </c:if>
                                </p>
                                <button class="btn btn-sm btn-outline-danger w-100" onclick="logout()">
                                    <i class="bi bi-box-arrow-right"></i> 退出
                                </button>
                            </div>
                        </div>
                    </c:if>
                </div>
                
                <c:if test="${not empty sessionScope.loginUser}">
                    <ul class="nav flex-column">
                        <li class="nav-item">
                            <a class="nav-link active" href="#" onclick="showDashboard()">
                                <i class="bi bi-speedometer2"></i> 控制台
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="#" onclick="showFileList()">
                                <i class="bi bi-folder"></i> 文件管理
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="#" onclick="showUpload()">
                                <i class="bi bi-cloud-upload"></i> 文件上传
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="#" onclick="showSpaceInfo()">
                                <i class="bi bi-hdd"></i> 空间管理
                            </a>
                        </li>
                        <c:if test="${sessionScope.loginUser.role == 1}">
                            <li class="nav-item">
                                <a class="nav-link" href="#" onclick="showAdminPanel()">
                                    <i class="bi bi-gear"></i> 管理后台
                                </a>
                            </li>
                        </c:if>
                        <li class="nav-item">
                            <a class="nav-link" href="#" onclick="showHotList()">
                                <i class="bi bi-fire"></i> 热门排行
                            </a>
                        </li>
                        <li class="nav-item position-relative">
                            <a class="nav-link" href="#" onclick="showNotifications()">
                                <i class="bi bi-bell"></i> 通知
                                <span id="notificationBadge" class="notification-badge d-none">0</span>
                            </a>
                        </li>
                    </ul>
                </c:if>
            </div>
            
            <!-- 主内容区 -->
            <div class="col-md-10 main-content p-4">
                <div id="contentArea">
                    <!-- 默认显示首页 -->
                    <div class="row">
                        <div class="col-12">
                            <h2>欢迎使用博客文件管理系统</h2>
                            <p class="text-muted">一个功能完整的文件分享和管理平台</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 登录模态框 -->
    <div class="modal fade" id="loginModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">登录</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <form id="loginForm">
                        <div class="mb-3">
                            <label for="username" class="form-label">用户名</label>
                            <input type="text" class="form-control" id="username" required>
                        </div>
                        <div class="mb-3">
                            <label for="password" class="form-label">密码</label>
                            <input type="password" class="form-control" id="password" required>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" onclick="login()">登录</button>
                </div>
            </div>
        </div>
    </div>

    <!-- 注册模态框 -->
    <div class="modal fade" id="registerModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">注册</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <form id="registerForm">
                        <div class="mb-3">
                            <label for="regUsername" class="form-label">用户名</label>
                            <input type="text" class="form-control" id="regUsername" required>
                        </div>
                        <div class="mb-3">
                            <label for="regPassword" class="form-label">密码</label>
                            <input type="password" class="form-control" id="regPassword" required>
                        </div>
                        <div class="mb-3">
                            <label for="email" class="form-label">邮箱</label>
                            <input type="email" class="form-control" id="email" required>
                        </div>
                        <div class="mb-3">
                            <label for="nickname" class="form-label">昵称</label>
                            <input type="text" class="form-control" id="nickname">
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" onclick="register()">注册</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="js/main.js"></script>
</body>
</html>