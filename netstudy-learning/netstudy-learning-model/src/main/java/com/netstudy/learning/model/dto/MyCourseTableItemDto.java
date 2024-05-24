package com.netstudy.learning.model.dto;

import com.netstudy.learning.model.po.XcCourseTables;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author Dico
 * @version 1.0
 * @description 我的课程查询条件
 * @date 2024/5/24 9:42
 */
@Data
@ToString
public class MyCourseTableItemDto extends XcCourseTables {

    /**
     * 最近学习时间
     */
    private LocalDateTime learnDate;

    /**
     * 学习时长
     */
    private Long learnLength;

    /**
     * 章节id
     */
    private Long teachplanId;

    /**
     * 章节名称
     */
    private String teachplanName;


}
