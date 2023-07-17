package com.baesullin.pro.review.domain;

import com.baesullin.pro.review.dto.ReviewRequestDto;
import com.baesullin.pro.store.domain.Store;
import com.baesullin.pro.tag.domain.Tag;
import com.baesullin.pro.user.domain.User;
import com.baesullin.pro.util.TimeStamped;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    //평가(댓글)
    @NotBlank(message = "내용을 입력해주세요")
    @Length(min = 20, max = 200, message = "20자 이상, 200자 이하로 작성해주세요")
    @Column(nullable = false)
    private String content;

    //별점
    @Column(nullable = false)
    private double point;


    //1.@OneToMany(mappedBy = "userId") // 연관관계의 주인인 OrderItem의 userId 매핑 되어있다는 뜻

    //2.JPA 영속성 전이(CASCADE)
    //부모 엔티티가 영속화될 때 자식 엔티티도 같이 영속화되고, 부모 엔티티가 삭제될 때 자식 엔티티도 삭제되는 등 특정 엔티티를 영속 상태로 만들 때 연관된 엔티티도 함께 영속 상태로 전이되는 것을 의미

    //CascadeType.ALL: 모든 Cascade를 적용
    //CascadeType.PERSIST: 엔티티를 영속화할 때, 연관된 엔티티도 함께 유지
    //CascadeType.MERGE: 엔티티 상태를 병합(Merge)할 때, 연관된 엔티티도 모두 병합
    //CascadeType.REMOVE: 엔티티를 제거할 때, 연관된 엔티티도 모두 제거
    //CascadeType.DETACH: 부모 엔티티를 detach() 수행하면, 연관 엔티티도 detach()상태가 되어 변경 사항 반영 X
    //CascadeType.REFRESH: 상위 엔티티를 새로고침(Refresh)할 때, 연관된 엔티티도 모두 새로고침

    //3.orpahnRemoval이란 고아 객체(Orphan)을 제거한다는 뜻으로, 부모 Entity와의 연관관계가 끊어진 자식 Entity를 자동으로 삭제하는 기능

    //4.@ManyToOne(fetch = FetchType.LAZY) 참고로 @ManyToOne 매핑의 기본 fetch가 EAGER라서 생략해도 EAGER로 동작한다
    // https://velog.io/@jin0849/JPA-%EC%A6%89%EC%8B%9C%EB%A1%9C%EB%94%A9EAGER%EA%B3%BC-%EC%A7%80%EC%97%B0%EB%A1%9C%EB%94%A9LAZY


    //리뷰 이미지 URL
    @OneToMany(mappedBy = "reviewId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> reviewImageList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID", nullable = false)
    private Store storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User userId;

    @OneToMany(mappedBy = "reviewId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tagList = new ArrayList<>();

    public Review(ReviewRequestDto reviewRequestDto, Store store, User user){
        this.point   = reviewRequestDto.getPoint();
        this.userId  = user;
        this.content = reviewRequestDto.getContent();
        this.storeId = store;
    }

    public void update(ReviewRequestDto reviewRequestDto){
        this.point   = reviewRequestDto.getPoint();
        this.content = reviewRequestDto.getContent();
    }

}
