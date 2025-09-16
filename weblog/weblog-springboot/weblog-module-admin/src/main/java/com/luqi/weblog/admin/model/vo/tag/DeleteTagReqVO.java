package com.luqi.weblog.admin.model.vo.tag;

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
@ApiModel(value = "Delete Tag VO")
public class DeleteTagReqVO {

    @NotNull(message = "Tag ID can not be null")
    private Long id;

}
