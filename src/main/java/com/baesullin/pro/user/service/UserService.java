package com.baesullin.pro.user.service;

import com.baesullin.pro.exception.CustomException;
import com.baesullin.pro.exception.ErrorCode;
import com.baesullin.pro.login.jwt.AuthTokenProvider;
import com.baesullin.pro.login.jwt.repository.UserRefreshTokenRepository;
import com.baesullin.pro.user.domain.User;
import com.baesullin.pro.user.dto.UserResponseDto;
import com.baesullin.pro.user.repository.UserRepository;
import com.baesullin.pro.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final AuthTokenProvider tokenProvider;

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.getCookie(request, "refresh_token")
                .map(Cookie::getValue)
                .orElse((null));

        if (refreshToken == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_EXIST);
        }

        // Cookie에 담겨있는 refresh token 삭제
        CookieUtil.deleteCookie(request, response, "refresh_token");

        // DB에 저장되어 있는 refresh token 삭제
        userRefreshTokenRepository.deleteByRefreshToken(refreshToken);
    }
    @Cacheable(value="user", key="#socialId", cacheManager = "cacheManager")
    public UserResponseDto getUserInfo(String socialId) {
        User targetUser = userRepository.findBySocialId(socialId);
        return new UserResponseDto(targetUser);
    }
}

