package com.luqi.weblog.admin.event.subscriber;

import com.luqi.weblog.admin.event.DeleteArticleEvent;
import com.luqi.weblog.admin.service.AdminStatisticsService;
import com.luqi.weblog.search.LuceneHelper;
import com.luqi.weblog.search.index.ArticleIndex;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.Term;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @date: 2023/11/9 10:08
 * @description: Article delete event subscriber
 **/
@Component
@Slf4j
public class DeleteArticleSubscriber implements ApplicationListener<DeleteArticleEvent> {

    @Autowired
    private LuceneHelper luceneHelper;
    @Autowired
    private AdminStatisticsService statisticsService;

    @Override
    @Async("threadPoolTaskExecutor")
    public void onApplicationEvent(DeleteArticleEvent event) {
        // Get event data
        Long articleId = event.getArticleId();

        // Get current thread name
        String threadName = Thread.currentThread().getName();
        
        log.info("==> threadName: {}", threadName);
        log.info("==> Article delete event consumed successfully, articleId: {}", articleId);

        // Delete condition, delete by article id
        Term condition = new Term(ArticleIndex.COLUMN_ID, String.valueOf(articleId));

        long count = luceneHelper.deleteDocument(ArticleIndex.NAME, condition);

        log.info("==> Finished deleting article Lucene document, articleId: {}, affected rows: {}", articleId, count);
        
        // Re-count total articles for each category
        statisticsService.statisticsCategoryArticleTotal();
        log.info("==> Re-counted total articles for each category");
        
        // Re-count total articles for each tag
        statisticsService.statisticsTagArticleTotal();
        log.info("==> Re-counted total articles for each tag");
    }
}