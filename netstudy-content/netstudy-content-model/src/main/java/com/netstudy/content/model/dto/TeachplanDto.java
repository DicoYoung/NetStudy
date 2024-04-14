package com.netstudy.content.model.dto;

import com.netstudy.content.model.po.Teachplan;
import com.netstudy.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author Dico
 * @version 1.0
 * @description 课程计划模型类
 * @date 2024/4/15 16:06
 **/
@Data
@ToString
public class TeachplanDto extends Teachplan {
    //与媒体资源关联的信息
    private TeachplanMedia teachplanMedia;
    //小张姐list
    private List<TeachplanDto> teachPlanTreeNodes;
}
