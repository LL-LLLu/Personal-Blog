package com.luqi.weblog.web.controller;

import com.luqi.weblog.common.aspect.ApiOperationLog;
import com.luqi.weblog.common.utils.Response;
import com.luqi.weblog.web.model.vo.category.FindCategoryArticlePageListReqVO;
import com.luqi.weblog.web.model.vo.category.FindCategoryListReqVO;
import com.luqi.weblog.web.service.CategoryService;
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
 * @url: www.luqi.com
 * @date: 2023-09-15 14:01
 * @description: Categories
 **/
@RestController
@RequestMapping("/category")
@Api(tags = "Categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/list")
    @ApiOperation(value = "Get category list for frontend")
    @ApiOperationLog(description = "Get category list for frontend")
    public Response findCategoryList(@RequestBody @Validated FindCategoryListReqVO findCategoryListReqVO) {
        return categoryService.findCategoryList(findCategoryListReqVO);
    }

    @PostMapping("/article/list")
    @ApiOperation(value = "Get paginated article data under a category for frontend")
    @ApiOperationLog(description  = "Get paginated article data under a category for frontend")
    public Response findCategoryArticlePageList(@RequestBody @Validated FindCategoryArticlePageListReqVO findCategoryArticlePageListReqVO) {
        return categoryService.findCategoryArticlePageList(findCategoryArticlePageListReqVO);
    }

}