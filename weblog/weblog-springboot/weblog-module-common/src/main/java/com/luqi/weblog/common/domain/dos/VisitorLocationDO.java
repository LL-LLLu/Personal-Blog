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
 * Visitor Location Statistics DO
 * Stores aggregated visitor counts by location
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_visitor_location")
public class VisitorLocationDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String province;

    private String city;

    private String country;

    private Long visitCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
