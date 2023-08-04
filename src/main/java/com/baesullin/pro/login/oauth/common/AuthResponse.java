package com.baesullin.pro.login.oauth.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class AuthResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status = 200;
    private final String code = "S-SUC200";
    private final String result = "SUCCESS";
    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }
}
