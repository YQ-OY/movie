package com.movie.api.controller;

import com.movie.api.annotation.DisableBaseResponse;
import com.movie.api.utils.MinioUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@Tag(name = "上传接口")
@RequestMapping("/api/upload")
public class UploadController {

    @Resource
    private MinioUtils minioUtils;

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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_WORKER')")
    public void delete(@RequestParam("id") String id) {
        minioUtils.removeObject(minioUtils.resolveObjectKey(id));
    }

    @GetMapping("/view")
    @Operation(summary = "读取 MinIO 对象（代理）")
    @PermitAll
    @DisableBaseResponse
    public void viewMinio(@RequestParam("key") String key, HttpServletResponse response) throws Exception {
        minioUtils.writeObjectToResponse(key, response);
    }

}
