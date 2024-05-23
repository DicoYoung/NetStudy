package com.netstudy.ucenter.service;

import com.netstudy.ucenter.model.po.XcUser;

/**
 * @author Dico
 * @version 1.0
 * @description 微信扫码接入
 * @date 2024/5/23 16:43
 */

public interface WxAuthService {
    /**
     * 微信扫码认证
     * 1.申请令牌
     * 2.携带令牌获取用户信息
     * 3.保存用户信息到数据库
     *
     * @param code 授权码
     * @return 用户信息
     */
    XcUser wxAuth(String code);
}
