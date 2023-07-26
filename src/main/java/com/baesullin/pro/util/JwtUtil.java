package com.baesullin.pro.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {


    //userName 호출 메소드
    public static String getUserName(String token, String secretKey) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("userName", String.class);
    }


    //만료 확인 메소드
    public static boolean isExpired(String token, String secretKey){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }

    public static String createJwt(String userName, String secretKey, Long expiredMs){
        //Claims 내가 원하는 정보를 담는 공간 일종의 map형태
        Claims claims = Jwts.claims();
        claims.put("userName", userName);

        return Jwts.builder()
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}
