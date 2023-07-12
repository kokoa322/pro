package com.baesullin.pro.tag.domain;

import com.baesullin.pro.review.domain.Review;
import com.baesullin.pro.util.TimeStamped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Tag extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column
    private String tag;
    //jpa를 리스트/JSON 집어넣는 방법?


}

