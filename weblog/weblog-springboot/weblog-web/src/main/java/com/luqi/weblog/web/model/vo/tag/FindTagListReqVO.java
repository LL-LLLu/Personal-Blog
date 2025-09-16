package com.luqi.weblog.web.model.vo.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: luqi
 * @description: Request VO for finding tag list
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindTagListReqVO {

    /**
     * Number of tags to display
     */
    private Long size;

}