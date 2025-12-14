package com.luqi.weblog.web.model.vo.wiki;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindWikiListRspVO {
    private Long id;
    private String cover;
    private String title;
    private String summary;

    /**
     * Is Top
     */
    private Boolean isTop;

    /**
     * First Article ID
     */
    private Long firstArticleId;
}