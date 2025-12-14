package com.luqi.weblog.web.service;

import com.luqi.weblog.common.utils.Response;
import com.luqi.weblog.web.model.vo.comment.FindCommentListReqVO;
import com.luqi.weblog.web.model.vo.comment.PublishCommentReqVO;

/**
 * @description: 评论服务接口
 **/
public interface CommentService {

    /**
     * 发布评论
     * @param publishCommentReqVO
     * @return
     */
    Response publishComment(PublishCommentReqVO publishCommentReqVO);

    /**
     * 查询页面所有评论
     * @param findCommentListReqVO
     * @return
     */
    Response findCommentList(FindCommentListReqVO findCommentListReqVO);
}
