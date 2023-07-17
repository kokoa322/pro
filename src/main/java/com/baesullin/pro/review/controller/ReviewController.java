package com.baesullin.pro.review.controller;

import com.baesullin.pro.common.SuccessResponse;
import com.baesullin.pro.exception.CustomException;
import com.baesullin.pro.exception.ErrorCode;
import com.baesullin.pro.review.dto.PageInfoResponseDto;
import com.baesullin.pro.review.dto.ReviewRequestDto;
import com.baesullin.pro.review.service.ReviewService;
import com.baesullin.pro.user.domain.User;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

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


    /**
     * 리뷰 조회
     */
    @GetMapping("/review/{storeId}")
    public ResponseEntity<PageInfoResponseDto> getStoreReview(@PathVariable int storeId, User user,
                                                              @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        if (user == null) {
            PageInfoResponseDto pageInfoResponseDto = reviewService.getReview(storeId, pageable);
            return new ResponseEntity<>(pageInfoResponseDto, HttpStatus.OK);
        }

        PageInfoResponseDto pageInfoResponseDto = reviewService.getReview(storeId, user.getUsername(), pageable);
        return new ResponseEntity<>(pageInfoResponseDto, HttpStatus.OK);


    }

    /**
     * 리뷰 수정
     */

    @PatchMapping("/review/{reviewId}")
    public ResponseEntity<?> reviewUpdate(@ModelAttribute @Valid ReviewRequestDto reviewRequestDto,
                                          BindingResult bindingResult, User user, @PathVariable int reviewId) {

        if (bindingResult.hasErrors()) {
            List<ObjectError> errorList = bindingResult.getAllErrors();
            Logger log = LogManager.getLogger(this.getClass());
            for (ObjectError error : errorList) {
                log.info("test");
            }
        }

        if (user == null) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        reviewService.reviewUpdate(reviewRequestDto, user.getUsername(), reviewId);
        return ResponseEntity.ok("리뷰 수정 성공");
    }

    /**
     * 리뷰 삭제
     */
    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<?> reviewDelete(User user,
                                          @PathVariable int reviewId) {

        if (user == null) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        reviewService.reviewDelete(user.getUsername(), reviewId);
        return ResponseEntity.ok("리뷰 삭제 성공");
    }
}

