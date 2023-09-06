package com.baesullin.pro.bookmark.controller;

import com.baesullin.pro.bookmark.dto.BookmarkInfoDto;
import com.baesullin.pro.bookmark.dto.BookmarkRequestDto;
import com.baesullin.pro.bookmark.service.BookmarkService;
import com.baesullin.pro.common.SuccessResponse;
import com.baesullin.pro.exception.CustomException;
import com.baesullin.pro.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BookmarkController {
    private final BookmarkService bookmarkService;

    /**
     * 북마크 생성 폴더 담기
     */
    @PostMapping("/bookmark")
    public SuccessResponse bookmark(@RequestBody BookmarkRequestDto bookmarkRequestDto,
                                    @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new CustomException(ErrorCode.NO_USER_FOUND);
        }
        bookmarkService.bookmark(bookmarkRequestDto, user.getUsername());
        return new SuccessResponse("북마크를 폴더에 저장 완료");
    }

    @DeleteMapping("/bookmark/{storeId}")
    public SuccessResponse bookmarkDelete(@PathVariable Long storeId,
                                          @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new CustomException(ErrorCode.NO_USER_FOUND);
        }
        bookmarkService.bookmarkDelete(storeId, user.getUsername());
        return new SuccessResponse("북마크를 삭제 완료");
    }

    @GetMapping("/bookmarkTop")
    public ResponseEntity<List<BookmarkInfoDto>> bookmarkTop(@AuthenticationPrincipal User user,
                                                             @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        List<BookmarkInfoDto> bookmarkList = bookmarkService.bookmarkTop(user.getUsername(), pageable);
        return new ResponseEntity<>(bookmarkList, HttpStatus.OK);
    }
}
