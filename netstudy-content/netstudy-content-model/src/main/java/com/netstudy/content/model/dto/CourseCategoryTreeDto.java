package com.netstudy.content.model.dto;

import com.netstudy.content.model.po.CourseCategory;
import lombok.Data;

import java.util.List;

/**
 * @author Dico
 * @version 1.0
 * @description TODO
 * @date 2024/4/11 15:34
 **/
@Data
public class CourseCategoryTreeDto extends CourseCategory implements java.io.Serializable {
    //子节点
    List<CourseCategoryTreeDto> childrenTreeNodes;
}
