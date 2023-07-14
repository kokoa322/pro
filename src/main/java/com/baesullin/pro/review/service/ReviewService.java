package com.baesullin.pro.review.service;

import com.baesullin.pro.exception.CustomException;
import com.baesullin.pro.exception.ErrorCode;
import com.baesullin.pro.review.domain.Review;
import com.baesullin.pro.review.domain.ReviewImage;
import com.baesullin.pro.review.dto.PageInfoResponseDto;
import com.baesullin.pro.review.dto.ReviewRequestDto;
import com.baesullin.pro.review.dto.ReviewResponseDto;
import com.baesullin.pro.review.repository.ReviewImageRepository;
import com.baesullin.pro.review.repository.ReviewRepository;
import com.baesullin.pro.store.Service.StoreService;
import com.baesullin.pro.store.domain.Store;
import com.baesullin.pro.store.repository.StoreRepository;
import com.baesullin.pro.tag.domain.Tag;
import com.baesullin.pro.tag.repository.TagRepository;
import com.baesullin.pro.user.domain.User;
import com.baesullin.pro.user.repository.UserRepository;
import com.baesullin.pro.util.AwsS3Manager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final AwsS3Manager awsS3Manager;
    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final StoreService storeService;
    /**
     * 리뷰 작성
     */
    public void review(ReviewRequestDto reviewRequestDto, String socialId) throws IOException {

        for(String tag: reviewRequestDto.getTagList()){
            for(String tagsList: reviewRequestDto.Tags()){
                if(tag != tagsList){
                    new Exception("해당 태그명으로 리뷰 등록할 수 없습니다");
                }
            }
        }

        long storeId = reviewRequestDto.getStoreId();
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("해당하는 업장이 존재하지 않습니다."));
        User user = userRepository.findBySocialId(socialId);
        Review review = new Review(reviewRequestDto, store, user);

        // todo 태크 매핑
        List<Tag> tagList = new ArrayList<>();
        for (String s : reviewRequestDto.getTagList()) {
            System.out.println("tag --> " + s);
            tagList.add(new Tag(s, review));
        } // 태그 -> 엔티티 변환

        List<ReviewImage> reviewImageUrlList = new ArrayList<>();
        List<MultipartFile> newReviewImage = reviewRequestDto.getImageFile();

        // todo 이미지가 널값이 아니라면 업로드 실행
        if (newReviewImage != null && !newReviewImage.isEmpty()) {
            for (MultipartFile reviewImageFile : newReviewImage) {
                String fileDir = awsS3Manager.uploadFile(reviewImageFile);
                log.info("upload --> " + fileDir);
                reviewImageUrlList.add(ReviewImage.builder().reviewId(review).reviewImageUrl(fileDir).build());
            } // 리뷰이미지 -> url -> 엔티티 변환
        }

        tagRepository.saveAll(tagList);
        reviewImageRepository.saveAll(reviewImageUrlList);
        reviewRepository.save(review); // 아래의 {store.updatePointAvg()} 보다 리뷰가 먼저 처리되게 해야한다.
        storeService.updateAvg(store, socialId);
    }


    /**
     * 리뷰 조회
     */

    public PageInfoResponseDto getReview(long storeId, Pageable pageable) {

        Store store = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("해당 가게가 없습니다"));
        Page<Review> reviewList = reviewRepository.findAllByStoreId(store, pageable);

        List<ReviewResponseDto> reviewResponseDtoList = new ArrayList<>();
        for (Review review : reviewList) {
            ReviewResponseDto reviewResponseDto = new ReviewResponseDto(review);
            User user = userRepository.findById(reviewResponseDto.getUserId()).orElseThrow(() -> new CustomException(ErrorCode.NO_USER_FOUND));
            reviewResponseDto.userInfo(user);
            reviewResponseDtoList.add(reviewResponseDto);
        }

        return PageInfoResponseDto
                .builder()
                .totalElements((int) reviewList.getTotalElements())
                .totalPages(reviewList.getTotalPages())
                .number(reviewList.getNumber())
                .size(reviewList.getSize())
                .reviewResponseDtoList(reviewResponseDtoList)
                .hasNextPage(reviewList.isFirst())
                .hasPreviousPage(reviewList.isLast())
                .build();
    }

    /**
     * 리뷰 조회
     */

    public PageInfoResponseDto getReview(long storeId, String socialId, Pageable pageable) {

        User myUser = userRepository.findBySocialId(socialId);
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new CustomException(ErrorCode.NO_STORE_FOUND));
        Page<Review> reviewList = reviewRepository.findAllByStoreId(store, pageable);

        List<ReviewResponseDto> reviewResponseDtoList = new ArrayList<>();


        for (Review review : reviewList) {
            ReviewResponseDto reviewResponseDto = new ReviewResponseDto(review);
            User user = userRepository.findById(reviewResponseDto.getUserId()).orElseThrow(() -> new CustomException(ErrorCode.NO_USER_FOUND));
            reviewResponseDto.userInfo(user, myUser);
            reviewResponseDtoList.add(reviewResponseDto);
        }

        return PageInfoResponseDto
                .builder()
                .totalElements((int) reviewList.getTotalElements())
                .totalPages(reviewList.getTotalPages())
                .number(reviewList.getNumber())
                .size(reviewList.getSize())
                .reviewResponseDtoList(reviewResponseDtoList)
                .hasNextPage(!reviewList.isFirst())
                .hasPreviousPage(reviewList.isLast())
                .build();
    }


}
