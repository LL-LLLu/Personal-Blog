package com.luqi.weblog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ArticleTypeEnum {
    NORMAL(1, "普通文章"),
    WIKI(2, "知识库文章");

    private final Integer value;
    private final String description;
}