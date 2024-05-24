package com.netstudy.learning;

import com.netstudy.content.model.po.CoursePublish;
import com.netstudy.learning.feignclient.ContentServiceClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Dico
 * @version 1.0
 * @description 远程调用测试类
 * @date 2024/5/24 20:14
 */
@SpringBootTest
public class FeignClientTest {

    @Autowired
    ContentServiceClient contentServiceClient;


    @Test
    public void testContentServiceClient() {
        CoursePublish coursepublish = contentServiceClient.getCoursePublish(18L);
        Assertions.assertNotNull(coursepublish);
    }
}