package com.luqi.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luqi.weblog.admin.model.vo.tag.*;
import com.luqi.weblog.admin.service.AdminTagService;
import com.luqi.weblog.common.domain.dos.ArticleTagRelDO;
import com.luqi.weblog.common.domain.dos.TagDO;
import com.luqi.weblog.common.domain.mapper.ArticleTagRelMapper;
import com.luqi.weblog.common.domain.mapper.TagMapper;
import com.luqi.weblog.common.enums.ResponseCodeEnum;
import com.luqi.weblog.common.exception.BizException;
import com.luqi.weblog.common.model.vo.SelectRspVO;
import com.luqi.weblog.common.utils.PageResponse;
import com.luqi.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
 * @description: Admin tag service implementation
 **/
@Service
@Slf4j
public class AdminTagServiceImpl extends ServiceImpl<TagMapper, TagDO> implements AdminTagService {

    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;

    /**
     * Add tag collection
     *
     * @param addTagReqVO
     * @return
     */
    @Override
    public Response addTags(AddTagReqVO addTagReqVO) {
        // vo to do
        List<TagDO> tagDOS = addTagReqVO.getTags().stream()
                .map(tagName -> TagDO.builder()
                        .name(tagName.trim()) // remove leading and trailing spaces
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());

        // Batch insert
        try {
            saveBatch(tagDOS);
        } catch (Exception e) {
            log.warn("This tag already exists", e);
        }

        return Response.success();
    }

    /**
     * Query tag pagination
     *
     * @param findTagPageListReqVO
     * @return
     */
    @Override
    public PageResponse findTagPageList(FindTagPageListReqVO findTagPageListReqVO) {
        // Pagination parameters and condition parameters
        Long current = findTagPageListReqVO.getCurrent();
        Long size = findTagPageListReqVO.getSize();
        String name = findTagPageListReqVO.getName();
        LocalDate startDate = findTagPageListReqVO.getStartDate();
        LocalDate endDate = findTagPageListReqVO.getEndDate();

        // Pagination query
        Page<TagDO> page = tagMapper.selectPageList(current, size, name, startDate, endDate);

        List<TagDO> records = page.getRecords();

        // do to vo
        List<FindTagPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(records)) {
            vos = records.stream().map(tagDO -> FindTagPageListRspVO.builder()
                    .id(tagDO.getId())
                    .name(tagDO.getName())
                    .createTime(tagDO.getCreateTime())
                    .articlesTotal(tagDO.getArticlesTotal())
                    .build()).collect(Collectors.toList());
        }

        return PageResponse.success(page, vos);
    }


    /**
     * Fuzzy query by tag keywords
     *
     * @param searchTagsReqVO
     * @return
     */
    @Override
    public Response searchTags(SearchTagsReqVO searchTagsReqVO) {
        String key = searchTagsReqVO.getKey();

        // Execute fuzzy query
        List<TagDO> tagDOS = tagMapper.selectByKey(key);

        // do to vo
        List<SelectRspVO> vos = null;
        if (!CollectionUtils.isEmpty(tagDOS)) {
            vos = tagDOS.stream()
                    .map(tagDO -> SelectRspVO.builder()
                            .label(tagDO.getName())
                            .value(tagDO.getId())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(vos);
    }

    /**
     * Query tag Select list data
     *
     * @return
     */
    @Override
    public Response findTagSelectList() {
        // Query all tags
        List<TagDO> tagDOS = tagMapper.selectList(Wrappers.emptyWrapper());

        // DO to VO
        List<SelectRspVO> vos = null;
        if (!CollectionUtils.isEmpty(tagDOS)) {
            vos = tagDOS.stream()
                    .map(tagDO -> SelectRspVO.builder()
                            .label(tagDO.getName())
                            .value(tagDO.getId())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(vos);
    }

    /**
     * Delete tag
     *
     * @param deleteTagReqVO
     * @return
     */
    @Override
    public Response deleteTag(DeleteTagReqVO deleteTagReqVO) {
        // Tag ID
        Long tagId = deleteTagReqVO.getId();

        // Check if there are associated articles under this tag, if so, deletion is not allowed
        ArticleTagRelDO articleTagRelDO = articleTagRelMapper.selectOneByTagId(tagId);

        if (Objects.nonNull(articleTagRelDO)) {
            log.warn("==> This category contains articles and cannot be deleted, categoryId: {}", tagId);
            throw new BizException(ResponseCodeEnum.CATEGORY_CAN_NOT_DELETE);
        }

        // Delete by tag ID
        int count = tagMapper.deleteById(tagId);

        return count == 1 ? Response.success() : Response.fail(ResponseCodeEnum.TAG_NOT_EXISTED);
    }
}
