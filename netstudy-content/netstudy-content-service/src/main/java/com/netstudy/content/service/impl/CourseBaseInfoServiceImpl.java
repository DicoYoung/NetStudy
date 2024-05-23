package com.netstudy.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.netstudy.base.exception.NetStudyException;
import com.netstudy.base.model.PageParams;
import com.netstudy.base.model.PageResult;
import com.netstudy.content.mapper.*;
import com.netstudy.content.model.dto.AddCourseDto;
import com.netstudy.content.model.dto.CourseBaseInfoDto;
import com.netstudy.content.model.dto.EditCourseDto;
import com.netstudy.content.model.dto.QueryCourseParamsDto;
import com.netstudy.content.model.po.*;
import com.netstudy.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Dico
 * @version 1.0
 * @description TODO
 * @date 2024/4/8 14:41
 **/
@Slf4j
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {
    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(Long companyId, PageParams pageParams, QueryCourseParamsDto courseParamsDto) {
        //拼装查询条件
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //根据培训机构id拼装查询条件
        queryWrapper.eq(CourseBase::getCompanyId, companyId);
        //根据名称模糊查询,在sql中拼接 course_base.name like '%值%'
        queryWrapper.like(StringUtils.isNotEmpty(courseParamsDto.getCourseName()), CourseBase::getName, courseParamsDto.getCourseName());
        //根据课程审核状态查询 course_base.audit_status = ?
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, courseParamsDto.getAuditStatus());
        //按课程发布状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getPublishStatus()), CourseBase::getStatus, courseParamsDto.getPublishStatus());

        //创建page分页参数对象，参数：当前页码，每页记录数
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        //开始进行分页查询
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
        //数据列表
        List<CourseBase> items = pageResult.getRecords();
        //总记录数
        long total = pageResult.getTotal();
        //List<T> items, long counts, long page, long pageSize
        return new PageResult<>(items, total, pageParams.getPageNo(), pageParams.getPageSize());
    }

    @Transactional//增删改查都要添加这个注解
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto) {
        //往course_base里添加数据
        CourseBase courseBase = new CourseBase();
        //BeanUtils.copy 只要属性名称一致，就可以拷贝，新值将全覆盖旧对象的值，无需再set、get
        BeanUtils.copyProperties(addCourseDto, courseBase);
        //还需要输入机构ID等未直接添加的信息
        courseBase.setCompanyId(companyId);
        courseBase.setCreateDate(LocalDateTime.now());
        courseBase.setAuditStatus("202002");
        courseBase.setStatus("203001");
        //插入数据库
        int insert = courseBaseMapper.insert(courseBase);
        if (insert <= 0) {
            throw new RuntimeException("课程添加错误");
        }
        //往course_market添加数据
        CourseMarket courseMarket = new CourseMarket();
        //转移存储对象
        BeanUtils.copyProperties(addCourseDto, courseMarket);
        //由于逆天表的原因，课程信息主键和营销信息主键相同，一一对应
        Long courseId = courseBase.getId();
        courseMarket.setId(courseId);
        //保存营销信息
        saveCourseMarket(courseMarket);
        //从数据库查询课程详细信息
        return getCourseBaseInfo(courseId);
    }

    //查询课程信息
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId) {
        //从课程基本信息表查询
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            return null;
        }
        //从课程营销表查询
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);

        //组装在一起
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }

        //通过courseCategoryMapper查询分类信息，将分类名称放在courseBaseInfoDto对象
        CourseCategory mtObj = courseCategoryMapper.selectById(courseBase.getMt());
        String mtName = mtObj.getName();//大分类名称
        courseBaseInfoDto.setMtName(mtName);
        CourseCategory stObj = courseCategoryMapper.selectById(courseBase.getSt());
        String stName = stObj.getName();//小分类名称
        courseBaseInfoDto.setStName(stName);

        return courseBaseInfoDto;
    }

    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {
        //根据传进来的表单，检验数据库是否有该课程，有才能修改
        Long courseId = editCourseDto.getId();
        //查询
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        //判断
        if (courseBase == null) {
            NetStudyException.cast("课程不存在");
        }
        //数据合法性校验
        //非参数，而是业务逻辑的校验写在service里
        //本机构只可修改本机构的课程
        if (!companyId.equals(courseBase.getCompanyId())) {
            NetStudyException.cast("只能修改本机构的课程");
        }
        //封装数据,将页面传进来的表单数据覆盖掉数据库查询出来的旧数据
        BeanUtils.copyProperties(editCourseDto, courseBase);
        //修改时间
        courseBase.setChangeDate(LocalDateTime.now());
        //存储数据库
        int i = courseBaseMapper.updateById(courseBase);
        if (i <= 0) {
            NetStudyException.cast("修改课程失败");
        }
        //更新营销信息
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(editCourseDto, courseMarket);
        saveCourseMarket(courseMarket);
        //查询课程信息
        return getCourseBaseInfo(courseId);
    }

    @Override
    public void deleteCourseBase(Long companyId, Long courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (!companyId.equals(courseBase.getCompanyId())) {
            NetStudyException.cast("不允许更改非本机构的课程");
        }
        // 删除课程教师信息
        LambdaQueryWrapper<CourseTeacher> teacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teacherLambdaQueryWrapper.eq(CourseTeacher::getCourseId, courseId);
        courseTeacherMapper.delete(teacherLambdaQueryWrapper);
        // 删除课程计划
        LambdaQueryWrapper<Teachplan> teachplanLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teachplanLambdaQueryWrapper.eq(Teachplan::getCourseId, courseId);
        teachplanMapper.delete(teachplanLambdaQueryWrapper);
        // 删除营销信息
        courseMarketMapper.deleteById(courseId);
        // 删除课程基本信息
        courseBaseMapper.deleteById(courseId);
    }

    //保存营销信息的单独方法，存在数据则更新，没有就添加
    private int saveCourseMarket(CourseMarket courseMarket) {
        //参数合法性校验
        String charge = courseMarket.getCharge();
        if (StringUtils.isEmpty(charge)) {
            throw new RuntimeException("收费规则为空");
        }
        //如果课程收费，价格没有填写需要抛出异常
        if (charge.equals("201001")) {
            if (courseMarket.getPrice() == null || courseMarket.getPrice().floatValue() <= 0) {
//                throw new RuntimeException("课程价格不为空且需要大于0");
                NetStudyException.cast("课程价格不为空且需要大于0");
            }
        }
        //从数据库查询,有则更新,没有添加
        Long id = courseMarket.getId();
        CourseMarket tempCourseMarket = courseMarketMapper.selectById(id);
        if (tempCourseMarket == null) {
            //插入
            return courseMarketMapper.insert(courseMarket);
        } else {
            //对象拷贝
            BeanUtils.copyProperties(courseMarket, tempCourseMarket);
            //以防ID主键为空
            tempCourseMarket.setId(courseMarket.getId());
            //更新
            return courseMarketMapper.updateById(tempCourseMarket);
        }
    }
}
