package com.netstudy.content.service;

import com.netstudy.content.model.dto.SavaTeachplanDto;
import com.netstudy.content.model.dto.TeachplanDto;

import java.util.List;

/**
 * @author Dico
 * @version 1.0
 * @description 课程计划service
 * @date 2024/4/16 15:03
 **/
public interface TeachplanService {
    /**
     * 根据课程id查询课程计划
     *
     * @param courseId 课程id
     * @return 课程计划信息
     */
    List<TeachplanDto> findTeachplanTree(Long courseId);

    /**
     * 新增、修改课程计划
     *
     * @param savaTeachplanDto 信息表单模型
     */
    void saveTeachplan(SavaTeachplanDto savaTeachplanDto);

    /**
     * 删除课程计划
     *
     * @param teachplanId 课程计划id
     */
    void deletTeachplan(Long teachplanId);
}
