package com.luqi.weblog.admin.event.subscriber;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.luqi.weblog.admin.event.ReadArticleEvent;
import com.luqi.weblog.common.domain.dos.StatisticsArticlePVDO;
import com.luqi.weblog.common.domain.mapper.ArticleMapper;
import com.luqi.weblog.common.domain.mapper.StatisticsArticlePVMapper;
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
 * @description: TODO
 **/
@Component
@Slf4j
public class ReadArticleSubscriber implements ApplicationListener<ReadArticleEvent> {


    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private StatisticsArticlePVMapper articlePVMapper;

    @Override
    @Async("threadPoolTaskExecutor")
    public void onApplicationEvent(ReadArticleEvent event) {
        // 在这里处理收到的事件，可以是任何逻辑操作
        Long articleId = event.getArticleId();

        // 获取当前线程名称
        String threadName = Thread.currentThread().getName();

        log.info("==> threadName: {}", threadName);
        log.info("==> 文章阅读事件消费成功，articleId: {}", articleId);

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
    }
}