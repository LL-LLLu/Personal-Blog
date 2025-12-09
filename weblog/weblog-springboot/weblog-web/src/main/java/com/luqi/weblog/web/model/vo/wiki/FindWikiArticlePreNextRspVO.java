package com.luqi.weblog.web.model.vo.wiki;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindWikiArticlePreNextRspVO {
    private FindPreNextArticleRspVO preArticle;
    private FindPreNextArticleRspVO nextArticle;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class FindPreNextArticleRspVO {
        private Long articleId;
        private String articleTitle;
    }
}