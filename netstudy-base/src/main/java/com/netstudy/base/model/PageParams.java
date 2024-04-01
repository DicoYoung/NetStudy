package com.netstudy.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author Dico
 * @version 1.0
 * @description 分页查询
 * @date 2024/4/3
 */
@Data
@ToString
public class PageParams {
    @ApiModelProperty(value = "页码")
    private Long pageNo = 1L;
    @ApiModelProperty(value = "每页大小")
    private Long pageSize = 30L;

    public PageParams(Long pageNo, Long pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public PageParams() {
    }

}
