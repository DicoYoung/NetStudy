package com.netstudy.content.service;

import com.netstudy.base.model.PageParams;
import com.netstudy.base.model.PageResult;
import com.netstudy.content.model.dto.AddCourseDto;
import com.netstudy.content.model.dto.CourseBaseInfoDto;
import com.netstudy.content.model.dto.EditCourseDto;
import com.netstudy.content.model.dto.QueryCourseParamsDto;
import com.netstudy.content.model.po.CourseBase;

/**
 * @author Dico
 * @version 1.0
 * @description 课程信息管理接口
 * @date 2024/4/8 14:39
 **/
public interface CourseBaseInfoService {

    /**
     * @param pageParams      分页参数查询
     * @param courseParamsDto 查询条件
     * @return 查询结果
     */
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto courseParamsDto);

    /**
     * @param companyId    机构ID
     * @param addCourseDto 新增课程条件
     * @return 新增课程结果
     */
    CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    /**
     * 根据课程id查询课程信息
     *
     * @param courseId 课程id
     * @return 课程详细信息
     */
    CourseBaseInfoDto getCourseBaseInfo(Long courseId);

    /**
     * 修改课程，基于机构ID
     *
     * @param companyId     机构id
     * @param editCourseDto 修改课程条件
     * @return 修改后课程信息
     */
    CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto);

    /**
     * 删除课程
     *
     * @param courseId 课程ID
     */
    void deleteCourseBase(Long companyId, Long courseId);

}
