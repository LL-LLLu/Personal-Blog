package com.luqi.weblog.admin.model.vo.tag;

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
 * @description: tag page
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "Search for Tag page input VO")
public class FindTagPageListReqVO extends BasePageQuery {

    /**
     * 标签名称
     */
    private String name;

    /**
     * 创建的起始日期
     */
    private LocalDate startDate;

    /**
     * 创建的结束日期
     */
    private LocalDate endDate;

}
