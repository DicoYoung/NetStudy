package com.netstudy.content;


import com.netstudy.content.config.MultipartSupportConfig;
import com.netstudy.content.feignclient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author Dico
 * @version 1.0
 * @description feign远程调用媒资上传测试类
 * @date 2024/5/15 17:22
 **/
@SpringBootTest
public class FeignUploadTest {
    @Autowired
    MediaServiceClient mediaServiceClient;

    @Test
    public void test() throws IOException {

        //将file转成MultipartFile
        File file = new File("E:\\JavaCode\\netstudy_tool\\templateTest\\122.html");
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        //远程调用得到返回值
        String upload = mediaServiceClient.upload(multipartFile, "course/122.html");
        if (upload == null) {
            System.out.println("走了降级逻辑");
        }
    }
}
