package com.movie.api.utils;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 上传文件到 MinIO。支持返回 MinIO 直链或本后端代理 URL（私有桶推荐代理）。
 */
@Slf4j
@Component
public class MinioUtils {

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp");

    @Resource
    private MinioClient minioClient;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.public-url:}")
    private String publicUrl;

    @Value("${minio.bucket-name}")
    private String bucketName;

    /** true：返回 /api/upload/view?key=… ，桶可不开放匿名读；false：返回 MinIO 直链（需桶可读或预签名） */
    @Value("${minio.use-proxy-url:true}")
    private boolean useProxyUrl;

    /** 浏览器访问后端的根地址，须与前端 config 里 API 域名一致（含端口） */
    @Value("${minio.proxy-base-url:http://127.0.0.1:8888}")
    private String proxyBaseUrl;

    public String upload(MultipartFile photo) throws Exception {
        if (photo == null || photo.isEmpty()) {
            throw new IllegalArgumentException("请选择文件");
        }
        String originalFilename = photo.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("文件名无效");
        }
        String ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXT.contains(ext)) {
            throw new IllegalArgumentException("仅支持 jpg / jpeg / png / gif / webp");
        }

        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            String objectName = UUID.randomUUID() + "." + ext;
            String contentType;
            switch (ext) {
                case "jpg":
                case "jpeg":
                    contentType = "image/jpeg";
                    break;
                case "png":
                    contentType = "image/png";
                    break;
                case "gif":
                    contentType = "image/gif";
                    break;
                case "webp":
                    contentType = "image/webp";
                    break;
                default:
                    contentType = "application/octet-stream";
                    break;
            }

            try (InputStream in = photo.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(in, photo.getSize(), -1)
                                .contentType(contentType)
                                .build());
            }

            return buildObjectUrl(objectName);
        } catch (Exception e) {
            Throwable t = e;
            while (t != null) {
                if (t instanceof ConnectException) {
                    throw new IllegalStateException(
                            "无法连接 MinIO（" + endpoint + "）。请确认 MinIO 已启动、网络可达且 minio.endpoint 正确。",
                            e);
                }
                t = t.getCause();
            }
            throw e;
        }
    }

    /**
     * 将 MinIO 对象流式输出到响应（供私有桶通过后端访问图片）。
     */
    public void writeObjectToResponse(String objectName, HttpServletResponse response) throws Exception {
        if (!isSafeObjectKey(objectName)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "非法对象名");
            return;
        }
        String ct = minioClient.statObject(
                StatObjectArgs.builder().bucket(bucketName).object(objectName).build()).contentType();
        if (ct != null && !ct.isBlank()) {
            response.setContentType(ct);
        } else {
            response.setContentType(guessContentType(objectName));
        }

        try (InputStream in = minioClient.getObject(
                GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
             OutputStream out = response.getOutputStream()) {
            in.transferTo(out);
        }
    }

    public void removeObject(String objectName) {
        if (objectName == null || objectName.isBlank()) {
            return;
        }
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : "";
            if (msg.contains("NoSuchKey") || msg.contains("404")) {
                return;
            }
            log.warn("MinIO 删除对象失败: {}", objectName, e);
        }
    }

    private boolean isSafeObjectKey(String key) {
        if (key == null || key.isBlank() || key.length() > 256) {
            return false;
        }
        return !key.contains("..") && !key.contains("/") && !key.contains("\\");
    }

    private String guessContentType(String objectName) {
        String ext = objectName.substring(objectName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        switch (ext) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            default:
                return "application/octet-stream";
        }
    }

    private String buildObjectUrl(String objectName) {
        if (useProxyUrl) {
            String base = proxyBaseUrl.trim();
            while (base.endsWith("/")) {
                base = base.substring(0, base.length() - 1);
            }
            return base + "/api/upload/view?key=" + URLEncoder.encode(objectName, StandardCharsets.UTF_8);
        }
        String base = (publicUrl != null && !publicUrl.isBlank()) ? publicUrl.trim() : endpoint.trim();
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "/" + bucketName + "/" + objectName;
    }
}
