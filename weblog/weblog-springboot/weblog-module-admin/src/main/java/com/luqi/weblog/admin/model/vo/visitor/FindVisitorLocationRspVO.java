package com.luqi.weblog.admin.model.vo.visitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Visitor location statistics response VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindVisitorLocationRspVO {

    private Long id;

    private String country;

    private String province;

    private String city;

    private Long visitCount;

    /**
     * Full location display name (e.g., "China - Beijing - Beijing")
     */
    private String locationName;
}
