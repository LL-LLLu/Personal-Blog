package com.luqi.weblog.admin.service;

import com.luqi.weblog.common.utils.Response;

/**
 * Admin visitor statistics service interface
 */
public interface AdminVisitorService {

    /**
     * Get visitor location statistics (aggregated by location)
     */
    Response findVisitorLocationStats();

    /**
     * Get recent visitor logs with pagination
     */
    Response findVisitorLogs(Long current, Long size);

    /**
     * Get visitor statistics summary
     */
    Response findVisitorSummary();
}
