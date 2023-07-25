package com.baesullin.pro.user.service;

import com.baesullin.pro.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    @Value("${jwt.secret}")
    private String secretKey;

    private Long expiredMs = 1000 * 60 * 60l;
    public String login(String username, String password){
        //인증과정 생략
        return JwtUtil.createJwt(username, secretKey, expiredMs);
    }
}
