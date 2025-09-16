package com.luqi.weblog.admin.model.vo.article;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @date: 2023-09-15 14:07
 * @description: 文章发布
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "Publish Article VO")
public class PublishArticleReqVO {

    @NotBlank(message = "Article title cannot be empty")
    @Length(min = 1, max = 40, message = "Article title must be between 1 and 40 characters")
    private String title;

    @NotBlank(message = "Article content cannot be empty")
    private String content;

    @NotBlank(message = "Article cover cannot be empty")
    private String cover;

    private String summary;

    @NotNull(message = "Article category cannot be empty")
    private Long categoryId;

    @NotEmpty(message = "Article tags cannot be empty")
    private List<String> tags;
}