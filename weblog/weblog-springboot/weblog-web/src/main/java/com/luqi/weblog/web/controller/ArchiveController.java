package com.luqi.weblog.web.controller;

import com.luqi.weblog.common.aspect.ApiOperationLog;
import com.luqi.weblog.common.utils.Response;
import com.luqi.weblog.web.model.vo.archive.FindArchiveArticlePageListReqVO;
import com.luqi.weblog.web.service.ArchiveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @date: 2023-09-15 14:01
 * @description: Article Archives
 **/
@RestController
@Api(tags = "Article Archive")
public class ArchiveController {

    @Autowired
    private ArchiveService archiveService;

    @PostMapping("/archive/list")
    @ApiOperation(value = "Get paginated article archive data")
    @ApiOperationLog(description = "Get paginated article archive data")
    public Response findArchivePageList(@RequestBody @Validated FindArchiveArticlePageListReqVO findArchiveArticlePageListReqVO) {
        return archiveService.findArchivePageList(findArchiveArticlePageListReqVO);
    }

}