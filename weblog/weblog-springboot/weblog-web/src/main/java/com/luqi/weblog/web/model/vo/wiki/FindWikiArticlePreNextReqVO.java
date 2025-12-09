package com.luqi.weblog.web.model.vo.wiki;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindWikiArticlePreNextReqVO {
    @NotNull(message = "Wiki ID cannot be null")
    private Long id;
    @NotNull(message = "Article ID cannot be null")
    private Long articleId;
}