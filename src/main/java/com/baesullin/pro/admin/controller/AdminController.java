package com.baesullin.pro.admin.controller;

import com.baesullin.pro.admin.dto.AdminResponseDto;
import com.baesullin.pro.admin.service.AdminService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @ApiOperation(value = "유저가 등록한 업장을 관리자 페이지에서 조회하는 메소드")
    @GetMapping("/approval")
    public List<AdminResponseDto> getUserRegisterStore() {
        return adminService.getUserRegisterStore();
    }
}
