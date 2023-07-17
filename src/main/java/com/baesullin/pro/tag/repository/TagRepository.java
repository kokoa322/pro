package com.baesullin.pro.tag.repository;

import com.baesullin.pro.review.domain.Review;
import com.baesullin.pro.tag.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    @Modifying
    @Query("delete from Tag t where t.reviewId= :review")
    void deleteAllByReviewId(@Param("review") Review review);

    List<Tag> findAllByReviewId(Review review);

}
