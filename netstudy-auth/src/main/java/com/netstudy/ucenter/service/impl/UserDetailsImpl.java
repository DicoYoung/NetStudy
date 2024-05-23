package com.netstudy.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.netstudy.ucenter.mapper.XcMenuMapper;
import com.netstudy.ucenter.mapper.XcUserMapper;
import com.netstudy.ucenter.model.dto.AuthParamsDto;
import com.netstudy.ucenter.model.dto.XcUserExt;
import com.netstudy.ucenter.model.po.XcMenu;
import com.netstudy.ucenter.model.po.XcUser;
import com.netstudy.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dico
 * @version 1.0
 * @description 实现spring security需要自定义UserDetailService的接口实现类
 * @date 2024/5/20
 */

@Service
@Slf4j
public class UserDetailsImpl implements UserDetailsService {
    @Autowired
    XcUserMapper xcUserMapper;
    @Autowired
    XcMenuMapper xcMenuMapper;

    @Autowired
    ApplicationContext applicationContext;

    /**
     * @param s 用户输入的登录账号
     * @return UserDetails
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //查询到用户不存在，返回null, spring security会自动抛出异常
        AuthParamsDto authParamsDto = null;
        try {
            authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
        } catch (Exception e) {
            log.error("认证请求数据格式不对：{}", s);
            throw new RuntimeException("认证请求数据格式不对");
        }
        // 获取认证类型，beanName就是 认证类型 + 后缀，例如 password + _authservice = password_authservice
        String authType = authParamsDto.getAuthType();
        // 根据认证类型，从Spring容器中取出对应的bean
        AuthService authService = applicationContext.getBean(authType + "_authservice", AuthService.class);
        XcUserExt user = authService.execute(authParamsDto);
        return getUserPrincipal(user);
    }

    public UserDetails getUserPrincipal(XcUserExt user) {
        // 获取用户id
        String userId = user.getId();
        // 根据用户id查询用户权限
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(userId);
        ArrayList<String> permissions = new ArrayList<>();
        // 没权限，给一个默认的
        if (xcMenus.isEmpty()) {
            permissions.add("test");
        } else {
            // 获取权限，加入到集合里
            xcMenus.forEach(xcMenu -> {
                permissions.add(xcMenu.getCode());
            });
        }
        // 设置权限
        user.setPermissions(permissions);
        String[] authorities = permissions.toArray(new String[0]);
        String password = user.getPassword();
        //保证密码不会被传输，还可以在user的password属性上加@JsonIgnore
        user.setPassword(null);
        //将用户信息（多信息）转成JSON传输
        String userJsonStr = JSON.toJSONString(user);
        return User.withUsername(userJsonStr).password(password).authorities(authorities).build();
    }
}
