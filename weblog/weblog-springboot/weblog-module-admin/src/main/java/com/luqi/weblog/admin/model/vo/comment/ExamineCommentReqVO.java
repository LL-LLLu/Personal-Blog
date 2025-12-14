package com.luqi.weblog.admin.model.vo.comment;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "Examine Comment Request VO")
public class ExamineCommentReqVO {

    @NotNull(message = "Comment ID cannot be empty")
    private Long id;

    @NotNull(message = "Comment status cannot be empty")
    private Integer status;

    /**
     * Reason for rejection
     */
    private String reason;
}
