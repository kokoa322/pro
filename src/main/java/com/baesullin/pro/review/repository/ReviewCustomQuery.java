package com.baesullin.pro.review.repository;

import com.baesullin.pro.review.domain.Review;

import java.util.List;

public interface ReviewCustomQuery {

    Review findByIdDsl(int reviewId);
}
