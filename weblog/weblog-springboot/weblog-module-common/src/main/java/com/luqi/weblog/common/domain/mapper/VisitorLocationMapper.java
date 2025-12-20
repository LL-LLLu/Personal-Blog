package com.luqi.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.luqi.weblog.common.domain.dos.VisitorLocationDO;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface VisitorLocationMapper extends BaseMapper<VisitorLocationDO> {

    /**
     * Find location record by country, province, and city
     */
    default VisitorLocationDO selectByLocation(String country, String province, String city) {
        return selectOne(Wrappers.<VisitorLocationDO>lambdaQuery()
                .eq(VisitorLocationDO::getCountry, country)
                .eq(VisitorLocationDO::getProvince, province)
                .eq(VisitorLocationDO::getCity, city));
    }

    /**
     * Increment visit count for a specific location
     */
    @Update("UPDATE t_visitor_location SET visit_count = visit_count + 1, update_time = NOW() WHERE id = #{id}")
    int incrementVisitCount(Long id);

    /**
     * Get top N locations by visit count
     */
    default List<VisitorLocationDO> selectTopLocations(int limit) {
        return selectList(Wrappers.<VisitorLocationDO>lambdaQuery()
                .orderByDesc(VisitorLocationDO::getVisitCount)
                .last("LIMIT " + limit));
    }

    /**
     * Get all locations ordered by visit count
     */
    default List<VisitorLocationDO> selectAllOrderByVisitCount() {
        return selectList(Wrappers.<VisitorLocationDO>lambdaQuery()
                .orderByDesc(VisitorLocationDO::getVisitCount));
    }
}
