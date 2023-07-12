package com.baesullin.pro.review.controller;

import com.baesullin.pro.review.dto.PageInfoResponseDto;
import com.baesullin.pro.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReviewController {

    @GetMapping("/review/{storeId}")
    public ResponseEntity<PageInfoResponseDto> getStoreReview(@PathVariable int storeId,
                                                             User user,
                                                             @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        if (user == null) {
            PageInfoResponseDto pageInfoResponseDto = reviewService.getReview(storeId, pageable);
            return new ResponseEntity<>(pageInfoResponseDto, HttpStatus.OK);
        }
        PageInfoResponseDto pageInfoResponseDto = reviewService.getReview(storeId, user.getUsername(), pageable);
        return new ResponseEntity<>(pageInfoResponseDto, HttpStatus.OK);
    }

}
