package com.luqi.weblog.admin.service;

import com.luqi.weblog.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import com.luqi.weblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.luqi.weblog.common.utils.Response;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @date: 2023-09-15 14:03
 * @description: TODO
 **/
public interface AdminBlogSettingsService {
    /**
     * 更新博客设置信息
     * @param updateBlogSettingsReqVO
     * @return
     */
    Response updateBlogSettings(UpdateBlogSettingsReqVO updateBlogSettingsReqVO);

    /**
     * 获取博客设置详情
     * @return
     */
    Response findDetail();
}

