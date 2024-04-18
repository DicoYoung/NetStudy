package com.netstudy.content.service;

import com.netstudy.content.model.po.CourseTeacher;

import java.util.List;

/**
 * @author Dico
 * @version 1.0
 * @description 课程老师service
 * @date 2024/4/17 16:28
 **/
public interface CourseTeacherService {
    /**
     * 根据课程ID返回课程老师信息
     *
     * @param courseId 课程ID
     * @return 返回老师信息列表
     */
    List<CourseTeacher> getCourseTeacherList(Long courseId);

    /**
     * 新增和修改老师信息
     *
     * @param courseTeacher 课程老师包装类
     * @return 新增和修改后的老师信息
     */
    CourseTeacher saveCourseTeacher(CourseTeacher courseTeacher);

    /**
     * 删除课程老师
     *
     * @param courseId  课程ID
     * @param teacherId 老师ID
     */
    void deleteCourseTeacher(Long courseId, Long teacherId);
}
