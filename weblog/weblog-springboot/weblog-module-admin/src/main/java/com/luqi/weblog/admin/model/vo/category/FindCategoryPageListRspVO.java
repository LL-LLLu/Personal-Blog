package com.luqi.weblog.admin.model.vo.category;

import com.luqi.weblog.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @date: 2023-09-15 14:07
 * @description: Category page list response
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindCategoryPageListRspVO {

    /**
     * Category ID
     */
    private Long id;

    /**
     * Category name
     */
    private String name;

    /**
     * Create time
     */
    private LocalDateTime createTime;

    /**
     * Total articles count
     */
    private Integer articlesTotal;

}
