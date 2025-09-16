package com.luqi.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Maps;
import com.luqi.weblog.admin.service.AdminStatisticsService;
import com.luqi.weblog.common.domain.dos.ArticleCategoryRelDO;
import com.luqi.weblog.common.domain.dos.ArticleTagRelDO;
import com.luqi.weblog.common.domain.dos.CategoryDO;
import com.luqi.weblog.common.domain.dos.TagDO;
import com.luqi.weblog.common.domain.mapper.ArticleCategoryRelMapper;
import com.luqi.weblog.common.domain.mapper.ArticleTagRelMapper;
import com.luqi.weblog.common.domain.mapper.CategoryMapper;
import com.luqi.weblog.common.domain.mapper.TagMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminStatisticsServiceImpl implements AdminStatisticsService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;

    @Override
    public void statisticsCategoryArticleTotal() {
        // Query all categories
        List<CategoryDO> categoryDOS = categoryMapper.selectList(Wrappers.emptyWrapper());

        // Query all article-category mapping records
        List<ArticleCategoryRelDO> articleCategoryRelDOS = articleCategoryRelMapper.selectList(Wrappers.emptyWrapper());

        // Group by category ID
        Map<Long, List<ArticleCategoryRelDO>> categoryIdAndArticleCategoryRelDOMap = Maps.newHashMap();
        // If not empty
        if (!CollectionUtils.isEmpty(articleCategoryRelDOS)) {
            categoryIdAndArticleCategoryRelDOMap = articleCategoryRelDOS.stream()
                    .collect(Collectors.groupingBy(ArticleCategoryRelDO::getCategoryId));
        }

        if (!CollectionUtils.isEmpty(categoryDOS)) {
            // Loop through and count total articles for each category
            for (CategoryDO categoryDO : categoryDOS) {
                Long categoryId = categoryDO.getId();
                // Get all mapping records for this category
                List<ArticleCategoryRelDO> articleCategoryRelDOList = categoryIdAndArticleCategoryRelDOMap.get(categoryId);

                // Get total article count
                int articlesTotal = CollectionUtils.isEmpty(articleCategoryRelDOList) ? 0 : articleCategoryRelDOList.size();

                // Update the category's total article count
                CategoryDO categoryDO1 = CategoryDO.builder()
                        .id(categoryId)
                        .articlesTotal(articlesTotal)
                        .build();
                categoryMapper.updateById(categoryDO1);
            }
        }
    }

    /**
     * Count total articles for each tag
     */
    @Override
    public void statisticsTagArticleTotal() {
        // Query all tags
        List<TagDO> tagDOS = tagMapper.selectList(Wrappers.emptyWrapper());

        // Query all article-tag mapping records
        List<ArticleTagRelDO> articleTagRelDOS = articleTagRelMapper.selectList(Wrappers.emptyWrapper());

        // Group by tag ID
        Map<Long, List<ArticleTagRelDO>> tagIdAndArticleTagRelDOMap = Maps.newHashMap();
        // If not empty
        if (!CollectionUtils.isEmpty(articleTagRelDOS)) {
            tagIdAndArticleTagRelDOMap = articleTagRelDOS.stream()
                    .collect(Collectors.groupingBy(ArticleTagRelDO::getTagId));
        }

        if (!CollectionUtils.isEmpty(tagDOS)) {
            // Loop through and count total articles for each tag
            for (TagDO tagDO : tagDOS) {
                Long tagId = tagDO.getId();

                // Get all mapping records for this tag
                List<ArticleTagRelDO> articleTagRelDOList = tagIdAndArticleTagRelDOMap.get(tagId);

                // Get total article count
                int articlesTotal = CollectionUtils.isEmpty(articleTagRelDOList) ? 0 : articleTagRelDOList.size();

                // Update the tag's total article count
                TagDO tagDO1 = TagDO.builder()
                        .id(tagId)
                        .articlesTotal(articlesTotal)
                        .build();
                tagMapper.updateById(tagDO1);
            }
        }
    }
}