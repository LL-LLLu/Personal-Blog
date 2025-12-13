package com.luqi.weblog.web.model.vo.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindArticleDetailRspVO {
    private String title;
    private String content;
    private String createTime;
    private Long categoryId;
    private String categoryName;
    private Long readNum;
    private List<Long> tagIds;
    private List<String> tags;
    private Integer totalWords;
    private String readTime;
    private FindPreNextArticleRspVO preArticle;
    private FindPreNextArticleRspVO nextArticle;
    /**
     * Last update time
     */
    private LocalDateTime updateTime;
}