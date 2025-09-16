package com.luqi.weblog.web.model.vo.article;

import com.luqi.weblog.web.model.vo.category.FindCategoryListRspVO;
import com.luqi.weblog.web.model.vo.tag.FindTagListRspVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * @author: Lu Qi
 * @url: www.luqi.com
 * @date: 2023-09-15 14:07
 * @description: Homepage - Article pagination
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindIndexArticlePageListRspVO {
    private Long id;
    private String cover;
    private String title;
    private LocalDate createDate;
    private String summary;
    /**
     * Article category
     */
    private FindCategoryListRspVO category;

    /**
     * Article tags
     */
    private List<FindTagListRspVO> tags;
}