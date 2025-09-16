package com.luqi.weblog.admin.controller;

import com.luqi.weblog.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import com.luqi.weblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.luqi.weblog.admin.service.AdminBlogSettingsService;
import com.luqi.weblog.admin.service.AdminUserService;
import com.luqi.weblog.common.aspect.ApiOperationLog;
import com.luqi.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @date: 2023-09-15 14:01
 * @description: Blog Settings
 **/
@RestController
@RequestMapping("/admin/blog/settings")
@Api(tags = "Admin Blog Settings Module")
public class AdminBlogSettingsController {

    @Autowired
    private AdminBlogSettingsService blogSettingsService;

    @PostMapping("/update")
    @ApiOperation(value = "Update Blog Basic Information")
    @ApiOperationLog(description = "Update Blog Basic Information")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response updateBlogSettings(@RequestBody @Validated UpdateBlogSettingsReqVO updateBlogSettingsReqVO) {
        return blogSettingsService.updateBlogSettings(updateBlogSettingsReqVO);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "Get Blog Settings Details")
    @ApiOperationLog(description = "Get Blog Settings Details")
    public Response findDetail() {
        return blogSettingsService.findDetail();
    }

}