package com.itzhihao.blog.controller;

import com.itzhihao.blog.common.Result;
import com.itzhihao.blog.entity.FileInfo;
import com.itzhihao.blog.service.FileService;
import com.itzhihao.blog.service.SpaceService;
import com.itzhihao.blog.service.UserService;
import com.itzhihao.blog.util.SessionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/download")
@RequiredArgsConstructor
@Slf4j
public class DownloadController {
    
    private final FileService fileService;
    private final SpaceService spaceService;
    private final UserService userService;
    
    private static final String UPLOAD_DIR = "uploads";
    
    @GetMapping("/file/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId, HttpSession session) {
        try {
            // 检查用户是否登录
            Long userId = SessionUtil.getUserId(session);
            if (userId == null) {
                return ResponseEntity.badRequest().build();
            }
            
            // 获取文件信息
            FileInfo fileInfo = fileService.findById(fileId);
            if (fileInfo == null) {
                log.warn("文件不存在: {}", fileId);
                return ResponseEntity.notFound().build();
            }
            
            // 检查文件状态
            if (fileInfo.getStatus() == 1) {
                log.warn("文件已被冻结: {}", fileId);
                return ResponseEntity.badRequest().build();
            }
            
            // 检查用户权限（只能下载自己的文件，管理员可以下载所有文件）
            var currentUser = userService.findById(userId);
            if (!fileInfo.getUserId().equals(userId) && currentUser.getRole() != 1) {
                log.warn("用户 {} 无权限下载文件 {}", userId, fileId);
                return ResponseEntity.badRequest().build();
            }
            
            // 检查用户状态
            var user = userService.findById(fileInfo.getUserId());
            if (user.getStatus() == 1) {
                log.warn("用户已被冻结: {}", fileInfo.getUserId());
                return ResponseEntity.badRequest().build();
            }
            
            // 检查空间状态
            var space = spaceService.findById(fileInfo.getSpaceId());
            if (space.getStatus() == 1) {
                log.warn("空间已被冻结: {}", fileInfo.getSpaceId());
                return ResponseEntity.badRequest().build();
            }
            
            // 构建文件路径
            String filePath = UPLOAD_DIR + File.separator + fileInfo.getFilePath();
            File file = new File(filePath);
            if (!file.exists()) {
                log.warn("物理文件不存在: {}", filePath);
                return ResponseEntity.notFound().build();
            }
            
            // 创建资源
            Resource resource = new FileSystemResource(file);
            
            // 增加下载次数
            fileService.incrementDownloadCount(fileId);
            
            // 设置响应头
            String encodedFileName = URLEncoder.encode(fileInfo.getOriginalName(), StandardCharsets.UTF_8.toString());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename=\"" + encodedFileName + "\"; filename*=utf-8''" + encodedFileName)
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("下载文件失败: fileId={}", fileId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}