package com.luqi.weblog.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luqi.weblog.admin.convert.CommentConvert;
import com.luqi.weblog.admin.event.UpdateCommentEvent;
import com.luqi.weblog.admin.model.vo.comment.DeleteCommentReqVO;
import com.luqi.weblog.admin.model.vo.comment.ExamineCommentReqVO;
import com.luqi.weblog.admin.model.vo.comment.FindCommentPageListReqVO;
import com.luqi.weblog.admin.model.vo.comment.FindCommentPageListRspVO;
import com.luqi.weblog.admin.service.AdminCommentService;
import com.luqi.weblog.common.domain.dos.CommentDO;
import com.luqi.weblog.common.domain.mapper.CommentMapper;
import com.luqi.weblog.common.enums.CommentStatusEnum;
import com.luqi.weblog.common.enums.ResponseCodeEnum;
import com.luqi.weblog.common.exception.BizException;
import com.luqi.weblog.common.utils.PageResponse;
import com.luqi.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @description: Admin Comment Service Implementation
 **/
@Service
@Slf4j
public class AdminCommentServiceImpl implements AdminCommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * Query comment page list
     *
     * @param findCommentPageListReqVO
     * @return
     */
    @Override
    public Response findCommentPageList(FindCommentPageListReqVO findCommentPageListReqVO) {
        // Get current page and page size
        Long current = findCommentPageListReqVO.getCurrent();
        Long size = findCommentPageListReqVO.getSize();
        LocalDate startDate = findCommentPageListReqVO.getStartDate();
        LocalDate endDate = findCommentPageListReqVO.getEndDate();
        String routerUrl = findCommentPageListReqVO.getRouterUrl();
        Integer status = findCommentPageListReqVO.getStatus();

        // Execute pagination query
        Page<CommentDO> commentDOPage = commentMapper.selectPageList(current, size, routerUrl, startDate, endDate, status);

        List<CommentDO> commentDOS = commentDOPage.getRecords();

        // Convert DO to VO
        List<FindCommentPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(commentDOS)) {
            vos = commentDOS.stream()
                    .map(commentDO -> CommentConvert.INSTANCE.convertDO2VO(commentDO))
                    .collect(Collectors.toList());
        }

        return PageResponse.success(commentDOPage, vos);
    }

    /**
     * Delete comment
     *
     * @param deleteCommentReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response deleteComment(DeleteCommentReqVO deleteCommentReqVO) {
        Long commentId = deleteCommentReqVO.getId();

        // Query comment
        CommentDO commentDO = commentMapper.selectById(commentId);

        // If comment doesn't exist
        if (commentDO == null) {
            log.warn("==> Comment does not exist, commentId: {}", commentId);
            throw new BizException(ResponseCodeEnum.COMMENT_NOT_FOUND);
        }

        // Delete comment
        commentMapper.deleteById(commentId);

        // Delete all child comments whose reply_comment_id points to this comment
        List<CommentDO> childComments = commentMapper.selectByReplyCommentId(commentId);
        if (!CollectionUtils.isEmpty(childComments)) {
            // Recursively delete child comments
            childComments.forEach(childComment -> {
                DeleteCommentReqVO vo = DeleteCommentReqVO.builder()
                        .id(childComment.getId())
                        .build();
                deleteComment(vo);
            });
        }

        // Delete all comments whose parent_comment_id is this comment
        commentMapper.deleteByParentCommentId(commentId);

        return Response.success();
    }

    /**
     * Examine comment
     *
     * @param examineCommentReqVO
     * @return
     */
    @Override
    public Response examine(ExamineCommentReqVO examineCommentReqVO) {
        Long commentId = examineCommentReqVO.getId();
        Integer status = examineCommentReqVO.getStatus();
        String reason = examineCommentReqVO.getReason();

        // Query comment by ID
        CommentDO commentDO = commentMapper.selectById(commentId);

        // Check if comment exists
        if (Objects.isNull(commentDO)) {
            log.warn("==> Comment does not exist, commentId: {}", commentId);
            throw new BizException(ResponseCodeEnum.COMMENT_NOT_FOUND);
        }

        // Get current comment status
        Integer currStatus = commentDO.getStatus();

        // Check if comment is in pending review status
        if (!Objects.equals(currStatus, CommentStatusEnum.WAIT_EXAMINE.getCode())) {
            log.warn("==> Comment is not in pending review status, commentId: {}", commentId);
            throw new BizException(ResponseCodeEnum.COMMENT_STATUS_NOT_WAIT_EXAMINE);
        }

        // Update comment
        commentMapper.updateById(CommentDO.builder()
                .id(commentId)
                .status(status)
                .reason(reason)
                .updateTime(LocalDateTime.now())
                .build());

        // Publish comment update event
        eventPublisher.publishEvent(new UpdateCommentEvent(this, commentId));

        return Response.success();
    }

}
