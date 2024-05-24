package com.netstudy.learning.api;

import com.netstudy.base.exception.NetStudyException;
import com.netstudy.base.model.PageResult;
import com.netstudy.learning.model.dto.MyCourseTableParams;
import com.netstudy.learning.model.dto.XcChooseCourseDto;
import com.netstudy.learning.model.dto.XcCourseTablesDto;
import com.netstudy.learning.model.po.XcCourseTables;
import com.netstudy.learning.service.MyCourseTablesService;
import com.netstudy.learning.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Dico
 * @version 1.0
 * @description 我的课程表接口
 * @date 2024/5/24 9:40
 */

@Api(value = "我的课程表接口", tags = "我的课程表接口")
@Slf4j
@RestController
public class MyCourseTablesController {

    @Autowired
    MyCourseTablesService myCourseTablesService;


    @ApiOperation("添加选课")
    @PostMapping("/choosecourse/{courseId}")
    public XcChooseCourseDto addChooseCourse(@PathVariable("courseId") Long courseId) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if (user == null) {
            NetStudyException.cast("请登录后继续选课");
        }
        String userId = user.getId();
        return myCourseTablesService.addChooseCourse(userId, courseId);
    }

    @ApiOperation("查询学习资格")
    @PostMapping("/choosecourse/learnstatus/{courseId}")
    public XcCourseTablesDto getLearnstatus(@PathVariable("courseId") Long courseId) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if (user == null) {
            NetStudyException.cast("请登录后继续选课");
        }
        String userId = user.getId();
        return myCourseTablesService.getLearningStatus(userId, courseId);
    }

    @ApiOperation("我的课程表")
    @GetMapping("/mycoursetable")
    public PageResult<XcCourseTables> mycoursetable(MyCourseTableParams params) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if (user == null) {
            NetStudyException.cast("请登录后查看课程表");
        }
        String userId = user.getId();
        params.setUserId(userId);
        return myCourseTablesService.myCourseTables(params);
    }

}
