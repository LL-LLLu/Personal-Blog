package com.luqi.weblog.web.model.vo.article;

import com.luqi.weblog.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

/**
 * @author: Lu Qi
 * @url: www.luqi.com
 * @date: 2023-09-15 14:07
 * @description: Homepage - Article pagination
 **/
@Data
@Builder
@ApiModel(value = "Homepage article pagination query VO")
public class FindIndexArticlePageListReqVO extends BasePageQuery {
}