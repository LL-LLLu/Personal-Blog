package com.luqi.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.luqi.weblog.common.domain.dos.ArticleDO;
import com.luqi.weblog.common.domain.mapper.ArticleMapper;
import com.luqi.weblog.common.domain.mapper.CategoryMapper;
import com.luqi.weblog.common.domain.mapper.TagMapper;
import com.luqi.weblog.common.utils.Response;
import com.luqi.weblog.web.model.vo.statistics.FindStatisticsInfoRspVO;
import com.luqi.weblog.web.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @date: 2025-09-05
 * @description: Statistics information business implementation class
 **/
@Service
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TagMapper tagMapper;

    /**
     * Get article count, category count, tag count, and total page views statistics
     *
     * @return
     */
    @Override
    public Response findInfo() {
        // Query total article count
        Long articleTotalCount = articleMapper.selectCount(Wrappers.emptyWrapper());

        // Query total category count
        Long categoryTotalCount = categoryMapper.selectCount(Wrappers.emptyWrapper());

        // Query total tag count
        Long tagTotalCount = tagMapper.selectCount(Wrappers.emptyWrapper());

        // Total page views
        List<ArticleDO> articleDOS = articleMapper.selectAllReadNum();
        Long pvTotalCount = 0L;

        if (!CollectionUtils.isEmpty(articleDOS)) {
            // Sum all read_num values
            pvTotalCount = articleDOS.stream().mapToLong(ArticleDO::getReadNum).sum();
        }

        // Build VO object
        FindStatisticsInfoRspVO vo = FindStatisticsInfoRspVO.builder()
                .articleTotalCount(articleTotalCount)
                .categoryTotalCount(categoryTotalCount)
                .tagTotalCount(tagTotalCount)
                .pvTotalCount(pvTotalCount)
                .build();

        return Response.success(vo);
    }
}