package com.luqi.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.luqi.weblog.admin.model.vo.visitor.FindVisitorLocationRspVO;
import com.luqi.weblog.admin.model.vo.visitor.FindVisitorLogRspVO;
import com.luqi.weblog.admin.service.AdminVisitorService;
import com.luqi.weblog.common.constant.Constants;
import com.luqi.weblog.common.domain.dos.ArticleDO;
import com.luqi.weblog.common.domain.dos.VisitorLocationDO;
import com.luqi.weblog.common.domain.dos.VisitorLogDO;
import com.luqi.weblog.common.domain.mapper.ArticleMapper;
import com.luqi.weblog.common.domain.mapper.VisitorLocationMapper;
import com.luqi.weblog.common.domain.mapper.VisitorLogMapper;
import com.luqi.weblog.common.utils.PageResponse;
import com.luqi.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminVisitorServiceImpl implements AdminVisitorService {

    @Autowired
    private VisitorLocationMapper visitorLocationMapper;

    @Autowired
    private VisitorLogMapper visitorLogMapper;

    @Autowired
    private ArticleMapper articleMapper;

    /**
     * Get visitor location statistics
     */
    @Override
    public Response findVisitorLocationStats() {
        List<VisitorLocationDO> locations = visitorLocationMapper.selectAllOrderByVisitCount();

        if (CollectionUtils.isEmpty(locations)) {
            return Response.success(List.of());
        }

        List<FindVisitorLocationRspVO> vos = locations.stream()
                .map(location -> {
                    String locationName = buildLocationName(location.getCountry(), location.getProvince(), location.getCity());
                    return FindVisitorLocationRspVO.builder()
                            .id(location.getId())
                            .country(location.getCountry())
                            .province(location.getProvince())
                            .city(location.getCity())
                            .visitCount(location.getVisitCount())
                            .locationName(locationName)
                            .build();
                })
                .collect(Collectors.toList());

        return Response.success(vos);
    }

    /**
     * Get recent visitor logs with pagination
     */
    @Override
    public Response findVisitorLogs(Long current, Long size) {
        Page<VisitorLogDO> page = visitorLogMapper.selectRecentLogs(current, size);

        List<VisitorLogDO> logs = page.getRecords();

        if (CollectionUtils.isEmpty(logs)) {
            return PageResponse.success(page, List.of());
        }

        // Get article titles for the logs
        List<Long> articleIds = logs.stream()
                .map(VisitorLogDO::getArticleId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> articleTitleMap = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(articleIds)) {
            List<ArticleDO> articles = articleMapper.selectBatchIds(articleIds);
            articleTitleMap = articles.stream()
                    .collect(Collectors.toMap(ArticleDO::getId, ArticleDO::getTitle));
        }

        Map<Long, String> finalArticleTitleMap = articleTitleMap;
        List<FindVisitorLogRspVO> vos = logs.stream()
                .map(log -> {
                    String articleTitle = log.getArticleId() != null ?
                            finalArticleTitleMap.getOrDefault(log.getArticleId(), "Unknown Article") : "";

                    return FindVisitorLogRspVO.builder()
                            .id(log.getId())
                            .ipAddress(log.getIpAddress())
                            .country(log.getCountry())
                            .province(log.getProvince())
                            .city(log.getCity())
                            .articleId(log.getArticleId())
                            .articleTitle(articleTitle)
                            .visitTime(log.getVisitTime() != null ?
                                    Constants.DATE_TIME_FORMATTER.format(log.getVisitTime()) : "")
                            .build();
                })
                .collect(Collectors.toList());

        return PageResponse.success(page, vos);
    }

    /**
     * Get visitor statistics summary
     */
    @Override
    public Response findVisitorSummary() {
        Map<String, Object> summary = Maps.newHashMap();

        // Total unique locations
        Long totalLocations = visitorLocationMapper.selectCount(Wrappers.emptyWrapper());
        summary.put("totalLocations", totalLocations);

        // Total visits from location table
        List<VisitorLocationDO> locations = visitorLocationMapper.selectList(Wrappers.emptyWrapper());
        Long totalVisits = locations.stream()
                .mapToLong(loc -> loc.getVisitCount() != null ? loc.getVisitCount() : 0L)
                .sum();
        summary.put("totalVisits", totalVisits);

        // Today's visits
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        Long todayVisits = visitorLogMapper.selectCount(
                Wrappers.<VisitorLogDO>lambdaQuery()
                        .ge(VisitorLogDO::getVisitTime, startOfDay)
        );
        summary.put("todayVisits", todayVisits);

        // Top countries
        List<Map<String, Object>> topCountries = locations.stream()
                .collect(Collectors.groupingBy(
                        loc -> StringUtils.hasText(loc.getCountry()) ? loc.getCountry() : "Unknown",
                        Collectors.summingLong(loc -> loc.getVisitCount() != null ? loc.getVisitCount() : 0L)
                ))
                .entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(5)
                .map(entry -> {
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("country", entry.getKey());
                    map.put("count", entry.getValue());
                    return map;
                })
                .collect(Collectors.toList());
        summary.put("topCountries", topCountries);

        return Response.success(summary);
    }

    /**
     * Build location display name
     */
    private String buildLocationName(String country, String province, String city) {
        StringBuilder sb = new StringBuilder();

        if (StringUtils.hasText(country)) {
            sb.append(country);
        }

        if (StringUtils.hasText(province) && !province.equals(country)) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(province);
        }

        if (StringUtils.hasText(city) && !city.equals(province)) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(city);
        }

        return sb.length() > 0 ? sb.toString() : "Unknown";
    }
}
