package com.luqi.weblog.admin.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @date: 2023/11/9 10:05
 * @description: Article update event
 **/

@Getter
public class UpdateArticleEvent extends ApplicationEvent {

    /**
     * Article ID
     */
    private Long articleId;

    /**
     * Article title
     */
    private String title;

    /**
     * Article content
     */
    private String content;

    /**
     * Article summary
     */
    private String summary;

    /**
     * Cover image
     */
    private String cover;

    /**
     * Update time
     */
    private String updateTime;

    public UpdateArticleEvent(Object source, Long articleId, String title, String content, String summary, String cover, String updateTime) {
        super(source);
        this.articleId = articleId;
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.cover = cover;
        this.updateTime = updateTime;
    }
}