package com.luqi.weblog.admin.controller;

import com.luqi.weblog.admin.model.vo.category.AddCategoryReqVO;
import com.luqi.weblog.admin.model.vo.category.DeleteCategoryReqVO;
import com.luqi.weblog.admin.model.vo.category.FindCategoryPageListReqVO;
import com.luqi.weblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.luqi.weblog.admin.service.AdminCategoryService;
import com.luqi.weblog.admin.service.AdminUserService;
import com.luqi.weblog.common.aspect.ApiOperationLog;
import com.luqi.weblog.common.utils.PageResponse;
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
 * @description: Category
 **/
@RestController
@RequestMapping("/admin")
@Api(tags = "Admin Category Module")
public class AdminCategoryController {

    @Autowired
    private AdminCategoryService categoryService;

    @PostMapping("/category/add")
    @ApiOperation(value = "Add Category")
    @ApiOperationLog(description = "Add Category")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response addCategory(@RequestBody @Validated AddCategoryReqVO addCategoryReqVO) {
        return categoryService.addCategory(addCategoryReqVO);
    }

    @PostMapping("/category/list")
    @ApiOperation(value = "Get Paginated Category Data")
    @ApiOperationLog(description = "Get Paginated Category Data")
    public PageResponse findCategoryPageList(@RequestBody @Validated FindCategoryPageListReqVO findCategoryPageListReqVO) {
        return categoryService.findCategoryPageList(findCategoryPageListReqVO);
    }

    @PostMapping("/category/delete")
    @ApiOperation(value = "Delete Category")
    @ApiOperationLog(description = "Delete Category")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response deleteCategory(@RequestBody @Validated DeleteCategoryReqVO deleteCategoryReqVO) {
        return categoryService.deleteCategory(deleteCategoryReqVO);
    }

    @PostMapping("/category/select/list")
    @ApiOperation(value = "Get Category Select Dropdown List Data")
    @ApiOperationLog(description = "Get Category Select Dropdown List Data")
    public Response findCategorySelectList() {
        return categoryService.findCategorySelectList();
    }

}