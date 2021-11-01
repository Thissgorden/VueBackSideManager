package com.gdlearn.backsidemanager.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gdlearn.backsidemanager.entity.SysUser;
import com.gdlearn.backsidemanager.service.*;
import com.gdlearn.backsidemanager.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

public class BaseController {
    @Autowired
    HttpServletRequest req;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    ISysUserService sysUserService;

    @Autowired
    ISysRoleService sysRoleService;

    @Autowired
    ISysMenuService sysMenuService;

    @Autowired
    ISysRoleMenuService sysRoleMenuService;

    @Autowired
    ISysUserRoleService sysUserRoleService;

    public Page getPage(){
        int current = ServletRequestUtils.getIntParameter(req,"current",1);
        int size = ServletRequestUtils.getIntParameter(req,"size",10);
        return new Page(current,size);
    }
}
