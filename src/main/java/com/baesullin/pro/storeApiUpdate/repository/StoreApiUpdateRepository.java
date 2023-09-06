package com.baesullin.pro.storeApiUpdate.repository;

import com.baesullin.pro.storeApiUpdate.StoreApiUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreApiUpdateRepository extends JpaRepository<StoreApiUpdate, Long> {
}