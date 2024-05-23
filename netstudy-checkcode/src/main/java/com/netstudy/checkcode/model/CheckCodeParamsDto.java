package com.netstudy.checkcode.model;

import lombok.Data;

import java.util.Map;

/**
 * @author Dico
 * @version 1.0
 * @description 验证码生成参数类
 * @date 2024/5/21 15:48
 */
@Data
public class CheckCodeParamsDto {

    /**
     * 验证码类型:pic、sms、email等
     */
    private String checkCodeType;

    /**
     * 业务携带参数
     */
    private String param1;
    private String param2;
    private String param3;
}
