package com.baesullin.pro.user.domain;

import com.baesullin.pro.bookmark.domain.Folder;
import com.baesullin.pro.login.oauth.entity.ProviderType;
import com.baesullin.pro.login.oauth.entity.RoleType;
import com.baesullin.pro.review.domain.Review;
import com.baesullin.pro.util.TimeStamped;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name="user_")
@Getter
@Setter
@NoArgsConstructor
public class User extends TimeStamped {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false, unique = true)
    private String socialId;

    @Column(nullable = false)
    private String name;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 1)
    private String emailVerifiedYn; // 이메일을 필수값으로 설정해줬기 때문에 없어도 될 것 같다.

    @Column(nullable = false)
    private String profileImageUrl;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType roleType;



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
    // 연관관계 매핑
    @OneToMany(mappedBy = "userId",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Folder> folderList = new ArrayList<>();

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Folder> bookmarkList = new ArrayList<>();

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewList = new ArrayList<>();


}