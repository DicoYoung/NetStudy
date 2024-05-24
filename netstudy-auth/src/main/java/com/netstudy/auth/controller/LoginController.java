package com.netstudy.auth.controller;

import com.netstudy.ucenter.mapper.XcUserMapper;
import com.netstudy.ucenter.model.dto.FindPswDto;
import com.netstudy.ucenter.model.dto.RegisterDto;
import com.netstudy.ucenter.model.po.XcUser;
import com.netstudy.ucenter.service.VerifyService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author Dico
 * @version 1.0
 * @description 测试controller
 * @date 2024/5/17 17:25
 */
@Slf4j
@RestController
public class LoginController {

    @Autowired
    XcUserMapper userMapper;

    @Autowired
    VerifyService verifyService;


    @RequestMapping("/login-success")
    public String loginSuccess() {
        return "登录成功";
    }


    @RequestMapping("/user/{id}")
    public XcUser getuser(@PathVariable("id") String id) {
        return userMapper.selectById(id);
    }

    @RequestMapping("/r/r1")
    @PreAuthorize("hasAnyAuthority('p1')")//拥有p1权限可以访问
    public String r1() {
        return "访问r1资源";
    }

    @RequestMapping("/r/r2")
    @PreAuthorize("hasAnyAuthority('p2')")//拥有p2权限可以访问
    public String r2() {
        return "访问r2资源";
    }

    @ApiOperation(value = "找回密码", tags = "找回密码")
    @PostMapping("/findpassword")
    public void findPassword(@RequestBody FindPswDto findPswDto) {
        verifyService.findPassword(findPswDto);
    }

    @ApiOperation(value = "注册", tags = "注册")
    @PostMapping("/register")
    public void register(@RequestBody RegisterDto registerDto) {
        verifyService.register(registerDto);
    }
}
