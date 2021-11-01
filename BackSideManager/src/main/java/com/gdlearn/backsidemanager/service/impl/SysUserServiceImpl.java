package com.gdlearn.backsidemanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gdlearn.backsidemanager.entity.SysMenu;
import com.gdlearn.backsidemanager.entity.SysRole;
import com.gdlearn.backsidemanager.entity.SysUser;
import com.gdlearn.backsidemanager.mapper.SysUserMapper;
import com.gdlearn.backsidemanager.service.ISysMenuService;
import com.gdlearn.backsidemanager.service.ISysRoleService;
import com.gdlearn.backsidemanager.service.ISysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdlearn.backsidemanager.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jobob
 * @since 2021-10-27
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Autowired
    ISysRoleService sysRoleService;
    @Autowired
    SysUserMapper sysUserMapper;
    @Autowired
    ISysMenuService sysMenuService;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public SysUser getByUsername(String username) {

        return getOne(new QueryWrapper<SysUser>().eq("username", username));
    }

    @Override
    public String getUserAuthorityInfo(Long userId) {

        String authority = "";
        SysUser sysUser = sysUserMapper.selectById(userId);
/*
        if(redisUtil.hasKey("GrantedAuthority:" + sysUser.getUsername())){
            String o = (String) redisUtil.get("GrantedAuthority:" + sysUser.getUsername());
            return o;
        }
*/
        //获取角色
        List<SysRole> roles = sysRoleService.list(
                new QueryWrapper<SysRole>()
                        .inSql("id", "select role_id from sys_user_role where user_id =" + userId));
        if(roles.size()>0){
            authority = roles.stream().map(r -> "ROLE_" + r.getCode()).collect(Collectors.joining(","));
        }
        //获取菜单
        List<Long> menuIds = sysUserMapper.getNavMenuIdsd(userId);
        if(menuIds.size()>0){
            List<SysMenu> sysMenus = sysMenuService.listByIds(menuIds);
            String menuperms = sysMenus.stream().map(m -> m.getPerms()).collect(Collectors.joining(","));
            authority = authority.concat(",").concat(menuperms);
        }

        //查询完数据库后存入redis 缓存60*60秒（一小时）
        //redisUtil.set("GrantedAuthority:" + sysUser.getUsername(),authority,60*60);

        return authority;
    }

    @Override
    public void clearUserAuthorityInfo(String username) {
        //通过用户名删除缓存
        redisUtil.del("GrantedAuthority:" +username);
    }

    @Override
    public void clearUserAuthorityInfoByroleId(Long roleId) {
        //首先根据roleId查询哪些sysUser有相应的角色，需要查询中间表，查询出来后进行遍历，所有有关的sysUser的缓存都会根据用户名被清除
        List<SysUser> sysUsers = this.list(new QueryWrapper<SysUser>()
                .inSql("id","select user_id from sys_user_role where role_id ="+ roleId));
        sysUsers.forEach(u ->{
            this.clearUserAuthorityInfo(u.getUsername());
        });
    }

    @Override
    public void clearUserAuthorityInfoBymenuId(Long menuId) {
        List<SysUser> sysUsers = sysUserMapper.listByMenuId(menuId);
        sysUsers.forEach(u ->{
            this.clearUserAuthorityInfo(u.getUsername());
        });
    }

}
