package com.luqi.weblog.admin.model.vo.article;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @date: 2023-09-15 14:07
 * @description: Article Deletion
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "Delete Article VO")
public class DeleteArticleReqVO {

    @NotNull(message = "Article ID cannot be empty")
    private Long id;
}