package com.netstudy.content;

import com.netstudy.content.mapper.CourseCategoryMapper;
import com.netstudy.content.model.dto.CourseCategoryTreeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Dico
 * @version 1.0
 * @description TODO
 * @date 2024/4/7 16:00
 **/
@SpringBootTest
public class CourseCategoryMapperTests {

    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Test
    public void testCourseCategoryMapper() {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes("1");
        System.out.println(courseCategoryTreeDtos);
    }
}
