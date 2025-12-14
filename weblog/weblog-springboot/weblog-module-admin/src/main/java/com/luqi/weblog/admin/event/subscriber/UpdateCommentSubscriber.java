package com.luqi.weblog.admin.event.subscriber;

import com.luqi.weblog.admin.event.UpdateCommentEvent;
import com.luqi.weblog.common.domain.dos.BlogSettingsDO;
import com.luqi.weblog.common.domain.dos.CommentDO;
import com.luqi.weblog.common.domain.mapper.BlogSettingsMapper;
import com.luqi.weblog.common.domain.mapper.CommentMapper;
import com.luqi.weblog.common.enums.CommentStatusEnum;
import com.luqi.weblog.common.mail.MailHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @description: Comment Update Event Subscriber
 **/
@Component
@Slf4j
public class UpdateCommentSubscriber implements ApplicationListener<UpdateCommentEvent> {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private BlogSettingsMapper blogSettingsMapper;
    @Autowired
    private MailHelper mailHelper;

    @Override
    @Async("threadPoolTaskExecutor")
    public void onApplicationEvent(UpdateCommentEvent event) {
        // Get comment ID from event
        Long commentId = event.getCommentId();

        // Get current thread name
        String threadName = Thread.currentThread().getName();

        log.info("==> threadName: {}", threadName);
        log.info("==> Comment update event consumed successfully, commentId: {}", commentId);

        CommentDO commentDO = commentMapper.selectById(commentId);
        Long replyCommentId = commentDO.getReplyCommentId();
        String nickname = commentDO.getNickname();
        String content = commentDO.getContent();
        Integer status = commentDO.getStatus();
        String mail = commentDO.getMail();
        String routerUrl = commentDO.getRouterUrl();

        BlogSettingsDO blogSettingsDO = blogSettingsMapper.selectById(1L);
        String blogName = blogSettingsDO.getName();
        // Blog domain
        String domain = "http://116.62.199.48/#";

        // Check comment status
        // If rejected, notify the commenter about the rejection reason
        if (Objects.equals(status, CommentStatusEnum.EXAMINE_FAILED.getCode())
                && StringUtils.isNotBlank(mail)) {

            String reason = commentDO.getReason();
            String title = String.format("Your comment on %s was not approved", blogName);

            String html = String.format("<html><body>" +
                            "<h2>Your comment:</h2><p>%s</p>" +
                            "<h2>Rejection reason:</h2><p>%s</p>" +
                            "<p><a href='%s%s' target='_blank'>View details</a></p>" +
                            "</body></html>",
                    content, reason, domain, routerUrl);
            mailHelper.sendHtml(mail, title, html);
        } else if (Objects.equals(status, CommentStatusEnum.NORMAL.getCode())) {
            // If approved, notify the commenter that their comment has been approved
            String title = String.format("Your comment on %s has been approved", blogName);
            String html = String.format("<html><body>" +
                            "<h2>Your comment:</h2><p>%s</p>" +
                            "<p><a href='%s%s' target='_blank'>View details</a></p>" +
                            "</body></html>",
                    content, domain, routerUrl);
            mailHelper.sendHtml(mail, title, html);

            // Also notify the user being replied to
            notifyRepliedUser(replyCommentId, blogName, nickname, content, domain);
        }
    }

    /**
     * Notify the user being replied to via email
     * @param replyCommentId
     * @param blogName
     * @param nickname
     * @param content
     * @param domain
     */
    private void notifyRepliedUser(Long replyCommentId, String blogName, String nickname, String content, String domain) {
        if (Objects.isNull(replyCommentId))
            return;

        // Get the replied comment
        CommentDO replyCommentDO = commentMapper.selectById(replyCommentId);

        // Email address
        String to = replyCommentDO.getMail();

        // Check if email is empty
        if (StringUtils.isBlank(to))
            return;

        String routerUrl = replyCommentDO.getRouterUrl();
        String title = String.format("Your comment on %s has received a reply", blogName);

        String html = String.format("<html><body>" +
                        "<h2>Your comment:</h2><p>%s</p>" +
                        "<h2>%s replied:</h2><p>%s</p>" +
                        "<p><a href='%s%s' target='_blank'>View details</a></p>" +
                        "</body></html>",
                replyCommentDO.getContent(), nickname, content, domain, routerUrl);
        mailHelper.sendHtml(to, title, html);
    }
}
