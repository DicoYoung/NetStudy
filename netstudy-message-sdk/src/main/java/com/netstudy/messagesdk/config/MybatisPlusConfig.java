package com.netstudy.messagesdk.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis-Plus 配置
 */
@Configuration("messagesdk_mpconfig")
@MapperScan("com.netstudy.messagesdk.mapper")
public class MybatisPlusConfig {


}