package com.baesullin.pro.storeApiUpdate;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity(name = "Store_api_update")
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class StoreApiUpdate {
    @Id
    private Long id;

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

    //    @Column(nullable = false)
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



//    public StoreApiUpdate(PublicApiResponseDto.Row row) {
//        //storeId - 임시
//        this.id          = row.getStoreId();
//        this.name        = row.getSISULNAME();
//        this.address     = row.getADDR();
//        this.phoneNumber = row.getTEL();
//
//        //접근로
//        this.approach        = row.getST1();
//        //주차장
//        this.parking         = row.getST2();
//        //높이차이제거
//        this.heightDifferent = row.getST3();
//        //승강기
//        this.elevator        = row.getST4();
//        //화장실
//        this.toilet          = row.getST5();
//
//
//        this.latitude  = row.getLatitude();
//        this.longitude = row.getLongitude();
//        this.category  = row.getCategory();
//    }


}