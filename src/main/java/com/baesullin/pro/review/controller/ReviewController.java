package com.baesullin.pro.review.controller;

import com.baesullin.pro.common.SuccessResponse;
import com.baesullin.pro.exception.CustomException;
import com.baesullin.pro.exception.ErrorCode;
import com.baesullin.pro.review.dto.PageInfoResponseDto;
import com.baesullin.pro.review.dto.ReviewRequestDto;
import com.baesullin.pro.review.service.ReviewService;
import com.baesullin.pro.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

//
//    @GetMapping("/review/{storeId}")
//    public ResponseEntity<PageInfoResponseDto> getStoreReview(@PathVariable int storeId,
//                                                             User user,
//                                                             @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
//        if (user == null) {
//            PageInfoResponseDto pageInfoResponseDto = reviewService.getReview(storeId, pageable);
//            return new ResponseEntity<>(pageInfoResponseDto, HttpStatus.OK);
//        }
//        PageInfoResponseDto pageInfoResponseDto = reviewService.getReview(storeId, user.getUsername(), pageable);
//        return new ResponseEntity<>(pageInfoResponseDto, HttpStatus.OK);
//    }

    /**
     * 리뷰 작성
     */
    @PostMapping("/review")
    public SuccessResponse review(@ModelAttribute @Valid ReviewRequestDto reviewRequestDto, User user) throws IOException {



        if (user == null) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        reviewService.review(reviewRequestDto, user.getUsername());
        return new SuccessResponse("리뷰 등록 성공");
    }


}
