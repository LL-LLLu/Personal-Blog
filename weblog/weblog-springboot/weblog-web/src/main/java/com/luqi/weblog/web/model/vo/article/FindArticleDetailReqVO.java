package com.luqi.weblog.web.model.vo.article;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@ApiModel(value = "Query Article Detail VO")
public class FindArticleDetailReqVO {
    /**
     * Article ID
     */
    private Long articleId;
}