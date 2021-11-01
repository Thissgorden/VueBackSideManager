package com.gdlearn.backsidemanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gdlearn.backsidemanager.entity.SysRole;
import com.gdlearn.backsidemanager.mapper.SysRoleMapper;
import com.gdlearn.backsidemanager.service.ISysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jobob
 * @since 2021-10-27
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {
    @Override
    public List<SysRole> listRoleByUserId(Long id) {
        List<SysRole> reslist = this.list(new QueryWrapper<SysRole>()
                .inSql("id", "select role_id from sys_user_role where user_id = " + id));
        return reslist;
    }
}
