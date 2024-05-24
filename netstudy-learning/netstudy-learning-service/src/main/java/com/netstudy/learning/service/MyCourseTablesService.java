package com.netstudy.learning.service;

import com.netstudy.base.model.PageResult;
import com.netstudy.content.model.po.CoursePublish;
import com.netstudy.learning.model.dto.MyCourseTableParams;
import com.netstudy.learning.model.dto.XcChooseCourseDto;
import com.netstudy.learning.model.dto.XcCourseTablesDto;
import com.netstudy.learning.model.po.XcChooseCourse;
import com.netstudy.learning.model.po.XcCourseTables;

/**
 * @author Dico
 * @version 1.0
 * @description 课程表service
 * @date 2024/5/24 15:54
 **/
public interface MyCourseTablesService {
    /**
     * 添加选课
     *
     * @param userId   用户id
     * @param courseId 课程id
     */
    XcChooseCourseDto addChooseCourse(String userId, Long courseId);

    /**
     * 获取学习资格
     *
     * @param userId   用户id
     * @param courseId 课程id
     * @return 学习资格状态
     */
    XcCourseTablesDto getLearningStatus(String userId, Long courseId);

    XcChooseCourse addFreeCourse(String userId, CoursePublish coursePublish);

    XcChooseCourse addChargeCourse(String userId, CoursePublish coursePublish);

    boolean saveChooseCourseStatus(String chooseCourseId);

    PageResult<XcCourseTables> myCourseTables(MyCourseTableParams params);
}
