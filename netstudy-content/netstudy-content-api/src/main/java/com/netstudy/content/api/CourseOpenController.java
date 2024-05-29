package com.netstudy.content.api;

import com.netstudy.content.model.dto.CoursePreviewDto;
import com.netstudy.content.service.CourseBaseInfoService;
import com.netstudy.content.service.CoursePublishService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Dico
 * @version 1.0
 * @description 公共开放接口
 * @date 2024/5/13 22:25
 **/
@Api(value = "课程公开查询接口", tags = "课程公开查询接口")
@Slf4j
@RestController
@RequestMapping("/open")
public class CourseOpenController {
    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @Autowired
    private CoursePublishService coursePublishService;

    //根据课程ID查询信息
    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDto getPreviewInfo(@PathVariable("courseId") Long courseId) {
        //获取课程预览信息
        return coursePublishService.getCoursePreviewInfo(courseId);
    }
}
