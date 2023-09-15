package com.baesullin.pro.config.batch.util;

import com.baesullin.pro.api.dto.LocationInfoDto;
import com.baesullin.pro.api.model.BarrierCode;
import com.baesullin.pro.api.model.LocationKeywordSearchForm;
import com.baesullin.pro.api.model.PublicApiCategoryForm;
import com.baesullin.pro.api.model.PublicApiV2Form;
import com.baesullin.pro.common.DataClarification;
import com.baesullin.pro.store.domain.Category;
import com.baesullin.pro.store.domain.Store;
import com.baesullin.pro.storeApiUpdate.StoreApiUpdate;
//import com.fasterxml.jackson.dataformat.xml.XmlMapper;
//import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.*;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class ApiUpdateThread extends Thread {

    private String               kakaoApiKey;
    private List<List<String>>   csvList;
    private List<StoreApiUpdate> storeApiUpdateList;
    private int                  pageNo;
    private String               publicKey;
    private int                  threadCount;


    public ApiUpdateThread(List<List<String>> csvList, List<StoreApiUpdate> storeApiUpdateList,int pageNo, String publicKey, String kakaoApiKey,int threadCount){
        this.kakaoApiKey        = kakaoApiKey;
        this.threadCount        = threadCount;
        this.publicKey          = publicKey;
        this.csvList            = csvList;
        this.pageNo             = pageNo;
        this.storeApiUpdateList = storeApiUpdateList;

    }

    @Override
    public void run(){processApi();}

    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        restTemplate.setUriTemplateHandler(uriBuilderFactory);

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(60000); // 연결 타임아웃 1분으로 설정
        factory.setReadTimeout(60000); // 읽기 타임아웃 1분으로 설정
        restTemplate.setRequestFactory(factory);

        return restTemplate;
    }
    public List<StoreApiUpdate> getList(){
        return storeApiUpdateList;
    }
    /**
     * String siDoNm 데이터를 가져올 시(지역)
     * String cggNm  데이터를 가져올 구(지역)
     * int pageNo 데이터 가져올 페이지
     */

    @Autowired
    public void processApi() {
        HttpHeaders headers;

        // 헤더 세팅
        for(List<String> csv: csvList){
            String siDoNm = csv.get(0);
            String cggNm  = csv.get(1);

            headers = setHttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);
            log.info("thread "+ threadCount +" --> "+"{} {}", siDoNm, cggNm);
            // URI 생성
            String publicV2Uri = "http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/getDisConvFaclList";

            UriComponents uri = UriComponentsBuilder
                    .fromHttpUrl(publicV2Uri)
                    .queryParam("siDoNm", siDoNm)
                    .queryParam("cggNm", cggNm)
                    .queryParam("numOfRows", "1000")
                    .queryParam("pageNo", String.valueOf(pageNo))
                    .queryParam("faclTyCd", "UC0B01")
                    .queryParam("serviceKey", publicKey)
                    .build();

            log.warn("thread "+ threadCount +" --> "+uri.toUriString());

            RestTemplate restTemplate = getRestTemplate();
            PublicApiV2Form result = new PublicApiV2Form();

            synchronized (this) {
                ResponseEntity<PublicApiV2Form> resultRe = restTemplate.exchange(
                        uri.toUriString(), HttpMethod.GET, entity, PublicApiV2Form.class
                );

                log.info("resultRe.getBody().getTotalCount() --> {}", resultRe.getBody().getTotalCount());
                log.info("resultRe.getBody().getResultCode() --> {}", resultRe.getBody().getResultCode());
                log.info("resultRe.getBody().getResultMessage() --> {}", resultRe.getBody().getResultMessage());
                log.info("resultRe.getBody().getServList().get(0).getFaclNm() --> {}", resultRe.getBody().getServList().get(0).getFaclNm());

                if (resultRe.getStatusCode().is2xxSuccessful()) {
                    result = resultRe.getBody();
                } else {
                    System.err.println("Error response: " + resultRe.getStatusCode());
                }

            }

            if (result == null){      // 결과 가 없으면 false 리턴
                log.info("result --> NULL");
                continue;
            }

           List<List<Store>> storeListList = processForm(result);
            // totalSize, 현 페이지를 통해 다음 페이지가 있는지 확인하고 T/F 리턴


            if (storeListList == null){      // 결과가 없으면 false 리턴
                log.info("result --> NULL");
                continue;
            }



          for(List<Store> storeList: storeListList) {
                for(Store store: storeList){
                    StoreApiUpdate storeApiUpdate = new StoreApiUpdate(store);
                    synchronized (this) {
                        storeApiUpdateList.add(storeApiUpdate);
                    }
                }
            }




            log.info("thread "+ threadCount +" : store SIZE --> "+ storeApiUpdateList.size());
        }
        // totalSize, 현 페이지를 통해 다음 페이지가 있는지 확인하고 T/F 리턴

    }



    public List<List<Store>> processForm(PublicApiV2Form formResult) {
        log.info("processForm -->");
        if (formResult == null || formResult.getServList() == null) return null;
        // servList + Barrier Free Tag  + category

        List<List<Store>> storeListList = new ArrayList<>();

        for (PublicApiV2Form.ServList servList : formResult.getServList()) {
            // servList 요소 - 각각의 배리어 프리 업장 하나하나를 검증
            if (!servList.validateServList()) continue;

            List<Store> storeList = mapApiToStoreWithPaging(servList);

            if (storeList == null) continue;

            storeListList.add(storeList);
        }
        // 검증 완료된 store들을 저장
        return storeListList;
    }


    /**
     * @return 헤더 세팅 - V2에서는 공통으로 XML 사용
     */
    private HttpHeaders setHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.APPLICATION_XML);
        //headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
        headers.set("Accept", "application/xml;charset=UTF-8");
        headers.setAccept(List.of(MediaType.APPLICATION_XML));
        return headers;
    }
    /**
     * @param servList V2의 결과 Row
     */
   private List<Store> mapApiToStoreWithPaging(PublicApiV2Form.ServList servList) {
       log.info("mapApiToStoreWithPaging -->");
        // 태그 String을 분리 & 매핑해 리스트에 저장
        List<String> barrierTagList = tagStrToList(servList.getWfcltId());

        // TODO 태그가 비어있다면 어떻게 해야 할 지 ? -> 저장 혹은 버리기 (현재 버리기로 구현)
        if (barrierTagList.isEmpty()) return null;

        /*
         * 주소 + 위/경도를 사용해 해당 건물의 배리어 프리 매장들을
         * 등록하도록 변경             */
//        if (searchWithStoreName(servList, barrierTagList)) return;
        // 검색 결과가 없을 경우


        return searchWithAddress(servList, barrierTagList);
    }



    /**
     * @param servList       대상 Row
     * @param barrierTagList 배리어 태그 리스트
     */

    @Transactional
    public List<Store> searchWithAddress(PublicApiV2Form.ServList servList, List<String> barrierTagList) {
        log.info("searchWithAddress -->");

        List<LocationInfoDto.LocationResponse> locationResponseMapList = convertGeoAndAddressToKeyword(servList.getFaclLat(), servList.getFaclLng(), DataClarification.clarifyString(servList.getLcMnad()));
        List<Store> storeList = new ArrayList<>();
        for (LocationInfoDto.LocationResponse locationResponse : locationResponseMapList) {
            Store nStore = new Store(locationResponse, servList, barrierTagList);

            storeList.add(nStore);
//            // ID 값으로 store 중복 검사해 중복되지 않을 시에만 리스트에 저장
//            if (!storeRepository.existsById(nStore.getId())) {
//                storeRepository.saveAndFlush(nStore);
//                storeImageService.saveImage(nStore.getId());
//            }
        }
        return storeList;
    }

    public List<LocationInfoDto.LocationResponse> convertGeoAndAddressToKeyword(String lat, String lng, String address) {
        log.info("convertGeoAndAddressToKeyword -->");
        List<LocationInfoDto.LocationResponse> resultList = new ArrayList<>();
        getStoreResults(lat, lng, address, "FD6", resultList);
        getStoreResults(lat, lng, address, "CE7", resultList);
        return resultList;
    }

    private void getStoreResults(String lat, String lng, String address, String type, List<LocationInfoDto.LocationResponse> resultList) {
        log.info("getStoreResults -->");
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

    private String categoryFilter(String category) {
        log.info("categoryFilter -->");
        if (category == null) {
            return Category.ETC.getDesc();
        } else if (category.contains(">")) {
            return Category.giveCategory(category.split(" > ")[1]).getDesc();
        } else {
            return null;
        }
    }


    private LocationKeywordSearchForm getCategoryByCode(String lat, String lng, String storeName, String cateCode, int page) {
        log.info("getCategoryByCode -->");
        log.info("Authorization --> {} ", kakaoApiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", kakaoApiKey);



        UriComponents uri = UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com/v2/local/search/keyword.json")
                .queryParam("query", storeName)
                .queryParam("x", lng)//위도, 경도 지정
                .queryParam("y", lat)
                .queryParam("category_group_code", cateCode) // 카테고리 그룹을 설정
                .queryParam("radius", 20)
                .queryParam("page", page)
                .queryParam("size", 15)
                .build();

        log.warn("thread "+ threadCount +" --> "+uri.toUriString());
        RestTemplate restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(60000); // 연결 타임아웃 1분으로 설정
        factory.setReadTimeout(60000); // 읽기 타임아웃 1분으로 설정
        restTemplate.setRequestFactory(factory);
        ResponseEntity<LocationKeywordSearchForm> resultRe = restTemplate.exchange(
                uri.toUriString(), HttpMethod.GET, new HttpEntity<>(headers), LocationKeywordSearchForm.class
        );
        return resultRe.getBody();
    }



    /**
     * @param sisulNum 시설 고유 번호
     * @return API 결과로 나온 문자열을 리스트로 분리
     */

    public List<String> tagStrToList(String sisulNum) {
        log.info("tagStrToList -->");
        HttpHeaders headers = setHttpHeaders();
        String publicV2CategoryUri = "http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/getFacInfoOpenApiJpEvalInfoList";

        UriComponents uri = UriComponentsBuilder
                .fromUriString(publicV2CategoryUri)
                .queryParam("serviceKey", publicKey)
                .queryParam("wfcltId", sisulNum)
                .build();

       RestTemplate restTemplate = getRestTemplate();

        log.warn("thread "+ threadCount +" --> "+uri.toUriString());
        ResponseEntity<PublicApiCategoryForm> resultRe = restTemplate.exchange(
                uri.toUriString(), HttpMethod.GET, new HttpEntity<>(headers), PublicApiCategoryForm.class
        );
        PublicApiCategoryForm result = resultRe.getBody();
        return mapTags(result);
    }

    /**
     * @param result API 결과로 나온 리스트
     * @return DB에 맞게 리스트를 변환
     */

    private List<String> mapTags(PublicApiCategoryForm result) {
        List<String> barrierTagResult = new ArrayList<>(); // 태그 결과들을 담을 리스트
        if (result == null || result.getServList() == null) {
            return barrierTagResult;
        } else {
            PublicApiCategoryForm.ServList first = result.getServList().stream().findFirst().orElse(null);
            // Input 한 개당 하나의 배리어 프리 정보가 생성되므로 하나만 찾는다
            List<String> splitInput = getStrings(first);
            if (splitInput != null) return splitInput;
        }
        return barrierTagResult;
    }


    /**
     * @param serv API 결과
     * @return Enum을 통해 String 가공해서 변환
     */

    private List<String> getStrings(PublicApiCategoryForm.ServList serv) {
        if (serv != null && serv.validation()) { // 결과가 존재할 떄
            String[] splitInput = serv.getEvalInfo().split(",");
            return Arrays.stream(splitInput)
                    .map(BarrierCode::getColumnFromDesc)
                    .filter(code -> code != null && !code.equals(""))
                    .collect(Collectors.toList());
        }
        return null;
    }




}


