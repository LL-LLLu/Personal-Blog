package com.luqi.weblog.admin.event.subscriber;

import com.luqi.weblog.admin.event.PublishArticleEvent;
import com.luqi.weblog.admin.service.AdminStatisticsService;
import com.luqi.weblog.common.constant.Constants;
import com.luqi.weblog.common.domain.dos.ArticleContentDO;
import com.luqi.weblog.common.domain.dos.ArticleDO;
import com.luqi.weblog.common.domain.mapper.ArticleContentMapper;
import com.luqi.weblog.common.domain.mapper.ArticleMapper;
import com.luqi.weblog.search.LuceneHelper;
import com.luqi.weblog.search.index.ArticleIndex;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @date: 2023/11/9 10:08
 * @description: Article publish event subscriber
 **/
@Component
@Slf4j
public class PublishArticleSubscriber implements ApplicationListener<PublishArticleEvent> {

    @Autowired
    private LuceneHelper luceneHelper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleContentMapper articleContentMapper;
    @Autowired
    private AdminStatisticsService statisticsService;

    @Override
    @Async("threadPoolTaskExecutor")
    public void onApplicationEvent(PublishArticleEvent event) {
        // Get event data
        Long articleId = event.getArticleId();

        // Get current thread name
        String threadName = Thread.currentThread().getName();
        
        log.info("==> threadName: {}", threadName);
        log.info("==> Article publish event consumed successfully, articleId: {}", articleId);

        // Query the newly published article
        ArticleDO articleDO = articleMapper.selectById(articleId);
        // Here we also save the article content to the document, but we don't query content during search
        // You can decide whether to add content to the search fields
        ArticleContentDO articleContentDO = articleContentMapper.selectByArticleId(articleId);

        // Build document
        Document document = new Document();
        document.add(new TextField(ArticleIndex.COLUMN_ID, String.valueOf(articleId), Field.Store.YES));
        document.add(new TextField(ArticleIndex.COLUMN_TITLE, articleDO.getTitle(), Field.Store.YES));
        document.add(new TextField(ArticleIndex.COLUMN_COVER, articleDO.getCover(), Field.Store.YES));
        document.add(new TextField(ArticleIndex.COLUMN_SUMMARY, articleDO.getSummary(), Field.Store.YES));
        document.add(new TextField(ArticleIndex.COLUMN_CONTENT, articleContentDO.getContent(), Field.Store.YES));
        document.add(new TextField(ArticleIndex.COLUMN_CREATE_TIME, Constants.DATE_TIME_FORMATTER.format(articleDO.getCreateTime()), Field.Store.YES));
        
        // Add document
        long count = luceneHelper.addDocument(ArticleIndex.NAME, document);

        log.info("==> Finished adding article Lucene document, articleId: {}, affected rows: {}", articleId, count);
        
        // Re-count total articles for each category
        statisticsService.statisticsCategoryArticleTotal();
        log.info("==> Re-counted total articles for each category");
        
        // Re-count total articles for each tag
        statisticsService.statisticsTagArticleTotal();
        log.info("==> Re-counted total articles for each tag");
    }
}