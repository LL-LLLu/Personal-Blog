package com.luqi.weblog.admin.convert;

import com.luqi.weblog.admin.model.vo.comment.FindCommentPageListRspVO;
import com.luqi.weblog.common.domain.dos.CommentDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @description: Comment Conversion
 **/
@Mapper
public interface CommentConvert {
    /**
     * Initialize convert instance
     */
    CommentConvert INSTANCE = Mappers.getMapper(CommentConvert.class);

    /**
     * Convert DO to VO
     * @param bean
     * @return
     */
    FindCommentPageListRspVO convertDO2VO(CommentDO bean);

}
