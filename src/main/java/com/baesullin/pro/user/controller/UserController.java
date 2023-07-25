package com.baesullin.pro.user.controller;

import com.baesullin.pro.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> login(String username, String password){
        return ResponseEntity.ok().body(userService.login(username,password));
    }


}
