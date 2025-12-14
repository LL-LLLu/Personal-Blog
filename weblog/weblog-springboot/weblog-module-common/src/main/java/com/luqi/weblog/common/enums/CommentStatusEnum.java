package com.luqi.weblog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 评论状态枚举
 **/
@Getter
@AllArgsConstructor
public enum CommentStatusEnum {

    WAIT_EXAMINE(1, "等待审核"),
    NORMAL(2, "正常"),
    EXAMINE_FAILED(3, "审核不通过");

    private final Integer code;
    private final String description;
}
