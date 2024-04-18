package com.netstudy.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.netstudy.base.exception.NetStudyException;
import com.netstudy.content.mapper.CourseTeacherMapper;
import com.netstudy.content.model.po.CourseTeacher;
import com.netstudy.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Dico
 * @version 1.0
 * @description 课程老师service实现
 * @date 2024/4/17 17:32
 **/
@Slf4j
@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {
    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @Override
    public List<CourseTeacher> getCourseTeacherList(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        return courseTeacherMapper.selectList(queryWrapper);
    }

    @Transactional
    @Override
    public CourseTeacher saveCourseTeacher(CourseTeacher courseTeacher) {
        //根据有无ID判断是新增还是修改
        Long id = courseTeacher.getId();
        if (id == null) {
            //新增
            CourseTeacher newTeacher = new CourseTeacher();
            BeanUtils.copyProperties(courseTeacher, newTeacher);
            newTeacher.setCreateDate(LocalDateTime.now());
            int insert = courseTeacherMapper.insert(newTeacher);
            if (insert <= 0) NetStudyException.cast("教师添加失败");
            return getCourseTeacher(newTeacher);
        } else {
            //修改
            CourseTeacher newTeacher = new CourseTeacher();
            BeanUtils.copyProperties(courseTeacher, newTeacher);
            int insert = courseTeacherMapper.updateById(newTeacher);
            if (insert <= 0) NetStudyException.cast("修改教师失败");
            return getCourseTeacher(newTeacher);
        }
    }

    @Transactional
    @Override
    public void deleteCourseTeacher(Long courseId, Long teacherId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getId, teacherId)
                .eq(CourseTeacher::getCourseId, courseId);
        int delete = courseTeacherMapper.delete(queryWrapper);
        if (delete <= 0) NetStudyException.cast("删除失败");
    }

    private CourseTeacher getCourseTeacher(CourseTeacher courseTeacher) {
        return courseTeacherMapper.selectById(courseTeacher.getId());
    }
}
