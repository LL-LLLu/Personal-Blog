package com.luqi.weblog.admin.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @date: 2023/11/9 10:05
 * @description: Article reading event
 **/

@Getter
public class ReadArticleEvent extends ApplicationEvent {

        /**
         * 文章 ID
         */
        private Long articleId;

    public ReadArticleEvent(Object source, Long articleId) {
            super(source);
            this.articleId = articleId;
    }
}
