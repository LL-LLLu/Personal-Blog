package com.luqi.weblog.admin.controller;

import com.luqi.weblog.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import com.luqi.weblog.admin.service.AdminBlogSettingsService;
import com.luqi.weblog.admin.service.AdminFileService;
import com.luqi.weblog.common.aspect.ApiOperationLog;
import com.luqi.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: lu qi
 * @url: www.luqi.com
 * @date: 2023-09-15 14:01
 * @description: File Module
 **/
@RestController
@RequestMapping("/admin")
@Api(tags = "Admin File Module")
public class AdminFileController {

    @Autowired
    private AdminFileService fileService;

    @PostMapping("/file/upload")
    @ApiOperation(value = "File Upload")
    @ApiOperationLog(description = "File Upload")
    public Response uploadFile(@RequestParam MultipartFile file) {
        return fileService.uploadFile(file);
    }

}