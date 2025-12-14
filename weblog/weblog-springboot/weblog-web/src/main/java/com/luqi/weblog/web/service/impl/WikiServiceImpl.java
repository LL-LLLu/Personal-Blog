package com.luqi.weblog.web.service.impl;

import com.luqi.weblog.common.domain.dos.WikiCatalogDO;
import com.luqi.weblog.common.domain.dos.WikiDO;
import com.luqi.weblog.common.domain.mapper.WikiCatalogMapper;
import com.luqi.weblog.common.domain.mapper.WikiMapper;
import com.luqi.weblog.common.enums.ResponseCodeEnum;
import com.luqi.weblog.common.exception.BizException;
import com.luqi.weblog.common.utils.Response;
import com.luqi.weblog.web.model.vo.wiki.FindWikiDetailReqVO;
import com.luqi.weblog.web.model.vo.wiki.FindWikiDetailRspVO;
import com.luqi.weblog.web.model.vo.wiki.FindWikiListRspVO;
import com.luqi.weblog.web.service.WikiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.luqi.weblog.common.enums.WikiCatalogLevelEnum;
import com.luqi.weblog.web.model.vo.wiki.FindWikiCatalogListReqVO;
import com.luqi.weblog.web.model.vo.wiki.FindWikiCatalogListRspVO;
import java.util.Comparator;

import com.luqi.weblog.web.model.vo.article.FindPreNextArticleRspVO;
import com.luqi.weblog.web.model.vo.wiki.FindWikiArticlePreNextReqVO;
import com.luqi.weblog.web.model.vo.wiki.FindWikiArticlePreNextRspVO;

@Service
@Slf4j
public class WikiServiceImpl implements WikiService {

    @Autowired
    private WikiMapper wikiMapper;
    @Autowired
    private WikiCatalogMapper wikiCatalogMapper;

    @Override
    public Response findWikiList() {
        // Find all published wikis
        List<WikiDO> wikiDOS = wikiMapper.selectPublished();

        // DO to VO
        List<FindWikiListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(wikiDOS)) {
            vos = wikiDOS.stream()
                    .map(wikiDO -> FindWikiListRspVO.builder()
                            .id(wikiDO.getId())
                            .title(wikiDO.getTitle())
                            .cover(wikiDO.getCover())
                            .summary(wikiDO.getSummary())
                            .isTop(wikiDO.getWeight() > 0)
                            .build())
                    .collect(Collectors.toList());

            // Set the first article ID for each wiki
            vos.forEach(vo -> {
                Long wikiId = vo.getId();
                WikiCatalogDO wikiCatalogDO = wikiCatalogMapper.selectFirstArticleId(wikiId);
                vo.setFirstArticleId(Objects.nonNull(wikiCatalogDO) ? wikiCatalogDO.getArticleId() : null);
            });
        }

