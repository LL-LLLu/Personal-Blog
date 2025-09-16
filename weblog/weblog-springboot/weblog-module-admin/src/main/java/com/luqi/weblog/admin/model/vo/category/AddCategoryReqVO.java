package com.luqi.weblog.admin.model.vo.category;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @description: 分类新增
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "add category VO")
public class AddCategoryReqVO {

    @NotBlank(message = "the category name can not be null")
    @Length(min = 1, max = 10, message = "name of category need to be within 1 ~ 10 characters")
    private String name;

}
