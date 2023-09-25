package com.baesullin.pro.config.batch;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.baesullin.pro.api.dto.LocationInfoDto;
import com.baesullin.pro.api.dto.LocationPartDto;
import com.baesullin.pro.api.model.*;
import com.baesullin.pro.api.service.LocationService;
import com.baesullin.pro.api.service.LocationServiceRT;
import com.baesullin.pro.common.DataClarification;
import com.baesullin.pro.config.batch.requestDto.JsonDTO;
import com.baesullin.pro.config.batch.requestDto.StoreDTO;
import com.baesullin.pro.config.batch.util.ApiUpdateThread;
import com.baesullin.pro.exception.CustomException;
import com.baesullin.pro.exception.ErrorCode;
import com.baesullin.pro.store.domain.Store;
import com.baesullin.pro.store.repository.StoreRepository;
import com.baesullin.pro.store.service.StoreImageService;
import com.baesullin.pro.storeApiUpdate.StoreApiUpdate;
import com.baesullin.pro.storeApiUpdate.repository.StoreApiUpdateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import software.amazon.ion.Decimal;

import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class BatchConfiguration {

    @Qualifier("locationServiceRT")
    private final LocationServiceRT        locationServiceRT;
    private final StoreRepository          storeRepository;
    private final JobBuilderFactory        jobBuilderFactory;   //Job 생성자
    private final StepBuilderFactory       stepBuilderFactory;  //Step 생성자
    private final EntityManagerFactory     entityManagerFactory;
    private final StoreApiUpdateRepository storeApiUpdateRepository;
    private final StoreImageService        storeImageService;



    private static final int CHUNKSIZE = 100; //쓰기 단위인 청크사이즈

    private static int STORE_SIZE = 0; //쓰기 단위인 청크사이즈


    @Bean
    public Job JpaPageJob1_storeApiUpdate() throws JsonProcessingException{
            return jobBuilderFactory.get("JpaPageJob1_storeApiUpdate")
                    .start(JpaPageJob1_step1()) // store_api_update API 응답데이터 받기
                    //.next(jpaPageJob1_step2())  // 추가된 업장이 있으면 store 테이블에 INSERT
                    //.start(JpaPageJob4_step1())  // 사라진 업장이 있으면 store 테이블에 DELETE
                    //.next(JpaPageJob1_step4()) // 수정된 업장이 있다면 store 테이블에 UPDATE
                    .build();
    }




    @Bean
    public Step JpaPageJob1_step1() throws JsonProcessingException{

        return stepBuilderFactory.get("JpaPageJob2_step1")
                //청크사이즈 설정
                .<StoreApiUpdate, StoreApiUpdate>chunk(CHUNKSIZE)
                .reader(jpaPageJob1_ItemReader())
                .processor(jpaPageJob1_Processor())
                .writer(jpaPageJob1_dbItemWriter())
                .build();

    }

        @Bean
        public ListItemReader<StoreApiUpdate> jpaPageJob1_ItemReader() throws JsonProcessingException{

            // System.currentTimeMillis(); --- 현재 시간을 밀리초(ms, 천분의 일초) 단위로 반환
            long sTime = System.currentTimeMillis(); // 시작시간
            Double sec = (System.currentTimeMillis() - sTime) / 1000.0;
            System.out.printf("시작 --- (%.2f초)%n", sec);

            List<StoreApiUpdate> storeApiUpdateList = new ArrayList<>();

            List<List<String>> csvList = readCSVFile("src/main/resources/static/sigungu.csv");
            List<List<List<String>>> csvListList = Lists.partition(csvList, csvList.size()/1);

            System.out.println("Thred 갯수 -- >: "+ csvListList.size());
            List<ApiUpdateThread> apiUpdateThreadList = new ArrayList<>();
            int index = 1;


            csvListList.forEach((csvListAvg) -> {
                ApiUpdateThread apiUpdateThread = new ApiUpdateThread(csvListAvg, storeApiUpdateList, 1, publicV2Key, kokoaApiKey, index);
                apiUpdateThread.start();
                apiUpdateThreadList.add(apiUpdateThread);
            });

            /*
            for(List<List<String>> csvListAvg: csvListList){
                ApiUpdateThread apiUpdateThread = new ApiUpdateThread(csvListAvg, storeApiUpdateList, 1, publicV2Key, kokoaApiKey, index);
                apiUpdateThread.start();
                apiUpdateThreadList.add(apiUpdateThread);
                index ++;
            }
             */

            try {
                for (ApiUpdateThread apiUpdateThread: apiUpdateThreadList){
                    apiUpdateThread.join();
                }
            } catch(Exception e){
                e.printStackTrace();
            }

            log.info("store SIZE --> "+ storeApiUpdateList.size());

            /*
            HttpHeaders  headers        = new HttpHeaders();
            RestTemplate restTemplate   = new RestTemplate();
            String body                 = "";

            HttpEntity<String>      requestEntity  = new HttpEntity<String>(body, headers);
            ResponseEntity<String>  responseEntity = restTemplate.exchange("http://openapi.seoul.go.kr:8088/5274616b45736f7933376e6c525658/json/touristFoodInfo/1/1000/", HttpMethod.GET, requestEntity, String.class);
            HttpStatus              httpStatus     = responseEntity.getStatusCode();
            String                  response       = responseEntity.getBody();



            JsonDTO jsonDTO = new Gson().fromJson(response, JsonDTO.class);  //conversion using Gson Library.
            setInfos(jsonDTO);

            storeApiUpdateList.addAll(saveValidStores(jsonDTO.getTouristFoodInfo().getRow()));

            log.info("store SIZE --> "+ storeApiUpdateList.size());

            STORE_SIZE = storeApiUpdateList.size();

             */

            sec = (System.currentTimeMillis() - sTime) / 1000.0;
            System.out.printf("소요시간 --- (%.2f초)%n", sec);
            return new ListItemReader<>(storeApiUpdateList);


        }

        private ItemProcessor<StoreApiUpdate, StoreApiUpdate> jpaPageJob1_Processor() {
            return storeApiUpdate -> {

                log.info("********** This is jpaPageJob1_Processor");
                return storeApiUpdate;

            };
        }

        public ItemWriter<StoreApiUpdate> jpaPageJob1_dbItemWriter(){

            log.info("********** This is jpaPageJob1_dbItemWriter");

            return list -> {
                for(StoreApiUpdate storeApiUpdate: list){
                    System.out.println(storeApiUpdate.getId());
                   // if(!storeApiUpdateRepository.existsById(storeApiUpdate.getId())){
                     //   storeImageService.saveImage(storeApiUpdate.getId());
                       // storeApiUpdateRepository.save(storeApiUpdate);
                   // }
                }
             //storeApiUpdateRepository.saveAll(list);
            };
        }

/*
    @Bean
    public Step jpaPageJob1_step2() throws JsonProcessingException {
        return stepBuilderFactory.get("jpaPageJob1_step2")
                //청크사이즈 설정
                .<StoreApiUpdate, Store>chunk(CHUNKSIZE)
                .reader(jpaPageJob1_step2_ItemReader())
                .processor(jpaPageJob1_step2_Processor())
                .writer(jpaPageJob1_step2_dbItemWriter())
                .build();

    }

        @Bean
        public JpaPagingItemReader<StoreApiUpdate> jpaPageJob1_step2_ItemReader() throws JsonProcessingException {

        log.info("********** This is jpaPageJob1_step2_ItemReader");
        return new JpaPagingItemReaderBuilder<StoreApiUpdate>()
                .name("jpaPageJob3_dbItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNKSIZE)
                .queryString("select a from Store_api_update a left join Store b on a.id = b.id where b.id is null order by a.id asc")
                .build();
    }


        private ItemProcessor<StoreApiUpdate, Store> jpaPageJob1_step2_Processor() {
        log.info("********** This is jpaPageJob1_step2_Processor");
        return storeApiUpdate -> {
            return new Store(storeApiUpdate);
        };

    }


        private ItemWriter<Store> jpaPageJob1_step2_dbItemWriter() {
        log.info("********** This is jpaPageJob1_step2_dbItemWriter");
        return list -> {
            for(Store store: list){
                log.info("{} {}",store.getId(), store.getName());
                System.out.println(store.getId());

            }
            JpaItemWriter<Store> jpaItemWriter = new JpaItemWriter<>();
            jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        };

//            JpaItemWriter<Store> jpaItemWriter = new JpaItemWriter<>();
//            jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
//            return jpaItemWriter;
    }
 */



//    @Bean
//    public Step JpaPageJob4_step1() throws JsonProcessingException {
//        return stepBuilderFactory.get("JpaPageJob4_step2")
//                //청크사이즈 설정
//                .<Store, Store>chunk(CHUNKSIZE)
//                .reader(jpaPageJob4_ItemReader())
//                .processor(jpaPageJob4_Processor())
//                .writer(jpaPageJob4_dbItemWriter())
//                .build();
//
//    }
//
//    @Bean
//    public JpaPagingItemReader<Store> jpaPageJob4_ItemReader() throws JsonProcessingException {
//
//
//        System.out.println("test ㅅㅅㄷㄴㅅ");
//        log.info("********** This is unPaidStoreReader");
//        return new JpaPagingItemReaderBuilder<Store>()
//                .name("jpaPageJob3_dbItemReader")
//                .entityManagerFactory(entityManagerFactory)
//                .pageSize(CHUNKSIZE)
//                .queryString("select a from Store_api_update a left join Store b on a.id = b.id where b.id is null order by a.id asc")
//                .build();
//    }
//
//
//
//
//    private ItemProcessor<Store, Store> jpaPageJob4_Processor() {
//        log.info("********** This is unPaidStoreProcessor");
//        return new ItemProcessor<Store, Store>() {  //
//
//            @Override
//            public Store process(Store store) throws Exception {
//                log.info("********** This is unPaidMemberProcessor");
//                return store;  // 2
//
//            }
//        };
//
//    }
//
//
//    private ItemWriter<Store> jpaPageJob4_dbItemWriter() {
//        log.info("********** This is jpaPageJob3_dbItemWriter");
//
//        return ((List<? extends Store> storeList) -> storeRepository.deleteAll(storeList));
//    }


    /*
    @Bean
    public Step JpaPageJob1_step4() throws JsonProcessingException {
        return stepBuilderFactory.get("JpaPageJob1_step4")
                //청크사이즈 설정
                .<StoreApiUpdate, Store>chunk(CHUNKSIZE)
                .reader(JpaPageJob1_step4_ItemReader())
                .processor(JpaPageJob1_step4_Processor())
                .writer(JpaPageJob1_step4_dbItemWriter())
                .build();
    }



    @Bean
    public JpaPagingItemReader<StoreApiUpdate> JpaPageJob1_step4_ItemReader() throws JsonProcessingException {

        log.info("********** This is JpaPageJob1_step4_ItemReader");
        return new JpaPagingItemReaderBuilder<StoreApiUpdate>()
                .name("jpaPageJob5_dbItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNKSIZE)
                .queryString("select a from Store_api_update a join Store b on a.id = b.id where a.id = b.id \n" +
                        "and a.approach != b.approach \n" +
                        "or a.address != b.address \n" +
                        "or a.elevator != b.elevator  \n" +
                        "or a.latitude != b.latitude \n" +
                        "or a.longitude != b.longitude \n" +
                        "or a.name != b.name \n" +
                        "or a.parking != b.parking \n" +
                        "or a.phoneNumber != b.phoneNumber\n" +
                        "or a.heightDifferent != b.heightDifferent \n" +
                        "or a.toilet != b.toilet order by a.id asc")
                .build();
    }


    private ItemProcessor<StoreApiUpdate, Store> JpaPageJob1_step4_Processor() {
        log.info("********** This is JpaPageJob1_step4_Processor");
        return storeApiUpdate -> {

            Optional<Store> store = storeRepository.findById(storeApiUpdate.getId());
            store.get().apiUpdate(storeApiUpdate);

            return store.get();
        };
    }


    private ItemWriter<Store> JpaPageJob1_step4_dbItemWriter() {
        log.info("********** This is JpaPageJob1_step4_dbItemWriter"+ "  STORE_SIZE -->"+ STORE_SIZE);

        return list -> {
            for(Store store: list){
                System.out.println(store.getId());
            }

        };

        //        return ((List<? extends Store> storeList) -> storeRepository.saveAll(storeList));
//            JpaItemWriter<Store> jpaItemWriter = new JpaItemWriter<>();
//            jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
//            return jpaItemWriter;

    }

     */




    @Value("${kakao.api.key}")
    private String kokoaApiKey;

    @Value("${public.api.v2.key}")
    private String publicV2Key;
    @Value("${public.api.v2.key2}")
    private String publicV2Key2;



    /** CSV 파일 읽기 */
    private List<List<String>> readCSVFile(String filePath){
        List<List<String>> csvList = new ArrayList<>();
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = Files.newBufferedReader(Paths.get(filePath));
            String line = "";

            while ((line = bufferedReader.readLine()) != null){
                List<String> stringList = new ArrayList<>();
                String stringArray[] = line.split(",");

                stringList = Arrays.asList(stringArray);
                csvList.add(stringList);

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                assert bufferedReader != null;
                bufferedReader.close();

            } catch(IOException e) {
                e.printStackTrace();
            }
            return csvList;
        }
    }







}



