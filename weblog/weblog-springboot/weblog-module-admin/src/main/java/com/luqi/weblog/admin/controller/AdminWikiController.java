package com.luqi.weblog.admin.controller;

import com.luqi.weblog.admin.model.vo.wiki.*;
import com.luqi.weblog.admin.service.AdminWikiService;
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

@RestController
@RequestMapping("/admin/wiki")
@Api(tags = "Admin Wiki Module")
public class AdminWikiController {

    @Autowired
    private AdminWikiService adminWikiService;

    @PostMapping("/add")
    @ApiOperation(value = "Add Wiki")
    @ApiOperationLog(description = "Add Wiki")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response addWiki(@RequestBody @Validated AddWikiReqVO addWikiReqVO) {
        return adminWikiService.addWiki(addWikiReqVO);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "Delete Wiki")
    @ApiOperationLog(description = "Delete Wiki")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response deleteWiki(@RequestBody @Validated DeleteWikiReqVO deleteWikiReqVO) {
        return adminWikiService.deleteWiki(deleteWikiReqVO);
    }

    @PostMapping("/list")
    @ApiOperation(value = "Wiki Page List")
    @ApiOperationLog(description = "Wiki Page List")
    public Response findWikiPageList(@RequestBody @Validated FindWikiPageListReqVO findWikiPageListReqVO) {
        return adminWikiService.findWikiPageList(findWikiPageListReqVO);
    }

    @PostMapping("/isTop/update")
    @ApiOperation(value = "Update Wiki Top")
    @ApiOperationLog(description = "Update Wiki Top")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response updateWikiIsTop(@RequestBody @Validated UpdateWikiIsTopReqVO updateWikiIsTopReqVO) {
        return adminWikiService.updateWikiIsTop(updateWikiIsTopReqVO);
    }

    @PostMapping("/isPublish/update")
    @ApiOperation(value = "Update Wiki Publish")
    @ApiOperationLog(description = "Update Wiki Publish")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response updateWikiIsPublish(@RequestBody @Validated UpdateWikiIsPublishReqVO updateWikiIsPublishReqVO) {
        return adminWikiService.updateWikiIsPublish(updateWikiIsPublishReqVO);
    }

    @PostMapping("/update")
    @ApiOperation(value = "Update Wiki")
    @ApiOperationLog(description = "Update Wiki")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response updateWiki(@RequestBody @Validated UpdateWikiReqVO updateWikiReqVO) {
        return adminWikiService.updateWiki(updateWikiReqVO);
    }

    @PostMapping("/catalog/list")
    @ApiOperation(value = "Wiki Catalog List")
    @ApiOperationLog(description = "Wiki Catalog List")
    public Response findWikiCatalogList(@RequestBody @Validated DeleteWikiReqVO reqVO) { // Reusing DeleteWikiReqVO as it has ID
        return adminWikiService.findWikiCatalogList(reqVO.getId());
    }

    @PostMapping("/catalog/update")
    @ApiOperation(value = "Update Wiki Catalog")
    @ApiOperationLog(description = "Update Wiki Catalog")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response updateWikiCatalog(@RequestBody @Validated UpdateWikiCatalogReqVO updateWikiCatalogReqVO) {
        return adminWikiService.updateWikiCatalog(updateWikiCatalogReqVO);
    }
}