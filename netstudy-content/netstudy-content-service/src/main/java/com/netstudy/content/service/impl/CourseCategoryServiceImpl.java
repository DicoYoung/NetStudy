package com.netstudy.content.service.impl;

import com.netstudy.content.mapper.CourseCategoryMapper;
import com.netstudy.content.model.dto.CourseCategoryTreeDto;
import com.netstudy.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Dico
 * @version 1.0
 * @description TODO
 * @date 2024/4/12 14:31
 **/
@Slf4j
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {
    @Autowired
    CourseCategoryMapper courseCategoryMapper;


    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        //调用mapper查询条件
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);
        //得到结果，封装成目标List
        //现将其映射到map里，采用stream流
        Map<String, CourseCategoryTreeDto> collect = courseCategoryTreeDtos.stream().filter(item -> !id.equals(item.getId())).collect(Collectors.toMap(key -> key.getId(), value -> value, (key1, key2) -> key2));
        //遍历map开始组装，往children里添加数据
        List<CourseCategoryTreeDto> categoryTreeDtoList = new ArrayList<>();
        courseCategoryTreeDtos.stream().filter(item -> !id.equals(item.getId())).forEach(item -> {
            //开始处理
            //当父节点是根节点，直接加入，这是第一层的，第零层的不显示，前面没有加入结果，被过滤了
            if (item.getParentid().equals(id)) {
                categoryTreeDtoList.add(item);
            }
            //
            CourseCategoryTreeDto courseCategoryParent = collect.get(item.getParentid());
            if (courseCategoryParent != null) {
                //如果还没有children,注意新建对象
                if (courseCategoryParent.getChildrenTreeNodes() == null) {
                    courseCategoryParent.setChildrenTreeNodes(new ArrayList<>());
                }
                //将子节点放入children中
                courseCategoryParent.getChildrenTreeNodes().add(item);
            }
        });

        return categoryTreeDtoList;
    }
}
