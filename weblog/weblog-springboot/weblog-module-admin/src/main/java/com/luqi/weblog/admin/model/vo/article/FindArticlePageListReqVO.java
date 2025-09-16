package com.luqi.weblog.admin.model.vo.article;

import com.luqi.weblog.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @date: 2023-09-15 14:07
 * @description: Article Pages
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "Query Article Page List Request VO")
public class FindArticlePageListReqVO extends BasePageQuery {

    /**
     * Article title
     */
    private String title;

    /**
     * Publication start date
     */
    private LocalDate startDate;

    /**
     * Publication end date
     */
    private LocalDate endDate;

}