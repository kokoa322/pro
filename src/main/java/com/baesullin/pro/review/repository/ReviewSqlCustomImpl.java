package com.baesullin.pro.review.repository;

import com.baesullin.pro.review.domain.QReview;
import com.baesullin.pro.review.domain.Review;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ReviewSqlCustomImpl implements SubjectSorceCustom{

    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public List<Review> querySqlfindById(Integer reviewId) {
        QReview review = QReview.review;

         JPAQuery<Tuple> query = jpaQueryFactory.select(
                review.id,
                review.content,
                review.point,
                review.reviewImageList,
                review.userId,
                review.tagList
                ).
                where(review.id.eq(reviewId)).
                from(review);


        //list<review>이면
          return query.stream().map(tuple -> Review.builder()
                 .id(tuple.get(review.id))
                 .content(tuple.get(review.content))
                 .point(tuple.get(review.point))
                 .reviewImageList(tuple.get(review.reviewImageList))
                 .storeId(tuple.get(review.storeId))
                 .userId(tuple.get(review.userId))
                 .tagList(tuple.get(review.tagList))
                 .build()).collect(Collectors.toList());
    }
}
