package com.luqi.weblog.admin.service;

import com.luqi.weblog.common.utils.Response;

public interface AdminDashboardService {

    /**
     * Get dashboard basic statistics
     * @return
     */
    Response findDashboardStatistics();

    /**
     * Get article publish heatmap statistics
     * @return
     */
    Response findDashboardPublishArticleStatistics();

    /**
     * Get article weekly PV statistics
     * @return
     */
    Response findDashboardPVStatistics();
}