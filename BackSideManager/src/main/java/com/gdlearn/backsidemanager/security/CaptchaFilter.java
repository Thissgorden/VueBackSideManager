package com.gdlearn.backsidemanager.security;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.gdlearn.backsidemanager.exception.CaptchaException;
import com.gdlearn.backsidemanager.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CaptchaFilter extends OncePerRequestFilter {

    @Autowired
    LoginFailureHandler loginFailureHandler;

    @Autowired
    RedisUtil redisUtil;

    /**
     * 校验验证码的过滤器
     * @param httpServletRequest
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = httpServletRequest.getRequestURI();
        //是否是/login登陆请求 以及是否为post方法
        if ("/login".equals(uri) && httpServletRequest.getMethod().equals("POST")) {
            //校验验证码
            try {
                validate(httpServletRequest);
            } catch (AuthenticationException e) {

                //如果不正确则跳转至失败处理器
                loginFailureHandler.onAuthenticationFailure(httpServletRequest, response, e);
            }
        }
        filterChain.doFilter(httpServletRequest,response);
    }

    //校验验证码的逻辑
    private void validate(HttpServletRequest httpServletRequest) throws AuthenticationException {
        String code = httpServletRequest.getParameter("code");
        String key = httpServletRequest.getParameter("key");

        if (StringUtils.isBlank(code) || StringUtils.isBlank(key)) {
            throw new CaptchaException("验证码错误");
        }
        if (!code.equals(redisUtil.hget("captcha", key))) {
            throw new CaptchaException("验证码错误");
        }
        //一次性使用
        redisUtil.hdel("captcha", key);
    }
}
