package com.luqi.weblog.admin.model.vo.visitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Visitor log response VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindVisitorLogRspVO {

    private Long id;

    private String ipAddress;

    private String country;

    private String province;

    private String city;

    private Long articleId;

    private String articleTitle;

    private String visitTime;
}
