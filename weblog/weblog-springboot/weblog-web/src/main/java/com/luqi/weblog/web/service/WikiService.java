package com.luqi.weblog.web.service;

import com.luqi.weblog.common.utils.Response;
import com.luqi.weblog.web.model.vo.wiki.FindWikiArticlePreNextReqVO;

public interface WikiService {
    Response findWikiList();
    Response findWikiCatalogList(Long id);
    Response findWikiArticlePreNext(FindWikiArticlePreNextReqVO findWikiArticlePreNextReqVO);
}