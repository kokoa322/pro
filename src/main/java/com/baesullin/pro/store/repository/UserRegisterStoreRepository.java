package com.baesullin.pro.store.repository;

import com.baesullin.pro.store.domain.UserRegisterStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRegisterStoreRepository extends JpaRepository<UserRegisterStore, Long> {
    Page<UserRegisterStore> findAll(Pageable pageable);
}
