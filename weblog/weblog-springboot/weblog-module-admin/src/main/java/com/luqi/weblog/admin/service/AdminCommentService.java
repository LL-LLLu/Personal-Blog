package com.luqi.weblog.admin.service;

import com.luqi.weblog.admin.model.vo.comment.DeleteCommentReqVO;
import com.luqi.weblog.admin.model.vo.comment.FindCommentPageListReqVO;
import com.luqi.weblog.common.utils.Response;

/**
 * @description: Admin Comment Service
 **/
public interface AdminCommentService {

    /**
     * Query comment page list
     * @param findCommentPageListReqVO
     * @return
     */
    Response findCommentPageList(FindCommentPageListReqVO findCommentPageListReqVO);

    /**
     * Delete comment
     * @param deleteCommentReqVO
     * @return
     */
    Response deleteComment(DeleteCommentReqVO deleteCommentReqVO);

}
