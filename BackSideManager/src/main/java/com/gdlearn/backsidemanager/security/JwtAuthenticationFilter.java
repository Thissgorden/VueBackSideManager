package com.gdlearn.backsidemanager.security;

import cn.hutool.core.util.StrUtil;
import com.gdlearn.backsidemanager.entity.SysUser;
import com.gdlearn.backsidemanager.service.ISysUserService;
import com.gdlearn.backsidemanager.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter{

    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    ISysUserService sysUserService;

    //重写构造方法
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    //重写过滤的流程
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String jwt = request.getHeader(jwtUtils.getHeader());
        if(StrUtil.isBlankOrUndefined(jwt)){
            chain.doFilter(request,response);//如果JWT为空则直接放行
            return;
        }

        Claims claimByToken = jwtUtils.getClaimByToken(jwt);
        if(claimByToken == null){
            throw new JwtException("token异常");
        }
        if(jwtUtils.isTokenExpired(claimByToken)){
            throw new JwtException("token过期");
        }



        //没有进入到if里，JWT合法
        String username = claimByToken.getSubject();

        SysUser sysUser = sysUserService.getByUsername(username);
        //获取用户的权限信息
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,null,userDetailsService.getUserAuthority(sysUser.getId()));
        //交由Security上下文自动完成登陆的过程
        SecurityContextHolder.getContext().setAuthentication(token);

        chain.doFilter(request,response);
    }
}
