package com.itzhihao.blog.controller;

import com.itzhihao.blog.common.Result;
import com.itzhihao.blog.entity.FileInfo;
import com.itzhihao.blog.service.FileService;
import com.itzhihao.blog.service.SpaceService;
import com.itzhihao.blog.util.SessionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {
    
    private final FileService fileService;
    private final SpaceService spaceService;
    
    private static final String UPLOAD_DIR = "uploads";
    
    @PostMapping("/upload")
    public Result<FileInfo> upload(@RequestParam("file") MultipartFile file,
                                   @RequestParam(required = false) String category,
                                   @RequestParam(required = false) String subCategory,
                                   HttpSession session) {
        try {
            // 检查用户是否登录
            Long userId = SessionUtil.getUserId(session);
            if (userId == null) {
                return Result.error("未登录");
            }
            
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            // 获取用户空间
            var space = spaceService.findByUserId(userId);
            if (space == null) {
                return Result.error("用户空间不存在");
            }
            
            // 检查空间状态
            if (space.getStatus() == 1) {
                return Result.error("空间已被冻结");
            }
            
            // 生成文件名和路径
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String fileName = UUID.randomUUID().toString() + fileExtension;
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String relativePath = datePath + "/" + fileName;
            String fullPath = UPLOAD_DIR + File.separator + relativePath;
            
            // 创建目录
            Path uploadPath = Paths.get(fullPath).getParent();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // 保存文件
            file.transferTo(new File(fullPath));
            
            // 保存文件信息到数据库
            FileInfo fileInfo = fileService.uploadFile(
                userId, 
                space.getId(), 
                fileName, 
                originalFilename, 
                relativePath, 
                file.getContentType(), 
                file.getSize(), 
                category != null ? category : "other",
                subCategory
            );
            
            return Result.success(fileInfo);
        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/list")
    public Result<List<FileInfo>> getFileList(@RequestParam(required = false) String category,
                                             HttpSession session) {
        try {
            Long userId = SessionUtil.getUserId(session);
            if (userId == null) {
                return Result.error("未登录");
            }
            
            List<FileInfo> files;
            if (category != null && !category.trim().isEmpty()) {
                files = fileService.findByCategory(userId, category);
            } else {
                files = fileService.findByUserId(userId);
            }
            
            return Result.success(files);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/info/{fileId}")
    public Result<FileInfo> getFileInfo(@PathVariable Long fileId, HttpSession session) {
        try {
            Long userId = SessionUtil.getUserId(session);
            if (userId == null) {
                return Result.error("未登录");
            }
            
            FileInfo fileInfo = fileService.findById(fileId);
            if (fileInfo == null) {
                return Result.error("文件不存在");
            }
            
            // 检查权限
            if (!fileInfo.getUserId().equals(userId)) {
                return Result.error("无权限访问该文件");
            }
            
            return Result.success(fileInfo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/update/{fileId}")
    public Result<Void> updateFile(@PathVariable Long fileId,
                                   @RequestBody FileInfo fileInfo,
                                   HttpSession session) {
        try {
            Long userId = SessionUtil.getUserId(session);
            if (userId == null) {
                return Result.error("未登录");
            }
            
            FileInfo existingFile = fileService.findById(fileId);
            if (existingFile == null) {
                return Result.error("文件不存在");
            }
            
            // 检查权限
            if (!existingFile.getUserId().equals(userId)) {
                return Result.error("无权限修改该文件");
            }
            
            // 更新文件信息
            fileInfo.setId(fileId);
            fileInfo.setUserId(userId);
            fileInfo.setSpaceId(existingFile.getSpaceId());
            fileService.updateFileInfo(fileInfo);
            
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/delete/{fileId}")
    public Result<Void> deleteFile(@PathVariable Long fileId, HttpSession session) {
        try {
            Long userId = SessionUtil.getUserId(session);
            if (userId == null) {
                return Result.error("未登录");
            }
            
            FileInfo fileInfo = fileService.findById(fileId);
            if (fileInfo == null) {
                return Result.error("文件不存在");
            }
            
            // 检查权限
            if (!fileInfo.getUserId().equals(userId)) {
                return Result.error("无权限删除该文件");
            }
            
            // 删除物理文件
            String filePath = UPLOAD_DIR + File.separator + fileInfo.getFilePath();
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
            }
            
            // 删除数据库记录
            fileService.deleteFile(fileId);
            
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/freeze/{fileId}")
    public Result<Void> freezeFile(@PathVariable Long fileId,
                                   @RequestParam Integer status,
                                   HttpSession session) {
        try {
            Long userId = SessionUtil.getUserId(session);
            if (userId == null) {
                return Result.error("未登录");
            }
            
            FileInfo fileInfo = fileService.findById(fileId);
            if (fileInfo == null) {
                return Result.error("文件不存在");
            }
            
            // 检查权限
            if (!fileInfo.getUserId().equals(userId)) {
                return Result.error("无权限操作该文件");
            }
            
            fileService.updateFileStatus(fileId, status);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}