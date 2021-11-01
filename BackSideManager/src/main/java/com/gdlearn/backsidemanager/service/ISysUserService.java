package com.gdlearn.backsidemanager.service;

import com.gdlearn.backsidemanager.entity.SysRole;
import com.gdlearn.backsidemanager.entity.SysUser;
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
public interface ISysUserService extends IService<SysUser> {

    SysUser getByUsername(String username);

    String getUserAuthorityInfo(Long userId);

    void clearUserAuthorityInfo(String username);

    void clearUserAuthorityInfoByroleId(Long roleId);

    void clearUserAuthorityInfoBymenuId(Long menuId);

}
