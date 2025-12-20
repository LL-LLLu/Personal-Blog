package com.luqi.weblog.admin.event.subscriber;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.luqi.weblog.admin.event.ReadArticleEvent;
import com.luqi.weblog.common.domain.dos.StatisticsArticlePVDO;
import com.luqi.weblog.common.domain.dos.VisitorLocationDO;
import com.luqi.weblog.common.domain.dos.VisitorLogDO;
import com.luqi.weblog.common.domain.mapper.ArticleMapper;
import com.luqi.weblog.common.domain.mapper.StatisticsArticlePVMapper;
import com.luqi.weblog.common.domain.mapper.VisitorLocationMapper;
import com.luqi.weblog.common.domain.mapper.VisitorLogMapper;
import com.luqi.weblog.common.utils.IpLocationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @date: 2023/11/9 10:08
 * @description: Article read event subscriber - handles PV counting and visitor location tracking
 **/
@Component
@Slf4j
public class ReadArticleSubscriber implements ApplicationListener<ReadArticleEvent> {


    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private StatisticsArticlePVMapper articlePVMapper;
    @Autowired
    private VisitorLocationMapper visitorLocationMapper;
    @Autowired
    private VisitorLogMapper visitorLogMapper;

    @Override
    @Async("threadPoolTaskExecutor")
    public void onApplicationEvent(ReadArticleEvent event) {
        // 在这里处理收到的事件，可以是任何逻辑操作
        Long articleId = event.getArticleId();
        String ipAddress = event.getIpAddress();
        String userAgent = event.getUserAgent();

        // 获取当前线程名称
        String threadName = Thread.currentThread().getName();

        log.info("==> threadName: {}", threadName);
        log.info("==> 文章阅读事件消费成功，articleId: {}, ip: {}", articleId, ipAddress);

        // 执行文章阅读量 +1
        articleMapper.increaseReadNum(articleId);
        log.info("==> 文章阅读量 +1 操作成功，articleId: {}", articleId);

        // 当日文章 PV 访问量 +1
        LocalDate currDate = LocalDate.now();

        // 先检查今天的记录是否存在
        StatisticsArticlePVDO todayRecord = articlePVMapper.selectOne(
            Wrappers.<StatisticsArticlePVDO>lambdaQuery()
                .eq(StatisticsArticlePVDO::getPvDate, currDate)
        );

        // 如果今天的记录不存在，则创建一个新记录
        if (todayRecord == null) {
            log.info("==> 今日 PV 记录不存在，正在创建新记录，date: {}", currDate);
            StatisticsArticlePVDO newRecord = StatisticsArticlePVDO.builder()
                .pvDate(currDate)
                .pvCount(1L) // 初始访问量为 1
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
            articlePVMapper.insert(newRecord);
            log.info("==> 今日 PV 记录创建成功，date: {}", currDate);
        } else {
            // 记录存在，则更新访问量 +1
            articlePVMapper.increasePVCount(currDate);
            log.info("==> 当日文章 PV 访问量 +1 操作成功，date: {}", currDate);
        }

        // 记录访客位置信息
        if (ipAddress != null && !ipAddress.isEmpty()) {
            try {
                recordVisitorLocation(articleId, ipAddress, userAgent);
            } catch (Exception e) {
                log.error("==> 记录访客位置失败", e);
            }
        }
    }

    /**
     * 记录访客位置信息
     */
    private void recordVisitorLocation(Long articleId, String ipAddress, String userAgent) {
        // 获取 IP 地理位置
        IpLocationUtil.Location location = IpLocationUtil.getLocation(ipAddress);

        String country = location.getCountry();
        String province = location.getProvince();
        String city = location.getCity();

        log.info("==> 访客位置: country={}, province={}, city={}, ip={}", country, province, city, ipAddress);

        // 1. 记录到访客日志表 (详细记录)
        VisitorLogDO visitorLog = VisitorLogDO.builder()
                .ipAddress(ipAddress)
                .country(country)
                .province(province)
                .city(city)
                .articleId(articleId)
                .userAgent(userAgent != null ? userAgent.substring(0, Math.min(userAgent.length(), 500)) : "")
                .visitTime(LocalDateTime.now())
                .build();
        visitorLogMapper.insert(visitorLog);

        // 2. 更新位置统计表 (聚合统计)
        VisitorLocationDO existingLocation = visitorLocationMapper.selectByLocation(country, province, city);

        if (existingLocation != null) {
            // 位置已存在，增加访问计数
            visitorLocationMapper.incrementVisitCount(existingLocation.getId());
        } else {
            // 新位置，插入记录
            VisitorLocationDO newLocation = VisitorLocationDO.builder()
                    .country(country)
                    .province(province)
                    .city(city)
                    .visitCount(1L)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            visitorLocationMapper.insert(newLocation);
        }

        log.info("==> 访客位置记录成功");
    }
}