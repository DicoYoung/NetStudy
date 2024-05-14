package com.netstudy.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.netstudy.base.exception.NetStudyException;
import com.netstudy.content.mapper.CourseBaseMapper;
import com.netstudy.content.mapper.CourseMarketMapper;
import com.netstudy.content.mapper.CoursePublishMapper;
import com.netstudy.content.mapper.CoursePublishPreMapper;
import com.netstudy.content.model.dto.CourseBaseInfoDto;
import com.netstudy.content.model.dto.CoursePreviewDto;
import com.netstudy.content.model.dto.TeachplanDto;
import com.netstudy.content.model.po.CourseBase;
import com.netstudy.content.model.po.CourseMarket;
import com.netstudy.content.model.po.CoursePublish;
import com.netstudy.content.model.po.CoursePublishPre;
import com.netstudy.content.service.CourseBaseInfoService;
import com.netstudy.content.service.CoursePublishService;
import com.netstudy.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Dico
 * @version 1.0
 * @description 课程发布实现
 * @date 2024/5/13 17:40
 **/
@Slf4j
@Service
public class CoursePublishServiceImpl implements CoursePublishService {

    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @Autowired
    TeachplanService teachplanService;

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;

    @Autowired
    CoursePublishMapper coursePublishMapper;

//    @Autowired
//    MqMessageService mqMessageService;
//
//    @Autowired
//    MediaServiceClient mediaServiceClient;
//
//    @Autowired
//    RedisTemplate redisTemplate;
//
//    @Autowired
//    RedissonClient redissonClient;


    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        //课程基本信息、营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        //课程计划信息
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);

        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(teachplanTree);
        return coursePreviewDto;
    }

    @Transactional
    @Override
    public void commitAudit(Long companyId, Long courseId) {
        // 查询课程基本信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        // 查询课程营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        // 查询课程基本信息、课程营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        // 查询课程计划
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);

        // 1. 约束
        String auditStatus = courseBaseInfo.getAuditStatus();
        // 1.1 审核完后，方可提交审核
        if ("202003".equals(auditStatus)) {
            NetStudyException.cast("该课程现在属于待审核状态，审核完成后可再次提交");
        }
        // 1.2 本机构只允许提交本机构的课程
        if (!companyId.equals(courseBaseInfo.getCompanyId())) {
            NetStudyException.cast("本机构只允许提交本机构的课程");
        }
        // 1.3 没有上传图片，不允许提交
        if (StringUtils.isEmpty(courseBaseInfo.getPic())) {
            NetStudyException.cast("没有上传课程封面，不允许提交审核");
        }
        // 1.4 没有添加课程计划，不允许提交审核
        if (teachplanTree.isEmpty()) {
            NetStudyException.cast("没有添加课程计划，不允许提交审核");
        }
        // 2. 准备封装返回对象
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        BeanUtils.copyProperties(courseBaseInfo, coursePublishPre);
        coursePublishPre.setMarket(JSON.toJSONString(courseMarket));
        coursePublishPre.setTeachplan(JSON.toJSONString(teachplanTree));
        coursePublishPre.setCompanyId(companyId);
        coursePublishPre.setCreateDate(LocalDateTime.now());
        // 3. 设置预发布记录状态为已提交
        coursePublishPre.setStatus("202003");
        // 判断是否已经存在预发布记录，若存在，则更新
        CoursePublishPre coursePublishPreUpdate = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPreUpdate == null) {
            coursePublishPreMapper.insert(coursePublishPre);
        } else {
            coursePublishPreMapper.updateById(coursePublishPre);
        }
        // 4. 设置课程基本信息审核状态为已提交
        courseBase.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBase);
    }

    @Override
    public void publish(Long companyId, Long courseId) {

    }

    @Override
    public CoursePublish getCoursePublish(Long courseId) {
        return null;
    }

    @Override
    public CoursePublish getCoursePublishCache(Long courseId) {
        return null;
    }
}
