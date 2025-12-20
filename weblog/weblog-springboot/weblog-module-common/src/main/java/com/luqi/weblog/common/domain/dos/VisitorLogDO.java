package com.luqi.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Visitor Log DO
 * Stores individual visit records with location details
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_visitor_log")
public class VisitorLogDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String ipAddress;

    private String country;

    private String province;

    private String city;

    private Long articleId;

    private String userAgent;

    private LocalDateTime visitTime;
}
