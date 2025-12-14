package com.luqi.weblog.web.service.impl;

import com.luqi.weblog.common.domain.dos.BlogSettingsDO;
import com.luqi.weblog.common.domain.dos.CommentDO;
import com.luqi.weblog.common.domain.mapper.BlogSettingsMapper;
import com.luqi.weblog.common.domain.mapper.CommentMapper;
import com.luqi.weblog.common.enums.CommentStatusEnum;
import com.luqi.weblog.common.enums.ResponseCodeEnum;
import com.luqi.weblog.common.exception.BizException;
import com.luqi.weblog.common.utils.Response;
import com.luqi.weblog.web.convert.CommentConvert;
import com.luqi.weblog.web.model.vo.comment.FindCommentItemRspVO;
import com.luqi.weblog.web.model.vo.comment.FindCommentListReqVO;
import com.luqi.weblog.web.model.vo.comment.FindCommentListRspVO;
import com.luqi.weblog.web.model.vo.comment.PublishCommentReqVO;
import com.luqi.weblog.web.event.PublishCommentEvent;
import com.luqi.weblog.web.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import toolgood.words.IllegalWordsSearch;
import toolgood.words.IllegalWordsSearchResult;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @description: 评论服务实现类
 **/
@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    private BlogSettingsMapper blogSettingsMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private IllegalWordsSearch illegalWordsSearch;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * 发布评论
     *
     * @param publishCommentReqVO
     * @return
     */
    @Override
    public Response publishComment(PublishCommentReqVO publishCommentReqVO) {
        // 回复的评论 ID
        Long replyCommentId = publishCommentReqVO.getReplyCommentId();
        // 评论内容
        String content = publishCommentReqVO.getContent();
        // 昵称
        String nickname = publishCommentReqVO.getNickname();

        // 查询博客设置相关信息（约定的 ID 为 1）
        BlogSettingsDO blogSettingsDO = blogSettingsMapper.selectById(1L);
        // 是否开启了敏感词过滤
        boolean isCommentSensiWordOpen = blogSettingsDO.getIsCommentSensiWordOpen();
        // 是否开启了审核
        boolean isCommentExamineOpen = blogSettingsDO.getIsCommentExamineOpen();

        // 设置默认状态（正常）
        Integer status = CommentStatusEnum.NORMAL.getCode();
        // 审核不通过原因
        String reason = "";

        // 如果开启了审核, 设置状态为待审核，等待博主后台审核通过
        if (isCommentExamineOpen) {
            status = CommentStatusEnum.WAIT_EXAMINE.getCode();
        }

        // 评论内容是否包含敏感词
        boolean isContainSensitiveWord = false;
        // 是否开启了敏感词过滤
        if (isCommentSensiWordOpen) {
            isContainSensitiveWord = illegalWordsSearch.ContainsAny(content);
            if (isContainSensitiveWord) {
                // 设置状态为审核不通过
                status = CommentStatusEnum.EXAMINE_FAILED.getCode();
                // 查找所有匹配的敏感词
                List<IllegalWordsSearchResult> results = illegalWordsSearch.FindAll(content);
                List<String> keywords = results.stream()
                        .map(result -> result.Keyword)
                        .collect(Collectors.toList());
                reason = String.format("系统自动拦截，包含敏感词：%s", keywords);
                log.info("评论包含敏感词: {}, 昵称: {}", keywords, nickname);
            }
        }

        // 构建 DO 对象
        CommentDO commentDO = CommentDO.builder()
                .avatar(publishCommentReqVO.getAvatar())
                .content(content)
                .mail(publishCommentReqVO.getMail())
                .createTime(LocalDateTime.now())
                .nickname(nickname)
                .routerUrl(publishCommentReqVO.getRouterUrl())
                .website(publishCommentReqVO.getWebsite())
                .replyCommentId(replyCommentId)
                .parentCommentId(publishCommentReqVO.getParentCommentId())
                .status(status)
                .reason(reason)
                .build();

        // 新增评论
        commentMapper.insert(commentDO);

        // 获取评论 ID
        Long commentId = commentDO.getId();

        // 发送评论发布事件（异步发送邮件通知）
        eventPublisher.publishEvent(new PublishCommentEvent(this, commentId));

        // 根据评论状态返回不同提示
        if (isContainSensitiveWord) {
            // 包含敏感词，抛出异常提示用户
            throw new BizException(ResponseCodeEnum.COMMENT_CONTAIN_SENSITIVE_WORD);
        } else if (isCommentExamineOpen) {
            // 开启了审核，提示用户等待审核
            throw new BizException(ResponseCodeEnum.COMMENT_WAIT_EXAMINE);
        }

        return Response.success();
    }

    /**
     * 查询页面所有评论
     *
     * @param findCommentListReqVO
     * @return
     */
    @Override
    public Response findCommentList(FindCommentListReqVO findCommentListReqVO) {
        // 路由地址
        String routerUrl = findCommentListReqVO.getRouterUrl();

        // 查询该路由地址下所有评论（仅查询状态正常的）
        List<CommentDO> commentDOS = commentMapper.selectByRouterUrlAndStatus(routerUrl, CommentStatusEnum.NORMAL.getCode());
        // 总评论数
        Integer total = commentDOS.size();

        List<FindCommentItemRspVO> vos = null;
        // DO 转 VO
        if (!CollectionUtils.isEmpty(commentDOS)) {
            // 一级评论（parentCommentId 父级 ID 为空，则表示为一级评论）
            vos = commentDOS.stream()
                    .filter(commentDO -> Objects.isNull(commentDO.getParentCommentId()))
                    .map(commentDO -> CommentConvert.INSTANCE.convertDO2VO(commentDO))
                    .collect(Collectors.toList());

            // 循环设置评论回复数据
            vos.forEach(vo -> {
                Long commentId = vo.getId();
                List<FindCommentItemRspVO> childComments = commentDOS.stream()
                        .filter(commentDO -> Objects.equals(commentDO.getParentCommentId(), commentId)) // 过滤出一级评论下所有子评论
                        .sorted(Comparator.comparing(CommentDO::getCreateTime)) // 按发布时间升序排列
                        .map(commentDO -> {
                            FindCommentItemRspVO findCommentItemRspVO = CommentConvert.INSTANCE.convertDO2VO(commentDO);
                            Long replyCommentId = commentDO.getReplyCommentId();
                            // 若二级评论的 replyCommentId 不等于一级评论 ID, 前端则需要展示【回复 @ xxx】，需要设置回复昵称
                            if (!Objects.equals(replyCommentId, commentId)) {
                                // 设置回复用户的昵称
                                Optional<CommentDO> optionalCommentDO = commentDOS.stream()
                                        .filter(commentDO1 -> Objects.equals(commentDO1.getId(), replyCommentId)).findFirst();
                                if (optionalCommentDO.isPresent()) {
                                    findCommentItemRspVO.setReplyNickname(optionalCommentDO.get().getNickname());
                                }
                            }
                            return findCommentItemRspVO;
                        }).collect(Collectors.toList());

                vo.setChildComments(childComments);
            });
        }

        return Response.success(FindCommentListRspVO.builder()
                .total(total)
                .comments(vos)
                .build());
    }
}
