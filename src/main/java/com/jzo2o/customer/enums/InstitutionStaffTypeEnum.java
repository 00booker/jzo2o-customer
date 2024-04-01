package com.jzo2o.customer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @author: zjw16
 * @time: 2024/4/1 16:41
 */
@Getter
@AllArgsConstructor
public enum InstitutionStaffTypeEnum {
    STAFF_TYPE_ENUM(2, "服务人员"),
    INSTITUTION_STAFF_TYPE_ENUM(3, "服务机构");
    /**
     * 状态值
     */
    private final int status;

    /**
     * 描述
     */
    private final String description;
}
