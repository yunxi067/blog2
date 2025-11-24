// 全局变量
let currentUser = null;

// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', function() {
    checkLoginStatus();
    loadNotifications();
});

// 检查登录状态
function checkLoginStatus() {
    fetch('/user/info')
        .then(response => response.json())
        .then(data => {
            if (data.code === 200) {
                currentUser = data.data;
                showDashboard();
            }
        })
        .catch(error => console.error('检查登录状态失败:', error));
}

// 登录
function login() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    
    if (!username || !password) {
        alert('请输入用户名和密码');
        return;
    }
    
    const formData = new FormData();
    formData.append('username', username);
    formData.append('password', password);
    
    fetch('/user/login', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.code === 200) {
            currentUser = data.data;
            location.reload();
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        console.error('登录失败:', error);
        alert('登录失败');
    });
}

// 注册
function register() {
    const username = document.getElementById('regUsername').value;
    const password = document.getElementById('regPassword').value;
    const email = document.getElementById('email').value;
    const nickname = document.getElementById('nickname').value;
    
    if (!username || !password || !email) {
        alert('请填写必填项');
        return;
    }
    
    const formData = new FormData();
    formData.append('username', username);
    formData.append('password', password);
    formData.append('email', email);
    if (nickname) {
        formData.append('nickname', nickname);
    }
    
    fetch('/user/register', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.code === 200) {
            alert('注册成功，请登录');
            bootstrap.Modal.getInstance(document.getElementById('registerModal')).hide();
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        console.error('注册失败:', error);
        alert('注册失败');
    });
}

// 退出登录
function logout() {
    if (confirm('确定要退出登录吗？')) {
        fetch('/user/logout', {
            method: 'POST'
        })
        .then(response => response.json())
        .then(data => {
            if (data.code === 200) {
                location.reload();
            }
        })
        .catch(error => {
            console.error('退出失败:', error);
            location.reload();
        });
    }
}

