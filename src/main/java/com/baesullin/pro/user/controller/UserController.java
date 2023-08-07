package com.baesullin.pro.user.controller;

import com.baesullin.pro.common.SuccessResponse;
import com.baesullin.pro.user.dto.UserResponseDto;
import com.baesullin.pro.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ApiOperation(value = "로그아웃")
    @RequestMapping("/logout")
    public SuccessResponse logout(
            HttpServletRequest request,
            HttpServletResponse response
            ) {
        userService.logout(request, response);

        return new SuccessResponse("로그아웃");
    }

    @GetMapping
    @ApiOperation(value = "유저 정보를 반환합니다")
    public UserResponseDto getUserInfo(@AuthenticationPrincipal User user) {
        return userService.getUserInfo(user.getUsername());
    }
}
