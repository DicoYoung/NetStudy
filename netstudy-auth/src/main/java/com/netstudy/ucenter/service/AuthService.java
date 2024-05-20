package com.netstudy.ucenter.service;

import com.netstudy.ucenter.model.dto.AuthParamsDto;
import com.netstudy.ucenter.model.dto.XcUserExt;

/**
 * @author Dico
 * @version 1.0
 * @description 认证接口
 * @date 2024/5/20 14:48
 */
public interface AuthService {

    /**
     * @param authParamsDto 认证参数
     * @return com.netstudy.ucenter.model.po.XcUser 用户信息
     * @description 认证方法
     * @author Dico
     * @date 2024/5/20 12:11
     */
    XcUserExt execute(AuthParamsDto authParamsDto);

}
