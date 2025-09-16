package com.luqi.weblog.web.service;

import com.luqi.weblog.common.utils.Response;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @date: 2025-09-05
 * @description: Statistics information business interface
 **/
public interface StatisticsService {
    /**
     * Get article count, category count, tag count, and total page views statistics
     * @return
     */
    Response findInfo();
}