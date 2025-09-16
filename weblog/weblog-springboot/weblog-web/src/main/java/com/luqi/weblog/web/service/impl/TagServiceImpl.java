package com.luqi.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luqi.weblog.common.domain.dos.ArticleDO;
import com.luqi.weblog.common.domain.dos.ArticleTagRelDO;
import com.luqi.weblog.common.domain.dos.TagDO;
import com.luqi.weblog.common.domain.mapper.ArticleMapper;
import com.luqi.weblog.common.domain.mapper.ArticleTagRelMapper;
import com.luqi.weblog.common.domain.mapper.TagMapper;
import com.luqi.weblog.common.enums.ResponseCodeEnum;
import com.luqi.weblog.common.exception.BizException;
import com.luqi.weblog.common.utils.PageResponse;
import com.luqi.weblog.common.utils.Response;
import com.luqi.weblog.web.convert.ArticleConvert;
import com.luqi.weblog.web.model.vo.tag.FindTagArticlePageListReqVO;
import com.luqi.weblog.web.model.vo.tag.FindTagArticlePageListRspVO;
import com.luqi.weblog.web.model.vo.tag.FindTagListReqVO;
import com.luqi.weblog.web.model.vo.tag.FindTagListRspVO;
import com.luqi.weblog.web.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @date: 2023-09-15 14:03
 * @description: Tag
 **/
@Service
@Slf4j
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;
    @Autowired
    private ArticleMapper articleMapper;

    /**
     * Get tag list
     *
     * @param findTagListReqVO
     * @return
     */
    @Override
    public Response findTagList(FindTagListReqVO findTagListReqVO) {
        Long size = findTagListReqVO.getSize();

        List<TagDO> tagDOS = null;
        // If size is not specified in the request parameters
        if (Objects.isNull(size) || size == 0) {
            // Query all tags
            tagDOS = tagMapper.selectList(Wrappers.emptyWrapper());
        } else {
            // Otherwise query specified number
            tagDOS = tagMapper.selectByLimit(size);
        }

        // DO to VO
        List<FindTagListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(tagDOS)) {
            vos = tagDOS.stream()
                    .map(tagDO -> FindTagListRspVO.builder()
                            .id(tagDO.getId())
                            .name(tagDO.getName())
                            .articlesTotal(tagDO.getArticlesTotal())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(vos);
    }

    /**
     * Get paginated article list under tag
     *
     * @param findTagArticlePageListReqVO
     * @return
     */
    @Override
    public Response findTagPageList(FindTagArticlePageListReqVO findTagArticlePageListReqVO) {
        Long current = findTagArticlePageListReqVO.getCurrent();
        Long size = findTagArticlePageListReqVO.getSize();
        // Tag ID
        Long tagId = findTagArticlePageListReqVO.getId();

        // Check if the tag exists
        TagDO tagDO = tagMapper.selectById(tagId);
        if (Objects.isNull(tagDO)) {
            log.warn("==> Tag does not exist, tagId: {}", tagId);
            throw new BizException(ResponseCodeEnum.TAG_NOT_EXISTED);
        }

        // First query all associated article IDs under this tag
        List<ArticleTagRelDO> articleTagRelDOS = articleTagRelMapper.selectByTagId(tagId);

        // If no articles have been published under this tag
        if (CollectionUtils.isEmpty(articleTagRelDOS)) {
            log.info("==> There is no Article published under this tag, tagId: {}", tagId);
            return PageResponse.success(null, null);
        }

        // Extract all article IDs
        List<Long> articleIds = articleTagRelDOS.stream().map(ArticleTagRelDO::getArticleId).collect(Collectors.toList());

        // Query paginated article data based on article ID collection
        Page<ArticleDO> page = articleMapper.selectPageListByArticleIds(current, size, articleIds);
        List<ArticleDO> articleDOS = page.getRecords();

        // DO to VO
        List<FindTagArticlePageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(articleDOS)) {
            vos = articleDOS.stream()
                    .map(articleDO -> ArticleConvert.INSTANCE.convertDO2TagArticleVO(articleDO))
                    .collect(Collectors.toList());
        }

        return PageResponse.success(page, vos);
    }
}
