package com.netstudy.content.api;

import com.netstudy.content.model.dto.BindTeachplanMediaDto;
import com.netstudy.content.model.dto.SavaTeachplanDto;
import com.netstudy.content.model.dto.TeachplanDto;
import com.netstudy.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Dico
 * @version 1.0
 * @description 课程计划相关的接口
 * @date 2024/4/15 16:11
 **/
@Api(value = "课程计划编辑接口", tags = "课程计划编辑接口")
@RestController
public class TeachplanController {
    @Autowired
    TeachplanService teachplanService;

    @ApiOperation("查询课程计划树形结构")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId) {
        return teachplanService.findTeachplanTree(courseId);
    }

    @ApiOperation("课程计划新建和修改")
    @PostMapping("/teachplan")
    public void saveTeachplan(@RequestBody SavaTeachplanDto savaTeachplanDto) {
        teachplanService.saveTeachplan(savaTeachplanDto);
    }

    @ApiOperation("/课程计划删除")
    @DeleteMapping("/teachplan/{teachplanId}")
    public void deleteTeachplan(@PathVariable Long teachplanId) {
        teachplanService.deletTeachplan(teachplanId);
    }

    @ApiOperation("课程计划移动")
    @PostMapping("/teachplan/{moveType}/{teachplanId}")
    public void orderByTeachplan(@PathVariable String moveType, @PathVariable Long teachplanId) {
        teachplanService.orderByTeachplan(moveType, teachplanId);
    }

    @ApiOperation(value = "课程计划和媒资绑定")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto) {
        teachplanService.associationMedia(bindTeachplanMediaDto);
    }
}
