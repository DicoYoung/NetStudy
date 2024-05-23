package com.netstudy.ucenter.service.impl;

import com.netstudy.ucenter.model.dto.AuthParamsDto;
import com.netstudy.ucenter.model.dto.XcUserExt;
import com.netstudy.ucenter.service.AuthService;
import org.springframework.stereotype.Service;

/**
 * @author Dico
 * @version 1.0
 * @description 微信认证
 * @date 2024/5/21 15:43
 **/
@Service("wx_authservice")
public class WxAuthServiceImpl implements AuthService {
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {

        return null;
    }
}
