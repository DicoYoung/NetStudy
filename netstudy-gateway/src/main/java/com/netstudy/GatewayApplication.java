package com.netstudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 网关作用：
 * <p>
 * 1.路由转发
 * 2.认证，校验JWT令牌合法性
 * 3.维护过滤白名单
 */
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

}
