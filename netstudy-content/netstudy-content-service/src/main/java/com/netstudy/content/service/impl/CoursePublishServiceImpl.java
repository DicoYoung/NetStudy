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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
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
import java.util.Random;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    StringRedisTemplate redisTemplate;

    //布隆过滤器
    @Autowired
    RedissonClient redissonClient;


    /**
     * 主要面向全查询，open接口的,不需要查询发布表的
     *
     * @param courseId 课程id
     * @return CoursePreviewDto
     */
    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        // 课程基本信息、营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        // 课程计划信息
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        // 封装返回
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(teachplanTree);
        return coursePreviewDto;
    }

    /**
     * 面向需要查询发布表的
     *
     * @param courseId 课程id
     * @return CoursePreviewDto
     */
    @Override
    public CoursePreviewDto getCoursePreviewInfoWithPublish(Long courseId) {
        //封装数据
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        //查询课程发布表
//        CoursePublish coursePublish = getCoursePublish(courseId);
        //先从缓存查询，缓存中有直接返回，没有再查询数据库
        CoursePublish coursePublish = getCoursePublishCache(courseId);
        if (coursePublish == null) {
            return coursePreviewDto;
        }
        //开始向coursePreviewDto填充数据
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(coursePublish, courseBaseInfoDto);
        //课程计划信息
        String teachPlanJson = coursePublish.getTeachplan();
        //转成List<TeachplanDto>
        List<TeachplanDto> teachPlanDtos = JSON.parseArray(teachPlanJson, TeachplanDto.class);
        coursePreviewDto.setCourseBase(courseBaseInfoDto);
        coursePreviewDto.setTeachplans(teachPlanDtos);
        return coursePreviewDto;
//        return null;
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
        // 【缓存穿透】：高并发查询数据库没有的数据，缓存中没有，穿透了缓存，缓存不起作用了
        // 进行数据类型校验，解决缓存穿透的方法1
        // 布隆过滤器，解决缓存穿透的方法2
        // 【缓存击穿】：高并发访问某一个数据，且此时在缓存中它过期了，就会大量访问数据库
        // 解决方法：
        // 1.热点数据不过期，后台可以以提前存到缓存里，过期时间不过期
        // 2.同步锁，太慢，只需要锁住查询数据库，查询缓存不需要锁住,用锁尽量缩小锁的范围
        //            并且同步锁只能控制当前JVM里的锁，微服务多个实例里互相无法控制，不符合要求
        // 这时候需要把【锁单独部署起来】，实现分布式锁，让【多个虚拟机去争抢同一个锁】
        // 主要方法为Redis的SETNX和redisson
        // 1.SETNX：redisTemplate.opsForValue().setIfAbsent();
        //   SETNX问题在于拿到锁在执行业务时，还没执行完，锁就过期了，此时另外的线程就会又请求新锁，再次访问数据库
        //   并且会出现线程1把线程2的锁解开了的情况

        // 1. 先从缓存中查询
        String courseCacheJson = redisTemplate.opsForValue().get("course:" + courseId);
        // 2. 如果缓存里有，直接返回
        if (StringUtils.isNotEmpty(courseCacheJson)) {
            log.debug("从缓存中查询");
            if ("null".equals(courseCacheJson)) {
                return null;
            }
            return JSON.parseObject(courseCacheJson, CoursePublish.class);
        } else {
            // 2.redisson分布式锁
            RLock lock = redissonClient.getLock("courseQueryLock" + courseId);
            lock.lock();
            try {
                // 再次查询一下缓存
                // 1. 先从缓存中查询
                courseCacheJson = redisTemplate.opsForValue().get("course:" + courseId);
                // 2. 如果缓存里有，直接返回
                if (StringUtils.isNotEmpty(courseCacheJson)) {
                    log.debug("从缓存中查询");
                    if ("null".equals(courseCacheJson)) {
                        return null;
                    }
                    return JSON.parseObject(courseCacheJson, CoursePublish.class);
                }
                log.debug("缓存中没有，查询数据库");
                System.out.println("缓存中没有，查询数据库");
                // 3. 如果缓存里没有，查询数据库
                CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
                if (coursePublish == null) {
                    // 解决缓存穿透的方法3
                    // 数据库没有这个数据，依然缓存，但是缓存的是一个空
                    // 这样当再次请求这个空查询时在缓存里直接给它返回null，而不再需要查询数据库了
                    // 并且需要设置过期时间，为30s，此外还加一个随机数，解决缓存雪崩
                    // 【缓存雪崩】：缓存中的key同时设置了相同的时间期限，就会在同一时间大面积过期，从而导致访问数据库
                    // 上诉对一类信息加随机事件是缓存雪崩的解决方法之1
                    // 2.同步锁
                    // 3.缓存预热
                    redisTemplate.opsForValue().set("course:" + courseId, JSON.toJSONString(null), 30 + new Random().nextInt(100), TimeUnit.SECONDS);
                    return null;
                }
                String jsonString = JSON.toJSONString(coursePublish);
                // 3.1 将查询结果缓存
                redisTemplate.opsForValue().set("course:" + courseId, jsonString, 300 + new Random().nextInt(100), TimeUnit.SECONDS);
                // 3.1 返回查询结果
                return coursePublish;
            } finally {
                lock.unlock();
            }
        }
//        return null;
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
