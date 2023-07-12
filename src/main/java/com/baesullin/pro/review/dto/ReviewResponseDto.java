package com.baesullin.pro.review.dto;

import com.baesullin.pro.tag.domain.Tag;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDto {

    private int reviewId; // storeId
    private long storeId;
    private int userId;

    private String email;

    private String name;

    private String userImage;

    private String myReview;

    private Double point;
    private String content;
    private List<ReviewImageResponseDto> reviewImageUrlList;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<TagResponseDto> tagList;




    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class TagResponseDto{
        private int id;
        private String tag;

        public TagResponseDto(Tag tag) {
            this.id = tag.getId();
            this.tag = tag.getTag();
        }
    }
}
