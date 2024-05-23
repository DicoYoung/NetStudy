package com.netstudy.checkcode.model;

import lombok.Data;

/**
 * @author Dico
 * @version 1.0
 * @description 验证码生成结果类
 * @date 2024/5/21 15:48
 */
@Data
public class CheckCodeResultDto {

    /**
     * key用于验证
     */
    private String key;

    /**
     * 混淆后的内容
     * 举例：
     * 1.图片验证码为:图片base64编码
     * 2.短信验证码为:null
     * 3.邮件验证码为: null
     * 4.邮件链接点击验证为：null
     * ...
     */
    private String aliasing;
}
