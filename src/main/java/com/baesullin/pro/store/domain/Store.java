package com.baesullin.pro.store.domain;

import com.baesullin.pro.api.dto.LocationInfoDto;
import com.baesullin.pro.api.model.PublicApiV1Form;
import com.baesullin.pro.api.model.PublicApiV2Form;
import com.baesullin.pro.bookmark.domain.Bookmark;
import com.baesullin.pro.common.DataClarification;
import com.baesullin.pro.review.domain.Review;
import com.baesullin.pro.storeApiUpdate.StoreApiUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity(name="Store")
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Slf4j
public class Store implements Serializable {
    @Id
    private long id;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 25, scale = 22)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 25, scale = 22)
    private BigDecimal longitude;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String elevator;

    @Column(nullable = false)
    private String toilet;

    @Column(nullable = false)
    private String parking;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String heightDifferent;

    @Column(nullable = false)
    private String approach;

    @Column(nullable = false)
    private int bookMarkCount = 0;

    @Column(nullable = false)
    private int reviewCount = 0;

    @Column(nullable = false)
    private double pointAvg = 0.0;


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

    // 연관관계 매핑
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreImage> storeImageList = new ArrayList<>();

    @OneToMany(mappedBy = "storeId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewList = new ArrayList<>();

    @OneToMany(mappedBy = "storeId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarkList = new ArrayList<>();



    public Store(LocationInfoDto.LocationResponse sr, PublicApiV2Form.ServList servList, List<String> barrierTagList) {
        this.id = sr.getStoreId();
        this.name = sr.getStoreName();
        this.latitude = new BigDecimal(servList.getFaclLat());
        this.longitude = new BigDecimal(servList.getFaclLng());
        this.address = DataClarification.clarifyString(servList.getLcMnad());
        this.elevator = barrierTagList.contains("elevator") ? "Y" : "N";
        this.heightDifferent = barrierTagList.contains("height_different") ? "Y" : "N";
        this.toilet = barrierTagList.contains("toilet") ? "Y" : "N";
        this.parking = barrierTagList.contains("parking") ? "Y" : "N";
        this.approach = barrierTagList.contains("approach") ? "Y" : "N";

        this.phoneNumber = sr.getPhoneNumber();
        this.category = sr.getCategory();
    }

    public Store(PublicApiV1Form.Row row) {
        //storeId - 임시
        this.id = row.getStoreId();
        this.name = row.getSISULNAME();
        this.address = DataClarification.clarifyString(row.getADDR());
        this.phoneNumber = row.getTEL();
        //접근로
        this.approach = row.getST1();
        //주차장
        this.parking = row.getST2();
        //높이차이제거
        this.heightDifferent = row.getST3();
        //승강기
        this.elevator = row.getST4();
        //화장실
        this.toilet = row.getST5();

        this.latitude = row.getLatitude();
        this.longitude = row.getLongitude();
        this.category = row.getCategory();
    }

    public Store updatePointAvg() {
        this.reviewCount = reviewList.size();
        double totalPoint = 0.0;
        for (Review review : reviewList) {
            totalPoint += review.getPoint();
        }
        this.pointAvg = reviewCount == 0 ? 0 : Double.parseDouble(String.format("%.1f", totalPoint / reviewList.size()));
        return this;
    }

    public void removeReview(Review review) {
        this.reviewList.remove(review);
    }
    public void removeBookmark(Bookmark bookmark) {
        this.bookmarkList.remove(bookmark);
    }

    public Store updateBookmarkCount() {
        this.bookMarkCount = this.getBookmarkList().size();
        return this;
    }

    public Store(StoreApiUpdate row){
        this.id          = row.getId();
        this.name        = row.getName();
        this.address     = row.getAddress();
        this.phoneNumber = row.getPhoneNumber();

        /* 태그 */
        //접근로
        this.approach        = row.getApproach();
        //주차장
        this.parking         = row.getParking();
        //승강기
        this.elevator        = row.getElevator();
        //화장실
        this.toilet          = row.getToilet();
        //높이차이제거
        this.heightDifferent = row.getHeightDifferent();

        this.category  = row.getCategory();
        this.latitude  = row.getLatitude();
        this.longitude = row.getLongitude();
    }


    public void apiUpdate(StoreApiUpdate row) {
        this.id          = row.getId();
        this.name        = row.getName();
        this.address     = row.getAddress();
        this.phoneNumber = row.getPhoneNumber();

        /* 태그 */
        //접근로
        this.approach        = row.getApproach();
        //주차장
        this.parking         = row.getParking();
        //승강기
        this.elevator        = row.getElevator();
        //화장실
        this.toilet          = row.getToilet();
        //높이차이제거
        this.heightDifferent = row.getHeightDifferent();

        this.category  = row.getCategory();
        this.latitude  = row.getLatitude();
        this.longitude = row.getLongitude();
    }

    public void removeReviewImage(Review review) {
        this.reviewList.remove(review);
    }


}