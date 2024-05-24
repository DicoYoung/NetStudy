package com.netstudy.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.netstudy.base.exception.NetStudyException;
import com.netstudy.content.config.MultipartSupportConfig;
import com.netstudy.content.feignclient.CourseIndex;
import com.netstudy.content.feignclient.MediaServiceClient;
import com.netstudy.content.feignclient.SearchServiceClient;
import com.netstudy.content.mapper.CourseBaseMapper;
import com.netstudy.content.mapper.CourseMarketMapper;
import com.netstudy.content.mapper.CoursePublishMapper;
import com.netstudy.content.mapper.CoursePublishPreMapper;
import com.netstudy.content.model.dto.CourseBaseInfoDto;
import com.netstudy.content.model.dto.CoursePreviewDto;
import com.netstudy.content.model.dto.TeachplanDto;
import com.netstudy.content.model.po.CourseBase;
import com.netstudy.content.model.po.CourseMarket;
import com.netstudy.content.model.po.CoursePublish;
import com.netstudy.content.model.po.CoursePublishPre;
import com.netstudy.content.service.CourseBaseInfoService;
import com.netstudy.content.service.CoursePublishService;
import com.netstudy.content.service.TeachplanService;
import com.netstudy.messagesdk.model.po.MqMessage;
import com.netstudy.messagesdk.service.MqMessageService;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * @author Dico
 * @version 1.0
 * @description 课程发布实现
 * @date 2024/5/13 17:40
 **/
@Slf4j
@Service
public class CoursePublishServiceImpl implements CoursePublishService {

    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @Autowired
    TeachplanService teachplanService;

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;

    @Autowired
    CoursePublishMapper coursePublishMapper;

    @Autowired
    MqMessageService mqMessageService;

    @Autowired
    MediaServiceClient mediaServiceClient;

