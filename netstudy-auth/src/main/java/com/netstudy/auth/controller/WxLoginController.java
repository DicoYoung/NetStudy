package com.netstudy.auth.controller;

import com.netstudy.ucenter.model.po.XcUser;
import com.netstudy.ucenter.service.impl.WxAuthServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Slf4j
@Controller
public class WxLoginController {
    @Autowired
    WxAuthServiceImpl wxAuthService;

    @RequestMapping("/wxLogin")
    public String wxLogin(String code, String state) throws IOException {
        log.debug("微信扫码回调,code:{},state:{}", code, state);
        XcUser user = wxAuthService.wxAuth(code);
//        XcUser user = new XcUser();
//        user.setName("t1");
        if (user == null) {
            return "redirect:http://www.51xuecheng.cn/error.html";
        }
        String username = user.getUsername();
        return "redirect:http://www.51xuecheng.cn/sign.html?username=" + username + "&authType=wx";
    }
}
