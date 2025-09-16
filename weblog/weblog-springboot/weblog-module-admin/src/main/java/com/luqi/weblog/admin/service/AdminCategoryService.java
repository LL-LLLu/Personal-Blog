package com.luqi.weblog.admin.service;

import com.luqi.weblog.admin.model.vo.category.AddCategoryReqVO;
import com.luqi.weblog.admin.model.vo.category.DeleteCategoryReqVO;
import com.luqi.weblog.admin.model.vo.category.FindCategoryPageListReqVO;
import com.luqi.weblog.admin.model.vo.tag.AddTagReqVO;
import com.luqi.weblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.luqi.weblog.common.utils.PageResponse;
import com.luqi.weblog.common.utils.Response;

/**
 * @author: 犬小哈
 * @url: www.quanxiaoha.com
 * @date: 2023-09-15 14:03
 * @description: TODO
 **/
public interface AdminCategoryService {
    /**
     * 添加分类
     * @param addCategoryReqVO
     * @return
     */
    Response addCategory(AddCategoryReqVO addCategoryReqVO);

    /**
     * 分类分页数据查询
     * @param findCategoryPageListReqVO
     * @return
     */
    PageResponse findCategoryPageList(FindCategoryPageListReqVO findCategoryPageListReqVO);

    /**
     * 删除分类
     * @param deleteCategoryReqVO
     * @return
     */
    Response deleteCategory(DeleteCategoryReqVO deleteCategoryReqVO);

    /**
     * 获取文章分类的 Select 列表数据
     * @return
     */
    Response findCategorySelectList();

}
