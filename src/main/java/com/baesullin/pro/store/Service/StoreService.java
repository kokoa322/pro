package com.baesullin.pro.store.Service;

import com.baesullin.pro.store.domain.Store;
import com.baesullin.pro.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@EnableScheduling
//@EnableSchedulerLock(defaultLockAtMostFor = "PT10S")
@Slf4j
public class StoreService {

    private final StoreRepository storeRepository;



    public void updateAvg(Store store, String socialId) {
        storeRepository.save(store.updatePointAvg());
    }

}