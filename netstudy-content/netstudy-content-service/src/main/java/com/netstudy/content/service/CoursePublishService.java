package com.netstudy.content.service;

import com.netstudy.content.model.dto.CoursePreviewDto;
import com.netstudy.content.model.po.CoursePublish;

import java.io.File;

/**
 * @author Dico
 * @version 1.0
 * @description 课程发布接口
 * @date 2024/5/13 17:39
 **/
public interface CoursePublishService {
    /**
     * @param courseId 课程id
     * @return com.netstudy.content.model.dto.CoursePreviewDto
     * @description 获取课程预览信息
     * @author Dico
     * @date 2024/5/13 17:39
     */
    CoursePreviewDto getCoursePreviewInfo(Long courseId);

    /**
     * 提交课程审核
     *
     * @param companyId 机构ID
     * @param courseId  课程ID
     */
    void commitAudit(Long companyId, Long courseId);

    /**
     * 课程发布
     *
     * @param companyId 机构ID
     * @param courseId  课程ID
     */
    void publish(Long companyId, Long courseId);

    /**
     * 查询课程发布信息
     *
     * @param courseId 课程ID
     * @return CoursePublish
     */
    CoursePublish getCoursePublish(Long courseId);

    /**
     * 从缓存中查询课程发布信息
     *
     * @param courseId 课程ID
     * @return CoursePublish
     */
    CoursePublish getCoursePublishCache(Long courseId);

    /**
     * 课程静态化
     *
     * @param courseId 课程id
     * @return File 静态化文件
     */
    File generateCourseHtml(Long courseId);

    /**
     * 上传课程静态化页面
     *
     * @param file 静态化文件
     */
    void uploadCourseHtml(Long courseId, File file);

    /**
     * 保存索引
     *
     * @param courseId 课程ID
     * @return 成功与否的结果
     */
    Boolean saveCourseIndex(Long courseId);
}
