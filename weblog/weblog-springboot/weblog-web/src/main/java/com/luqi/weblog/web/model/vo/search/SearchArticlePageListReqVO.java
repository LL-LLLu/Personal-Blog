package com.luqi.weblog.web.model.vo.search;

import com.luqi.weblog.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "文章搜索 VO")
public class SearchArticlePageListReqVO extends BasePageQuery {
    /**
     * 查询关键词
     */
    @NotBlank(message = "搜索关键词不能为空")
    @ApiModelProperty(value = "查询关键词", required = true)
    private String word;

    /**
     * 是否启用模糊搜索
     */
    @ApiModelProperty(value = "是否启用模糊搜索", example = "false")
    private Boolean fuzzySearch = false;

    /**
     * 模糊搜索最大编辑距离 (0-2)
     */
    @Min(value = 0, message = "编辑距离不能小于0")
    @Max(value = 2, message = "编辑距离不能大于2")
    @ApiModelProperty(value = "模糊搜索最大编辑距离", example = "2")
    private Integer maxEdits = 2;

    /**
     * 是否启用通配符搜索（前缀/后缀匹配）
     */
    @ApiModelProperty(value = "是否启用通配符搜索", example = "false")
    private Boolean wildcardSearch = false;
}