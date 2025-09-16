package com.luqi.weblog.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.luqi.weblog.admin.convert.ArticleDetailConvert;
import com.luqi.weblog.admin.event.DeleteArticleEvent;
import com.luqi.weblog.admin.event.PublishArticleEvent;
import com.luqi.weblog.admin.event.UpdateArticleEvent;
import com.luqi.weblog.admin.model.vo.article.*;
import com.luqi.weblog.admin.service.AdminArticleService;
import com.luqi.weblog.common.constant.Constants;
import com.luqi.weblog.common.domain.dos.*;
import com.luqi.weblog.common.domain.mapper.*;
import com.luqi.weblog.common.enums.ResponseCodeEnum;
import com.luqi.weblog.common.exception.BizException;
import com.luqi.weblog.common.utils.PageResponse;
import com.luqi.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @date: 2023-09-15 14:03
 * @description: Article
 **/
@Service
@Slf4j
public class AdminArticleServiceImpl implements AdminArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleContentMapper articleContentMapper;
    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * Publish article
     *
     * @param publishArticleReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response publishArticle(PublishArticleReqVO publishArticleReqVO) {
        // 1. Convert VO to ArticleDO and save
        ArticleDO articleDO = ArticleDO.builder()
                .title(publishArticleReqVO.getTitle())
                .cover(publishArticleReqVO.getCover())
                .summary(publishArticleReqVO.getSummary())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        articleMapper.insert(articleDO);

        // Get the primary key ID of the inserted record
        Long articleId = articleDO.getId();

        // 2. Convert VO to ArticleContentDO and save
        ArticleContentDO articleContentDO = ArticleContentDO.builder()
                .articleId(articleId)
                .content(publishArticleReqVO.getContent())
                .build();
        articleContentMapper.insert(articleContentDO);

        // 3. Handle article-related categories
        Long categoryId = publishArticleReqVO.getCategoryId();

        // 3.1 Verify if the submitted category actually exists
        CategoryDO categoryDO = categoryMapper.selectById(categoryId);
        if (Objects.isNull(categoryDO)) {
            log.warn("==> Category does not exist, categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }

        ArticleCategoryRelDO articleCategoryRelDO = ArticleCategoryRelDO.builder()
                .articleId(articleId)
                .categoryId(categoryId)
                .build();
        articleCategoryRelMapper.insert(articleCategoryRelDO);

        // 4. Save the collection of tags related to the article
        List<String> publishTags = publishArticleReqVO.getTags();
        insertTags(articleId, publishTags);

        // 5. Publish article publish event
        eventPublisher.publishEvent(new PublishArticleEvent(this, articleId, 
            publishArticleReqVO.getTitle(), 
            publishArticleReqVO.getContent(), 
            publishArticleReqVO.getSummary(), 
            publishArticleReqVO.getCover(), 
            Constants.DATE_TIME_FORMATTER.format(LocalDateTime.now())));

        return Response.success();
    }

    /**
     * Delete article
     *
     * @param deleteArticleReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response deleteArticle(DeleteArticleReqVO deleteArticleReqVO) {
        Long articleId = deleteArticleReqVO.getId();

        // 1. Delete the article
        articleMapper.deleteById(articleId);

        // 2. Delete the article content
        articleContentMapper.deleteByArticleId(articleId);

        // 3. Delete article-category relation records
        articleCategoryRelMapper.deleteByArticleId(articleId);

        // 4. Delete article-tag relation records
        articleTagRelMapper.deleteByArticleId(articleId);

        // 5. Publish article delete event
        eventPublisher.publishEvent(new DeleteArticleEvent(this, articleId));

        return Response.success();
    }

    /**
     * Query article page list
     *
     * @param findArticlePageListReqVO
     * @return
     */
    @Override
    public Response findArticlePageList(FindArticlePageListReqVO findArticlePageListReqVO) {
        // Get current page and number of data to be displayed per page
        Long current = findArticlePageListReqVO.getCurrent();
        Long size = findArticlePageListReqVO.getSize();
        String title = findArticlePageListReqVO.getTitle();
        LocalDate startDate = findArticlePageListReqVO.getStartDate();
        LocalDate endDate = findArticlePageListReqVO.getEndDate();

        // Execute paginated query
        Page<ArticleDO> articleDOPage = articleMapper.selectPageList(current, size, title, startDate, endDate);

        List<ArticleDO> articleDOS = articleDOPage.getRecords();

        // Convert DO to VO
        List<FindArticlePageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(articleDOS)) {
            vos = articleDOS.stream()
                    .map(articleDO -> FindArticlePageListRspVO.builder()
                            .id(articleDO.getId())
                            .title(articleDO.getTitle())
                            .cover(articleDO.getCover())
                            .createTime(articleDO.getCreateTime())
                            .build())
                    .collect(Collectors.toList());
        }

        return PageResponse.success(articleDOPage, vos);
    }

    /**
     * Query article details
     *
     * @param findArticlePageListReqVO
     * @return
     */
    @Override
    public Response findArticleDetail(FindArticleDetailReqVO findArticlePageListReqVO) {
        Long articleId = findArticlePageListReqVO.getId();

        ArticleDO articleDO = articleMapper.selectById(articleId);

        if (Objects.isNull(articleDO)) {
            log.warn("==> The queried article does not exist, articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        ArticleContentDO articleContentDO = articleContentMapper.selectByArticleId(articleId);

        // Associated category
        ArticleCategoryRelDO articleCategoryRelDO = articleCategoryRelMapper.selectByArticleId(articleId);

        // Associated tags
        List<ArticleTagRelDO> articleTagRelDOS = articleTagRelMapper.selectByArticleId(articleId);
        // Get the collection of corresponding tag IDs
        List<Long> tagIds = articleTagRelDOS.stream().map(ArticleTagRelDO::getTagId).collect(Collectors.toList());

        // Convert DO to VO
        FindArticleDetailRspVO vo = ArticleDetailConvert.INSTANCE.convertDO2VO(articleDO);
        vo.setContent(articleContentDO.getContent());
        vo.setCategoryId(articleCategoryRelDO.getCategoryId());
        vo.setTagIds(tagIds);

        return Response.success(vo);
    }

    /**
     * Update article
     *
     * @param updateArticleReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response updateArticle(UpdateArticleReqVO updateArticleReqVO) {
        Long articleId = updateArticleReqVO.getId();

        // 1. Convert VO to ArticleDO and update
        ArticleDO articleDO = ArticleDO.builder()
                .id(articleId)
                .title(updateArticleReqVO.getTitle())
                .cover(updateArticleReqVO.getCover())
                .summary(updateArticleReqVO.getSummary())
                .updateTime(LocalDateTime.now())
                .build();
        int count = articleMapper.updateById(articleDO);

        // Determine if the article exists based on whether the update was successful
        if (count == 0) {
            log.warn("==> This article does not exist, articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 2. Convert VO to ArticleContentDO and update
        ArticleContentDO articleContentDO = ArticleContentDO.builder()
                .articleId(articleId)
                .content(updateArticleReqVO.getContent())
                .build();
        articleContentMapper.updateByArticleId(articleContentDO);

        // 3. Update article category
        Long categoryId = updateArticleReqVO.getCategoryId();

        // 3.1 Verify if the submitted category actually exists
        CategoryDO categoryDO = categoryMapper.selectById(categoryId);
        if (Objects.isNull(categoryDO)) {
            log.warn("==> Category does not exist, categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }

        // First delete the category record associated with the article, then insert the new association
        articleCategoryRelMapper.deleteByArticleId(articleId);
        ArticleCategoryRelDO articleCategoryRelDO = ArticleCategoryRelDO.builder()
                .articleId(articleId)
                .categoryId(categoryId)
                .build();
        articleCategoryRelMapper.insert(articleCategoryRelDO);

        // 4. Save the collection of tags related to the article
        // First delete the tags corresponding to the article
        articleTagRelMapper.deleteByArticleId(articleId);
        List<String> publishTags = updateArticleReqVO.getTags();
        insertTags(articleId, publishTags);

        // 5. Publish article update event
        eventPublisher.publishEvent(new UpdateArticleEvent(this, articleId, 
            updateArticleReqVO.getTitle(), 
            updateArticleReqVO.getContent(), 
            updateArticleReqVO.getSummary(), 
            updateArticleReqVO.getCover(), 
            Constants.DATE_TIME_FORMATTER.format(LocalDateTime.now())));

        return Response.success();
    }

    /**
     * Save tags
     * @param articleId
     * @param publishTags
     */
    private void insertTags(Long articleId, List<String> publishTags) {
        // Filter submitted tags (tags that don't exist in the table)
        List<String> notExistTags = null;
        // Filter submitted tags (tags that already exist in the table)
        List<String> existedTags = null;

        // Query all tags
        List<TagDO> tagDOS = tagMapper.selectList(null);

        // If no tags have been added to the table yet
        if (CollectionUtils.isEmpty(tagDOS)) {
            notExistTags = publishTags;
        } else {
            List<String> tagIds = tagDOS.stream().map(tagDO -> String.valueOf(tagDO.getId())).collect(Collectors.toList());
            // If related tags have been added to the table, filtering is needed
            // Filter by tag ID, if it contains the corresponding ID, it means the submitted tag exists in the table
            existedTags = publishTags.stream().filter(publishTag -> tagIds.contains(publishTag)).collect(Collectors.toList());
            // Otherwise, it doesn't exist
            notExistTags = publishTags.stream().filter(publishTag -> !tagIds.contains(publishTag)).collect(Collectors.toList());
        }

        // For the submitted tags that already exist in the table, insert the article-tag association into the database
        if (!CollectionUtils.isEmpty(existedTags)) {
            List<ArticleTagRelDO> articleTagRelDOS = Lists.newArrayList();
            existedTags.forEach(tagId -> {
                ArticleTagRelDO articleTagRelDO = ArticleTagRelDO.builder()
                        .articleId(articleId)
                        .tagId(Long.valueOf(tagId))
                        .build();
                articleTagRelDOS.add(articleTagRelDO);
            });
            // Batch insert
            articleTagRelMapper.insertBatchSomeColumn(articleTagRelDOS);
        }

        // For the submitted tags that don't exist in the table, save them to the database
        if (!CollectionUtils.isEmpty(notExistTags)) {
            // Need to first insert the tags into the database, get the corresponding tag IDs, and then insert the article-tag association into the database
            List<ArticleTagRelDO> articleTagRelDOS = Lists.newArrayList();
            notExistTags.forEach(tagName -> {
                TagDO tagDO = TagDO.builder()
                        .name(tagName)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build();

                tagMapper.insert(tagDO);

                // Get the saved tag ID
                Long tagId = tagDO.getId();

                // Article-tag association
                ArticleTagRelDO articleTagRelDO = ArticleTagRelDO.builder()
                        .articleId(articleId)
                        .tagId(tagId)
                        .build();
                articleTagRelDOS.add(articleTagRelDO);
            });
            // Batch insert
            articleTagRelMapper.insertBatchSomeColumn(articleTagRelDOS);
        }
    }
}