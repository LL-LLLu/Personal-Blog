package com.luqi.weblog.admin.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @date: 2023/11/9 10:05
 * @description: Article delete event
 **/

@Getter
public class DeleteArticleEvent extends ApplicationEvent {

    /**
     * Article ID
     */
    private Long articleId;

    public DeleteArticleEvent(Object source, Long articleId) {
        super(source);
        this.articleId = articleId;
    }
}