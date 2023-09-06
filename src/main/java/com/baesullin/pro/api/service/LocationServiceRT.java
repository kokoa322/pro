package com.baesullin.pro.api.service;

import com.baesullin.pro.api.dto.LocationInfoDto;
import com.baesullin.pro.api.dto.LocationPartDto;
import com.baesullin.pro.api.model.LocationAddressSearchForm;
import com.baesullin.pro.api.model.LocationKeywordSearchForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.baesullin.pro.common.DataClarification.categoryFilter;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LocationServiceRT implements LocationService {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;


    /**
     * @param address 주소
     * @return 위도, 경도, Status를 가지는 Map 반환
     * 주소 -> 위도/경도 이므로 <<1개 반환>>
     * Controller, Service에서 같은 Map 형태로 사용
     */
    public LocationPartDto.LatLong convertAddressToGeo(String address) {
        // status, latitude, longitude 를 키로 가지는 HashMap 생성
        LocationKeywordSearchForm locationKeywordSearchForm = getLatLngByAddress(address);
        return LocationPartDto.LatLong.convertPart(locationKeywordSearchForm);
    }

    /**
     * @param address 변환할 주소
     * @return RestTemplate를 이용해 변환한 위도, 경도
     * 위도/경도 -> 주소 이므로 <<1개 반환>>
     */
    private LocationKeywordSearchForm getLatLngByAddress(String address) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", kakaoApiKey);
        URI uri = UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com/v2/local/search/keyword.json")
                .queryParam("query", address)
                .queryParam("page", 1)
                .queryParam("size", 1)
                .encode()
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<LocationKeywordSearchForm> resultRe = restTemplate.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(headers), LocationKeywordSearchForm.class
        );
        return resultRe.getBody();
    }
    // 주소 넣는 것으로 바꾸기
    // 주소 넣는 것으로 바꾸기

    /**
     * @param lat     위도
     * @param lng     경도
     * @param keyword 검색 키워드
     * @return 음식점, 카페 검색을 통해 얻은 결과 - V1, 1개의 결과만을 반환
     */
    private LocationKeywordSearchForm getCategoryByLatLngKeyword(String lat, String lng, String keyword) {
        LocationKeywordSearchForm searchFormResult = getCategoryByCode(lat, lng, keyword, "FD6", 1);
        if (searchFormResult == null) {
            searchFormResult = getCategoryByCode(lat, lng, keyword, "CE7", 1);
        }
        return searchFormResult;
    }

    /**
     * @param lat       위도
     * @param lng       경도
     * @param storeName 업장명
     * @param cateCode  카테고리 코드
     * @return 위도, 경도, 업장명, 카테고리 코드 조건에 맞는 정보를 리턴
     */
    private LocationKeywordSearchForm getCategoryByCode(String lat, String lng, String storeName, String cateCode, int page) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", kakaoApiKey);
        URI uri = UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com/v2/local/search/keyword.json")
                .queryParam("query", storeName)
                .queryParam("x", lng)//위도, 경도 지정
                .queryParam("y", lat)
                .queryParam("category_group_code", cateCode) // 카테고리 그룹을 설정
                .queryParam("radius", 20)
                .queryParam("page", page)
                .queryParam("size", 15)
                .encode()
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<LocationKeywordSearchForm> resultRe = restTemplate.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(headers), LocationKeywordSearchForm.class
        );
        return resultRe.getBody();
    }

    /**
     * @param lat       검색할 위도
     * @param lng       검색할 경도
     * @param storeName 검색할 업장명
     * @return 위도, 경도, 업장명을 통해 업장의 정보 반환
     */
    @Override
    public LocationInfoDto.LocationResponse convertGeoAndStoreNameToKeyword(String lat, String lng, String storeName) {
        LocationKeywordSearchForm locationKeywordSearchForm = getCategoryByLatLngKeyword(lat, lng, storeName);
        // 위도, 경도, 업장명을 가지고 업장 정보를 찾는다
        return LocationInfoDto.LocationResponse.KeywordToRes(locationKeywordSearchForm);
    }

    /**
     * @param lat     위도
     * @param lng     경도
     * @param address 주소
     * @return 업장명 대신에 주소를 입력해 해당 건물에 있는 업장을 배리어 프리 시설로 등록한다
     */

    @Override
    public List<LocationInfoDto.LocationResponse> convertGeoAndAddressToKeyword(String lat, String lng, String address) {
        List<LocationInfoDto.LocationResponse> resultList = new ArrayList<>();
        getStoreResults(lat, lng, address, "FD6", resultList);
        getStoreResults(lat, lng, address, "CE7", resultList);
        return resultList;
    }

    /**
     * @param lat        위도
     * @param lng        경도
     * @param address    주소
     * @param type       검색 타입
     * @param resultList 검색 결과
     */
    private void getStoreResults(String lat, String lng, String address, String type, List<LocationInfoDto.LocationResponse> resultList) {
        LocationKeywordSearchForm locationKeywordSearchForm;
        int page = 1;
        do {
            locationKeywordSearchForm = getCategoryByCode(lat, lng, address, type, page++);
            // 위도, 경도, 업장명을 가지고 업장 정보를 찾는다
            if (locationKeywordSearchForm == null) {
                return;
            }
            LocationKeywordSearchForm.Documents[] latLngDocArr = locationKeywordSearchForm.getDocuments();
            // 다음 페이지가 있는지 조사가 필요 - SearchForm에서 확인한다
            for (LocationKeywordSearchForm.Documents latLngDoc : latLngDocArr) {
                if (latLngDoc != null) {
                    LocationInfoDto.LocationResponse newResult = LocationInfoDto.LocationResponse.builder()
                            .latitude(latLngDoc.getY())
                            .longitude(latLngDoc.getX())
                            .category(categoryFilter(latLngDoc.getCategory_name()))
                            .storeName(latLngDoc.getPlace_name())
                            .storeId(Long.parseLong(latLngDoc.getId()))
                            .phoneNumber(latLngDoc.getPhone())
                            .build();
                    if (newResult.validate()) {
                        resultList.add(newResult);
                    }
                }
            }
        } while (locationKeywordSearchForm.getMeta().is_end()); // 마지막 페이지까지 검사
    }

    /**
     * @param lat 위도
     * @param lng 경도
     * @return 위도, 경도를 카카오맵 API(RestTemplate)를 통해 주소로 변환 후 DTO로 반환
     */
    public LocationPartDto.Address convertGeoToAddress(String lat, String lng) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", kakaoApiKey);
        URI uri = UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com/v2/local/geo/coord2address.json")
                .queryParam("x", lng)//위도, 경도 지정
                .queryParam("y", lat)
                .encode()
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<LocationAddressSearchForm> resultRe = restTemplate.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(headers), LocationAddressSearchForm.class
        );
        LocationAddressSearchForm locationKeywordSearchForm = resultRe.getBody();
        return LocationPartDto.Address.formToDto(locationKeywordSearchForm);
    }
}
