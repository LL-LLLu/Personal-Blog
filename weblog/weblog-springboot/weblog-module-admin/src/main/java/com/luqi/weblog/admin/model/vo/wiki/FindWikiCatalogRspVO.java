package com.luqi.weblog.admin.model.vo.wiki;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindWikiCatalogRspVO {
    private Long id;
    private Long articleId;
    private String title;
    private Integer level;
    private Integer sort;
    private List<FindWikiCatalogRspVO> children;
}