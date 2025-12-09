package com.luqi.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.luqi.weblog.admin.model.vo.wiki.*;
import com.luqi.weblog.admin.service.AdminWikiService;
import com.luqi.weblog.common.domain.dos.WikiCatalogDO;
import com.luqi.weblog.common.domain.dos.WikiDO;
import com.luqi.weblog.common.domain.mapper.WikiCatalogMapper;
import com.luqi.weblog.common.domain.mapper.WikiMapper;
import com.luqi.weblog.common.enums.ResponseCodeEnum;
import com.luqi.weblog.common.exception.BizException;
import com.luqi.weblog.common.utils.PageResponse;
import com.luqi.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminWikiServiceImpl implements AdminWikiService {

    @Autowired
    private WikiMapper wikiMapper;
    @Autowired
    private WikiCatalogMapper wikiCatalogMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response addWiki(AddWikiReqVO addWikiReqVO) {
        // VO to DO
        WikiDO wikiDO = WikiDO.builder()
                .title(addWikiReqVO.getTitle())
                .cover(addWikiReqVO.getCover())
                .summary(addWikiReqVO.getSummary())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        // Add Wiki
        wikiMapper.insert(wikiDO);
        
        // Get ID
        Long wikiId = wikiDO.getId();

        // Init default catalog
        // > Overview
        // > Basic
        wikiCatalogMapper.insert(WikiCatalogDO.builder().wikiId(wikiId).title("Overview").sort(1).build());
        wikiCatalogMapper.insert(WikiCatalogDO.builder().wikiId(wikiId).title("Basic").sort(2).build());
        
        return Response.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response deleteWiki(DeleteWikiReqVO deleteWikiReqVO) {
        Long wikiId = deleteWikiReqVO.getId();

        // Delete wiki
        int count = wikiMapper.deleteById(wikiId);

        // Check if exists
        if (count == 0) {
            log.warn("Wiki does not exist, wikiId: {}", wikiId);
            throw new BizException(ResponseCodeEnum.WIKI_NOT_FOUND);
        }

        // Find catalogs
        List<WikiCatalogDO> wikiCatalogDOS = wikiCatalogMapper.selectByWikiId(wikiId);
        // Filter article IDs
        List<Long> articleIds = wikiCatalogDOS.stream()
                .filter(wikiCatalogDO -> Objects.nonNull(wikiCatalogDO.getArticleId())
                        && Objects.equals(wikiCatalogDO.getLevel(), com.luqi.weblog.common.enums.WikiCatalogLevelEnum.TWO.getValue()))
                .map(WikiCatalogDO::getArticleId)
                .collect(Collectors.toList());

        // Update article type to NORMAL
        if (!CollectionUtils.isEmpty(articleIds)) {
            articleMapper.updateByIds(ArticleDO.builder()
                    .type(com.luqi.weblog.common.enums.ArticleTypeEnum.NORMAL.getValue())
                    .build(), articleIds);
        }

        // Delete catalogs
        wikiCatalogMapper.deleteByWikiId(wikiId);
        return Response.success();
    }

    @Override
    public Response findWikiPageList(FindWikiPageListReqVO findWikiPageListReqVO) {
        Long current = findWikiPageListReqVO.getCurrent();
        Long size = findWikiPageListReqVO.getSize();
        String title = findWikiPageListReqVO.getTitle();
        LocalDate startDate = findWikiPageListReqVO.getStartDate();
        LocalDate endDate = findWikiPageListReqVO.getEndDate();

        Page<WikiDO> wikiDOPage = wikiMapper.selectPageList(current, size, title, startDate, endDate, null);
        List<WikiDO> records = wikiDOPage.getRecords();

        List<FindWikiPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(records)) {
            vos = records.stream().map(wikiDO -> FindWikiPageListRspVO.builder()
                    .id(wikiDO.getId())
                    .title(wikiDO.getTitle())
                    .cover(wikiDO.getCover())
                    .summary(wikiDO.getSummary())
                    .createTime(wikiDO.getCreateTime())
                    .isTop(wikiDO.getWeight() > 0)
                    .isPublish(wikiDO.getIsPublish())
                    .build()).collect(Collectors.toList());
        }
        return PageResponse.success(wikiDOPage, vos);
    }

    @Override
    public Response updateWikiIsTop(UpdateWikiIsTopReqVO updateWikiIsTopReqVO) {
        Long id = updateWikiIsTopReqVO.getId();
        Boolean isTop = updateWikiIsTopReqVO.getIsTop();
        Integer weight = 0;
        if (isTop) {
            WikiDO wikiDO = wikiMapper.selectMaxWeight();
            Integer maxWeight = 0;
            if (Objects.nonNull(wikiDO)) {
                maxWeight = wikiDO.getWeight();
            }
            weight = maxWeight + 1;
        }
        wikiMapper.updateById(WikiDO.builder().id(id).weight(weight).build());
        return Response.success();
    }

    @Override
    public Response updateWikiIsPublish(UpdateWikiIsPublishReqVO updateWikiIsPublishReqVO) {
        Long id = updateWikiIsPublishReqVO.getId();
        Boolean isPublish = updateWikiIsPublishReqVO.getIsPublish();
        wikiMapper.updateById(WikiDO.builder().id(id).isPublish(isPublish).build());
        return Response.success();
    }

    @Override
    public Response updateWiki(UpdateWikiReqVO updateWikiReqVO) {
        WikiDO wikiDO = WikiDO.builder()
                .id(updateWikiReqVO.getId())
                .title(updateWikiReqVO.getTitle())
                .cover(updateWikiReqVO.getCover())
                .summary(updateWikiReqVO.getSummary())
                .updateTime(LocalDateTime.now())
                .build();
        wikiMapper.updateById(wikiDO);
        return Response.success();
    }

    @Override
    public Response findWikiCatalogList(Long id) {
        List<WikiCatalogDO> catalogDOS = wikiCatalogMapper.selectList(new LambdaQueryWrapper<WikiCatalogDO>()
                .eq(WikiCatalogDO::getWikiId, id)
                .orderByAsc(WikiCatalogDO::getSort));
        
        // Convert flat list to tree
        List<FindWikiCatalogRspVO> vos = null;
        if (!CollectionUtils.isEmpty(catalogDOS)) {
            vos = Lists.newArrayList();
            // Level 1
            List<WikiCatalogDO> level1Catalogs = catalogDOS.stream()
                    .filter(catalog -> catalog.getLevel() == 1)
                    .sorted((o1, o2) -> o1.getSort().compareTo(o2.getSort()))
                    .collect(Collectors.toList());

            for (WikiCatalogDO level1 : level1Catalogs) {
                FindWikiCatalogRspVO level1VO = FindWikiCatalogRspVO.builder()
                        .id(level1.getId())
                        .articleId(level1.getArticleId())
                        .title(level1.getTitle())
                        .level(level1.getLevel())
                        .sort(level1.getSort())
                        .build();
                
                // Find children (Level 2)
                List<FindWikiCatalogRspVO> children = catalogDOS.stream()
                        .filter(catalog -> Objects.equals(catalog.getParentId(), level1.getId()))
                        .sorted((o1, o2) -> o1.getSort().compareTo(o2.getSort()))
                        .map(catalog -> FindWikiCatalogRspVO.builder()
                                .id(catalog.getId())
                                .articleId(catalog.getArticleId())
                                .title(catalog.getTitle())
                                .level(catalog.getLevel())
                                .sort(catalog.getSort())
                                .build())
                        .collect(Collectors.toList());
                level1VO.setChildren(children);
                vos.add(level1VO);
            }
        }
        return Response.success(vos);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response updateWikiCatalog(UpdateWikiCatalogReqVO updateWikiCatalogReqVO) {
        Long wikiId = updateWikiCatalogReqVO.getId();
        List<UpdateWikiCatalogReqVO.CatalogItem> catalogs = updateWikiCatalogReqVO.getCatalogs();

        // 1. Delete old catalogs
        wikiCatalogMapper.delete(new LambdaQueryWrapper<WikiCatalogDO>().eq(WikiCatalogDO::getWikiId, wikiId));

        // 2. Insert new catalogs
        if (!CollectionUtils.isEmpty(catalogs)) {
            for (int i = 0; i < catalogs.size(); i++) {
                UpdateWikiCatalogReqVO.CatalogItem level1 = catalogs.get(i);
                WikiCatalogDO level1DO = WikiCatalogDO.builder()
                        .wikiId(wikiId)
                        .title(level1.getTitle())
                        .level(1)
                        .sort(i + 1)
                        .articleId(level1.getArticleId())
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .isDeleted(false)
                        .build();
                wikiCatalogMapper.insert(level1DO);
                
                if (!CollectionUtils.isEmpty(level1.getChildren())) {
                    for (int j = 0; j < level1.getChildren().size(); j++) {
                        UpdateWikiCatalogReqVO.CatalogItem level2 = level1.getChildren().get(j);
                        WikiCatalogDO level2DO = WikiCatalogDO.builder()
                                .wikiId(wikiId)
                                .parentId(level1DO.getId())
                                .title(level2.getTitle())
                                .level(2)
                                .sort(j + 1)
                                .articleId(level2.getArticleId())
                                .createTime(LocalDateTime.now())
                                .updateTime(LocalDateTime.now())
                                .isDeleted(false)
                                .build();
                        wikiCatalogMapper.insert(level2DO);
                    }
                }
            }
        }
        return Response.success();
    }
}