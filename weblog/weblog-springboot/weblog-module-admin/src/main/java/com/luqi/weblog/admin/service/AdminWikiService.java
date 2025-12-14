package com.luqi.weblog.admin.service;

import com.luqi.weblog.admin.model.vo.wiki.*;
import com.luqi.weblog.common.utils.Response;

import java.util.List;

public interface AdminWikiService {
    Response addWiki(AddWikiReqVO addWikiReqVO);
    Response deleteWiki(DeleteWikiReqVO deleteWikiReqVO);
    Response findWikiPageList(FindWikiPageListReqVO findWikiPageListReqVO);
    Response updateWikiIsTop(UpdateWikiIsTopReqVO updateWikiIsTopReqVO);
    Response updateWikiIsPublish(UpdateWikiIsPublishReqVO updateWikiIsPublishReqVO);
    Response updateWiki(UpdateWikiReqVO updateWikiReqVO);
    /**
     * 查询知识库目录
     * @param findWikiCatalogListReqVO
     * @return
     */
    Response findWikiCatalogList(FindWikiCatalogListReqVO findWikiCatalogListReqVO);
    /**
     * 更新知识库目录
     * @param updateWikiCatalogReqVO
     * @return
     */
    Response updateWikiCatalogs(UpdateWikiCatalogReqVO updateWikiCatalogReqVO);
}