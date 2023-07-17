package com.baesullin.pro.review.repository;

import com.baesullin.pro.review.domain.Review;
import com.baesullin.pro.store.domain.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findAllByStoreId(Store store);
    Page<Review> findAllByStoreId(Store store, Pageable pageable);

}
