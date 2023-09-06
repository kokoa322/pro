package com.baesullin.pro.config.batch.util;

import com.baesullin.pro.api.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class ApiUpdate {
    private final LocationService locationService;


    private HttpHeaders setHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setAccept(List.of(MediaType.APPLICATION_XML));
        return headers;
    }



    /**
     * @param siDoNm 데이터를 가져올 시(지역)
     * @param cggNm  데이터를 가져올 구(지역)
     * @param pageNo 데이터 가져올 페이지
     */


    /**
     * @param formResult 공공 API 결과에서 각각의 Row
     */


}
