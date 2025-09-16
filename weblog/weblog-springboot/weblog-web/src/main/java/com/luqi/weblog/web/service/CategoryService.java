package com.luqi.weblog.web.service;

import com.luqi.weblog.common.utils.Response;
import com.luqi.weblog.web.model.vo.category.FindCategoryArticlePageListReqVO;
import com.luqi.weblog.web.model.vo.category.FindCategoryListReqVO;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @date: 2023-09-15 14:03
 * @description: 分类
 **/
public interface CategoryService {
    /**
     * Get category list
     * @param findCategoryListReqVO
     * @return
     */
    Response findCategoryList(FindCategoryListReqVO findCategoryListReqVO);


    /**
     * 获取分类下文章分页数据
     * @param findCategoryArticlePageListReqVO
     * @return
     */
    Response findCategoryArticlePageList(FindCategoryArticlePageListReqVO findCategoryArticlePageListReqVO);

}