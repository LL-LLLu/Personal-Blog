package com.luqi.weblog.admin.controller;

import com.luqi.weblog.admin.service.AdminDashboardService;
import com.luqi.weblog.common.aspect.ApiOperationLog;
import com.luqi.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard")
@Api(tags = "Admin Dashboard")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService dashboardService;

    @PostMapping("/statistics")
    @ApiOperation(value = "Get dashboard basic statistics")
    @ApiOperationLog(description = "Get dashboard basic statistics")
    public Response findDashboardStatistics() {
        return dashboardService.findDashboardStatistics();
    }

    @PostMapping("/publishArticle/statistics")
    @ApiOperation(value = "Get dashboard article publish heatmap statistics")
    @ApiOperationLog(description = "Get dashboard article publish heatmap statistics")
    public Response findDashboardPublishArticleStatistics() {
        return dashboardService.findDashboardPublishArticleStatistics();
    }

    @PostMapping("/pv/statistics")
    @ApiOperation(value = "Get dashboard weekly PV statistics")
    @ApiOperationLog(description = "Get dashboard weekly PV statistics")
    public Response findDashboardPVStatistics() {
        return dashboardService.findDashboardPVStatistics();
    }

}