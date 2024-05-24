package com.netstudy.learning.model.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @author Dico
 * @version 1.0
 * @description 我的课程查询条件
 * @date 2024/5/24 9:42
 */
@Data
@ToString
public class MyCourseTableParams {

    String userId;

    //课程类型  [{"code":"700001","desc":"免费课程"},{"code":"700002","desc":"收费课程"}]
    private String courseType;

    //排序 1按学习时间进行排序 2按加入时间进行排序
    private String sortType;

    //1即将过期、2已经过期
    private String expiresType;

    int page = 1;
    int startIndex;
    int size = 4;

}
