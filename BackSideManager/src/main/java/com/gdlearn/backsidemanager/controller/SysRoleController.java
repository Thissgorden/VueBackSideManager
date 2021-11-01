package com.gdlearn.backsidemanager.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gdlearn.backsidemanager.common.Result;
import com.gdlearn.backsidemanager.entity.SysRole;
import com.gdlearn.backsidemanager.entity.SysRoleMenu;
import com.gdlearn.backsidemanager.entity.SysUserRole;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jobob
 * @since 2021-10-27
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends BaseController {

    @PreAuthorize("hasAuthority('sys:role:list')")
    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id){
        SysRole sysRole = sysRoleService.getById(id);

        //获取角色相关联的菜单id
        List<SysRoleMenu> rolemenus = sysRoleMenuService.list(new QueryWrapper<SysRoleMenu>().eq("role_id", id));
        List<Long> menuIds = rolemenus.stream().map(p -> p.getMenuId()).collect(Collectors.toList());

        sysRole.setMenuIds(menuIds);

        return Result.succ(sysRole);
    }

    @PreAuthorize("hasAuthority('sys:role:list')")
    @GetMapping("/list")
    public Result list(String name){
        //数据需要进行分页再返回
        Page<SysRole> pageData = sysRoleService.page(getPage(),
                new QueryWrapper<SysRole>()
                        .like(StrUtil.isNotBlank(name), "name", name));
                      //↑这里是like不是eq
        return Result.succ(pageData);
    }
    @PreAuthorize("hasAuthority('sys:role:save')")
    @PostMapping("/save")
    public Result save(@Validated @RequestBody SysRole sysRole){
        sysRole.setCreated(LocalDateTime.now());
        sysRole.setStatu(1);
        sysRoleService.save(sysRole);
        //更新redis缓存
        sysUserService.clearUserAuthorityInfoByroleId(sysRole.getId());

        return Result.succ(sysRole);
    }
    @PreAuthorize("hasAuthority('sys:role:update')")
    @PostMapping("/update")
    public Result update(@Validated @RequestBody SysRole sysRole){
        sysRole.setUpdated(LocalDateTime.now());

        sysRoleService.updateById(sysRole);

        sysUserService.clearUserAuthorityInfoByroleId(sysRole.getId());

        return Result.succ(sysRole);
    }
    @PreAuthorize("hasAuthority('sys:role:delete')")
    @PostMapping("/delete")
    @Transactional//事务，要么执行完毕要么回退
    public Result delete(@RequestBody Long[] Ids){

        sysRoleService.removeByIds(Arrays.asList(Ids));

        Arrays.stream(Ids).forEach(id ->{
            //清除相关缓存
            sysUserService.clearUserAuthorityInfoByroleId(id);
        });
        //删除中间表的数据
        sysUserRoleService.remove(
                new QueryWrapper<SysUserRole>().in("role_id",Ids)
        );
        sysRoleMenuService.remove(
                new QueryWrapper<SysRoleMenu>().in("role_id",Ids)
        );

        return Result.succ("");
    }

    /**
     * 分配权限接口
     * @param roleId 要分配给谁权限
     * @param menuIds 要分配的权限
     */
    @PreAuthorize("hasAuthority('sys:role:perm')")
    @PostMapping("/perm/{roleId}")
    @Transactional
    public Result perm(@PathVariable("roleId") Long roleId, @RequestBody Long[] menuIds){
        List<SysRoleMenu> sysRoleMenus = new ArrayList<>();

        Arrays.stream(menuIds).forEach(menuId->{
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(roleId);
            sysRoleMenus.add(roleMenu);
        });
        //先删除中间表原有的权限数据 后添加
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("role_id",roleId));
        sysRoleMenuService.saveBatch(sysRoleMenus);

        sysUserService.clearUserAuthorityInfoByroleId(roleId);

        return Result.succ("");
    }
}
