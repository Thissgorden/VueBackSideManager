package com.gdlearn.backsidemanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gdlearn.backsidemanager.dto.SysMenuDto;
import com.gdlearn.backsidemanager.entity.SysMenu;
import com.gdlearn.backsidemanager.entity.SysUser;
import com.gdlearn.backsidemanager.mapper.SysMenuMapper;
import com.gdlearn.backsidemanager.mapper.SysUserMapper;
import com.gdlearn.backsidemanager.service.ISysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdlearn.backsidemanager.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jobob
 * @since 2021-10-27
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    @Autowired
    ISysUserService iSysUserService;
    @Autowired
    SysUserMapper sysUserMapper;

    @Override
    public List<SysMenuDto> getCurrentUserNav() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        SysUser sysUser = iSysUserService.getByUsername(username);
        List<Long> menuIdsd = sysUserMapper.getNavMenuIdsd(sysUser.getId());
        List<SysMenu> sysMenus = this.listByIds(menuIdsd);

        //转换为树状结构
        List<SysMenu> menuTree = buildTreeMenu(sysMenus);

        //实体转DTO
        return convert(menuTree);
    }

    @Override
    public List<SysMenu> tree() {
        List<SysMenu> menus = this.list(new QueryWrapper<SysMenu>().orderByAsc("orderNum"));


        return buildTreeMenuAll(menus);
    }

    private List<SysMenuDto> convert(List<SysMenu> menuTree) {
        List<SysMenuDto> menuDtos = new ArrayList<>();
        menuTree.forEach(m -> {
            SysMenuDto dto = new SysMenuDto();

            dto.setId(m.getId());
            dto.setName(m.getPerms());
            dto.setTitle(m.getName());
            dto.setIcon(m.getIcon());
            dto.setPath(m.getPath());
            dto.setComponent(m.getComponent());
            if (m.getChildren().size() > 0) {
                //子节点调用当前方法进行再次转换
                dto.setChildren(convert(m.getChildren()));
            }
            menuDtos.add(dto);
        });

        return menuDtos;
    }
    //只获取有实际path可读取的数据
    private List<SysMenu> buildTreeMenu(List<SysMenu> sysMenus) {
        List<SysMenu> finalmenus = new ArrayList<>();

        //找到各自的子级
        for (SysMenu menu : sysMenus) {
            /*for (SysMenu e : sysMenus) {
                if (menu.getId().equals(e.getParentId())) {//子父节点ID相同
                    menu.getChildren().add(e);
                }
            }

             */

            if (menu.getParentId() == 0L) {//没有父节点说明是底节点
                finalmenus.add(menu);
            }
        }
        for (SysMenu for2 : finalmenus) {
            for (SysMenu for2c : sysMenus) {
                if(for2c.getPath() != null && for2c.getParentId()!=0L && for2.getId().equals(for2c.getParentId())){
                    for2.getChildren().add(for2c);
                }
            }
        }

        return finalmenus;
    }

    //获取Menu中所有的数据
    private List<SysMenu> buildTreeMenuAll(List<SysMenu> sysMenus) {
        List<SysMenu> finalmenus = new ArrayList<>();

        //找到各自的子级
        for (SysMenu menu : sysMenus) {
            for (SysMenu e : sysMenus) {
                if (menu.getId().equals(e.getParentId())) {//子父节点ID相同
                    menu.getChildren().add(e);
                }
            }
            if (menu.getParentId() == 0L) {//没有父节点说明是底节点
                finalmenus.add(menu);
            }
        }
        /*
        for (SysMenu for2 : finalmenus) {
            for (SysMenu for2c : sysMenus) {
                if(for2c.getPath() != null && for2c.getParentId()!=0L && for2.getId().equals(for2c.getParentId())){
                    for2.getChildren().add(for2c);
                }
            }
        }
        */
        return finalmenus;
    }
}
