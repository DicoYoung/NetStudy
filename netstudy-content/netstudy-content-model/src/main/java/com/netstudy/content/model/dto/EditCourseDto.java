package com.netstudy.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Dico
 * @version 1.0
 * @description 修改课程DTO类
 * @date 2024/4/15 14:52
 **/
@Data
public class EditCourseDto {
    @ApiModelProperty(value = "课程id", required = true)
    private Long id;
}
