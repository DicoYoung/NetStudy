package com.netstudy.content.service;

import com.netstudy.base.model.PageParams;
import com.netstudy.base.model.PageResult;
import com.netstudy.content.model.dto.AddCourseDto;
import com.netstudy.content.model.dto.CourseBaseInfoDto;
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

}
