package com.luqi.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luqi.weblog.common.domain.dos.WikiDO;

import java.time.LocalDate;
import java.util.Objects;

public interface WikiMapper extends BaseMapper<WikiDO> {

    /**
     * 分页查询
     * @param current
     * @param size
     * @param title
     * @param startDate
     * @param endDate
     * @param isPublish
     * @return
     */
    default Page<WikiDO> selectPageList(Long current, Long size, String title, LocalDate startDate, LocalDate endDate, Boolean isPublish) {
        Page<WikiDO> page = new Page<>(current, size);
        LambdaQueryWrapper<WikiDO> wrapper = Wrappers.<WikiDO>lambdaQuery()
                .like(StringUtils.isNotBlank(title), WikiDO::getTitle, title)
                .ge(Objects.nonNull(startDate), WikiDO::getCreateTime, startDate)
                .le(Objects.nonNull(endDate), WikiDO::getCreateTime, endDate)
                .eq(Objects.nonNull(isPublish), WikiDO::getIsPublish, isPublish)
                .orderByDesc(WikiDO::getWeight)
                .orderByDesc(WikiDO::getCreateTime);
        return selectPage(page, wrapper);
    }

    /**
     * 查询最大权重
     * @return
     */
    default WikiDO selectMaxWeight() {
        return selectOne(Wrappers.<WikiDO>lambdaQuery()
                .orderByDesc(WikiDO::getWeight)
                .last("LIMIT 1"));
    /**
     * Query published wikis
     * @return
     */
    default java.util.List<WikiDO> selectPublished() {
        return selectList(Wrappers.<WikiDO>lambdaQuery()
                .eq(WikiDO::getIsPublish, true) 
                .orderByDesc(WikiDO::getWeight) 
                .orderByDesc(WikiDO::getCreateTime) 
        );
    }
}