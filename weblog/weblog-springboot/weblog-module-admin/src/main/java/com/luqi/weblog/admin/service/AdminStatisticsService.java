package com.luqi.weblog.admin.service;

/**
 * Statistics Service for Admin
 */
public interface AdminStatisticsService {

    /**
     * Count total articles for each category
     */
    void statisticsCategoryArticleTotal();

    /**
     * Count total articles for each tag
     */
    void statisticsTagArticleTotal();

}