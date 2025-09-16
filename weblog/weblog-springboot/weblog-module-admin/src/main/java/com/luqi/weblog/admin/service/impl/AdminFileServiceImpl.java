package com.luqi.weblog.admin.service.impl;

import com.luqi.weblog.admin.model.vo.file.UploadFileRspVO;
import com.luqi.weblog.admin.model.vo.user.FindUserInfoRspVO;
import com.luqi.weblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.luqi.weblog.admin.service.AdminFileService;
import com.luqi.weblog.admin.service.AdminUserService;
import com.luqi.weblog.admin.utils.MinioUtil;
import com.luqi.weblog.common.domain.mapper.UserMapper;
import com.luqi.weblog.common.enums.ResponseCodeEnum;
import com.luqi.weblog.common.exception.BizException;
import com.luqi.weblog.common.utils.Response;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: 犬小哈
 * @url: www.quanxiaoha.com
 * @date: 2023-09-15 14:03
 * @description: 文件上传
 **/
@Service
@Slf4j
public class AdminFileServiceImpl implements AdminFileService {

    @Autowired
    private MinioUtil minioUtil;

    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    @Override
    public Response uploadFile(MultipartFile file) {
        try {
            // 上传文件
            String url = minioUtil.uploadFile(file);

            // 构建成功返参，将图片的访问链接返回
            return Response.success(UploadFileRspVO.builder().url(url).build());
        } catch (Exception e) {
            log.error("==> Error uploading file to Minio: ", e);
            // 手动抛出业务异常，提示 "文件上传失败"
            throw new BizException(ResponseCodeEnum.FILE_UPLOAD_FAILED);
        }
    }
}