package com.luqi.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luqi.weblog.common.domain.dos.VisitorLogDO;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitorLogMapper extends BaseMapper<VisitorLogDO> {

    /**
     * Get recent visitor logs with pagination
     */
    default Page<VisitorLogDO> selectRecentLogs(Long current, Long size) {
        Page<VisitorLogDO> page = new Page<>(current, size);
        return selectPage(page, Wrappers.<VisitorLogDO>lambdaQuery()
                .orderByDesc(VisitorLogDO::getVisitTime));
    }

    /**
     * Get visitor logs for a specific article
     */
    default List<VisitorLogDO> selectByArticleId(Long articleId) {
        return selectList(Wrappers.<VisitorLogDO>lambdaQuery()
                .eq(VisitorLogDO::getArticleId, articleId)
                .orderByDesc(VisitorLogDO::getVisitTime));
    }

    /**
     * Get visitor logs within a time range
     */
    default List<VisitorLogDO> selectByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return selectList(Wrappers.<VisitorLogDO>lambdaQuery()
                .ge(VisitorLogDO::getVisitTime, startTime)
                .le(VisitorLogDO::getVisitTime, endTime)
                .orderByDesc(VisitorLogDO::getVisitTime));
    }

    /**
     * Count unique visitors (by IP) today
     */
    default Long countTodayUniqueVisitors(LocalDateTime startOfDay) {
        return selectCount(Wrappers.<VisitorLogDO>lambdaQuery()
                .ge(VisitorLogDO::getVisitTime, startOfDay)
                .groupBy(VisitorLogDO::getIpAddress));
    }
}
