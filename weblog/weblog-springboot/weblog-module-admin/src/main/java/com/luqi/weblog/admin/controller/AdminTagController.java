package com.luqi.weblog.admin.controller;

import com.luqi.weblog.admin.model.vo.tag.AddTagReqVO;
import com.luqi.weblog.admin.model.vo.tag.DeleteTagReqVO;
import com.luqi.weblog.admin.model.vo.tag.FindTagPageListReqVO;
import com.luqi.weblog.admin.model.vo.tag.SearchTagsReqVO;
import com.luqi.weblog.admin.service.AdminTagService;
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
 * @description: Tag module
 **/
@RestController
@RequestMapping("/admin/tag")
@Api(tags = "Admin Tag Module")
public class AdminTagController {

    @Autowired
    private AdminTagService tagService;

    @PostMapping("/add")
    @ApiOperation(value = "Add tag")
    @ApiOperationLog(description = "Add tag")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response addTags(@RequestBody @Validated AddTagReqVO addTagReqVO) {
        return tagService.addTags(addTagReqVO);
    }

    @PostMapping("/list")
    @ApiOperation(value = "Get tag pagination data")
    @ApiOperationLog(description = "Get tag pagination data")
    public PageResponse findTagPageList(@RequestBody @Validated FindTagPageListReqVO findTagPageListReqVO) {
        return tagService.findTagPageList(findTagPageListReqVO);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "Delete tag")
    @ApiOperationLog(description = "Delete tag")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response deleteTag(@RequestBody @Validated DeleteTagReqVO deleteTagReqVO) {
        return tagService.deleteTag(deleteTagReqVO);
    }

    @PostMapping("/search")
    @ApiOperation(value = "Fuzzy search tags")
    @ApiOperationLog(description = "Fuzzy search tags")
    public Response searchTags(@RequestBody @Validated SearchTagsReqVO searchTagsReqVO) {
        return tagService.searchTags(searchTagsReqVO);
    }

    @PostMapping("/select/list")
    @ApiOperation(value = "Query tag Select list data")
    @ApiOperationLog(description = "Query tag Select list data")
    public Response findTagSelectList() {
        return tagService.findTagSelectList();
    }

}