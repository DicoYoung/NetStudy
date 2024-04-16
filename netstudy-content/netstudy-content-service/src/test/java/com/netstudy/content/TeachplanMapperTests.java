package com.netstudy.content;

import com.netstudy.content.mapper.TeachplanMapper;
import com.netstudy.content.model.dto.TeachplanDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Dico
 * @version 1.0
 * @description 课程计划测试
 * @date 2024/4/16 14:43
 **/
@SpringBootTest
public class TeachplanMapperTests {
    @Autowired
    TeachplanMapper teachplanMapper;

    @Test
    public void testSelectTreeNodes() {
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(117L);
        System.out.println(teachplanDtos);
    }
}
