package com.luqi.weblog.admin.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @description: Comment Update Event
 **/
@Getter
public class UpdateCommentEvent extends ApplicationEvent {

    /**
     * Comment ID
     */
    private Long commentId;

    public UpdateCommentEvent(Object source, Long commentId) {
        super(source);
        this.commentId = commentId;
    }
}
