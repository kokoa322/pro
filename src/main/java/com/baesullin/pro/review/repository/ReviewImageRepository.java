package com.baesullin.pro.review.repository;

import com.baesullin.pro.review.domain.Review;
import com.baesullin.pro.review.domain.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Integer> {
    @Modifying
    @Query("delete from ReviewImage i where i.reviewId= :review")
    void deleteAllByReviewId(@Param("review") Review review);

    void deleteByReviewId(Review review);

    void deleteAllInBatchByReviewId(Review review);

    void deleteInBatchByReviewId(Review review);

    List<ReviewImage> findAllByReviewId(Review review);
}
