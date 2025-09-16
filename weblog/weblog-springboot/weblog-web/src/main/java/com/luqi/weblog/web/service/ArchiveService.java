package com.luqi.weblog.web.service;

import com.luqi.weblog.common.utils.Response;
import com.luqi.weblog.web.model.vo.archive.FindArchiveArticlePageListReqVO;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @date: 2023-09-15 14:03
 * @description: 归档文章
 **/
public interface ArchiveService {
    /**
     * 获取文章归档分页数据
     * @param findArchiveArticlePageListReqVO
     * @return
     */
    Response findArchivePageList(FindArchiveArticlePageListReqVO findArchiveArticlePageListReqVO);
}
