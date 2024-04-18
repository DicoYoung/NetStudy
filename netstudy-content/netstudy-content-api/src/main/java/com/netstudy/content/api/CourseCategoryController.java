package com.netstudy.content.api;

import com.netstudy.content.model.dto.CourseCategoryTreeDto;
import com.netstudy.content.service.CourseCategoryService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Dico
 * @version 1.0
 * @description 课程分类接口
 * @date 2024/4/11 15:40
 **/
@Api(value = "课程分类编辑接口", tags = "课程分类编辑接口")
@RestController
public class CourseCategoryController {
    @Autowired
    CourseCategoryService courseCategoryService;

    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes() {
        return courseCategoryService.queryTreeNodes("1");
    }

}
