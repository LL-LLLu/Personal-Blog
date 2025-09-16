package com.luqi.weblog.web.controller;

import com.luqi.weblog.common.aspect.ApiOperationLog;
import com.luqi.weblog.common.utils.Response;
import com.luqi.weblog.web.model.vo.tag.FindTagArticlePageListReqVO;
import com.luqi.weblog.web.model.vo.tag.FindTagListReqVO;
import com.luqi.weblog.web.service.TagService;
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
 * @description: Tags
 **/
@RestController
@RequestMapping("/tag")
@Api(tags = "Tag")
public class TagController {

    @Autowired
    private TagService tagService;

    @PostMapping("/list")
    @ApiOperation(value = "Get tag list for frontend")
    @ApiOperationLog(description = "Get tag list for frontend")
    public Response findTagList(@RequestBody @Validated FindTagListReqVO findTagListReqVO) {
        return tagService.findTagList(findTagListReqVO);
    }


    @PostMapping("/article/list")
    @ApiOperation(value = "Get Article list under corresponding tags")
    @ApiOperationLog(description = "Get Article list under corresponding tags")
    public Response findTagPageList(@RequestBody @Validated FindTagArticlePageListReqVO findTagArticlePageListReqVO) {
        return tagService.findTagPageList(findTagArticlePageListReqVO);
    }
}