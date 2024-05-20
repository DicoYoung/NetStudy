package com.netstudy.ucenter.model.dto;

import com.netstudy.ucenter.model.po.XcUser;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dico
 * @version 1.0
 * @description 用户扩展信息
 * @date 2024/5/20 13:56
 */
@Data
public class XcUserExt extends XcUser {
    //用户权限
    List<String> permissions = new ArrayList<>();
}
