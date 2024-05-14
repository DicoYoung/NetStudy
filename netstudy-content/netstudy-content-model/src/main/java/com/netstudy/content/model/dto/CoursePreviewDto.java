package com.netstudy.content.model.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author Dico
 * @version 1.0
 * @description 课程预览DTO
 * @date 2024/5/13 17:35
 **/
@Data
@ToString
public class CoursePreviewDto {
    //课程基本信息,课程营销信息
    CourseBaseInfoDto courseBase;

    //课程计划信息
    List<TeachplanDto> teachplans;
}
