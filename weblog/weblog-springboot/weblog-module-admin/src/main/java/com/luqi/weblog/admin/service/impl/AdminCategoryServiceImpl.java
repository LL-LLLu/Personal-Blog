package com.luqi.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.injector.methods.SelectPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luqi.weblog.admin.model.vo.category.*;
import com.luqi.weblog.admin.service.AdminCategoryService;
import com.luqi.weblog.common.domain.dos.ArticleCategoryRelDO;
import com.luqi.weblog.common.domain.dos.CategoryDO;
import com.luqi.weblog.common.domain.mapper.ArticleCategoryRelMapper;
import com.luqi.weblog.common.domain.mapper.CategoryMapper;
import com.luqi.weblog.common.enums.ResponseCodeEnum;
import com.luqi.weblog.common.exception.BizException;
import com.luqi.weblog.common.model.vo.SelectRspVO;
import com.luqi.weblog.common.utils.PageResponse;
import com.luqi.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @date: 2023-09-15 14:03
 * @description: Category service implementation
 **/
@Service
@Slf4j
public class AdminCategoryServiceImpl implements AdminCategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    /**
     * Add category
     *
     * @param addCategoryReqVO
     * @return
     */
    @Override
    public Response addCategory(AddCategoryReqVO addCategoryReqVO) {
        String categoryName = addCategoryReqVO.getName();

        // First check if the category already exists
        CategoryDO categoryDO = categoryMapper.selectByName(categoryName);

        if (Objects.nonNull(categoryDO)) {
            log.warn("Category name: {}, already exists", categoryName);
            throw new BizException(ResponseCodeEnum.CATEGORY_NAME_IS_EXISTED);
        }

        // Build DO class
        CategoryDO insertCategoryDO = CategoryDO.builder()
                .name(addCategoryReqVO.getName().trim())
                .build();

        // Execute insert
        categoryMapper.insert(insertCategoryDO);

        return Response.success();
    }

    /**
     * Category pagination data query
     *
     * @param findCategoryPageListReqVO
     * @return
     */
    @Override
    public PageResponse findCategoryPageList(FindCategoryPageListReqVO findCategoryPageListReqVO) {
        // Get current page and number of data to be displayed per page
        Long current = findCategoryPageListReqVO.getCurrent();
        Long size = findCategoryPageListReqVO.getSize();
        String name = findCategoryPageListReqVO.getName();
        LocalDate startDate = findCategoryPageListReqVO.getStartDate();
        LocalDate endDate = findCategoryPageListReqVO.getEndDate();

        // Execute pagination query
        Page<CategoryDO> categoryDOPage = categoryMapper.selectPageList(current, size, name, startDate, endDate);

        List<CategoryDO> categoryDOS = categoryDOPage.getRecords();

        // Convert DO to VO
        List<FindCategoryPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(categoryDOS)) {
            vos = categoryDOS.stream()
                    .map(categoryDO -> FindCategoryPageListRspVO.builder()
                            .id(categoryDO.getId())
                            .name(categoryDO.getName())
                            .createTime(categoryDO.getCreateTime())
                            .articlesTotal(categoryDO.getArticlesTotal())
                            .build())
                    .collect(Collectors.toList());
        }

        return PageResponse.success(categoryDOPage, vos);
    }

    /**
     * Get Select list data of article categories
     *
     * @return
     */
    @Override
    public Response findCategorySelectList() {
        // Query all categories
        List<CategoryDO> categoryDOS = categoryMapper.selectList(null);

        // Convert DO to VO
        List<SelectRspVO> selectRspVOS = null;
        // If category data is not empty
        if (!CollectionUtils.isEmpty(categoryDOS)) {
            // Use category ID as Value and category name as label
            selectRspVOS = categoryDOS.stream()
                    .map(categoryDO -> SelectRspVO.builder()
                            .label(categoryDO.getName())
                            .value(categoryDO.getId())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(selectRspVOS);
    }

    @Override
    public Response deleteCategory(DeleteCategoryReqVO deleteCategoryReqVO) {
        // Category ID
        Long categoryId = deleteCategoryReqVO.getId();

        // Check if there are articles under this category, if yes, prompt that all articles under the category need to be deleted first
        ArticleCategoryRelDO articleCategoryRelDO = articleCategoryRelMapper.selectOneByCategoryId(categoryId);

        if (Objects.nonNull(articleCategoryRelDO)) {
            log.warn("==> This category contains articles and cannot be deleted, categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_CAN_NOT_DELETE);
        }

        // Delete category
        categoryMapper.deleteById(categoryId);

        return Response.success();
    }
}