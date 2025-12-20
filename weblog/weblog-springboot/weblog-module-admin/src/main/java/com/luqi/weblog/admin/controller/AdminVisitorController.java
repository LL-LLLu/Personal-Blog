package com.luqi.weblog.admin.controller;

import com.luqi.weblog.admin.service.AdminVisitorService;
import com.luqi.weblog.common.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin Visitor Statistics Controller
 * Provides endpoints for viewing visitor location and statistics
 */
@RestController
@RequestMapping("/admin/visitor")
public class AdminVisitorController {

    @Autowired
    private AdminVisitorService visitorService;

    /**
     * Get visitor location statistics (aggregated by location)
     */
    @PostMapping("/location/list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response findVisitorLocationStats() {
        return visitorService.findVisitorLocationStats();
    }

    /**
     * Get recent visitor logs with pagination
     */
    @PostMapping("/log/list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response findVisitorLogs(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "20") Long size) {
        return visitorService.findVisitorLogs(current, size);
    }

    /**
     * Get visitor statistics summary
     */
    @PostMapping("/summary")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response findVisitorSummary() {
        return visitorService.findVisitorSummary();
    }
}
