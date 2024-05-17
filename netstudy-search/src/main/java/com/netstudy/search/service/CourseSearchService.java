package com.netstudy.search.service;

import com.netstudy.base.model.PageParams;
import com.netstudy.search.dto.SearchCourseParamDto;
import com.netstudy.search.dto.SearchPageResultDto;
import com.netstudy.search.po.CourseIndex;

/**
 * @author Dico
 * @version 1.0
 * @description 课程搜索service
 * @date 2024/5/16 22:40
 */
public interface CourseSearchService {


    /**
     * @param pageParams           分页参数
     * @param searchCourseParamDto 搜索条件
     * @return com.netstudy.base.model.PageResult<com.netstudy.search.po.CourseIndex> 课程列表
     * @description 搜索课程列表
     * @author Dico
     * @date 2024/5/16 22:40
     */
    SearchPageResultDto<CourseIndex> queryCoursePubIndex(PageParams pageParams, SearchCourseParamDto searchCourseParamDto);

}
