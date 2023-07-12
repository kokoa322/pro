package com.baesullin.pro.store.domain;

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


}