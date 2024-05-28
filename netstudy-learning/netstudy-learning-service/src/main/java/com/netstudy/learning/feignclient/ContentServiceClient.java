package com.netstudy.learning.feignclient;

import com.netstudy.content.model.po.CoursePublish;
import com.netstudy.content.model.po.Teachplan;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Dico
 * @version 1.0
 * @description 内容管理远程接口
 * @date 2024/5/24 9:42
 */
@FeignClient(value = "content-api", fallbackFactory = ContentServiceClientFallbackFactory.class)
public interface ContentServiceClient {

    @ResponseBody
    @GetMapping("/content/r/coursepublish/{courseId}")
    CoursePublish getCoursePublish(@PathVariable("courseId") Long courseId);

    @PostMapping("/content/teachplan/{teachplanId}")
    Teachplan getTeachplan(@PathVariable("teachplanId") Long teachplanId);

}
