package com.luqi.weblog.admin.controller;

import com.luqi.weblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.luqi.weblog.admin.service.AdminUserService;
import com.luqi.weblog.common.aspect.ApiOperationLog;
import com.luqi.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: luqi
 * @url: www.luqi//////////////////////////.com
 * @date: 2023-09-15 14:01
 * @description: User
 **/
@RestController
@RequestMapping("/admin")
@Api(tags = "Admin User Module")
public class AdminUserController {

    @Autowired
    private AdminUserService userService;

    @PostMapping("/password/update")
    @ApiOperation(value = "Change User Password")
    @ApiOperationLog(description = "Change User Password")
    public Response updatePassword(@RequestBody @Validated UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO) {
        return userService.updatePassword(updateAdminUserPasswordReqVO);
    }

    @PostMapping("/user/info")
    @ApiOperation(value = "Get User Information")
    @ApiOperationLog(description = "Get User Information")
    public Response findUserInfo() {
        return userService.findUserInfo();
    }

}