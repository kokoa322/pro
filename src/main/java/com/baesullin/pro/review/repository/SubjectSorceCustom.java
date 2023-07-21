package com.baesullin.pro.review.repository;

import com.baesullin.pro.review.domain.Review;
import com.baesullin.pro.store.domain.Store;

import java.util.List;

public interface SubjectSorceCustom {

    List<Review> querySqlfindById(Integer reviewId);

}
