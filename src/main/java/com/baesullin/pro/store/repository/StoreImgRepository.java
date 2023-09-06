package com.baesullin.pro.store.repository;

import com.baesullin.pro.store.domain.StoreImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreImgRepository extends JpaRepository<StoreImage, Integer> {
    List<StoreImage> findAllByStoreId(Long storeId);

    StoreImage findByStoreId(long storeId);
}
