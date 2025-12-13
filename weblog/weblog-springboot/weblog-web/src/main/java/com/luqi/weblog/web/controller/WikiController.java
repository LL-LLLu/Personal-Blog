package com.luqi.weblog.web.controller;

import com.luqi.weblog.common.aspect.ApiOperationLog;
import com.luqi.weblog.common.utils.Response;
import com.luqi.weblog.web.model.vo.wiki.FindWikiDetailReqVO;
import com.luqi.weblog.web.service.WikiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luqi.weblog.web.model.vo.wiki.FindWikiCatalogListReqVO;

@RestController
@RequestMapping("/wiki")
@Api(tags = "Wiki Module")
public class WikiController {

    @Autowired
    private WikiService wikiService;

    @PostMapping("/list")
    @ApiOperation(value = "Get all wikis")
    @ApiOperationLog(description = "Get all wikis")
    public Response findWikiList() {
        return wikiService.findWikiList();
    }

    @PostMapping("/detail")
    @ApiOperation(value = "Get wiki detail")
    @ApiOperationLog(description = "Get wiki detail")
    public Response findWikiDetail(@RequestBody @Validated FindWikiDetailReqVO findWikiDetailReqVO) {
        return wikiService.findWikiDetail(findWikiDetailReqVO);
    }

    @PostMapping("/catalog/list")
    @ApiOperation(value = "Get wiki catalog list")
    @ApiOperationLog(description = "Get wiki catalog list")
    public Response findWikiCatalogList(@RequestBody @Validated FindWikiCatalogListReqVO findWikiCatalogListReqVO) {
        return wikiService.findWikiCatalogList(findWikiCatalogListReqVO);
    }

    @PostMapping("/article/preNext")
    @ApiOperation(value = "Get wiki article pre next")
    @ApiOperationLog(description = "Get wiki article pre next")
    public Response findArticlePreNext(@RequestBody FindWikiArticlePreNextReqVO findWikiArticlePreNextReqVO) {
        return wikiService.findArticlePreNext(findWikiArticlePreNextReqVO);
    }
}