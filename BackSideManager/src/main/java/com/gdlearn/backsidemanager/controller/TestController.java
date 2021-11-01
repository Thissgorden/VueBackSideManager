package com.gdlearn.backsidemanager.controller;


import com.gdlearn.backsidemanager.common.Result;
import com.gdlearn.backsidemanager.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    ISysUserService userService;

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/test")
    public Result test(){
        return Result.succ(userService.list());
    }

    @GetMapping("/test/pass")
    public Result pass(){
        //加密后的密码
        String password = bCryptPasswordEncoder.encode("11111");

        boolean matches = bCryptPasswordEncoder.matches("11111", password);

        System.out.println("密码校验结果:"+matches+"||||"+password);

        return Result.succ(password);
    }
}