        return Response.success(vos);
    }

    @Override
    public Response findWikiDetail(FindWikiDetailReqVO findWikiDetailReqVO) {
        Long id = findWikiDetailReqVO.getId();
        WikiDO wikiDO = wikiMapper.selectById(id);

        if (Objects.isNull(wikiDO) || !wikiDO.getIsPublish()) {
            throw new BizException(ResponseCodeEnum.WIKI_NOT_FOUND);
        }

        FindWikiDetailRspVO vo = FindWikiDetailRspVO.builder()
                .id(wikiDO.getId())
                .title(wikiDO.getTitle())
                .cover(wikiDO.getCover())
                .summary(wikiDO.getSummary())
                .build();
        return Response.success(vo);
    }

    @Override
    public Response findWikiCatalogList(FindWikiCatalogListReqVO findWikiCatalogListReqVO) {
        Long wikiId = findWikiCatalogListReqVO.getId();

        // Get all catalog data for this wiki
        List<WikiCatalogDO> catalogDOS = wikiCatalogMapper.selectByWikiId(wikiId);

        // DO to VO
        List<FindWikiCatalogListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(catalogDOS)) {
            vos = Lists.newArrayList();

            // Filter Level 1 catalogs and sort ascending
            List<WikiCatalogDO> level1Catalogs = catalogDOS.stream()
                    .filter(catalogDO -> Objects.equals(catalogDO.getLevel(), WikiCatalogLevelEnum.ONE.getValue()))
                    .sorted(Comparator.comparing(WikiCatalogDO::getSort))
                    .collect(Collectors.toList());
            
            // Construct VO objects and add to vos list
            for (WikiCatalogDO level1Catalog : level1Catalogs) {
                vos.add(FindWikiCatalogListRspVO.builder()
                        .id(level1Catalog.getId())
                        .articleId(level1Catalog.getArticleId())
                        .title(level1Catalog.getTitle())
                        .level(level1Catalog.getLevel())
                        .build());
            }

            // Iterate vos list to construct Level 2 catalog data
            vos.forEach(level1Catalog -> {
                // Level 1 Catalog ID
                Long parentId = level1Catalog.getId();
                
                // Filter sub-catalogs under current Level 1 catalog and sort ascending
                List<WikiCatalogDO> level2CatalogDOS = catalogDOS.stream()
                        .filter(catalogDO -> Objects.equals(catalogDO.getParentId(), parentId)
                                && Objects.equals(catalogDO.getLevel(), WikiCatalogLevelEnum.TWO.getValue()))
                        .sorted(Comparator.comparing(WikiCatalogDO::getSort))
                        .collect(Collectors.toList());

                // Set sub-catalog data to children field
                if (!CollectionUtils.isEmpty(level2CatalogDOS)) {
                    List<FindWikiCatalogListRspVO> level2Catalogs = level2CatalogDOS.stream()
                            .map(catalogDO -> FindWikiCatalogListRspVO.builder()
                                    .id(catalogDO.getId())
                                    .articleId(catalogDO.getArticleId())
                                    .title(catalogDO.getTitle())
                                    .level(catalogDO.getLevel())
                                    .build())
                            .collect(Collectors.toList());
                    level1Catalog.setChildren(level2Catalogs);
                }
            });
        }

        return Response.success(vos);
    }

    /**
     * 获取上下页
     *
     * @param findWikiArticlePreNextReqVO
     * @return
     */
    @Override
    public Response findArticlePreNext(FindWikiArticlePreNextReqVO findWikiArticlePreNextReqVO) {
        // 知识库 ID
        Long wikiId = findWikiArticlePreNextReqVO.getId();
        // 文章 ID
        Long articleId = findWikiArticlePreNextReqVO.getArticleId();

        FindWikiArticlePreNextRspVO vo = new FindWikiArticlePreNextRspVO();
        // 获取当前文章所属知识库的目录
        WikiCatalogDO wikiCatalogDO = wikiCatalogMapper.selectByWikiIdAndArticleId(wikiId, articleId);

        // 构建上一篇文章 VO
        WikiCatalogDO preArticleDO = wikiCatalogMapper.selectPreArticle(wikiId, wikiCatalogDO.getId());
        if (Objects.nonNull(preArticleDO)) {
            FindPreNextArticleRspVO preArticleVO = FindPreNextArticleRspVO.builder()
                    .articleId(preArticleDO.getArticleId())
                    .articleTitle(preArticleDO.getTitle())
                    .build();
            vo.setPreArticle(preArticleVO);
        }

        // 构建下一篇文章 VO
        WikiCatalogDO nextArticleDO = wikiCatalogMapper.selectNextArticle(wikiId, wikiCatalogDO.getId());
        if (Objects.nonNull(nextArticleDO)) {
            FindPreNextArticleRspVO nextArticleVO = FindPreNextArticleRspVO.builder()
                    .articleId(nextArticleDO.getArticleId())
                    .articleTitle(nextArticleDO.getTitle())
                    .build();
            vo.setNextArticle(nextArticleVO);
        }

        return Response.success(vo);
    }
}