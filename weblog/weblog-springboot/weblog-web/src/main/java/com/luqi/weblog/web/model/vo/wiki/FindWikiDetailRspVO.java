package com.luqi.weblog.web.model.vo.wiki;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindWikiDetailRspVO {
    private Long id;
    private String title;
    private String cover;
    private String summary;
}