// 显示控制台
function showDashboard() {
    if (!currentUser) return;
    
    const content = `
        <div class="row">
            <div class="col-12">
                <h3>控制台</h3>
                <div class="row mt-4">
                    <div class="col-md-3">
                        <div class="card text-center">
                            <div class="card-body">
                                <h5 class="card-title">
                                    <i class="bi bi-files text-primary"></i>
                                </h5>
                                <p class="card-text">文件总数</p>
                                <h3 id="totalFiles">-</h3>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card text-center">
                            <div class="card-body">
                                <h5 class="card-title">
                                    <i class="bi bi-hdd text-success"></i>
                                </h5>
                                <p class="card-text">空间使用</p>
                                <h3 id="spaceUsage">-</h3>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card text-center">
                            <div class="card-body">
                                <h5 class="card-title">
                                    <i class="bi bi-download text-info"></i>
                                </h5>
                                <p class="card-text">总下载次数</p>
                                <h3 id="totalDownloads">-</h3>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card text-center">
                            <div class="card-body">
                                <h5 class="card-title">
                                    <i class="bi bi-people text-warning"></i>
                                </h5>
                                <p class="card-text">关注数</p>
                                <h3 id="followCount">-</h3>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    document.getElementById('contentArea').innerHTML = content;
    loadDashboardData();
}

// 加载控制台数据
function loadDashboardData() {
    // 加载文件列表
    fetch('/file/list')
        .then(response => response.json())
        .then(data => {
            if (data.code === 200) {
                document.getElementById('totalFiles').textContent = data.data.length;
            }
        });
    
    // 加载空间信息
    fetch('/user/space')
        .then(response => response.json())
        .then(data => {
            if (data.code === 200) {
                const space = data.data;
                const usagePercent = (space.usedSize / space.totalSize * 100).toFixed(1);
                document.getElementById('spaceUsage').textContent = usagePercent + '%';
                document.getElementById('totalDownloads').textContent = space.totalDownloads;
            }
        });
}

// 显示文件列表
function showFileList() {
    if (!currentUser) return;
    
    const content = `
        <div class="row">
            <div class="col-12">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h3>文件管理</h3>
                    <div>
                        <select class="form-select d-inline-block w-auto" onchange="filterFiles(this.value)">
                            <option value="">所有分类</option>
                            <option value="image">图片</option>
                            <option value="video">视频</option>
                            <option value="audio">音频</option>
                            <option value="document">文档</option>
                            <option value="other">其他</option>
                        </select>
                    </div>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>文件名</th>
                                <th>大小</th>
                                <th>分类</th>
                                <th>下载次数</th>
                                <th>上传时间</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody id="fileTableBody">
                            <tr>
                                <td colspan="6" class="text-center">加载中...</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    `;
    
    document.getElementById('contentArea').innerHTML = content;
    loadFileList('');
}

// 加载文件列表
function loadFileList(category) {
    const url = category ? `/file/list?category=${category}` : '/file/list';
    
    fetch(url)
        .then(response => response.json())
        .then(data => {
            if (data.code === 200) {
                renderFileList(data.data);
            } else {
                alert(data.message);
            }
        })
        .catch(error => {
            console.error('加载文件列表失败:', error);
            document.getElementById('fileTableBody').innerHTML = 
                '<tr><td colspan="6" class="text-center text-danger">加载失败</td></tr>';
        });
}

// 渲染文件列表
function renderFileList(files) {
    const tbody = document.getElementById('fileTableBody');
    
    if (files.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">暂无文件</td></tr>';
        return;
    }
    
    tbody.innerHTML = files.map(file => `
        <tr class="file-item">
            <td>
                <i class="bi ${getFileIcon(file.contentType)}"></i>
                ${file.originalName}
            </td>
            <td>${formatFileSize(file.fileSize)}</td>
            <td><span class="badge bg-secondary">${file.category || '其他'}</span></td>
            <td>${file.downloadCount}</td>
            <td>${formatDate(file.createTime)}</td>
            <td>
                <button class="btn btn-sm btn-primary" onclick="downloadFile(${file.id})">
                    <i class="bi bi-download"></i>
                </button>
                <button class="btn btn-sm btn-warning" onclick="editFile(${file.id})">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-sm btn-danger" onclick="deleteFile(${file.id})">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

// 获取文件图标
function getFileIcon(contentType) {
    if (!contentType) return 'bi-file-earmark';
    
    if (contentType.startsWith('image/')) return 'bi-file-earmark-image';
    if (contentType.startsWith('video/')) return 'bi-file-earmark-play';
    if (contentType.startsWith('audio/')) return 'bi-file-earmark-music';
    if (contentType.includes('pdf')) return 'bi-file-earmark-pdf';
    if (contentType.includes('word') || contentType.includes('document')) return 'bi-file-earmark-word';
    if (contentType.includes('excel') || contentType.includes('spreadsheet')) return 'bi-file-earmark-excel';
    if (contentType.includes('powerpoint') || contentType.includes('presentation')) return 'bi-file-earmark-slides';
    if (contentType.includes('zip') || contentType.includes('rar')) return 'bi-file-earmark-zip';
    if (contentType.includes('text')) return 'bi-file-earmark-text';
    
    return 'bi-file-earmark';
}

// 格式化文件大小
function formatFileSize(bytes) {
    if (bytes === 0) return '0 B';
    
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

// 格式化日期
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleString('zh-CN');
}

// 下载文件
function downloadFile(fileId) {
    window.open(`/download/file/${fileId}`, '_blank');
}

// 删除文件
function deleteFile(fileId) {
    if (confirm('确定要删除这个文件吗？')) {
        fetch(`/file/delete/${fileId}`, {
            method: 'POST'
        })
        .then(response => response.json())
        .then(data => {
            if (data.code === 200) {
                alert('删除成功');
                loadFileList('');
            } else {
                alert(data.message);
            }
        })
        .catch(error => {
            console.error('删除失败:', error);
            alert('删除失败');
        });
    }
}

// 筛选文件
function filterFiles(category) {
    loadFileList(category);
}

// 显示上传页面
function showUpload() {
    if (!currentUser) return;
    
    const content = `
        <div class="row">
            <div class="col-12">
                <h3>文件上传</h3>
                <div class="card mt-3">
                    <div class="card-body">
                        <form id="uploadForm" enctype="multipart/form-data">
                            <div class="mb-3">
                                <label for="fileInput" class="form-label">选择文件</label>
                                <input type="file" class="form-control" id="fileInput" required>
                            </div>
                            <div class="mb-3">
                                <label for="category" class="form-label">分类</label>
                                <select class="form-select" id="category">
                                    <option value="image">图片</option>
                                    <option value="video">视频</option>
                                    <option value="audio">音频</option>
                                    <option value="document">文档</option>
                                    <option value="other">其他</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label for="subCategory" class="form-label">二级分类</label>
                                <input type="text" class="form-control" id="subCategory" placeholder="可选">
                            </div>
                            <button type="button" class="btn btn-primary" onclick="uploadFile()">
                                <i class="bi bi-cloud-upload"></i> 上传文件
                            </button>
                        </form>
                        <div id="uploadProgress" class="mt-3 d-none">
                            <div class="progress">
                                <div class="progress-bar" role="progressbar" style="width: 0%">0%</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    document.getElementById('contentArea').innerHTML = content;
}

// 上传文件
function uploadFile() {
    const fileInput = document.getElementById('fileInput');
    const category = document.getElementById('category').value;
    const subCategory = document.getElementById('subCategory').value;
    
    if (!fileInput.files[0]) {
        alert('请选择文件');
        return;
    }
    
    const formData = new FormData();
    formData.append('file', fileInput.files[0]);
    formData.append('category', category);
    if (subCategory) {
        formData.append('subCategory', subCategory);
    }
    
    // 显示进度条
    const progressDiv = document.getElementById('uploadProgress');
    const progressBar = progressDiv.querySelector('.progress-bar');
    progressDiv.classList.remove('d-none');
    
    fetch('/file/upload', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        progressDiv.classList.add('d-none');
        if (data.code === 200) {
            alert('上传成功');
            fileInput.value = '';
            subCategory.value = '';
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        progressDiv.classList.add('d-none');
        console.error('上传失败:', error);
        alert('上传失败');
    });
}

// 显示空间信息
function showSpaceInfo() {
    if (!currentUser) return;
    
    const content = `
        <div class="row">
            <div class="col-12">
                <h3>空间管理</h3>
                <div class="card mt-3">
                    <div class="card-body" id="spaceInfo">
                        <p class="text-center">加载中...</p>
                    </div>
                </div>
                
                <div class="card mt-3">
                    <div class="card-header">
                        <h5>扩容申请</h5>
                    </div>
                    <div class="card-body">
                        <form id="applyForm">
                            <div class="mb-3">
                                <label for="requestedSize" class="form-label">申请扩容大小 (MB)</label>
                                <input type="number" class="form-control" id="requestedSize" min="1" required>
                            </div>
                            <div class="mb-3">
                                <label for="reason" class="form-label">申请理由</label>
                                <textarea class="form-control" id="reason" rows="3" required></textarea>
                            </div>
                            <button type="button" class="btn btn-primary" onclick="submitApply()">提交申请</button>
                        </form>
                    </div>
                </div>
                
                <div class="card mt-3">
                    <div class="card-header">
                        <h5>申请记录</h5>
                    </div>
                    <div class="card-body">
                        <div id="applyList">
                            <p class="text-center">加载中...</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    document.getElementById('contentArea').innerHTML = content;
    loadSpaceInfo();
    loadApplyList();
}

// 加载空间信息
function loadSpaceInfo() {
    fetch('/user/space')
        .then(response => response.json())
        .then(data => {
            if (data.code === 200) {
                const space = data.data;
                const usagePercent = (space.usedSize / space.totalSize * 100).toFixed(1);
                
                document.getElementById('spaceInfo').innerHTML = `
                    <div class="row">
                        <div class="col-md-6">
                            <p><strong>总空间:</strong> ${formatFileSize(space.totalSize)}</p>
                            <p><strong>已用空间:</strong> ${formatFileSize(space.usedSize)}</p>
                            <p><strong>剩余空间:</strong> ${formatFileSize(space.totalSize - space.usedSize)}</p>
                        </div>
                        <div class="col-md-6">
                            <p><strong>总下载次数:</strong> ${space.totalDownloads}</p>
                            <p><strong>状态:</strong> 
                                <span class="badge ${space.status === 0 ? 'bg-success' : 'bg-danger'}">
                                    ${space.status === 0 ? '正常' : '冻结'}
                                </span>
                            </p>
                        </div>
                    </div>
                    <div class="mt-3">
                        <div class="progress" style="height: 25px;">
                            <div class="progress-bar" role="progressbar" 
                                 style="width: ${usagePercent}%">${usagePercent}%</div>
                        </div>
                    </div>
                `;
            } else {
                document.getElementById('spaceInfo').innerHTML = 
                    '<p class="text-danger">加载空间信息失败</p>';
            }
        })
        .catch(error => {
            console.error('加载空间信息失败:', error);
            document.getElementById('spaceInfo').innerHTML = 
                '<p class="text-danger">加载空间信息失败</p>';
        });
}

// 加载申请记录
function loadApplyList() {
    fetch('/space-apply/my-applies')
        .then(response => response.json())
        .then(data => {
            if (data.code === 200) {
                renderApplyList(data.data);
            } else {
                document.getElementById('applyList').innerHTML = '<p class="text-danger">加载失败</p>';
            }
        })
        .catch(error => {
            console.error('加载申请记录失败:', error);
            document.getElementById('applyList').innerHTML = '<p class="text-danger">加载失败</p>';
        });
}

// 渲染申请记录
function renderApplyList(applies) {
    const container = document.getElementById('applyList');
    
    if (applies.length === 0) {
        container.innerHTML = '<p class="text-muted">暂无申请记录</p>';
        return;
    }
    
    container.innerHTML = applies.map(apply => {
        const statusClass = apply.status === 0 ? 'warning' : (apply.status === 1 ? 'success' : 'danger');
        const statusText = apply.status === 0 ? '待审核' : (apply.status === 1 ? '已通过' : '已驳回');
        
        return `
            <div class="card mb-2">
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-8">
                            <h6 class="card-title">申请扩容: ${formatFileSize(apply.requestedSize)}</h6>
                            <p class="card-text">${apply.reason}</p>
                            <small class="text-muted">
                                申请时间: ${formatDate(apply.createTime)}
                                ${apply.reviewTime ? `| 审核时间: ${formatDate(apply.reviewTime)}` : ''}
                            </small>
                        </div>
                        <div class="col-md-4 text-end">
                            <span class="badge bg-${statusClass}">${statusText}</span>
                            ${apply.reviewComment ? `<br><small class="text-muted">${apply.reviewComment}</small>` : ''}
                        </div>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

// 提交扩容申请
function submitApply() {
    const requestedSize = document.getElementById('requestedSize').value;
    const reason = document.getElementById('reason').value;
    
    if (!requestedSize || !reason) {
        alert('请填写完整信息');
        return;
    }
    
    const sizeInBytes = requestedSize * 1024 * 1024; // 转换为字节
    
    fetch('/space-apply/apply', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `requestedSize=${sizeInBytes}&reason=${encodeURIComponent(reason)}`
    })
    .then(response => response.json())
    .then(data => {
        if (data.code === 200) {
            alert('申请提交成功，请等待管理员审核');
            document.getElementById('requestedSize').value = '';
            document.getElementById('reason').value = '';
            loadApplyList();
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        console.error('提交申请失败:', error);
        alert('提交申请失败');
    });
}

// 显示热门排行
function showHotList() {
    const content = `
        <div class="row">
            <div class="col-12">
                <h3>热门排行</h3>
                <p class="text-muted">展示最受欢迎的文件和用户</p>
                
                <div class="row mt-4">
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-header">
                                <h5><i class="bi bi-fire text-danger"></i> 热门文件</h5>
                            </div>
                            <div class="card-body">
                                <div id="hotFilesList">
                                    <p class="text-center">加载中...</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-header">
                                <h5><i class="bi bi-trophy text-warning"></i> 活跃用户</h5>
                            </div>
                            <div class="card-body">
                                <div id="hotUsersList">
                                    <p class="text-center">加载中...</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    document.getElementById('contentArea').innerHTML = content;
    loadHotList();
}

// 加载热门数据
function loadHotList() {
    fetch('/hot/list')
        .then(response => response.json())
        .then(data => {
            if (data.code === 200) {
                renderHotFiles(data.data.hotFiles || []);
                renderHotUsers(data.data.hotUsers || []);
            } else {
                document.getElementById('hotFilesList').innerHTML = '<p class="text-danger">加载失败</p>';
                document.getElementById('hotUsersList').innerHTML = '<p class="text-danger">加载失败</p>';
            }
        })
        .catch(error => {
            console.error('加载热门数据失败:', error);
            document.getElementById('hotFilesList').innerHTML = '<p class="text-danger">加载失败</p>';
            document.getElementById('hotUsersList').innerHTML = '<p class="text-danger">加载失败</p>';
        });
}

// 渲染热门文件
function renderHotFiles(files) {
    const container = document.getElementById('hotFilesList');
    
    if (files.length === 0) {
        container.innerHTML = '<p class="text-muted">暂无热门文件</p>';
        return;
    }
    
    container.innerHTML = files.map((file, index) => `
        <div class="d-flex align-items-center mb-3">
            <div class="me-3">
                <span class="badge bg-${index < 3 ? 'danger' : 'secondary'} rounded-circle">
                    ${index + 1}
                </span>
            </div>
            <div class="flex-grow-1">
                <div class="fw-bold">${file.originalName}</div>
                <small class="text-muted">
                    <i class="bi bi-download"></i> ${file.downloadCount} 次 | 
                    <i class="bi bi-person"></i> ${file.userId}
                </small>
            </div>
            <div>
                <button class="btn btn-sm btn-outline-primary" onclick="downloadFile(${file.id})">
                    下载
                </button>
            </div>
        </div>
    `).join('');
}

// 渲染热门用户
function renderHotUsers(users) {
    const container = document.getElementById('hotUsersList');
    
    if (users.length === 0) {
        container.innerHTML = '<p class="text-muted">暂无活跃用户</p>';
        return;
    }
    
    container.innerHTML = users.map((user, index) => `
        <div class="d-flex align-items-center mb-3">
            <div class="me-3">
                <span class="badge bg-${index < 3 ? 'warning' : 'secondary'} rounded-circle">
                    ${index + 1}
                </span>
            </div>
            <div class="flex-grow-1">
                <div class="fw-bold">${user.nickname || user.username}</div>
                <small class="text-muted">
                    <i class="bi bi-download"></i> 总下载: ${user.totalDownloads || 0} | 
                    <i class="bi bi-hdd"></i> 文件: ${user.fileCount || 0}
                </small>
            </div>
            <div>
                <button class="btn btn-sm btn-outline-success" onclick="followUser(${user.id})">
                    <i class="bi bi-person-plus"></i> 关注
                </button>
            </div>
        </div>
    `).join('');
}

// 关注用户
function followUser(userId) {
    if (!currentUser) {
        alert('请先登录');
        return;
    }
    
    fetch(`/follow/${userId}`, {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        if (data.code === 200) {
            alert('关注成功');
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        console.error('关注失败:', error);
        alert('关注失败');
    });
}

// 显示通知
function showNotifications() {
    if (!currentUser) return;
    
    const content = `
        <div class="row">
            <div class="col-12">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h3>通知</h3>
                    <button class="btn btn-sm btn-outline-primary" onclick="markAllAsRead()">全部标记已读</button>
                </div>
                <div id="notificationList">
                    <p class="text-center">加载中...</p>
                </div>
            </div>
        </div>
    `;
    
    document.getElementById('contentArea').innerHTML = content;
    loadNotifications();
}

// 加载通知
function loadNotifications() {
    fetch('/notification/list')
        .then(response => response.json())
        .then(data => {
            if (data.code === 200) {
                renderNotifications(data.data);
                updateNotificationBadge();
            } else {
                document.getElementById('notificationList').innerHTML = '<p class="text-danger">加载失败</p>';
            }
        })
        .catch(error => {
            console.error('加载通知失败:', error);
            document.getElementById('notificationList').innerHTML = '<p class="text-danger">加载失败</p>';
        });
}

// 渲染通知列表
function renderNotifications(notifications) {
    const container = document.getElementById('notificationList');
    
    if (notifications.length === 0) {
        container.innerHTML = '<p class="text-muted">暂无通知</p>';
        return;
    }
    
    container.innerHTML = notifications.map(notification => `
        <div class="card mb-2 ${notification.status === 0 ? 'border-primary' : ''}">
            <div class="card-body py-2">
                <div class="d-flex justify-content-between align-items-start">
                    <div class="flex-grow-1">
                        <h6 class="card-title mb-1">${notification.title}</h6>
                        <p class="card-text small text-muted">${notification.content}</p>
                        <small class="text-muted">
                            <i class="bi bi-clock"></i> ${formatDate(notification.createTime)}
                            ${notification.status === 0 ? '<span class="badge bg-primary ms-2">未读</span>' : ''}
                        </small>
                    </div>
                    <div class="btn-group btn-group-sm">
                        ${notification.status === 0 ? 
                            `<button class="btn btn-outline-primary" onclick="markAsRead(${notification.id})">标记已读</button>` : 
                            ''}
                        <button class="btn btn-outline-danger" onclick="deleteNotification(${notification.id})">删除</button>
                    </div>
                </div>
            </div>
        </div>
    `).join('');
}

// 更新通知徽章
function updateNotificationBadge() {
    fetch('/notification/unread-count')
        .then(response => response.json())
        .then(data => {
            if (data.code === 200) {
                const badge = document.getElementById('notificationBadge');
                if (data.data > 0) {
                    badge.textContent = data.data;
                    badge.classList.remove('d-none');
                } else {
                    badge.classList.add('d-none');
                }
            }
        })
        .catch(error => console.error('获取未读数量失败:', error));
}

// 标记单个通知为已读
function markAsRead(notificationId) {
    fetch(`/notification/read/${notificationId}`, {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        if (data.code === 200) {
            loadNotifications();
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        console.error('标记已读失败:', error);
        alert('操作失败');
    });
}

// 删除通知
function deleteNotification(notificationId) {
    if (confirm('确定要删除这条通知吗？')) {
        fetch(`/notification/${notificationId}`, {
            method: 'DELETE'
        })
        .then(response => response.json())
        .then(data => {
            if (data.code === 200) {
                loadNotifications();
            } else {
                alert(data.message);
            }
        })
        .catch(error => {
            console.error('删除通知失败:', error);
            alert('删除失败');
        });
    }
}

// 全部标记已读
function markAllAsRead() {
    fetch('/notification/read-all', {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        if (data.code === 200) {
            loadNotifications();
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        console.error('标记全部已读失败:', error);
        alert('操作失败');
    });
}

// 编辑文件
function editFile(fileId) {
    // 这里可以实现文件编辑功能
    alert('编辑功能开发中...');
}

// 显示管理后台
function showAdminPanel() {
    if (!currentUser || currentUser.role !== 1) {
        alert('您没有权限访问管理后台');
        return;
    }
    
    const content = `
        <div class="row">
            <div class="col-12">
                <h3>管理后台</h3>
                
                <!-- 统计信息 -->
                <div class="row mb-4">
                    <div class="col-md-3">
                        <div class="card text-center">
                            <div class="card-body">
                                <h5 class="card-title">
                                    <i class="bi bi-people text-primary"></i>
                                </h5>
                                <p class="card-text">总用户数</p>
                                <h3 id="totalUsers">-</h3>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card text-center">
                            <div class="card-body">
                                <h5 class="card-title">
                                    <i class="bi bi-files text-success"></i>
                                </h5>
                                <p class="card-text">总文件数</p>
                                <h3 id="totalFiles">-</h3>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card text-center">
                            <div class="card-body">
                                <h5 class="card-title">
                                    <i class="bi bi-hdd text-info"></i>
                                </h5>
                                <p class="card-text">总存储</p>
                                <h3 id="totalStorage">-</h3>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card text-center">
                            <div class="card-body">
                                <h5 class="card-title">
                                    <i class="bi bi-clock-history text-warning"></i>
                                </h5>
                                <p class="card-text">待审核</p>
                                <h3 id="pendingApplies">-</h3>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- 管理功能标签页 -->
                <ul class="nav nav-tabs" id="adminTabs" role="tablist">
                    <li class="nav-item" role="presentation">
                        <button class="nav-link active" id="users-tab" data-bs-toggle="tab" data-bs-target="#users" type="button" role="tab">
                            <i class="bi bi-people"></i> 用户管理
                        </button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link" id="files-tab" data-bs-toggle="tab" data-bs-target="#files" type="button" role="tab">
                            <i class="bi bi-files"></i> 文件管理
                        </button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link" id="applies-tab" data-bs-toggle="tab" data-bs-target="#applies" type="button" role="tab">
                            <i class="bi bi-clipboard-check"></i> 扩容申请
                        </button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link" id="spaces-tab" data-bs-toggle="tab" data-bs-target="#spaces" type="button" role="tab">
                            <i class="bi bi-hdd"></i> 空间管理
                        </button>
                    </li>
                </ul>
                
                <div class="tab-content" id="adminTabContent">
                    <!-- 用户管理 -->
                    <div class="tab-pane fade show active" id="users" role="tabpanel">
                        <div class="card mt-3">
                            <div class="card-body">
                                <p class="text-center">用户管理功能开发中...</p>
                            </div>
                        </div>
                    </div>
                    
                    <!-- 文件管理 -->
                    <div class="tab-pane fade" id="files" role="tabpanel">
                        <div class="card mt-3">
                            <div class="card-body">
                                <p class="text-center">文件管理功能开发中...</p>
                            </div>
                        </div>
                    </div>
                    
                    <!-- 扩容申请 -->
                    <div class="tab-pane fade" id="applies" role="tabpanel">
                        <div class="card mt-3">
                            <div class="card-body">
                                <h5>待审核申请</h5>
                                <div id="pendingAppliesList">
                                    <p class="text-center">加载中...</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- 空间管理 -->
                    <div class="tab-pane fade" id="spaces" role="tabpanel">
                        <div class="card mt-3">
                            <div class="card-body">
                                <p class="text-center">空间管理功能开发中...</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    document.getElementById('contentArea').innerHTML = content;
    loadAdminStats();
    loadPendingApplies();
}

// 加载管理员统计信息
function loadAdminStats() {
    fetch('/admin/stats')
        .then(response => response.json())
        .then(data => {
            if (data.code === 200) {
                const stats = data.data;
                document.getElementById('totalUsers').textContent = stats.totalUsers || 0;
                document.getElementById('totalFiles').textContent = stats.totalFiles || 0;
                document.getElementById('totalStorage').textContent = formatFileSize(stats.totalStorage || 0);
                document.getElementById('pendingApplies').textContent = stats.pendingApplies || 0;
            }
        })
        .catch(error => console.error('加载统计信息失败:', error));
}

// 加载待审核申请
function loadPendingApplies() {
    fetch('/admin/applies/pending')
        .then(response => response.json())
        .then(data => {
            if (data.code === 200) {
                renderPendingApplies(data.data);
            } else {
                document.getElementById('pendingAppliesList').innerHTML = '<p class="text-danger">加载失败</p>';
            }
        })
        .catch(error => {
            console.error('加载待审核申请失败:', error);
            document.getElementById('pendingAppliesList').innerHTML = '<p class="text-danger">加载失败</p>';
        });
}

// 渲染待审核申请
function renderPendingApplies(applies) {
    const container = document.getElementById('pendingAppliesList');
    
    if (applies.length === 0) {
        container.innerHTML = '<p class="text-muted">暂无待审核申请</p>';
        return;
    }
    
    container.innerHTML = applies.map(apply => `
        <div class="card mb-3">
            <div class="card-body">
                <div class="row">
                    <div class="col-md-8">
                        <h6 class="card-title">用户ID: ${apply.userId} 申请扩容</h6>
                        <p class="card-text">${apply.reason}</p>
                        <p class="card-text">
                            <strong>申请大小:</strong> ${formatFileSize(apply.requestedSize)}<br>
                            <strong>申请时间:</strong> ${formatDate(apply.createTime)}
                        </p>
                    </div>
                    <div class="col-md-4">
                        <div class="btn-group-vertical w-100" role="group">
                            <button class="btn btn-success" onclick="approveApply(${apply.id})">
                                <i class="bi bi-check-circle"></i> 通过
                            </button>
                            <button class="btn btn-danger" onclick="rejectApply(${apply.id})">
                                <i class="bi bi-x-circle"></i> 驳回
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `).join('');
}

// 通过申请
function approveApply(applyId) {
    const reviewComment = prompt('请输入审核意见（可选）:');
    
    fetch(`/admin/apply/${applyId}/approve`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `reviewComment=${encodeURIComponent(reviewComment || '')}`
    })
    .then(response => response.json())
    .then(data => {
        if (data.code === 200) {
            alert('申请已通过');
            loadPendingApplies();
            loadAdminStats();
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        console.error('审批失败:', error);
        alert('审批失败');
    });
}

// 驳回申请
function rejectApply(applyId) {
    const reviewComment = prompt('请输入驳回理由:');
    
    if (!reviewComment) {
        alert('请输入驳回理由');
        return;
    }
    
    fetch(`/admin/apply/${applyId}/reject`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `reviewComment=${encodeURIComponent(reviewComment)}`
    })
    .then(response => response.json())
    .then(data => {
        if (data.code === 200) {
            alert('申请已驳回');
            loadPendingApplies();
            loadAdminStats();
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        console.error('审批失败:', error);
        alert('审批失败');
    });
}

// 全部标记已读
function markAllAsRead() {
    // 这里需要实现标记全部已读的接口
    document.getElementById('notificationBadge').classList.add('d-none');
}