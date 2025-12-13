package com.luqi.weblog.web.service;

import com.luqi.weblog.common.utils.Response;
import com.luqi.weblog.web.model.vo.wiki.FindWikiDetailReqVO;

import com.luqi.weblog.web.model.vo.wiki.FindWikiCatalogListReqVO;

public interface WikiService {
    /**
     * Get all wikis
     * @return
     */
    Response findWikiList();

    /**
     * Get wiki detail
     * @param findWikiDetailReqVO
     * @return
     */
    Response findWikiDetail(FindWikiDetailReqVO findWikiDetailReqVO);

    /**
     * Get wiki catalog list
     * @param findWikiCatalogListReqVO
     * @return
     */
    Response findWikiCatalogList(FindWikiCatalogListReqVO findWikiCatalogListReqVO);

    /**
     * 获取上下页
     * @param findWikiArticlePreNextReqVO
     * @return
     */
    Response findArticlePreNext(FindWikiArticlePreNextReqVO findWikiArticlePreNextReqVO);
}