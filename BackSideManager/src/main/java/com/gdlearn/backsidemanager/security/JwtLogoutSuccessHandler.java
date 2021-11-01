package com.gdlearn.backsidemanager.security;

import cn.hutool.json.JSONUtil;
import com.gdlearn.backsidemanager.common.Result;
import com.gdlearn.backsidemanager.utils.JwtUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
@Component
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        if(authentication!=null){
            //手动退出
            new SecurityContextLogoutHandler().logout(request,response,authentication);
        }

        response.setContentType("application/json,charset=utf-8");
        ServletOutputStream outputStream = response.getOutputStream();

        //清除客户端的jwt
        response.setHeader("Authorization"," ");

        Result result = Result.succ("登出成功");

        //@return
        outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
