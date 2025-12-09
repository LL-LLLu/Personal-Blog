package com.luqi.weblog.web.controller;

import com.luqi.weblog.common.aspect.ApiOperationLog;
import com.luqi.weblog.common.utils.Response;
import com.luqi.weblog.web.model.vo.wiki.FindWikiArticlePreNextReqVO;
import com.luqi.weblog.web.service.WikiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wiki")
@Api(tags = "Wiki Module")
public class WikiController {

    @Autowired
    private WikiService wikiService;

    @PostMapping("/list")
    @ApiOperation(value = "Get Wiki List")
    @ApiOperationLog(description = "Get Wiki List")
    public Response findWikiList() {
        return wikiService.findWikiList();
    }

    @PostMapping("/catalog/list")
    @ApiOperation(value = "Get Wiki Catalog")
    @ApiOperationLog(description = "Get Wiki Catalog")
    public Response findWikiCatalogList(@RequestBody @Validated FindWikiArticlePreNextReqVO reqVO) { // Using for ID
        return wikiService.findWikiCatalogList(reqVO.getId());
    }

    @PostMapping("/article/preNext")
    @ApiOperation(value = "Get Wiki Pre/Next Article")
    @ApiOperationLog(description = "Get Wiki Pre/Next Article")
    public Response findWikiArticlePreNext(@RequestBody @Validated FindWikiArticlePreNextReqVO reqVO) {
        return wikiService.findWikiArticlePreNext(reqVO);
    }
}