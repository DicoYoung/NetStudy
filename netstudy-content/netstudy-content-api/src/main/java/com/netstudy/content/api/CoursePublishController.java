package com.netstudy.content.api;

import com.alibaba.fastjson.JSON;
import com.netstudy.content.model.dto.CourseBaseInfoDto;
import com.netstudy.content.model.dto.CoursePreviewDto;
import com.netstudy.content.model.dto.TeachplanDto;
import com.netstudy.content.model.po.CoursePublish;
import com.netstudy.content.service.CoursePublishService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author Dico
 * @version 1.0
 * @description 课程发布控制器
 * @date 2024/5/10 17:04
 **/
@Controller
public class CoursePublishController {

    @Autowired
    CoursePublishService coursePublishService;

    @ApiOperation("获取课程预览")
    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId") Long courseId) {

        //获取课程预览信息
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);

        ModelAndView modelAndView = new ModelAndView();
        //指定模型
        modelAndView.addObject("model", coursePreviewInfo);
        //指定模板
        modelAndView.setViewName("course_template");
        return modelAndView;
    }

    @ApiOperation("提交课程审核")
    @ResponseBody
    @PostMapping("/courseaudit/commit/{courseId}")
    public void commitAudit(@PathVariable("courseId") Long courseId) {
        Long companyId = 1232141425L;
        coursePublishService.commitAudit(companyId, courseId);
    }

    @ApiOperation("课程发布")
    @ResponseBody
    @PostMapping("/coursepublish/{courseId}")
    public void coursepublish(@PathVariable("courseId") Long courseId) {
        Long companyId = 1232141425L;
        coursePublishService.publish(companyId, courseId);
    }

    @ApiOperation("查询课程发布信息")
    @ResponseBody
    @GetMapping("/r/coursepublish/{courseId}")
    public CoursePublish getCoursepublish(@PathVariable("courseId") Long courseId) {
        //查询课程发布信息
        return coursePublishService.getCoursePublish(courseId);
    }

    @ApiOperation("获取课程发布信息")
    @ResponseBody
    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDto getCoursePublish(@PathVariable("courseId") Long courseId) {
        //封装数据
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();

        //查询课程发布表
//        CoursePublish coursePublish = coursePublishService.getCoursePublish(courseId);
        //先从缓存查询，缓存中有直接返回，没有再查询数据库
        CoursePublish coursePublish = coursePublishService.getCoursePublishCache(courseId);
        if (coursePublish == null) {
            return coursePreviewDto;
        }
        //开始向coursePreviewDto填充数据
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(coursePublish, courseBaseInfoDto);
        //课程计划信息
        String teachplanJson = coursePublish.getTeachplan();
        //转成List<TeachplanDto>
        List<TeachplanDto> teachplanDtos = JSON.parseArray(teachplanJson, TeachplanDto.class);
        coursePreviewDto.setCourseBase(courseBaseInfoDto);
        coursePreviewDto.setTeachplans(teachplanDtos);
        return coursePreviewDto;
    }

}
