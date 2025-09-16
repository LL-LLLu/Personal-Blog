package com.luqi.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.luqi.weblog.admin.model.vo.dashboard.FindDashboardPVStatisticsInfoRspVO;
import com.luqi.weblog.admin.model.vo.dashboard.FindDashboardStatisticsInfoRspVO;
import com.luqi.weblog.admin.service.AdminDashboardService;
import com.luqi.weblog.common.constant.Constants;
import com.luqi.weblog.common.domain.dos.ArticleDO;
import com.luqi.weblog.common.domain.dos.ArticlePublishCountDO;
import com.luqi.weblog.common.domain.dos.StatisticsArticlePVDO;
import com.luqi.weblog.common.domain.mapper.ArticleMapper;
import com.luqi.weblog.common.domain.mapper.CategoryMapper;
import com.luqi.weblog.common.domain.mapper.StatisticsArticlePVMapper;
import com.luqi.weblog.common.domain.mapper.TagMapper;
import com.luqi.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminDashboardServiceImpl implements AdminDashboardService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private StatisticsArticlePVMapper articlePVMapper;

    /**
     * Get dashboard basic statistics
     *
     * @return
     */
    @Override
    public Response findDashboardStatistics() {
        // Query total article count
        Long articleTotalCount = articleMapper.selectCount(Wrappers.emptyWrapper());

        // Query total category count
        Long categoryTotalCount = categoryMapper.selectCount(Wrappers.emptyWrapper());

        // Query total tag count
        Long tagTotalCount = tagMapper.selectCount(Wrappers.emptyWrapper());

        // Total page views
        List<ArticleDO> articleDOS = articleMapper.selectAllReadNum();
        Long pvTotalCount = 0L;

        if (!CollectionUtils.isEmpty(articleDOS)) {
            // Sum all read_num values
            pvTotalCount = articleDOS.stream().mapToLong(ArticleDO::getReadNum).sum();
        }

        // Build VO object
        FindDashboardStatisticsInfoRspVO vo = FindDashboardStatisticsInfoRspVO.builder()
                .articleTotalCount(articleTotalCount)
                .categoryTotalCount(categoryTotalCount)
                .tagTotalCount(tagTotalCount)
                .pvTotalCount(pvTotalCount)
                .build();

        return Response.success(vo);
    }

    /**
     * Get article publish heatmap statistics
     *
     * @return
     */
    @Override
    public Response findDashboardPublishArticleStatistics() {
        // Current date
        LocalDate currDate = LocalDate.now();

        // Date one year ago from current date
        LocalDate startDate = currDate.minusYears(1);

        // Find daily article publish count within this year
        List<ArticlePublishCountDO> articlePublishCountDOS = articleMapper.selectDateArticlePublishCount(startDate, currDate.plusDays(1));

        Map<LocalDate, Long> map = null;
        if (!CollectionUtils.isEmpty(articlePublishCountDOS)) {
            // Convert DO to Map
            Map<LocalDate, Long> dateArticleCountMap = articlePublishCountDOS.stream()
                    .collect(Collectors.toMap(ArticlePublishCountDO::getDate, ArticlePublishCountDO::getCount));

            // Ordered Map, returned date article counts need to be sorted in ascending order
            map = Maps.newLinkedHashMap();
            // Loop from one year ago today to today
            for (; startDate.isBefore(currDate) || startDate.isEqual(currDate); startDate = startDate.plusDays(1)) {
                // Use date as key to get article publish count from dateArticleCountMap
                Long count = dateArticleCountMap.get(startDate);
                // Set to return Map
                map.put(startDate, Objects.isNull(count) ? 0 : count);
            }
        }

        return Response.success(map);
    }

    /**
     * Get article weekly PV statistics
     *
     * @return
     */
    @Override
    public Response findDashboardPVStatistics() {
        // Query PV access records for the last week
        List<StatisticsArticlePVDO> articlePVDOS = articlePVMapper.selectLatestWeekRecords();

        Map<LocalDate, Long> pvDateCountMap = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(articlePVDOS)) {
            // Convert to Map for easy PV access by date
            pvDateCountMap = articlePVDOS.stream()
                    .collect(Collectors.toMap(StatisticsArticlePVDO::getPvDate, StatisticsArticlePVDO::getPvCount));
        }

        FindDashboardPVStatisticsInfoRspVO vo = null;

        // Date collection
        List<String> pvDates = Lists.newArrayList();
        // PV collection
        List<Long> pvCounts = Lists.newArrayList();

        // Current date
        LocalDate currDate = LocalDate.now();
        // One week ago
        LocalDate tmpDate = currDate.minusWeeks(1);
        // Start loop from one week ago
        for (; tmpDate.isBefore(currDate) || tmpDate.isEqual(currDate); tmpDate = tmpDate.plusDays(1)) {
            // Set PV access count for corresponding date
            pvDates.add(tmpDate.format(Constants.MONTH_DAY_FORMATTER));
            Long pvCount = pvDateCountMap.get(tmpDate);
            pvCounts.add(Objects.isNull(pvCount) ? 0 : pvCount);
        }

        vo = FindDashboardPVStatisticsInfoRspVO.builder()
                .pvDates(pvDates)
                .pvCounts(pvCounts)
                .build();

        return Response.success(vo);
    }
}