package com.luqi.weblog.web.model.vo.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: Lu Qi
 * @url: www.luqi.com
 * @date: 2023-09-15 14:07
 * @description: Previous and next article
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindPreNextArticleRspVO {
    /**
     * Article ID
     */
    private Long articleId;

    /**
     * Article title
     */
    private String articleTitle;
}