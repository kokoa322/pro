package com.baesullin.pro.review.repository;

import com.baesullin.pro.common.QuerydslLocation;
import com.baesullin.pro.review.domain.Review;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

import static com.baesullin.pro.review.domain.QReview.review;
import static com.baesullin.pro.store.domain.QStore.store;

@Repository
@Transactional
public class ReviewQueryRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;

    public ReviewQueryRepository(JPAQueryFactory queryFactory) {
        super(Review.class);
        this.queryFactory = queryFactory;
    }

    public List<Review> findRecentReviews(BigDecimal lat,
                                          BigDecimal lng,
                                          int limit) {
        BooleanBuilder builder = new BooleanBuilder();
        if (lat != null && lng != null) {
            BigDecimal[] location = QuerydslLocation.getRange(lat, lng, 10);
            builder = QuerydslLocation.locationBuilder(location[0], location[1], location[2], location[3]);
        }
        // 위도 경도에 해당하는 가게를 찾음 -> 해당 댓글을 다 가져옴 -> 내림차순 정렬 -> limit
        return queryFactory.selectFrom(review)
                .join(review.storeId, store)
                .where(builder)
                .orderBy(review.createdAt.desc())
                .limit(limit)
                .fetch();
    }
}