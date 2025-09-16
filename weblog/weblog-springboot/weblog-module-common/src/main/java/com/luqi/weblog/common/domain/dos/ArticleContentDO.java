package com.luqi.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @date: 2023-08-22 17:01
 * @description: Article Content
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_article_content")
public class ArticleContentDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long articleId;

    private String content;

}
