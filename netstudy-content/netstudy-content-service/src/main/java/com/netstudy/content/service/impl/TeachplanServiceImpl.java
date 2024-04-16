package com.netstudy.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.netstudy.content.mapper.TeachplanMapper;
import com.netstudy.content.model.dto.SavaTeachplanDto;
import com.netstudy.content.model.dto.TeachplanDto;
import com.netstudy.content.model.po.Teachplan;
import com.netstudy.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Dico
 * @version 1.0
 * @description 课程计划方法实现
 * @date 2024/4/16 15:06
 **/
@Service
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    TeachplanMapper teachplanMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    private int getTeachplanCount(Long courseId, Long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper = queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count + 1;
    }

    @Override
    public void saveTeachplan(SavaTeachplanDto savaTeachplanDto) {
        //通过课程计划id判断有无主键，是新增还是修改
        Long teachplanId = savaTeachplanDto.getId();
        if (teachplanId == null) {
            //新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(savaTeachplanDto, teachplan);
            //确定排序字段
            Long courseId = savaTeachplanDto.getCourseId();
            Long parentId = savaTeachplanDto.getParentid();
            teachplan.setOrderby(getTeachplanCount(courseId, parentId));
            //插入
            teachplanMapper.insert(teachplan);
        } else {
            //修改
            Teachplan teachplan = teachplanMapper.selectById(teachplanId);
            BeanUtils.copyProperties(savaTeachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }

    @Override
    public void deletTeachplan(Long teachplanId) {
        //有子级信息不能删除
        //先根据id获取计划信息
        //根据id，作为parentID，寻找有没有结果
        //有结果，表示此ID是别人的父节点，即此节点有子节点，不可删除
        //无结果，则表示此ID不是任何记录的父节点，可以删除
        Teachplan sonTeachplan = teachplanMapper.selectById(teachplanId);
        //判断是否有子集

        //有，返回错误信息
        //无，删除
        //删除第二级时还需要删除关联的视频信息
        teachplanMapper.deleteById(teachplanId);
    }
}
