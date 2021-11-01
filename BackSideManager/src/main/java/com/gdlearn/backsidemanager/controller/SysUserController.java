package com.gdlearn.backsidemanager.controller;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gdlearn.backsidemanager.common.Const;
import com.gdlearn.backsidemanager.common.Result;
import com.gdlearn.backsidemanager.dto.PassDto;
import com.gdlearn.backsidemanager.entity.SysRole;
import com.gdlearn.backsidemanager.entity.SysUser;
import com.gdlearn.backsidemanager.entity.SysUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
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
@RequestMapping("/sys/user/")
public class SysUserController extends BaseController {

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @PreAuthorize("hasAuthority('sys:user:list')")
    @GetMapping("info/{id}")
    public Result info(@PathVariable Long id){

        SysUser byId = sysUserService.getById(id);
        Assert.notNull(byId,"找不到该角色");

        List<SysRole> roles = sysRoleService.listRoleByUserId(id);
        byId.setSysRoles(roles);//设置用户相关的角色信息

        return Result.succ(byId);

    }

    @PreAuthorize("hasAuthority('sys:user:list')")
    @GetMapping("list")
    public Result list(String username){
        Page<SysUser> pagedata =sysUserService.page(getPage(),new QueryWrapper<SysUser>()
                .like(StrUtil.isNotBlank(username),"username",username));
        pagedata.getRecords().forEach(u->{
            u.setSysRoles(sysRoleService.listRoleByUserId(u.getId()));
        });
        return Result.succ(pagedata);
    }
    @PreAuthorize("hasAuthority('sys:user:save')")
    @PostMapping("save")
    public Result save(@Validated @RequestBody SysUser sysUser){
        sysUser.setCreated(LocalDateTime.now());
        sysUser.setStatu(1);
        String BCpassword = passwordEncoder.encode(Const.DEFAULT_PASSWORD);
        sysUser.setPassword(BCpassword);
        sysUser.setAvatar(Const.DEFAULT_AVATAR);
        sysUserService.save(sysUser);

        return Result.succ(sysUser);

    }
    @PreAuthorize("hasAuthority('sys:user:update')")
    @PostMapping("update")
    public Result update(@PathVariable @RequestBody SysUser sysUser){
        sysUser.setUpdated(LocalDateTime.now());

        sysUserService.updateById(sysUser);

        return Result.succ(sysUser);
    }
    @PreAuthorize("hasAuthority('sys:user:delete')")
    @PostMapping("delete")
    @Transactional
    public Result delete(@RequestBody Long[] ids){
        sysUserService.removeByIds(Arrays.asList(ids));
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("user_id",ids));

        return Result.succ("");
    }

    /**
     *
     * @param userId 给谁分配角色
     * @param roleIds 分配什么角色
     */
    @PreAuthorize("hasAuthority('sys:user:role')")
    @PostMapping("role/{userId}")
    @Transactional
    public Result rolePerm(@PathVariable("userId") Long userId,@RequestBody Long[] roleIds){
        List<SysUserRole> userRoles = new ArrayList<>();

        Arrays.stream(roleIds).forEach(r ->{
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(r);
            sysUserRole.setUserId(userId);

            userRoles.add(sysUserRole);
        });
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().eq("user_id",userId));
        sysUserRoleService.saveBatch(userRoles);

        SysUser byId = sysUserService.getById(userId);
        sysUserService.clearUserAuthorityInfo(byId.getUsername());

        return Result.succ("");

    }
    @PreAuthorize("hasAuthority('sys:user:repass')")
    @PostMapping("repass")
    public Result repass(@RequestBody Long userId){

        SysUser sysUser = sysUserService.getById(userId);

        sysUser.setPassword(passwordEncoder.encode(Const.DEFAULT_PASSWORD));
        sysUser.setUpdated(LocalDateTime.now());

        sysUserService.updateById(sysUser);
        return Result.succ("");

    }


    @PostMapping("updatePass")
    public Result updatePass(@Validated @RequestBody PassDto passDto, Principal principal){
        if (!passDto.getNewPass().equals(passDto.getConfirmPass())){
            return Result.fail("确认密码不一致");
        }
        //获取当前用户
        SysUser usr = sysUserService.getByUsername(principal.getName());
        //对比旧密码是否正确
        boolean matches = passwordEncoder.matches(passDto.getCurrentPass(), usr.getPassword());
        if(matches){
            return Result.fail("旧密码错误");
        }

        usr.setPassword(passwordEncoder.encode(passDto.getNewPass()));
        usr.setUpdated(LocalDateTime.now());

        sysUserService.updateById(usr);

        return Result.succ("");
    }
}
