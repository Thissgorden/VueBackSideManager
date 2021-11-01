package com.gdlearn.backsidemanager.utils;

import io.jsonwebtoken.*;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

//JWT工具类
@Data
@Component
@ConfigurationProperties(prefix = "gdlearn.jwt")
public class JwtUtils {
    //通过配置文件来设置的可自定义过期时间
    private long expire;
    //秘钥
    private String secret;
    //jwt名称
    private String header;

    //生成JWT
    public String generateToken(String username){
        Date nowDate = new Date();
        Date expireDate = new Date(nowDate.getTime() + expire*1000);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(username)
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)// 7天過期
                .signWith(SignatureAlgorithm.HS512, "114514".getBytes(StandardCharsets.UTF_8))
                .compact();
    }
    //解析JWT
    public Claims getClaimByToken(String jwt){
        //由于JWT可能会被篡改，所以使用try catch防止篡改解析报错
        try {
            return Jwts.parser()
                    .setSigningKey("114514".getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (Exception e) {
            return null;//报错则直接返回空继续执行
        }
    }
    //jwt是否过期
    public Boolean isTokenExpired(Claims claims){
        return claims.getExpiration().before(new Date());
    }
}
