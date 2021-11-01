package com.gdlearn.backsidemanager.service;

import com.gdlearn.backsidemanager.entity.SysRole;
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
public interface ISysRoleService extends IService<SysRole> {

    List<SysRole> listRoleByUserId(Long id);
}
