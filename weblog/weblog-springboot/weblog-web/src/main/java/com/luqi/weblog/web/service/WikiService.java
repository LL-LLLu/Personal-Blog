package com.luqi.weblog.web.service;

import com.luqi.weblog.common.utils.Response;
import com.luqi.weblog.web.model.vo.wiki.FindWikiDetailReqVO;

public interface WikiService {
    /**
     * Get all wikis
     * @return
     */
    Response findAllWiki();

    /**
     * Get wiki detail
     * @param findWikiDetailReqVO
     * @return
     */
    Response findWikiDetail(FindWikiDetailReqVO findWikiDetailReqVO);
}