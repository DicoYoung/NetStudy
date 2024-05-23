package com.netstudy.checkcode.service;

import com.netstudy.checkcode.model.CheckCodeParamsDto;
import com.netstudy.checkcode.model.CheckCodeResultDto;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Dico
 * @version 1.0
 * @description 验证码接口
 * @date 2024/5/21 15:59
 */
public interface CheckCodeService {

    /**
     * @param checkCodeParamsDto 生成验证码参数
     * @return com.netstudy.checkcode.model.CheckCodeResultDto 验证码结果
     * @description 生成验证码
     * @author Dico
     * @date 2024/5/21 18:21
     */
    CheckCodeResultDto generate(CheckCodeParamsDto checkCodeParamsDto);

    /**
     * @param key
     * @param code
     * @return boolean
     * @description 校验验证码
     * @author Dico
     * @date 2024/5/21 18:46
     */
    boolean verify(String key, String code);


    /**
     * @author Dico
     * @description 验证码生成器
     * @date 2024/5/21 16:34
     */
    interface CheckCodeGenerator {
        /**
         * 验证码生成
         *
         * @return 验证码
         */
        String generate(int length);


    }

    /**
     * @author Dico
     * @description key生成器
     * @date 2024/5/21 16:34
     */
    interface KeyGenerator {

        /**
         * key生成
         *
         * @return 验证码
         */
        String generate(String prefix);
    }


    /**
     * @author Dico
     * @description 验证码存储
     * @date 2024/5/21 16:34
     */
    interface CheckCodeStore {

        /**
         * @param key    key
         * @param value  value
         * @param expire 过期时间,单位秒
         * @return void
         * @description 向缓存设置key
         * @author Dico
         * @date 2024/5/21 17:15
         */
        void set(String key, String value, Integer expire);

        String get(String key);

        void remove(String key);
    }
}
