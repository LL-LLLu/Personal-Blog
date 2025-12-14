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
@ApiModel(value = "Delete Comment Request VO")
public class DeleteCommentReqVO {

    @NotNull(message = "Comment ID cannot be empty")
    private Long id;
}
