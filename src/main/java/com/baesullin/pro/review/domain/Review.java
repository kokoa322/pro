package com.baesullin.pro.review.domain;

import com.baesullin.pro.util.TimeStamped;
import lombok.*;

import javax.persistence.*;
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

    //평가(댓글)내용
    @Column(nullable = false)
    private String content;

    //별점
    @Column(nullable = false)
    private double point;


}
