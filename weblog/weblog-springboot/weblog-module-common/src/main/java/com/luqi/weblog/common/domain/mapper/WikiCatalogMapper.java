package com.luqi.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.luqi.weblog.common.config.InsertBatchMapper;
import com.luqi.weblog.common.domain.dos.WikiCatalogDO;

import java.util.List;

import com.luqi.weblog.common.enums.WikiCatalogLevelEnum;

public interface WikiCatalogMapper extends InsertBatchMapper<WikiCatalogDO> {

    /**
     * Query first article in wiki catalog
     * @param wikiId
     * @return
     */
    default WikiCatalogDO selectFirstArticleId(Long wikiId) {
        return selectOne(Wrappers.<WikiCatalogDO>lambdaQuery()
                .eq(WikiCatalogDO::getWikiId, wikiId) 
                .eq(WikiCatalogDO::getLevel, WikiCatalogLevelEnum.TWO.getValue()) 
                .isNotNull(WikiCatalogDO::getArticleId) 
                .orderByAsc(WikiCatalogDO::getId) 
                .last("LIMIT 1") 
        );
    }

    /**
     * 根据某个知识库下所有目录
     * @param wikiId
     * @return
     */
    default List<WikiCatalogDO> selectByWikiId(Long wikiId) {
        return selectList(Wrappers.<WikiCatalogDO>lambdaQuery()
                .eq(WikiCatalogDO::getWikiId, wikiId)
        );
    }

    /**
     * 删除知识库
     * @param wikiId
     * @return
     */
    default int deleteByWikiId(Long wikiId) {
        return delete(Wrappers.<WikiCatalogDO>lambdaQuery()
                .eq(WikiCatalogDO::getWikiId, wikiId));
    }
}