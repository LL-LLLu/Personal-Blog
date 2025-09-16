package com.luqi.weblog.web.model.vo.article;

import com.luqi.weblog.web.model.vo.tag.FindTagListRspVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: Lu Qi
 * @url: www.luqi.com
 * @date: 2023-09-15 14:07
 * @description: Article details
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindArticleDetailRspVO {
    /**
     * Article title
     */
    private String title;
    /**
     * Article content (HTML)
     */
    private String content;
    /**
     * Publication time
     */
    private LocalDateTime createTime;
    /**
     * Category ID
     */
    private Long categoryId;
    /**
     * Category name
     */
    private String categoryName;
    /**
     * Read count
     */
    private Long readNum;
    /**
     * Tag collection
     */
    private List<FindTagListRspVO> tags;
    /**
     * Previous article
     */
    private FindPreNextArticleRspVO preArticle;
    /**
     * Next article
     */
    private FindPreNextArticleRspVO nextArticle;
    
    /**
     * Total word count
     */
    private Integer totalWords;

    /**
     * Reading time
     */
    private String readTime;
}