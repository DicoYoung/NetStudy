package com.netstudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Dico
 * @version 1.0
 * @description 单元测试启动类
 * @date 2024/4/7 14:36
 **/
@EnableFeignClients(basePackages = {"com.netstudy.content.feignclient"})
@SpringBootApplication
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
