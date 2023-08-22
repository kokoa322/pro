package com.baesullin.pro.review.controller;

import com.baesullin.pro.common.SuccessResponse;
import com.baesullin.pro.exception.CustomException;
import com.baesullin.pro.exception.ErrorCode;
import com.baesullin.pro.review.dto.ReviewRequestDto;
import com.baesullin.pro.review.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.io.IOException;
@RunWith(SpringRunner.class)
@WebMvcTest(ReviewController.class)
public class ReviewController {

    @MockBean
    ReviewService reviewService;

    @Autowired
    private MockBean mockBean;


    @Test
    @DisplayName("리뷰 작성 테스트")
    @PostMapping("/review")
    public SuccessResponse review(@ModelAttribute @Valid ReviewRequestDto reviewRequestDto, User user) throws IOException {


        if (user == null) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        reviewService.review(reviewRequestDto, user.getUsername());
        return new SuccessResponse("리뷰 등록 성공");
    }
}
