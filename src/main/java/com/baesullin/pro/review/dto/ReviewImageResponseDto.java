package com.baesullin.pro.review.dto;

import com.baesullin.pro.review.domain.ReviewImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewImageResponseDto {

    private String url;

    public ReviewImageResponseDto(ReviewImage reviewImage) {
        this.url = reviewImage.getReviewImageUrl();

    }
}
