package com.luqi.weblog.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @description: TODO
 **/
@Data
public class BasePageQuery {
    /**
     * 当前页码, 默认第一页
     */
    private Long current = 1L;
    /**
     * 每页展示的数据数量，默认每页展示 10 条数据
     */
    private Long size = 10L;
}
