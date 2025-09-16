package com.luqi.weblog.web.model.vo.archive;

import com.luqi.weblog.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @date: 2023-09-15 14:07
 * @description: 文章归档
 **/
@Data
@Builder
@ApiModel(value = "Article Archive VO")
public class FindArchiveArticlePageListReqVO extends BasePageQuery {
}
