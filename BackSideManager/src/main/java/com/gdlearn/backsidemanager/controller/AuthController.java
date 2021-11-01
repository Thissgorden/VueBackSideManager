package com.gdlearn.backsidemanager.controller;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.map.MapUtil;
import com.gdlearn.backsidemanager.common.Result;
import com.gdlearn.backsidemanager.entity.SysUser;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class AuthController extends BaseController {

    @Autowired
    Producer producer;


    @GetMapping("/captcha")
    public Result getCaptcha() throws IOException {

        String key = UUID.randomUUID().toString();
        String code = producer.createText();
        key="55555";
        code="55555";

        BufferedImage image = producer.createImage(code);
        //字节输出流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);

        //前缀
        String str = "data:image/jpeg;base64,";

        String base64Img = str + Base64Encoder.encode(outputStream.toByteArray());
        redisUtil.hset("captcha", key, code, 120);

        return Result.succ(
                MapUtil.builder()
                        .put("key", key)
                        .put("base64Img", base64Img)
                        .build()
        );
    }

    @GetMapping("/sys/userInfo")
    public Result userInfo(Principal principal){

        SysUser usr = sysUserService.getByUsername(principal.getName());
        Map<String,String> usrInfo = new HashMap<>();
        usrInfo.put("id",usr.getId()+"");
        usrInfo.put("username",usr.getUsername());
        usrInfo.put("avatar",usr.getAvatar());

        return Result.succ(usrInfo);
    }
}
