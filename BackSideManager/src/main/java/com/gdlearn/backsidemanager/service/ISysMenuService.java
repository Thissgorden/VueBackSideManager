package com.gdlearn.backsidemanager.service;

import com.gdlearn.backsidemanager.dto.SysMenuDto;
import com.gdlearn.backsidemanager.entity.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2021-10-27
 */
public interface ISysMenuService extends IService<SysMenu> {

    List<SysMenuDto> getCurrentUserNav();

    List<SysMenu> tree();


}