    @Autowired
    private SearchServiceClient searchServiceClient;
//
//    @Autowired
//    RedisTemplate redisTemplate;
//
//    @Autowired
//    RedissonClient redissonClient;


    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        //课程基本信息、营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        //课程计划信息
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);

        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(teachplanTree);
        return coursePreviewDto;
    }

    @Transactional
    @Override
    public void commitAudit(Long companyId, Long courseId) {
        // 查询课程基本信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        // 查询课程营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        // 查询课程基本信息、课程营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        // 查询课程计划
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);

        // 1. 约束
        String auditStatus = courseBaseInfo.getAuditStatus();
        // 1.1 审核完后，方可提交审核
        if ("202003".equals(auditStatus)) {
            NetStudyException.cast("该课程现在属于待审核状态，审核完成后可再次提交");
        }
        // 1.2 本机构只允许提交本机构的课程
        if (!companyId.equals(courseBaseInfo.getCompanyId())) {
            NetStudyException.cast("本机构只允许提交本机构的课程");
        }
        // 1.3 没有上传图片，不允许提交
        if (StringUtils.isEmpty(courseBaseInfo.getPic())) {
            NetStudyException.cast("没有上传课程封面，不允许提交审核");
        }
        // 1.4 没有添加课程计划，不允许提交审核
        if (teachplanTree.isEmpty()) {
            NetStudyException.cast("没有添加课程计划，不允许提交审核");
        }
        // 2. 准备封装返回对象
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        BeanUtils.copyProperties(courseBaseInfo, coursePublishPre);
        coursePublishPre.setMarket(JSON.toJSONString(courseMarket));
        coursePublishPre.setTeachplan(JSON.toJSONString(teachplanTree));
        coursePublishPre.setCompanyId(companyId);
        coursePublishPre.setCreateDate(LocalDateTime.now());
        // 3. 设置预发布记录状态为已提交
        coursePublishPre.setStatus("202003");
        // 判断是否已经存在预发布记录，若存在，则更新
        CoursePublishPre coursePublishPreUpdate = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPreUpdate == null) {
            coursePublishPreMapper.insert(coursePublishPre);
        } else {
            coursePublishPreMapper.updateById(coursePublishPre);
        }
        // 4. 设置课程基本信息审核状态为已提交
        courseBase.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBase);
    }

    @Transactional
    @Override
    public void publish(Long companyId, Long courseId) {
        // 1. 约束校验
        // 1.1 获取课程预发布表数据
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            NetStudyException.cast("请先提交课程审核，审核通过后方可发布");
        }
        // 1.2 课程审核通过后，方可发布
        if (!"202004".equals(coursePublishPre.getStatus())) {
            NetStudyException.cast("操作失败，课程审核通过后方可发布");
        }
        // 1.3 本机构只允许发布本机构的课程
        if (!coursePublishPre.getCompanyId().equals(companyId)) {
            NetStudyException.cast("操作失败，本机构只允许发布本机构的课程");
        }
        // 2. 向课程发布表插入数据
        saveCoursePublish(courseId);
        // 3. 向消息表插入数据
        saveCoursePublishMessage(courseId);
        // 4. 删除课程预发布表对应记录
        coursePublishPreMapper.deleteById(courseId);
    }

    @Override
    public CoursePublish getCoursePublish(Long courseId) {
        return coursePublishMapper.selectById(courseId);
    }

    @Override
    public CoursePublish getCoursePublishCache(Long courseId) {

        return null;
    }

    @Override
    public File generateCourseHtml(Long courseId) {
        File htmlFile = null;
        try {
            // 1. 创建一个Freemarker配置
            Configuration configuration = new Configuration(Configuration.getVersion());
            // 2. 告诉Freemarker在哪里可以找到模板文件
//            String classPath = this.getClass().getResource("/").getPath();
//            configuration.setDirectoryForTemplateLoading(new File(classPath + "/templates/"));
            configuration.setTemplateLoader(new ClassTemplateLoader(this.getClass().getClassLoader(), "/templates"));
            configuration.setDefaultEncoding("utf-8");
            // 3. 创建一个模型数据，与模板文件中的数据模型保持一致，这里是CoursePreviewDto类型
            CoursePreviewDto coursePreviewDto = this.getCoursePreviewInfo(courseId);
            HashMap<String, Object> map = new HashMap<>();
            map.put("model", coursePreviewDto);
            // 4. 加载模板文件
            Template template = configuration.getTemplate("course_template.ftl");
            // 5. 将数据模型应用于模板
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            // 5.1 将静态文件内容输出到文件中
            InputStream inputStream = IOUtils.toInputStream(content, "utf-8");
            htmlFile = File.createTempFile("coursePublish", ".html");
            FileOutputStream fos = new FileOutputStream(htmlFile);
            IOUtils.copy(inputStream, fos);
        } catch (Exception e) {
            log.debug("课程静态化失败：{},课程id:{}", e.getMessage(), courseId);
            e.printStackTrace();
        }
        return htmlFile;
    }

    @Override
    public void uploadCourseHtml(Long courseId, File file) {
        try {
            //将file转成MultipartFile
            MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
            //远程调用得到返回值
            String upload = mediaServiceClient.upload(multipartFile, "course/" + courseId + ".html");
            if (upload == null) {
                log.debug("远程调用走降级逻辑得到上传的结果为null,课程id:{}", courseId);
                NetStudyException.cast("上传静态文件过程中存在异常");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            NetStudyException.cast("上传静态文件过程中存在异常");
        }
    }

    @Override
    public Boolean saveCourseIndex(Long courseId) {
        // 1. 取出课程发布信息
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        // 2. 拷贝至课程索引对象
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish, courseIndex);
        // 3. 远程调用搜索服务API，添加课程索引信息
        Boolean result = searchServiceClient.add(courseIndex);
        if (!result) {
            NetStudyException.cast("添加索引失败");
        }
        return true;
    }

    /**
     * 保存课程发布信息
     *
     * @param courseId 课程id
     */
    private void saveCoursePublish(Long courseId) {
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            NetStudyException.cast("课程预发布数据为空");
        }
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        // 设置发布状态为已发布
        coursePublish.setStatus("203002");
        CoursePublish coursePublishUpdate = coursePublishMapper.selectById(courseId);
        // 有则更新，无则新增
        if (coursePublishUpdate == null) {
            coursePublishMapper.insert(coursePublish);
        } else {
            coursePublishMapper.updateById(coursePublish);
        }
        // 更新课程基本信息表的发布状态为已发布
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setAuditStatus("203002");
        courseBaseMapper.updateById(courseBase);
    }

    /**
     * 保存消息表
     *
     * @param courseId 课程id
     */
    private void saveCoursePublishMessage(Long courseId) {
        MqMessage mqMessage = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if (mqMessage == null) {
            NetStudyException.cast("添加消息记录失败");
        }
    }
}
