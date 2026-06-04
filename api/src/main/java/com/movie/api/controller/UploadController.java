package com.movie.api.controller;

import com.movie.api.annotation.DisableBaseResponse;
import com.movie.api.mapper.UploadMapper;
import com.movie.api.model.entity.Upload;
import com.movie.api.service.UploadService;
import com.movie.api.utils.MinioUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 上传图片存放为二进制到mysql
 */
@RestController
@Tag(name = "上传接口")
@RequestMapping("/api/upload")
public class UploadController {

    @Resource
    private MinioUtils minioUtils;

    @Resource
    private UploadService uploadService;

    @Resource
    private UploadMapper uploadMapper;

    @PostMapping("")
    @Operation(summary = "上传图片")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_WORKER')")
    @DisableBaseResponse
    public String upload(MultipartFile file) throws Exception {
        if (file == null) {
            throw new Exception("请求参数缺失");
        }
        if (file.isEmpty()) {
            throw new Exception("上传失败，请选择文件");
        }
        return minioUtils.upload(file);
    }

    @DeleteMapping("")
    @Operation(summary = "删除图片")
    public void delete(@RequestParam("id") String id) {
        // 兼容历史：id 为 t_upload 主键时删库；否则按 MinIO 对象名删除
        Upload legacy = uploadMapper.selectById(id);
        if (legacy != null) {
            uploadService.deleteById(id);
            return;
        }
        minioUtils.removeObject(id);
    }

    /**
     * 从 MinIO 读对象并写出（私有桶时上传接口返回的即为本地址，无需桶匿名读）。
     */
    @GetMapping("/view")
    @Operation(summary = "读取 MinIO 对象（代理）")
    @PermitAll
    @DisableBaseResponse
    public void viewMinio(@RequestParam("key") String key, HttpServletResponse response) throws Exception {
        minioUtils.writeObjectToResponse(key, response);
    }

    @GetMapping("")
    @Operation(summary = "获取图片")
    @PermitAll
    @DisableBaseResponse
    public void get(@RequestParam("id") String id, HttpServletResponse response) throws Exception {
        if ("".equals(id)) {
            return;
        }
        Upload upload = uploadMapper.selectById(id);
        if (upload == null) {
            throw new Exception("图片不存在");
        }
        byte[] data = upload.getBytes();
        response.setContentType("image/jpeg");
        response.setCharacterEncoding("UTF-8");
        OutputStream outputStream = response.getOutputStream();
        InputStream in = new ByteArrayInputStream(data);
        int len;
        byte[] buf = new byte[1024];
        while ((len = in.read(buf, 0, 1024)) != -1) {
            outputStream.write(buf, 0, len);
        }
        outputStream.close();
    }

}
