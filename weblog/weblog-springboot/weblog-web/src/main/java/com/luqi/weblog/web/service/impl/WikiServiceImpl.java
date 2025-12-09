package com.luqi.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.luqi.weblog.admin.model.vo.wiki.FindWikiCatalogRspVO;
import com.luqi.weblog.common.domain.dos.WikiCatalogDO;
import com.luqi.weblog.common.domain.dos.WikiDO;
import com.luqi.weblog.common.domain.mapper.WikiCatalogMapper;
import com.luqi.weblog.common.domain.mapper.WikiMapper;
import com.luqi.weblog.common.enums.ResponseCodeEnum;
import com.luqi.weblog.common.exception.BizException;
import com.luqi.weblog.common.utils.Response;
import com.luqi.weblog.web.model.vo.wiki.FindWikiArticlePreNextReqVO;
import com.luqi.weblog.web.model.vo.wiki.FindWikiArticlePreNextRspVO;
import com.luqi.weblog.web.model.vo.wiki.FindWikiListRspVO;
import com.luqi.weblog.web.service.WikiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WikiServiceImpl implements WikiService {

    @Autowired
    private WikiMapper wikiMapper;
    @Autowired
    private WikiCatalogMapper wikiCatalogMapper;

    @Override
    public Response findWikiList() {
        // Get all published wikis
        List<WikiDO> wikiDOS = wikiMapper.selectList(new LambdaQueryWrapper<WikiDO>()
                .eq(WikiDO::getIsPublish, true)
                .orderByDesc(WikiDO::getWeight)
                .orderByDesc(WikiDO::getCreateTime));

        List<FindWikiListRspVO> vos = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(wikiDOS)) {
            vos = wikiDOS.stream().map(wikiDO -> {
                // Find the first article ID for this wiki to link to
                Long firstArticleId = null;
                List<WikiCatalogDO> catalogs = wikiCatalogMapper.selectList(new LambdaQueryWrapper<WikiCatalogDO>()
                        .eq(WikiCatalogDO::getWikiId, wikiDO.getId())
                        .isNotNull(WikiCatalogDO::getArticleId)
                        .orderByAsc(WikiCatalogDO::getSort)
                        .last("LIMIT 1"));
                if (!CollectionUtils.isEmpty(catalogs)) {
                    firstArticleId = catalogs.get(0).getArticleId();
                }

                return FindWikiListRspVO.builder()
                        .id(wikiDO.getId())
                        .title(wikiDO.getTitle())
                        .cover(wikiDO.getCover())
                        .summary(wikiDO.getSummary())
                        .isTop(wikiDO.getWeight() > 0)
                        .firstArticleId(firstArticleId)
                        .build();
            }).collect(Collectors.toList());
        }
        return Response.success(vos);
    }

    @Override
    public Response findWikiCatalogList(Long id) {
        // Reuse logic or implement similar to Admin but maybe filtered?
        // For now, assuming public catalog structure is same as admin
        List<WikiCatalogDO> catalogDOS = wikiCatalogMapper.selectList(new LambdaQueryWrapper<WikiCatalogDO>()
                .eq(WikiCatalogDO::getWikiId, id)
                .orderByAsc(WikiCatalogDO::getSort));

        List<FindWikiCatalogRspVO> vos = null;
        if (!CollectionUtils.isEmpty(catalogDOS)) {
            vos = Lists.newArrayList();
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
    public Response findWikiArticlePreNext(FindWikiArticlePreNextReqVO reqVO) {
        Long wikiId = reqVO.getId();
        Long articleId = reqVO.getArticleId();

        // Get all catalogs with articles for this wiki, sorted
        List<WikiCatalogDO> catalogs = wikiCatalogMapper.selectList(new LambdaQueryWrapper<WikiCatalogDO>()
                .eq(WikiCatalogDO::getWikiId, wikiId)
                .isNotNull(WikiCatalogDO::getArticleId)
                .orderByAsc(WikiCatalogDO::getLevel) // This might be tricky if sort is per level
                // We need a global sort order. Usually we sort by Level 1 sort, then Level 2 sort.
                // But let's fetch all and sort in memory to be safe.
        );
        
        // Logic to sort catalogs correctly:
        // We need the full list including folders to traverse correctly, or just flatten it if we trust IDs
        // Let's fetch ALL catalogs
        List<WikiCatalogDO> allCatalogs = wikiCatalogMapper.selectList(new LambdaQueryWrapper<WikiCatalogDO>()
                .eq(WikiCatalogDO::getWikiId, wikiId));
        
        // Sort in memory
        List<WikiCatalogDO> sortedArticles = Lists.newArrayList();
        allCatalogs.stream()
                .filter(c -> c.getLevel() == 1)
                .sorted((o1, o2) -> o1.getSort().compareTo(o2.getSort()))
                .forEach(level1 -> {
                    if (level1.getArticleId() != null) sortedArticles.add(level1);
                    allCatalogs.stream()
                            .filter(c -> Objects.equals(c.getParentId(), level1.getId()))
                            .sorted((o1, o2) -> o1.getSort().compareTo(o2.getSort()))
                            .forEach(sortedArticles::add);
                });

        // Find current index
        int currentIndex = -1;
        for (int i = 0; i < sortedArticles.size(); i++) {
            if (Objects.equals(sortedArticles.get(i).getArticleId(), articleId)) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) {
             throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        FindWikiArticlePreNextRspVO vo = new FindWikiArticlePreNextRspVO();

        // Pre
        if (currentIndex > 0) {
            WikiCatalogDO pre = sortedArticles.get(currentIndex - 1);
            vo.setPreArticle(FindWikiArticlePreNextRspVO.FindPreNextArticleRspVO.builder()
                    .articleId(pre.getArticleId())
                    .articleTitle(pre.getTitle())
                    .build());
        }

        // Next
        if (currentIndex < sortedArticles.size() - 1) {
            WikiCatalogDO next = sortedArticles.get(currentIndex + 1);
            vo.setNextArticle(FindWikiArticlePreNextRspVO.FindPreNextArticleRspVO.builder()
                    .articleId(next.getArticleId())
                    .articleTitle(next.getTitle())
                    .build());
        }

        return Response.success(vo);
    }
}