package com.gdlearn.backsidemanager.security;

import com.gdlearn.backsidemanager.entity.SysUser;
import com.gdlearn.backsidemanager.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    ISysUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        SysUser sysUser = userService.getByUsername(username);
        if(sysUser == null){
            throw new UsernameNotFoundException("用户名或密码不正确");
        }

        return new AccountUser(
                sysUser.getId(),
                sysUser.getUsername(),
                sysUser.getPassword(),
                getUserAuthority(sysUser.getId())
        );
    }

    /**
     * 获取用户都得权限信息
     * @param userId
     * @return
     */
    public List<GrantedAuthority> getUserAuthority(Long userId){

        //获取角色，菜单权限
        String authority = userService.getUserAuthorityInfo(userId);//Role_admin,Role_normal:user:list

        return AuthorityUtils.commaSeparatedStringToAuthorityList(authority);
    }
}
