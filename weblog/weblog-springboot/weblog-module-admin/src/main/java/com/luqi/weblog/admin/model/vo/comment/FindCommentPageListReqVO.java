package com.luqi.weblog.admin.model.vo.comment;

import com.luqi.weblog.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "Query Comment Page List Request VO")
public class FindCommentPageListReqVO extends BasePageQuery {

    /**
     * Router URL
     */
    private String routerUrl;

    /**
     * Start date for filtering
     */
    private LocalDate startDate;

    /**
     * End date for filtering
     */
    private LocalDate endDate;

    /**
     * Comment status
     */
    private Integer status;
}
