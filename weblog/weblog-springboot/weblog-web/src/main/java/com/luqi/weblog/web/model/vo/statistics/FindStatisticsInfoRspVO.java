package com.luqi.weblog.web.model.vo.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: luqi
 * @url: www.luqi.com  
 * @date: 2025-09-05
 * @description: Frontend statistics information response VO
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindStatisticsInfoRspVO {
    /**
     * Total article count
     */
    private Long articleTotalCount;

    /**
     * Total category count
     */
    private Long categoryTotalCount;

    /**
     * Total tag count
     */
    private Long tagTotalCount;

    /**
     * Total page views
     */
    private Long pvTotalCount;
}