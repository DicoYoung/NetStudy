package com.netstudy.learning.service.impl;

import com.alibaba.fastjson.JSON;
import com.netstudy.base.model.RestResponse;
import com.netstudy.base.utils.StringUtil;
import com.netstudy.content.model.po.CoursePublish;
import com.netstudy.content.model.po.Teachplan;
import com.netstudy.learning.feignclient.ContentServiceClient;
import com.netstudy.learning.feignclient.MediaServiceClient;
import com.netstudy.learning.model.dto.XcCourseTablesDto;
import com.netstudy.learning.service.LearningService;
import com.netstudy.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LearningServiceImpl implements LearningService {

    @Autowired
    MyCourseTablesService courseTablesService;

    @Autowired
    ContentServiceClient contentServiceClient;

    @Autowired
    MediaServiceClient mediaServiceClient;

    @Override
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId) {
        // 1. 远程调用内容管理服务，查询课程信息
        CoursePublish coursePublish = contentServiceClient.getCoursePublish(courseId);
        if (coursePublish == null) {
            return RestResponse.validfail("课程信息不存在");
        }
        // 2. 判断试学规则，远程调用内容管理服务，查询教学计划teachplan
        Teachplan teachplan = contentServiceClient.getTeachplan(teachplanId);
        // 2.1 isPreview字段为1表示支持试学，返回课程url
        if ("1".equals(teachplan.getIsPreview())) {
            return mediaServiceClient.getPlayUrlByMediaId(mediaId);
        }
        // 3. 非试学,登录状态
        if (StringUtil.isNotEmpty(userId)) {
            // 3.1 判断是否选课
            XcCourseTablesDto learningStatus = courseTablesService.getLearningStatus(userId, courseId);
            String learnStatus = learningStatus.getLearnStatus();
            if ("702002".equals(learnStatus)) {
                RestResponse.validfail("没有选课或选课后没有支付");
            } else if ("702003".equals(learnStatus)) {
                RestResponse.validfail("已过期需要申请续期或重新支付");
            } else {
                return mediaServiceClient.getPlayUrlByMediaId(mediaId);
            }
        }
        // 4. 非试学，未登录状态
        String charge = coursePublish.getCharge();
        // 4.1 免费课程，返回课程url
        if ("201000".equals(charge)) {
            return mediaServiceClient.getPlayUrlByMediaId(mediaId);
        }
        return RestResponse.validfail("请购买课程后学习");
    }
}
