package com.netstudy.content.model.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @author Dico
 * @version 1.0
 * @description 课程查询条件模型类
 * @date 2024/4/3 15:11
 **/
@Data
@ToString
public class QueryCourseParamsDto {
    //审核状态
    private String auditStatus;
    //课程名称
    private String courseName;
    //发布状态
    private String publishStatus;
}
