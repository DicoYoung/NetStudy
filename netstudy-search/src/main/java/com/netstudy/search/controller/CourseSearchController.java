package com.netstudy.search.controller;

import com.netstudy.base.model.PageParams;
import com.netstudy.search.dto.SearchCourseParamDto;
import com.netstudy.search.dto.SearchPageResultDto;
import com.netstudy.search.po.CourseIndex;
import com.netstudy.search.service.CourseSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Dico
 * @version 1.0
 * @description 课程搜索接口
 * @date 2025/16 22:31
 */
@Api(value = "课程搜索接口", tags = "课程搜索接口")
@RestController
@RequestMapping("/course")
public class CourseSearchController {

    @Autowired
    CourseSearchService courseSearchService;


    @ApiOperation("课程搜索列表")
    @GetMapping("/list")
    public SearchPageResultDto<CourseIndex> list(PageParams pageParams, SearchCourseParamDto searchCourseParamDto) {

        return courseSearchService.queryCoursePubIndex(pageParams, searchCourseParamDto);

    }
}
