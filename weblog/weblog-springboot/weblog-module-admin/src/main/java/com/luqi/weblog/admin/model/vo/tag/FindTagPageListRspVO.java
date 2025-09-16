package com.luqi.weblog.admin.model.vo.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
public class FindTagPageListRspVO {

    /**
     * Tag ID
     */
    private Long id;

    /**
     * Tag name
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
