package com.baesullin.pro.review.repository;

import com.baesullin.pro.config.quaryDsl.QuerydslConfig;
import com.baesullin.pro.review.domain.QReview;
import com.baesullin.pro.review.domain.Review;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ReviewCustomQueryImpl implements ReviewCustomQuery{

    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public Review findByIdDsl(int reviewId) {

        System.out.println("IDIDIDIDIDIDIDIDID"+reviewId);
        QReview qReview = QReview.review;

        return jpaQueryFactory.selectFrom(
                qReview
        ).where(qReview.id.eq(reviewId)).fetchOne();



        /*
        JPAQuery<Tuple> query = jpaQueryFactory.select(
                qReview.id,
                qReview.content,
                qReview.reviewImageList,
                qReview.storeId,
                qReview.createdAt,
                qReview.modifiedAt,
                qReview.point,
                qReview.tagList,
                qReview.userId
        ).from(qReview).where(qReview.id.eq(reviewId));


        return query.fetch().stream().map(tupple -> Review.builder()
                .tagList(tupple.get(qReview.tagList))
                .reviewImageList(tupple.get(qReview.reviewImageList))
                .content(tupple.get(qReview.content))
                .userId(tupple.get(qReview.userId))
                .point(tupple.get(qReview.point))
                .id(tupple.get(qReview.id))
                .storeId(tupple.get(qReview.storeId)).build()).collect(Collectors.toList());
                */



    }
}
