package com.baesullin.pro.api.controller;

import com.baesullin.pro.api.dto.PublicApiRequestDto;
import com.baesullin.pro.api.service.PublicApiService;
import com.baesullin.pro.api.service.PublicApiServiceV2;
import com.baesullin.pro.common.SuccessResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@ApiOperation(value="공공 API에 접근해 정보를 받아오는 컨트롤러")
public class PublicApiController {
    private final PublicApiService publicApiService;
    private final PublicApiServiceV2 publicApiServiceV2;
    @ApiOperation(value="공공 API V1을 통해 DB에 정보를 저장/업데이트하는 함수")
    @PostMapping("/api")
    public SuccessResponse getPublicApi(@Valid @RequestBody PublicApiRequestDto publicApiRequestDto) {
//        publicApiService.processApiV1(publicApiRequestDto);
        return new SuccessResponse("공공 API V1 적용 완료");
    }

    @ApiOperation(value="공공 API V2을 통해 DB에 정보를 저장/업데이트하는 함수")
    @GetMapping("/new-api")
    public SuccessResponse getPublicApiV2(@RequestParam String siDoNm,
                                          @RequestParam String cggNm){
//        publicApiServiceV2.processApi(siDoNm, cggNm, 1);
        return new SuccessResponse("공공 API V2 적용 완료");
    }

    @GetMapping("/api-read")
    public void go(@RequestParam String serviceKey){
//        publicApiServiceV2.start();
    }
}
