package com.luqi.weblog.web.controller;

import com.luqi.weblog.common.aspect.ApiOperationLog;
import com.luqi.weblog.common.utils.Response;
import com.luqi.weblog.web.model.vo.article.FindArticleDetailReqVO;
import com.luqi.weblog.web.model.vo.article.FindIndexArticlePageListReqVO;
import com.luqi.weblog.web.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Lu Qi
 * @url: www.luqi.com
 * @date: 2023-09-15 14:01
 * @description: Article
 **/
@RestController
@RequestMapping("/article")
@Api(tags = "Article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping("/list")
    @ApiOperation(value = "Get homepage article pagination data")
    @ApiOperationLog(description = "Get homepage article pagination data")
    public Response findArticlePageList(@RequestBody FindIndexArticlePageListReqVO findIndexArticlePageListReqVO) {
        return articleService.findArticlePageList(findIndexArticlePageListReqVO);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "Get article details")
    @ApiOperationLog(description = "Get article details")
    public Response findArticleDetail(@RequestBody FindArticleDetailReqVO findArticleDetailReqVO) {
        return articleService.findArticleDetail(findArticleDetailReqVO);
    }

}