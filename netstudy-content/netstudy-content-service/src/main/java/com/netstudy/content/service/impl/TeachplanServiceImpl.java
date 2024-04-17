package com.netstudy.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.netstudy.base.exception.NetStudyException;
import com.netstudy.content.mapper.TeachplanMapper;
import com.netstudy.content.mapper.TeachplanMediaMapper;
import com.netstudy.content.model.dto.SavaTeachplanDto;
import com.netstudy.content.model.dto.TeachplanDto;
import com.netstudy.content.model.po.Teachplan;
import com.netstudy.content.model.po.TeachplanMedia;
import com.netstudy.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

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

    @Transactional
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
            int insert = teachplanMapper.insert(teachplan);
            if (insert <= 0) NetStudyException.cast("添加失败");
        } else {
            //修改
            Teachplan teachplan = teachplanMapper.selectById(teachplanId);
            BeanUtils.copyProperties(savaTeachplanDto, teachplan);
            int update = teachplanMapper.updateById(teachplan);
            if (update <= 0) NetStudyException.cast("修改失败");
        }
    }

    @Transactional
    @Override
    public void deletTeachplan(Long teachplanId) {
        //先根据id获取计划信息
        if (teachplanId == null) {
            NetStudyException.cast("课程计划ID为空");
        }
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        Integer grade = teachplan.getGrade();
        //判断是否有是I级别结构：章
        if (grade.equals(1)) {
            //是，判断是否有小节，有就抛出异常
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            //在plan表里查找父节点ID是本ID的节点，有则报错
            queryWrapper.eq(Teachplan::getParentid, teachplanId);
            Integer count = teachplanMapper.selectCount(queryWrapper);
            if (count > 0) {
                NetStudyException.cast("课程计划信息还有子级信息，无法操作");
            } else {
                //无，删除
                teachplanMapper.deleteById(teachplanId);
            }
        } else {
            //删除第二级时还需要删除关联的视频信息
            teachplanMapper.deleteById(teachplanId);
            //删除关联的媒体资源信息
            LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
            //找到媒体资源表中对应计划ID的条目
            queryWrapper.eq(TeachplanMedia::getTeachplanId, teachplanId);
            teachplanMediaMapper.delete(queryWrapper);
        }
    }

    @Transactional
    @Override
    public void orderByTeachplan(String moveType, Long teachplanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        //章节移动需要比较同一课程下的order
        Integer grade = teachplan.getGrade();
        //小节移动需要比较同一章节下的order
        Integer orderby = teachplan.getOrderby();

        Long courseId = teachplan.getCourseId();
        Long parentid = teachplan.getParentid();
        if (moveType.equals("moveup")) {
            if (grade == 1) {
                //章节上移
                //找到上一个章节的orderby，交换
                //根据课程ID=1和grade=1找到所有同级章节
                //然后找到order小于本章节的所有章节
                //并将其从大到小排序，并取第一个limit 1
                //即可得到比当前章节顺序刚好小1的章节
                // SELECT * FROM teachplan WHERE courseId = 117 AND grade = 1  AND orderby < 当前 ORDER BY orderby DESC limit 1
                LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Teachplan::getGrade, 1)
                        .eq(Teachplan::getCourseId, courseId)
                        .lt(Teachplan::getOrderby, orderby)
                        .orderByDesc(Teachplan::getOrderby)
                        .last("limit 1");
                Teachplan tmp = teachplanMapper.selectOne(queryWrapper);
                exchangeOrderby(teachplan, tmp);
            } else if (grade == 2) {
                //小节上移
                //找到上一个小节的order
                //小节则需要根据章节ID(parentId)寻找同级的所有小节
                //不再需要额外找grade=2
                // SELECT * FROM teachplan WHERE parentId = 268 AND orderby < 当前 ORDER BY orderby DESC LIMIT 1
                LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Teachplan::getParentid, parentid)
                        .lt(Teachplan::getOrderby, orderby)
                        .orderByDesc(Teachplan::getOrderby)
                        .last("LIMIT 1");
                Teachplan tmp = teachplanMapper.selectOne(queryWrapper);
                exchangeOrderby(teachplan, tmp);
            }
        } else if (moveType.equals("movedown")) {
            if (grade == 1) {
                //章节下移
                //跟上移类似，只需要找出大于当前章节order的所有章节
                //再将其从小打大排列，取第一个交换即可
                // SELECT * FROM teachplan WHERE courseId = 117 AND grade = 1 AND orderby > 1 ORDER BY orderby ASC LIMIT 1
                LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Teachplan::getCourseId, courseId)
                        .eq(Teachplan::getGrade, grade)
                        .gt(Teachplan::getOrderby, orderby)
                        .orderByAsc(Teachplan::getOrderby)
                        .last("LIMIT 1");
                Teachplan tmp = teachplanMapper.selectOne(queryWrapper);
                exchangeOrderby(teachplan, tmp);
            } else if (grade == 2) {
                //小节下移
                // SELECT * FROM teachplan WHERE parentId = 268 AND orderby > 1 ORDER BY orderby ASC LIMIT 1
                LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Teachplan::getParentid, parentid)
                        .gt(Teachplan::getOrderby, orderby)
                        .orderByAsc(Teachplan::getOrderby)
                        .last("LIMIT 1");
                Teachplan tmp = teachplanMapper.selectOne(queryWrapper);
                exchangeOrderby(teachplan, tmp);
            }
        }
    }

    private void exchangeOrderby(Teachplan teachplan, Teachplan tmp) {
        if (tmp == null) {
            NetStudyException.cast("已经到头了，不可移动");
        } else {
            //交换order
            Integer orderby = teachplan.getOrderby();
            Integer tmpOrderby = tmp.getOrderby();
            teachplan.setOrderby(tmpOrderby);
            tmp.setOrderby(orderby);
            teachplanMapper.updateById(teachplan);
            teachplanMapper.updateById(tmp);
        }
    }


}
