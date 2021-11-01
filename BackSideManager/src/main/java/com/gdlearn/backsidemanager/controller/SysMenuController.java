package com.gdlearn.backsidemanager.controller;


import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gdlearn.backsidemanager.common.Result;
import com.gdlearn.backsidemanager.dto.SysMenuDto;
import com.gdlearn.backsidemanager.entity.SysMenu;
import com.gdlearn.backsidemanager.entity.SysRoleMenu;
import com.gdlearn.backsidemanager.entity.SysUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jobob
 * @since 2021-10-27
 */
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController extends BaseController {

    @GetMapping("nav")
    public Result nav(Principal principal){
        SysUser usr = sysUserService.getByUsername(principal.getName());

        String authorityInfo = sysUserService.getUserAuthorityInfo(usr.getId());
        String[] Infos = StringUtils.tokenizeToStringArray(authorityInfo, ",");//ROLE_admin,Role_noraml....

        List<SysMenuDto> navs = sysMenuService.getCurrentUserNav();

        return Result.succ(MapUtil.builder().put("authoritys",Infos)
                .put("nav",navs)
                .map());
    }

    @GetMapping("info/{id}")
    @PreAuthorize("hasAnyAuthority('sys:menu:list')")
    public Result info(@PathVariable(name = "id") Long id){
        return Result.succ(sysMenuService.getById(id));
    }

    @GetMapping("list")
    @PreAuthorize("hasAnyAuthority('sys:menu:list')")
    public Result list(){
        List<SysMenu> list = sysMenuService.tree();
        return Result.succ(list);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('sys:menu:save')")
    public Result save(@Validated @RequestBody SysMenu sysMenu){
        sysMenu.setCreated(LocalDateTime.now());

        sysMenuService.save(sysMenu);
        return Result.succ(sysMenu);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority('sys:menu:update')")
    public Result updarte(@Validated @RequestBody SysMenu sysMenu){
        sysMenu.setUpdated(LocalDateTime.now());
        sysMenuService.updateById(sysMenu);

        //清除所有与该菜单相关的权限缓存
        sysUserService.clearUserAuthorityInfoBymenuId(sysMenu.getId());
        return Result.succ(sysMenu);
    }

    @PostMapping("delete/{id}")
    @PreAuthorize("hasAnyAuthority('sys:menu:delete')")
    public Result delete(@PathVariable("id")Long id){
        int count = sysMenuService.count(new QueryWrapper<SysMenu>().eq("parent_id", id));
        if(count>0){
            return Result.fail("请先删除子菜单");
        }
        //清除所有与该菜单相关的权限缓存
        sysUserService.clearUserAuthorityInfoBymenuId(id);

        sysUserService.removeById(id);
        //同步删除中间表
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("menu_id",id));
        return Result.succ("删除成功");
    }
}
