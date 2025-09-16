package com.luqi.weblog.admin.utils;

import com.luqi.weblog.admin.config.MinioProperties;
import com.luqi.weblog.common.exception.BizException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * @author: 犬小哈
 * @url: www.quanxiaoha.com
 * @date: 2023-05-11 9:02
 * @description: TODO
 **/
@Component
@Slf4j
public class MinioUtil {

    @Autowired
    private MinioProperties minioProperties;

    @Autowired
    private MinioClient minioClient;

    /**
     * 上传文件
     * @param file
     * @return
     * @throws Exception
     */
    public String uploadFile(MultipartFile file) throws Exception {
        // 判断文件是否为空
        if (file == null || file.getSize() == 0) {
            log.error("==> File upload error: File size is empty ...");
            throw new RuntimeException("File size cannot be empty");
        }

        // 文件的原始名称
        String originalFileName = file.getOriginalFilename();
        // 文件的 Content-Type
        String contentType = file.getContentType();

        // 生成存储对象的名称（将 UUID 字符串中的 - 替换成空字符串）
        String key = UUID.randomUUID().toString().replace("-", "");
        // 获取文件的后缀，如 .jpg
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));

        // 拼接上文件后缀，即为要存储的文件名
        String objectName = String.format("%s%s", key, suffix);

        log.info("==> Starting to upload file to Minio, ObjectName: {}", objectName);

        // 上传文件至 Minio
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(objectName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(contentType)
                .build());

        // 返回文件的访问链接 - 使用公共URL供浏览器访问
        String publicEndpoint = minioProperties.getPublicUrl() != null ? minioProperties.getPublicUrl() : minioProperties.getEndpoint();
        String url = String.format("%s/%s/%s", publicEndpoint, minioProperties.getBucketName(), objectName);
        log.info("==> File upload to Minio successful, access path: {}", url);
        return url;
    }
}