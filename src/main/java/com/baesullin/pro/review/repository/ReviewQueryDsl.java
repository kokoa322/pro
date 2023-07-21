package com.baesullin.pro.review.repository;

import com.baesullin.pro.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewQueryDsl extends JpaRepository<Review, Long>, ReviewCustomQuery{

}
