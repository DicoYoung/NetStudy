package com.netstudy.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.netstudy.content.model.dto.CourseCategoryTreeDto;
import com.netstudy.content.model.po.CourseCategory;

import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

    //使用递归查询分类结果
    public List<CourseCategoryTreeDto> selectTreeNodes(String id);

}
