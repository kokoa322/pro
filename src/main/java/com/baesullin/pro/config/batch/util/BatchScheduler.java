package com.baesullin.pro.config.batch.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.baesullin.pro.config.batch.BatchConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableSchedulerLock(defaultLockAtMostFor = "PT10S")
public class BatchScheduler {

    private final BatchConfiguration batchConfiguration;
    private final JobLauncher jobLauncher;
    private boolean scheduledEnable = false;

    // 주기적으로 실행될 스케줄링된 작업을 정의합니다.
    @Scheduled(cron = "0 0 0 ? * SUN", zone = "Asia/Seoul")
    @SchedulerLock(name = "updateScheduler", lockAtLeastFor = "PT58M", lockAtMostFor = "PT59M")
    public void storeApiUpdateJob() {
        // 처음 실행될 때만 실행하고 초기화 플래그를 설정
        if (scheduledEnable) {
            LocalDateTime now = LocalDateTime.now();
            System.out.println(now.getHour() + ":" + now.getMinute() + ":" + now.getSecond());

            // 작업에 필요한 매개변수를 설정
            Map<String, JobParameter> confMap = new HashMap<>();
            confMap.put("time", new JobParameter(System.currentTimeMillis()));
            JobParameters jobParameters = new JobParameters(confMap);

            try {
                // Spring Batch JobLauncher를 사용하여 배치 작업을 실행
                jobLauncher.run(batchConfiguration.JpaPageJob1_storeApiUpdate(), jobParameters);
            } catch (JobExecutionAlreadyRunningException
                     | JobInstanceAlreadyCompleteException
                     | JobParametersInvalidException
                     | org.springframework.batch.core.repository.JobRestartException e) {
                // 작업 실행 중 발생할 수 있는 예외를 처리
                log.error(e.getMessage());
            } catch (JsonProcessingException e) {
                // JSON 처리 예외를 런타임 예외로 던짐
                throw new RuntimeException(e);
            }

        } else if (!scheduledEnable){
            System.out.println("storeApiUpdateJob");
            scheduledEnable = true;
            return;
        }
    }
}

