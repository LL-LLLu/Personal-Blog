package com.luqi.weblog.admin.model.vo.wiki;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "更新知识库目录 VO")
public class UpdateWikiCatalogReqVO {

    @NotNull(message = "知识库 ID 不能为空")
    private Long id;

    @Valid
    private List<CatalogItem> catalogs;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CatalogItem {
        private Long id;
        private Long articleId;
        private String title;
        private Integer level;
        private Integer sort;
        private List<CatalogItem> children;
    }
}